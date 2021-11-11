package com.chaitupenjudcoder.recyclerviews

import androidx.recyclerview.widget.RecyclerView
import com.chaitupenjudcoder.recyclerviews.BucksCategoriesRecycler.BucksCategoriesAdapter
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.chaitupenjudcoder.buckstrack.R
import com.chaitupenjudcoder.buckstrack.databinding.ItemCategoriesListBinding
import org.jetbrains.annotations.NotNull
import java.util.ArrayList

class BucksCategoriesRecycler(private val categories: ArrayList<String>) :
    RecyclerView.Adapter<BucksCategoriesAdapter>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int) =
        BucksCategoriesAdapter(DataBindingUtil.inflate(LayoutInflater.from(viewGroup.context), R.layout.item_categories_list, viewGroup, false))

    override fun onBindViewHolder(bucksCategoriesAdapter: BucksCategoriesAdapter, i: Int) {
        val category = categories[i]
        bucksCategoriesAdapter.catsListBinding.tvCategoryItem.text = category
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    inner class BucksCategoriesAdapter(@param:NotNull var catsListBinding: ItemCategoriesListBinding) :
        RecyclerView.ViewHolder(catsListBinding.root)
}