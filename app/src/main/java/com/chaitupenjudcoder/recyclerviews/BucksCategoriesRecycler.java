package com.chaitupenjudcoder.recyclerviews;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.chaitupenjudcoder.buckstrack.R;
import com.chaitupenjudcoder.buckstrack.databinding.ItemCategoriesListBinding;
import com.chaitupenjudcoder.buckstrack.databinding.ItemCategoryTotalListBinding;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;

public class BucksCategoriesRecycler extends RecyclerView.Adapter<BucksCategoriesRecycler.BucksCategoriesAdapter> {
    private ArrayList<String> categories;

    public BucksCategoriesRecycler(ArrayList<String> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public BucksCategoriesAdapter onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        ItemCategoriesListBinding items = DataBindingUtil.inflate(inflater, R.layout.item_categories_list, viewGroup, false);
        return new BucksCategoriesRecycler.BucksCategoriesAdapter(items);
    }

    @Override
    public void onBindViewHolder(@NonNull BucksCategoriesAdapter bucksCategoriesAdapter, int i) {
        String category = categories.get(i);
        bucksCategoriesAdapter.catsListBinding.tvCategoryItem.setText(category);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class BucksCategoriesAdapter extends RecyclerView.ViewHolder {
        ItemCategoriesListBinding catsListBinding;

        public BucksCategoriesAdapter(@NotNull ItemCategoriesListBinding catsListBinding) {
            super(catsListBinding.getRoot());
            this.catsListBinding = catsListBinding;
        }
    }
}
