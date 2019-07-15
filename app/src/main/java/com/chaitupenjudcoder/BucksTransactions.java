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
import com.chaitupenjudcoder.firebasehelpers.FirebaseTransactionsHelper;
import com.chaitupenjudcoder.recyclerviews.BucksTransactionsRecycler;

import java.util.ArrayList;

public class BucksTransactions extends AppCompatActivity {

    ActivityBucksTransactionsBinding transactionUtil;
    RecyclerView rvTransactions;
    BucksTransactionsRecycler transactionRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transactionUtil = DataBindingUtil.setContentView(this, R.layout.activity_bucks_transactions);

        rvTransactions = transactionUtil.rvTransactions;
        FirebaseTransactionsHelper transactionsHelper = new FirebaseTransactionsHelper();

        transactionsHelper.getAllTransactions(this::initTransasctionsRecycler);
    }

    private void initTransasctionsRecycler(ArrayList<IncomeExpense> transactions) {
        transactionRecycler = new BucksTransactionsRecycler(getApplicationContext(), transactions);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        rvTransactions.setLayoutManager(manager);
        rvTransactions.setHasFixedSize(true);
        new ItemTouchHelper(swipeToDelete(transactions, transactionRecycler)).attachToRecyclerView(rvTransactions);
        transactionUtil.setTransactionAdapter(transactionRecycler);
    }

    private ItemTouchHelper.SimpleCallback swipeToDelete(final ArrayList<IncomeExpense> list, final BucksTransactionsRecycler recycler) {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {

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
}
