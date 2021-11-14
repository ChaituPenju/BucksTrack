package com.chaitupenjudcoder.recyclerviews

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chaitupenjudcoder.buckstrack.R
import com.chaitupenjudcoder.buckstrack.databinding.ItemCategoryTotalListBinding
import com.chaitupenjudcoder.datapojos.CategoriesAmount
import kotlin.math.roundToInt

class BucksOverviewAdapter: ListAdapter<CategoriesAmount, BucksOverviewAdapter.BucksOverviewHolder>(overviewCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BucksOverviewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_category_total_list, parent, false
        )
    )

    override fun onBindViewHolder(holder: BucksOverviewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BucksOverviewHolder(private val itemViewBinding: ItemCategoryTotalListBinding):
        RecyclerView.ViewHolder(itemViewBinding.root) {

        fun bind(catAmount: CategoriesAmount) {
            val (categoryName, totalAmount, percentage) = catAmount

            itemViewBinding.apply {
                tvCategory.text = categoryName
                tvAmount.text = totalAmount
                pbCategory.progress = percentage.roundToInt()
            }
        }
    }
}

private val overviewCallback = object : DiffUtil.ItemCallback<CategoriesAmount>() {
    override fun areItemsTheSame(oldItem: CategoriesAmount, newItem: CategoriesAmount
    ) = oldItem.categoryName == newItem.categoryName

    override fun areContentsTheSame(
        oldItem: CategoriesAmount,
        newItem: CategoriesAmount
    ) = oldItem.toString() == newItem.toString()
}