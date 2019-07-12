package com.chaitupenjudcoder.recyclerviews;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.chaitupenjudcoder.buckstrack.R;
import com.chaitupenjudcoder.buckstrack.databinding.ItemSpendingListBinding;
import com.chaitupenjudcoder.datapojos.IncomeExpense;

import java.util.ArrayList;

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
        ItemSpendingListBinding items = DataBindingUtil.inflate(inflater, R.layout.item_spending_list, viewGroup, false);
        return new BucksTransactionsAdapter(items);
    }

    @Override
    public void onBindViewHolder(@NonNull BucksTransactionsAdapter bucksTransactionsAdapter, int i) {
        bucksTransactionsAdapter.itenViewBinding.cvTransactions.setAnimation(AnimationUtils.loadAnimation(cxt, R.anim.transaction_card_scale_animation));
        IncomeExpense ie = allTransactions.get(i);
        bucksTransactionsAdapter.itenViewBinding.tvAmount.setText(ie.getAmount());
        bucksTransactionsAdapter.itenViewBinding.tvTitle.setText(ie.getTitle());
        bucksTransactionsAdapter.itenViewBinding.tvCategory.setText(ie.getCategory());
        bucksTransactionsAdapter.itenViewBinding.tvDate.setText(ie.getDate());
        bucksTransactionsAdapter.itenViewBinding.tvDescription.setText(ie.getNote());
    }

    @Override
    public int getItemCount() {
        return allTransactions.size();
    }

    class BucksTransactionsAdapter extends RecyclerView.ViewHolder {
        ItemSpendingListBinding itenViewBinding;

        BucksTransactionsAdapter(@NonNull ItemSpendingListBinding itemViewBinding) {
            super(itemViewBinding.getRoot());
            this.itenViewBinding = itemViewBinding;
        }
    }
}
