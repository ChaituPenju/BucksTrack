package com.chaitupenjudcoder.recyclerviews

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.chaitupenjudcoder.AddIncomeExpenseActivity
import com.chaitupenjudcoder.BucksActivity
import com.chaitupenjudcoder.buckstrack.R
import com.chaitupenjudcoder.buckstrack.databinding.ItemSpendingListBinding
import com.chaitupenjudcoder.datapojos.IncomeExpense
import com.chaitupenjudcoder.firebasehelpers.SharedPreferencesHelper
import com.chaitupenjudcoder.recyclerviews.BucksTransactionsRecycler.BucksTransactionsAdapter
import java.util.*

class BucksTransactionsRecycler(
    private val cxt: Context,
    private val allTransactions: ArrayList<IncomeExpense>
) : RecyclerView.Adapter<BucksTransactionsAdapter>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): BucksTransactionsAdapter = BucksTransactionsAdapter(
            ItemSpendingListBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
        )

    override fun onBindViewHolder(bucksTransactionsAdapter: BucksTransactionsAdapter, i: Int) {
        bucksTransactionsAdapter.itemViewBinding.cvTransactions.animation =
            AnimationUtils.loadAnimation(cxt, R.anim.transaction_card_scale_animation)
        val ie = allTransactions[i]
        ie.setDateFormat(SharedPreferencesHelper(cxt).getDateFormatPref("dd-MM-yyyy"))
        bucksTransactionsAdapter.bind(ie)
        bucksTransactionsAdapter.itemViewBinding.transactionClickListener = bucksTransactionsAdapter
    }

    override fun getItemCount() = allTransactions.size

    inner class BucksTransactionsAdapter internal constructor(var itemViewBinding: ItemSpendingListBinding) :
        RecyclerView.ViewHolder(itemViewBinding.root) {

        fun bind(ie: IncomeExpense?) {
            itemViewBinding.apply {
                incExpTransac = ie

                executePendingBindings()
            }
        }

        fun updateTransaction(ie: IncomeExpense) {
            Intent(cxt, AddIncomeExpenseActivity::class.java).also { intent ->
                intent.putExtra(BucksActivity.BUCKS_STRING_IS_INCOME_EXTRA, ie.bucksString == "income")
                intent.putExtra(BucksActivity.INCOME_EXPENSE_OBJECT_EXTRA, ie)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                cxt.startActivity(intent)
            }
        }
    }
}