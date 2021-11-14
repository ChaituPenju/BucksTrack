package com.chaitupenjudcoder.firebasehelpers

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.tasks.await
import java.util.*

//This class helps retrieve categories from Firebase Database and increases code re-usability
class FirebaseCategoriesHelper {
    private val mCategoryRef: DatabaseReference
    private val mAuth get() = FirebaseAuth.getInstance()
    private val mUser get() = mAuth.currentUser
    private val uid get() = mUser!!.uid
    private val categoriesList = ArrayList<String>()
    private var totalsRef: DatabaseReference? = null

    //  Constructor to instantiate all firebase variables
    init {
        mCategoryRef = FirebaseDatabase.getInstance().getReference("data/$uid/categories")
        //  set the keep synced true for offline capability
        mCategoryRef.keepSynced(true)
    }

    suspend fun getCategoryTotal(category: String, categoriesTotal: suspend (total: Int) -> Unit) {
        val totalsSnapshot = FirebaseDatabase.getInstance().getReference("data/$uid/spendings").get().await()
        var bucksStr: String
        var incExpTotal = 0

        for (incomeExpenseTotal in totalsSnapshot.children) {
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

        categoriesTotal(incExpTotal)
    }

    //  This method is called with this class' object everywhere and override the interface defined
    suspend fun getAllCategories(BUCKS_STRING: String?, categories: suspend (ArrayList<String>) -> Unit) {
        val categorySnapshot = mCategoryRef.get().await()

        categoriesList.clear()
        for (categoryShot in categorySnapshot.child(BUCKS_STRING!!).children) {
            val category = categoryShot.getValue(String::class.java)
            categoriesList.add(category!!)
        }

        categories(categoriesList)
    }
}