package com.chaitupenjudcoder.recyclerviews

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chaitupenjudcoder.AddIncomeExpenseActivity
import com.chaitupenjudcoder.BucksActivity
import com.chaitupenjudcoder.buckstrack.R
import com.chaitupenjudcoder.buckstrack.databinding.ItemSpendingListBinding
import com.chaitupenjudcoder.datapojos.IncomeExpense
import com.chaitupenjudcoder.firebasehelpers.SharedPreferencesHelper

class BucksTransactionsAdapter(
    private val context: Context
): ListAdapter<IncomeExpense, BucksTransactionsAdapter.BucksTransactionsViewHolder>(
    object : DiffUtil.ItemCallback<IncomeExpense>() {
        override fun areItemsTheSame(oldItem: IncomeExpense, newItem: IncomeExpense): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: IncomeExpense, newItem: IncomeExpense): Boolean {
            return oldItem.toString() == newItem.toString()
        }

    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BucksTransactionsViewHolder(
        ItemSpendingListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: BucksTransactionsViewHolder, position: Int) {
        holder.binding.cvTransactions.animation =
            AnimationUtils.loadAnimation(context, R.anim.transaction_card_scale_animation)
        val ie = getItem(position)
        ie.setDateFormat(SharedPreferencesHelper(context).getDateFormatPref("dd-MM-yyyy"))
        holder.bind(ie)
        holder.binding.transactionClickListener = holder
    }

    inner class BucksTransactionsViewHolder internal constructor(val binding: ItemSpendingListBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(ie: IncomeExpense) {
                binding.incExpTransac = ie

                binding.executePendingBindings()
            }

        fun updateTransaction(ie: IncomeExpense) {
            Intent(context, AddIncomeExpenseActivity::class.java).also { intent ->
                intent.putExtra(BucksActivity.BUCKS_STRING_IS_INCOME_EXTRA, ie.bucksString == "income")
                intent.putExtra(BucksActivity.INCOME_EXPENSE_OBJECT_EXTRA, ie)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }

    }
}