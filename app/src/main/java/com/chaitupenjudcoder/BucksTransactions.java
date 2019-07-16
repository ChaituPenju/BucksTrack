package com.chaitupenjudcoder;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Toast;

import com.chaitupenjudcoder.buckstrack.R;
import com.chaitupenjudcoder.buckstrack.databinding.ActivityBucksTransactionsBinding;
import com.chaitupenjudcoder.datapojos.IncomeExpense;
import com.chaitupenjudcoder.firebasehelpers.FirebaseTransactionsHelper;
import com.chaitupenjudcoder.recyclerviews.BucksTransactionsRecycler;

import java.util.ArrayList;
import java.util.function.Consumer;

public class BucksTransactions extends AppCompatActivity {

    ActivityBucksTransactionsBinding transactionUtil;
    RecyclerView rvTransactions;
    BucksTransactionsRecycler transactionRecycler;

    FirebaseTransactionsHelper helper;

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
        new ItemTouchHelper(swipeToDelete(transactions)).attachToRecyclerView(rvTransactions);
        transactionUtil.setTransactionAdapter(transactionRecycler);
    }

    private ItemTouchHelper.SimpleCallback swipeToDelete(final ArrayList<IncomeExpense> list) {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                confirmDeleteDialog(list, viewHolder.getAdapterPosition());
            }
        };
        return callback;
    }

    private void confirmDeleteDialog(ArrayList<IncomeExpense> list, int position) {
        AlertDialog.Builder confirmDelete = new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to delete transaction "+list.get(position).getTitle())
                .setPositiveButton("OK", (dialog, which) -> {
                    /*list.remove(position);
                    transactionRecycler.notifyDataSetChanged();*/
                    helper = new FirebaseTransactionsHelper();
                    helper.deleteATransaction(response -> Toast.makeText(BucksTransactions.this, response, Toast.LENGTH_SHORT).show(), list.get(position).getId());
                }).setNegativeButton("Don\'t delete", (dialog, which) -> {
                    transactionRecycler.notifyDataSetChanged();
                    dialog.dismiss();
                });
        AlertDialog confirmDeleteDialog = confirmDelete.create();
        confirmDeleteDialog.show();
    }
}
