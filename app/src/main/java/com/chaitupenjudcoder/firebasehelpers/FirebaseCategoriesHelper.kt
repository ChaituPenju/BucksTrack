package com.chaitupenjudcoder.firebasehelpers

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.coroutines.tasks.await
import java.util.*

//This class helps retrieve categories from Firebase Database and increases code re-usability
class FirebaseCategoriesHelper {
    private val mCategoryRef: DatabaseReference
    private val mAuth get() = FirebaseAuth.getInstance()
    private val mUser get() = mAuth.currentUser
    private val uid get() = mUser!!.uid
    private val categoriesList = ArrayList<String?>()
    private var totalsRef: DatabaseReference? = null

    //  Constructor to instantiate all firebase variables
    init {
        mCategoryRef = FirebaseDatabase.getInstance().getReference("data/$uid/categories")
        //  set the keep synced true for offline capability
        mCategoryRef.keepSynced(true)
    }

    //  This interface is called after creating object of this class to use as callback in other
    interface GetAllCategory {
        fun categories(categoriesList: ArrayList<String?>?)
    }

    interface GetCategoryTotal {
        fun categoriesTotal(total: Int)
    }

    suspend fun getCategoryTotal(getTotals: GetCategoryTotal, category: String) {
        totalsRef = FirebaseDatabase.getInstance().getReference("data/$uid/spendings")
        val otalsRef = FirebaseDatabase.getInstance().getReference("data/$uid/spendings").get().await()

        val totalListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var bucksStr: String
                var incExpTotal = 0
                for (incomeExpenseTotal in dataSnapshot.children) {
                    bucksStr = incomeExpenseTotal.child("bucksString").getValue(
                        String::class.java
                    ).toString()
                    val amount = Integer.valueOf(
                        incomeExpenseTotal.child("amount").getValue(
                            String::class.java
                        ).toString()
                    )
                    if (bucksStr == category) {
                        incExpTotal += amount
                    }
                }
                getTotals.categoriesTotal(incExpTotal)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        totalsRef!!.addValueEventListener(totalListener)
    }

    //  This method is called with this class' object everywhere and override the interface defined
    fun getAllCategories(allCategory: GetAllCategory, BUCKS_STRING: String?) {
        val categoryListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                categoriesList.clear()
                for (categorieShot in dataSnapshot.child(BUCKS_STRING!!).children) {
                    val category = categorieShot.getValue(String::class.java)
                    categoriesList.add(category)
                }
                allCategory.categories(categoriesList)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        mCategoryRef.addValueEventListener(categoryListener)
    }
}