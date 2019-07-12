package com.chaitupenjudcoder;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.chaitupenjudcoder.buckstrack.R;
import com.chaitupenjudcoder.buckstrack.databinding.ActivityBucksTransactionsBinding;
import com.chaitupenjudcoder.datapojos.IncomeExpense;
import com.chaitupenjudcoder.recyclerviews.BucksTransactionsRecycler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BucksTransactions extends AppCompatActivity {

    ActivityBucksTransactionsBinding transactionUtil;
    FirebaseDatabase dbTransactions;
    FirebaseUser user;
    DatabaseReference transacRef;
    ArrayList<IncomeExpense> trans;
    RecyclerView transactions;
    BucksTransactionsRecycler transactionRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transactionUtil = DataBindingUtil.setContentView(this, R.layout.activity_bucks_transactions);
        trans = new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser();

        //interface with callback function to update the UI
        getAllTransactions(new FirebaseCallBack() {
            @Override
            public void onCallBack(ArrayList<IncomeExpense> allTransactions) {
                transactionRecycler = new BucksTransactionsRecycler(getApplicationContext(), allTransactions);

                transactions = transactionUtil.rvTransactions;
                LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
                transactions.setLayoutManager(manager);
                transactions.setHasFixedSize(true);
                new ItemTouchHelper(swipeToDelete(allTransactions, transactionRecycler)).attachToRecyclerView(transactions);
//                transactions.setAdapter(transactionRecycler);
                transactionUtil.setTransactionAdapter(transactionRecycler);
            }
        });
    }

    private interface FirebaseCallBack {
        void onCallBack(ArrayList<IncomeExpense> allTransactions);
    }

    private ItemTouchHelper.SimpleCallback swipeToDelete(final ArrayList<IncomeExpense> list, final BucksTransactionsRecycler recycler) {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT){

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                list.remove(viewHolder.getAdapterPosition());
                recycler.notifyDataSetChanged();
            }
        };
        return callback;
    }

    private void getAllTransactions(final FirebaseCallBack callBack) {
        dbTransactions = FirebaseDatabase.getInstance();
        transacRef = dbTransactions.getReference("data/" + user.getUid());

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot incomeExpenseShot : dataSnapshot.child("spendings").getChildren()) {
                    IncomeExpense expenseIncome = incomeExpenseShot.getValue(IncomeExpense.class);
                    trans.add(expenseIncome);
                }
                callBack.onCallBack(trans);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //
            }
        };
        transacRef.addValueEventListener(postListener);
    }
}
