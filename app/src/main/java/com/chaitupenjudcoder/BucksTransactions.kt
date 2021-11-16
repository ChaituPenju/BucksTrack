package com.chaitupenjudcoder

import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.chaitupenjudcoder.buckstrack.R
import com.chaitupenjudcoder.buckstrack.databinding.ActivityBucksTransactionsBinding
import com.chaitupenjudcoder.datapojos.IncomeExpense
import com.chaitupenjudcoder.firebasehelpers.FirebaseTransactionsHelper
import com.chaitupenjudcoder.firebasehelpers.SharedPreferencesHelper
import com.chaitupenjudcoder.recyclerviews.BucksTransactionsAdapter
import java.util.*

class BucksTransactions : AppCompatActivity() {
    lateinit var transactionUtil: ActivityBucksTransactionsBinding

    lateinit var transactionRecycler: BucksTransactionsAdapter
    val helper: FirebaseTransactionsHelper get() = FirebaseTransactionsHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transactionUtil = DataBindingUtil.setContentView(this, R.layout.activity_bucks_transactions)

        val transactionsHelper = FirebaseTransactionsHelper()
        val dateFormat = SharedPreferencesHelper(applicationContext).getDateFormatPref("dd-MM-yyyy")

        if (intent.extras != null) {
            if (intent.extras!!.containsKey("WEEK") || intent.extras!!.containsKey("MONTH")) {
                val days = if (intent.extras!!
                        .containsKey("WEEK")
                ) intent.extras!!
                    .getLong("WEEK") else intent.extras!!.getLong("MONTH")
                transactionsHelper.getWeekOrMonthTransactions({ transactions: ArrayList<IncomeExpense> ->
                    initTransasctionsRecycler(
                        transactions
                    )
                }, days)
            } else if (intent.extras!!
                    .containsKey(FirebaseTransactionsHelper.DATE_ONE_EXTRA) && intent.extras!!.containsKey(
                    FirebaseTransactionsHelper.DATE_TWO_EXTRA
                )
            ) {
                val date1 = intent.extras!!.getString(FirebaseTransactionsHelper.DATE_ONE_EXTRA)
                val date2 = intent.extras!!.getString(FirebaseTransactionsHelper.DATE_TWO_EXTRA)
                transactionsHelper.getTransactionsBwTwoDates({ transactions: ArrayList<IncomeExpense> ->
                    initTransasctionsRecycler(
                        transactions
                    )
                }, date1, date2, dateFormat)
            }
        } else {
            transactionsHelper.getAllTransactions { transactions: ArrayList<IncomeExpense> ->
                initTransasctionsRecycler(transactions)
            }
        }
    }

    private fun initTransasctionsRecycler(transactions: ArrayList<IncomeExpense>) {
        transactions.sortWith { (_, _, date1), (_, _, date) ->
            date1.compareTo(date)
        }
        transactionRecycler = BucksTransactionsAdapter(applicationContext)
        transactionRecycler.submitList(transactions)

        ItemTouchHelper(swipeToDelete(transactions)).attachToRecyclerView(transactionUtil.rvTransactions)
        transactionUtil.transactionAdapter = transactionRecycler
    }

    private fun swipeToDelete(list: ArrayList<IncomeExpense>): ItemTouchHelper.SimpleCallback {
        return object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                viewHolder1: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                confirmDeleteDialog(list, viewHolder.adapterPosition)
            }
        }
    }

    private fun confirmDeleteDialog(list: ArrayList<IncomeExpense>, position: Int) {
        val confirmDelete = AlertDialog.Builder(this)
            .setMessage("Are you sure you want to delete transaction " + list[position].title)
            .setPositiveButton("OK") { _: DialogInterface?, _: Int ->

                helper.deleteATransaction({ response: String? ->
                    Toast.makeText(
                        this@BucksTransactions,
                        response,
                        Toast.LENGTH_SHORT
                    ).show()
                }, list[position].id)
            }.setNegativeButton("Don\'t delete") { dialog: DialogInterface, which: Int ->
                dialog.dismiss()
            }
        val confirmDeleteDialog = confirmDelete.create()
        confirmDeleteDialog.show()
    }
}