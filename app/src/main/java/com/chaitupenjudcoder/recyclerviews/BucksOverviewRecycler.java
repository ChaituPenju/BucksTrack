package com.chaitupenjudcoder.recyclerviews;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.chaitupenjudcoder.buckstrack.R;
import com.chaitupenjudcoder.buckstrack.databinding.ItemCategoryTotalListBinding;
import com.chaitupenjudcoder.datapojos.CategoriesAmount;

import java.util.ArrayList;

public class BucksOverviewRecycler extends RecyclerView.Adapter<BucksOverviewRecycler.BucksOverviewAdapter> {

    private Context ctx; ArrayList<CategoriesAmount> catAmounts;
    public BucksOverviewRecycler(Context ctx, ArrayList<CategoriesAmount> catAmounts) {
        this.ctx = ctx;
        this.catAmounts = catAmounts;
    }

    @NonNull
    @Override
    public BucksOverviewAdapter onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        ItemCategoryTotalListBinding items = DataBindingUtil.inflate(inflater, R.layout.item_category_total_list, viewGroup, false);
        return new BucksOverviewRecycler.BucksOverviewAdapter(items);
    }

    @Override
    public void onBindViewHolder(@NonNull BucksOverviewAdapter bucksOverviewAdapter, int i) {
        CategoriesAmount cAmount = catAmounts.get(i);
        bucksOverviewAdapter.itemViewBinding.tvCategory.setText(cAmount.getCategoryName());
        bucksOverviewAdapter.itemViewBinding.tvAmount.setText(cAmount.getTotalAmount());
        int percentage = Math.round(cAmount.getPercentage());
        bucksOverviewAdapter.itemViewBinding.pbCategory.setProgress(percentage);
    }

    @Override
    public int getItemCount() {
        return catAmounts.size();
    }

    class BucksOverviewAdapter extends RecyclerView.ViewHolder {
        ItemCategoryTotalListBinding itemViewBinding;

        public BucksOverviewAdapter(@NonNull ItemCategoryTotalListBinding itemViewBinding) {
            super(itemViewBinding.getRoot());
            this.itemViewBinding = itemViewBinding;
        }
    }
}
