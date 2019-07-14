package com.chaitupenjudcoder.recyclerviews;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.chaitupenjudcoder.AddIncomeExpenseActivity;
import com.chaitupenjudcoder.buckstrack.R;
import com.chaitupenjudcoder.buckstrack.databinding.ItemSpendingListBinding;
import com.chaitupenjudcoder.datapojos.IncomeExpense;

import java.util.ArrayList;

import static com.chaitupenjudcoder.BucksActivity.BUCKS_STRING_IS_INCOME_EXTRA;
import static com.chaitupenjudcoder.BucksActivity.INCOME_EXPENSE_OBJECT_EXTRA;

public class BucksTransactionsRecycler extends RecyclerView.Adapter<BucksTransactionsRecycler.BucksTransactionsAdapter> {

    private Context cxt;
    private ArrayList<IncomeExpense> allTransactions;

    public BucksTransactionsRecycler(Context cxt, ArrayList<IncomeExpense> allTransactions) {
        this.cxt = cxt;
        this.allTransactions = allTransactions;
    }

    @NonNull
    @Override
    public BucksTransactionsAdapter onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        ItemSpendingListBinding items = ItemSpendingListBinding.inflate(inflater, viewGroup, false);
        return new BucksTransactionsAdapter(items);
    }

    @Override
    public void onBindViewHolder(@NonNull BucksTransactionsAdapter bucksTransactionsAdapter, int i) {
        bucksTransactionsAdapter.itemViewBinding.cvTransactions.setAnimation(AnimationUtils.loadAnimation(cxt, R.anim.transaction_card_scale_animation));
        IncomeExpense ie = allTransactions.get(i);
        bucksTransactionsAdapter.bind(ie);
        bucksTransactionsAdapter.itemViewBinding.setTransactionClickListener(bucksTransactionsAdapter);
    }

    @Override
    public int getItemCount() {
        return allTransactions.size();
    }

    public class BucksTransactionsAdapter extends RecyclerView.ViewHolder {
        ItemSpendingListBinding itemViewBinding;

        BucksTransactionsAdapter(@NonNull ItemSpendingListBinding itemViewBinding) {
            super(itemViewBinding.getRoot());
            this.itemViewBinding = itemViewBinding;
        }

        public void bind(IncomeExpense ie) {
            this.itemViewBinding.setIncExpTransac(ie);
            this.itemViewBinding.executePendingBindings();
        }

        public void updateTransaction(IncomeExpense ie) {
            Intent in = new Intent(cxt, AddIncomeExpenseActivity.class);
            in.putExtra(BUCKS_STRING_IS_INCOME_EXTRA, ie.getBucksString().equals("income"));
            in.putExtra(INCOME_EXPENSE_OBJECT_EXTRA, ie);
            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            cxt.startActivity(in);
        }
    }
}
