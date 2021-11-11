package com.chaitupenjudcoder.recyclerviews

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.chaitupenjudcoder.buckstrack.R
import com.chaitupenjudcoder.buckstrack.databinding.ItemCategoryTotalListBinding
import com.chaitupenjudcoder.datapojos.CategoriesAmount
import java.util.*
import kotlin.math.roundToInt

class BucksOverviewRecycler(var catAmounts: ArrayList<CategoriesAmount>) :
    RecyclerView.Adapter<BucksOverviewRecycler.BucksOverviewAdapter>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int) = BucksOverviewAdapter(
        DataBindingUtil.inflate(
            LayoutInflater.from(viewGroup.context),
            R.layout.item_category_total_list, viewGroup, false
        )
    )

    override fun onBindViewHolder(holder: BucksOverviewAdapter, position: Int) {
        holder.bind(catAmounts[position])
    }

    override fun getItemCount(): Int = catAmounts.size


    inner class BucksOverviewAdapter(private val itemViewBinding: ItemCategoryTotalListBinding) :
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