package com.chaitupenjudcoder;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.chaitupenjudcoder.buckstrack.R;
import com.chaitupenjudcoder.buckstrack.databinding.ActivityBucksTransactionsBinding;
import com.chaitupenjudcoder.datapojos.AddIncomeExpense;
import com.chaitupenjudcoder.recyclerviews.BucksTransactionsRecycler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BucksTransactions extends AppCompatActivity {

    ActivityBucksTransactionsBinding transactionUtil;
    FirebaseDatabase dbTransactions;
    DatabaseReference transacRef;
    ArrayList<AddIncomeExpense> trans;
    RecyclerView transactions;
    BucksTransactionsRecycler transactionRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transactionUtil = DataBindingUtil.setContentView(this, R.layout.activity_bucks_transactions);
        trans = new ArrayList<>();
        getAllTransactions(new FirebaseCallBack() {
            @Override
            public void onCallBack(ArrayList<AddIncomeExpense> allTransactions) {
                transactionRecycler = new BucksTransactionsRecycler(getApplicationContext(), allTransactions);
                Toast.makeText(BucksTransactions.this, "size is :"+allTransactions.size(), Toast.LENGTH_SHORT).show();
                Log.d("WWWWW", allTransactions.toString());
                transactions = transactionUtil.rvTransactions;
                LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
                transactions.setLayoutManager(manager);
                transactions.setHasFixedSize(true);
//                transactions.setAdapter(transactionRecycler);
                transactionUtil.setTransactionAdapter(transactionRecycler);
            }
        });
    }

    private interface FirebaseCallBack {
        void onCallBack(ArrayList<AddIncomeExpense> allTransactions);
    }

    private void getAllTransactions(final FirebaseCallBack callBack) {
        dbTransactions = FirebaseDatabase.getInstance();
        transacRef = dbTransactions.getReference("user1");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot incomeExpenseShot:dataSnapshot.child("spendings").getChildren()) {
                    AddIncomeExpense expenseIncome = incomeExpenseShot.getValue(AddIncomeExpense.class);
                    trans.add(expenseIncome);
                }
                callBack.onCallBack(trans);

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message

                // ...
            }
        };
        transacRef.addValueEventListener(postListener);
    }
}
