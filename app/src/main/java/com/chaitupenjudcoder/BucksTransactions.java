package com.chaitupenjudcoder;

import android.content.Intent;
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
import com.chaitupenjudcoder.firebasehelpers.SharedPreferencesHelper;
import com.chaitupenjudcoder.recyclerviews.BucksTransactionsRecycler;

import java.util.ArrayList;
import java.util.Collections;

import static com.chaitupenjudcoder.firebasehelpers.FirebaseTransactionsHelper.DATE_ONE_EXTRA;
import static com.chaitupenjudcoder.firebasehelpers.FirebaseTransactionsHelper.DATE_TWO_EXTRA;

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
        String dateFormat = new SharedPreferencesHelper(getApplicationContext()).getDateFormatPref("dd-MM-yyyy");

        Intent in = getIntent();
        if (in.getExtras() != null) {
            if ((in.getExtras().containsKey("WEEK") || in.getExtras().containsKey("MONTH"))) {
                long days = in.getExtras().containsKey("WEEK") ? in.getExtras().getLong("WEEK") : in.getExtras().getLong(("MONTH"));
                transactionsHelper.getWeekOrMonthTransactions(this::initTransasctionsRecycler, days);
            } else if (in.getExtras().containsKey(DATE_ONE_EXTRA) && in.getExtras().containsKey(DATE_TWO_EXTRA)) {
                String date1 = in.getExtras().getString(DATE_ONE_EXTRA);
                String date2 = in.getExtras().getString(DATE_TWO_EXTRA);
                transactionsHelper.getTransactionsBwTwoDates(this::initTransasctionsRecycler, date1, date2, dateFormat);
            }
        } else {
            transactionsHelper.getAllTransactions(this::initTransasctionsRecycler);
        }
    }

    private void initTransasctionsRecycler(ArrayList<IncomeExpense> transactions) {
        Collections.sort(transactions, (d1, d2) -> d2.getDate().compareTo(d1.getDate()));
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
                .setMessage("Are you sure you want to delete transaction " + list.get(position).getTitle())
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
