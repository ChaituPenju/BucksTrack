package com.chaitupenjudcoder

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chaitupenjudcoder.buckstrack.R
import com.chaitupenjudcoder.buckstrack.databinding.ActivityCategoriesBinding
import com.chaitupenjudcoder.buckstrack.databinding.AddCategoryCustomDialogBinding
import com.chaitupenjudcoder.firebasehelpers.FirebaseCategoriesHelper
import com.chaitupenjudcoder.recyclerviews.BucksCategoriesRecycler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriesActivity : AppCompatActivity() {
    lateinit var catBind: ActivityCategoriesBinding
    lateinit var addCategoryBind: AddCategoryCustomDialogBinding
    var mUser: FirebaseUser? = null
    var incomeExpenseRef: DatabaseReference? = null
    private var addCategoryRef: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        catBind = DataBindingUtil.setContentView(this, R.layout.activity_categories)
        mUser = FirebaseAuth.getInstance().currentUser

        var userId = ""
        if (mUser != null) {
            userId = mUser!!.uid
        }

        incomeExpenseRef = FirebaseDatabase.getInstance().getReference("data/$userId/categories")

        //retrieve the list of rvIncome and rvExpense categories
        incomeExpenseCategories()

        registerIncomeExpenseButtons()
    }

    //  initialize/set on click functionality for rvIncome and rvExpense adding buttons
    private fun registerIncomeExpenseButtons() {
        catBind.btnAddIncomeCategory.setOnClickListener {
            initCategoryAddCustomDialog("Income")
        }

        catBind.btnAddExpenseCategory.setOnClickListener {
            initCategoryAddCustomDialog("Expense")
        }
    }

    private fun initCategoryAddCustomDialog(type: String) {
        addCategoryBind = AddCategoryCustomDialogBinding.inflate(layoutInflater)

        val addCatDialog = AlertDialog.Builder(this@CategoriesActivity)
            .setView(addCategoryBind.also { it.categoryType = type }.root)
            .create()

        addCategoryBind.btnCategoryCancel.setOnClickListener { addCatDialog.dismiss() }
        addCategoryBind.btnCategoryOk.setOnClickListener {
            val category = addCategoryBind.etAddCategory.text.toString()
            addIncomeExpenseCategory(type, category)
            addCatDialog.dismiss()
        }

        addCatDialog.setCanceledOnTouchOutside(true)

        addCatDialog.show()
    }

    //  adding category to corresponding child(rvIncome/rvExpense) on button click
    private fun addIncomeExpenseCategory(type: String, category: String?) {
        addCategoryRef = FirebaseDatabase.getInstance().getReference("data/" + mUser!!.uid + "/categories/" + type)
        addCategoryRef!!.push().setValue(category)
    }

    private fun incomeExpenseCategories() {
        val incExpCats = FirebaseCategoriesHelper()

        CoroutineScope(Dispatchers.IO).launch {
            incExpCats.getAllCategories("income") { allCategory ->
                setDataToAdapter(catBind.rvIncome, allCategory)
            }

            incExpCats.getAllCategories("expense") { allCategory ->
                setDataToAdapter(catBind.rvExpense, allCategory)
            }
        }
    }

    private suspend fun setDataToAdapter(mRv: RecyclerView, categoriesList: ArrayList<String>) = withContext(Dispatchers.Main) {
        mRv.adapter = BucksCategoriesRecycler(categoriesList)
    }
}