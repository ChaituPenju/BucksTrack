package com.chaitupenjudcoder

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.chaitupenjudcoder.buckstrack.R
import com.chaitupenjudcoder.buckstrack.databinding.ActivityBucksBinding
import com.chaitupenjudcoder.buckstrack.databinding.ContentBucksBinding
import com.chaitupenjudcoder.buckstrack.databinding.NavHeaderBucksBinding
import com.chaitupenjudcoder.datapojos.CategoriesAmount
import com.chaitupenjudcoder.firebasehelpers.BucksWidgetHelper
import com.chaitupenjudcoder.firebasehelpers.FirebaseCategoriesHelper
import com.chaitupenjudcoder.firebasehelpers.SharedPreferencesHelper
import com.chaitupenjudcoder.recyclerviews.BucksOverviewRecycler
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class BucksActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var bucksBinding: ActivityBucksBinding
    private lateinit var navHeader: NavHeaderBucksBinding
    lateinit var bucksContent: ContentBucksBinding

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var user: FirebaseUser
    var currencySymbol: String? = null
    var totalIncome = 0
    var totalExpense = 0

    var h: SharedPreferencesHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bucksBinding = DataBindingUtil.setContentView(this, R.layout.activity_bucks)
        navHeader = NavHeaderBucksBinding.bind(bucksBinding.navView.getHeaderView(0))
        bucksContent = bucksBinding.appBarLayout.contentBucks


        PreferenceManager.setDefaultValues(this, R.xml.bucks_preferences, true)
        h = SharedPreferencesHelper(this)
        BucksWidgetHelper().callIntentService(this, h!!.getWidgetOptionPref("Last Income")!!)


        //get Firebase authentication instance and user
        user = mAuth.currentUser!!

        // populate spinner function which populates with two classifications of categories
        populateCategorySpinner()

        lateinit var addIncExp: Intent

        //fab on click listeners, opens same activity and changes title and data insertion of activity based on fab selection
        bucksBinding.appBarLayout.fabAddIncome.setOnClickListener {
            bucksBinding.appBarLayout.fab.close(true)
            addIncExp = Intent(Intent(this@BucksActivity, AddIncomeExpenseActivity::class.java))
            addIncExp.putExtra(BUCKS_STRING_IS_INCOME_EXTRA, BUCKS_INCOME)
            startActivity(addIncExp)
        }

        bucksBinding.appBarLayout.fabAddExpnese.setOnClickListener {
            bucksBinding.appBarLayout.fab.close(true)
            addIncExp = Intent(Intent(this@BucksActivity, AddIncomeExpenseActivity::class.java))
            addIncExp.putExtra(BUCKS_STRING_IS_INCOME_EXTRA, BUCKS_EXPENSE)
            startActivity(addIncExp)
        }
        setSupportActionBar(bucksBinding.appBarLayout.toolbar)

        //set first option as always checked
        bucksBinding.navView.menu.getItem(0).isChecked = true

        //set username and email in nav header bucks
        //handles nullpointer exception and sets the value
        user.let { mUser ->
            for (info in mUser.providerData) {
                println("Display Name is " + info.displayName)
                navHeader.tvUserName.text = info.displayName
                navHeader.tvUserEmail.text = info.email
            }
        }
        val toggle = ActionBarDrawerToggle(
            this,
            bucksBinding.drawerLayout,
            bucksBinding.appBarLayout.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        bucksBinding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        bucksBinding.navView.setNavigationItemSelectedListener(this)

        //get database instance and reference
        setTotalIncomeAndExpense()
    }

    override fun onResume() {
        super.onResume()
        //  set first option as always checked when app resumes or comes back from another activity
        bucksBinding.navView.menu.getItem(0).isChecked = true
        CoroutineScope(Dispatchers.IO).launch {
            setCurrencyAndTotal(totalIncome, totalExpense)
        }
    }

    override fun onBackPressed() {
        if (bucksBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            bucksBinding.drawerLayout.closeDrawer(GravityCompat.START)
        } else
            super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.bucks, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /**
         * Handle action bar item clicks here. The action bar will
         * automatically handle clicks on the Home/Up button, so long
         * as you specify a parent activity in AndroidManifest.xml.
         **/
        val intent = Intent(this@BucksActivity, BucksTransactions::class.java)
        when (item.itemId) {
            R.id.action_week -> {
                intent.putExtra("WEEK", 7L)
                startActivity(intent)
                return true
            }
            R.id.action_month -> {
                intent.putExtra("MONTH", 30L)
                startActivity(intent)
                return true
            }
            R.id.action_date_range -> {
                startActivity(Intent(Intent(this@BucksActivity, DateChooserActivity::class.java)))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_transactions -> startActivity(
                Intent(
                    this@BucksActivity,
                    BucksTransactions::class.java
                )
            )
            R.id.nav_categories -> startActivity(
                Intent(
                    this@BucksActivity,
                    CategoriesActivity::class.java
                )
            )
            R.id.nav_settings -> startActivity(
                Intent(
                    this@BucksActivity,
                    SettingsActivity::class.java
                )
            )
            R.id.nav_logout -> {
                //sign out user
                mAuth.signOut()
                finish()
                startActivity(Intent(this@BucksActivity, LoginActivity::class.java))
            }
            else -> Unit
        }

        bucksBinding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    //gets the total rvIncome and rvExpense from firebase
    private fun setTotalIncomeAndExpense() {
        //  initialize categories helper class
        val helper = FirebaseCategoriesHelper()

        //  Get two totals one inside the other and set them to textviews
        CoroutineScope(Dispatchers.IO).launch {
            helper.getCategoryTotal("income") { total1: Int ->
                helper.getCategoryTotal("expense") { total2: Int ->
                    totalIncome = total1
                    totalExpense = total2
                    //set all the totals
                    setCurrencyAndTotal(total1, total2)
                }
            }
        }
    }


    private suspend fun setCurrencyAndTotal(totalIncome: Int, totalExpense: Int) = withContext(Dispatchers.Main) {
        val res = resources
        currencySymbol = h!!.getCurrencyPref("R")
        bucksContent.tvIncomeAmount.text = res.getString(R.string.currency_symbol, currencySymbol, totalIncome)
        bucksContent.tvExpenseAmount.text = res.getString(R.string.currency_symbol, currencySymbol, totalExpense)
        bucksContent.tvBalanceAmount.text = res.getString(R.string.currency_symbol, currencySymbol, totalIncome - totalExpense)
    }

    private fun populateCategorySpinner() {
        val categorie = arrayOf("INCOME", "EXPENSE")
        val cats =
            ArrayAdapter(application, android.R.layout.simple_spinner_dropdown_item, categorie)
        bucksContent.spiCategoriesChoose.adapter = cats
        val categoriesHelper = FirebaseCategoriesHelper()
        bucksContent.spiCategoriesChoose.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    CoroutineScope(Dispatchers.Main).launch {
                        categoriesHelper.getCategoryTotal(categorie[position].lowercase(Locale.getDefault())) { total: Int ->
//                        Log.d("abcde", "inside categories total"+total);
                            categoriesHelper.getAllCategories(categorie[position].lowercase(Locale.getDefault())) { categoriesList: ArrayList<String> ->
                                getIncomeExpenseCategoriesTotal(
                                    categoriesList,
                                    total
                                )
                            }
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun getIncomeExpenseCategoriesTotal(cats: ArrayList<String>, total: Int) {
        val categorySalary: Query =
            FirebaseDatabase.getInstance().getReference("data/" + user.uid + "/spendings")
        val catAmount = ArrayList<CategoriesAmount>()
        categorySalary.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val amounts = IntArray(cats.size)
                var cnt = 0
                val categories: Array<String> = cats.toTypedArray()
                for (category in categories) {
                    for (catShot in dataSnapshot.children) {
                        if (catShot.child("category").getValue(String::class.java) == category) {
                            amounts[cnt] += Integer.valueOf(
                                catShot.child("amount").getValue(
                                    String::class.java
                                )!!
                            )
                        }
                    }
                    catAmount.add(
                        CategoriesAmount(
                            category, amounts[cnt].toString(), amounts[cnt]
                                .toFloat() / total * 100
                        )
                    )
                    cnt++
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    catAmount.sortWith { first: CategoriesAmount, second: CategoriesAmount -> (second.percentage - first.percentage).toInt() }
                } else {
                    catAmount.sortWith { o1: CategoriesAmount, o2: CategoriesAmount -> (o2.percentage - o1.percentage).toInt() }
                }
                val overviewRecycler = BucksOverviewRecycler(catAmount)

                bucksContent.rvCategoriesAmount.apply {
                    layoutManager = LinearLayoutManager(applicationContext)
                    isNestedScrollingEnabled = true
                    adapter = overviewRecycler
                    layoutAnimation = AnimationUtils.loadLayoutAnimation(
                        applicationContext, R.anim.categorywise_card_bottom_animation
                    )
                }
            }

            override fun onCancelled(databaseError: DatabaseError) = Unit
        })
    }

    companion object {
        private const val BUCKS_INCOME = true
        const val BUCKS_STRING_IS_INCOME_EXTRA = "income_extra"
        private const val BUCKS_EXPENSE = false
        const val INCOME_EXPENSE_OBJECT_EXTRA = "income_expense_extra"
    }
}