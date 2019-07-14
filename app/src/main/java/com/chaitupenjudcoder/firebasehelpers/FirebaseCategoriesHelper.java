package com.chaitupenjudcoder.firebasehelpers;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

//This class helps retrieve categories from Firebase Database and increases code resulability
public class FirebaseCategoriesHelper {

    private DatabaseReference mCategoryRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String uid;
    private ArrayList<String> categoriesList = new ArrayList<>();
    private DatabaseReference totalsRef;

    //  This interface is called after creating object of this class to use as callback in other
    public interface GetAllCategory {
        void categories(ArrayList<String> categoriesList);
    }

    public interface GetCategoryTotal {
        void categoriesTotal(int total);
    }

    //  Constructor to instantiate all firebase variables
    public FirebaseCategoriesHelper() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        uid = mUser.getUid();
        mCategoryRef = FirebaseDatabase.getInstance().getReference("data/" + uid + "/categories");
        //  set the keep synced true for offline capability
        mCategoryRef.keepSynced(true);
    }

    public void getCategoryTotal(final GetCategoryTotal getTotals, final String category) {
        totalsRef = FirebaseDatabase.getInstance().getReference("data/" + uid + "/spendings");

        ValueEventListener totalListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String bucksStr;
                int incExpTotal = 0;
                for (DataSnapshot incomeExpenseTotal : dataSnapshot.getChildren()) {
                    bucksStr = incomeExpenseTotal.child("bucksString").getValue(String.class);
                    int amount = Integer.valueOf(incomeExpenseTotal.child("amount").getValue(String.class));
                    if (bucksStr != null) {
                        if (bucksStr.equals(category)) {
                            incExpTotal += amount;
                        }
                    }
                }
                getTotals.categoriesTotal(incExpTotal);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        totalsRef.addValueEventListener(totalListener);
    }

    //  This method is called with this class' object everywhere and override the interface defined
    public void getAllCategories(final GetAllCategory allCategory, final String BUCKS_STRING) {
        ValueEventListener categoryListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoriesList.clear();
                for (DataSnapshot categorieShot : dataSnapshot.child(BUCKS_STRING).getChildren()) {
                    String category = categorieShot.getValue(String.class);
                    categoriesList.add(category);
                }
                allCategory.categories(categoriesList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mCategoryRef.addValueEventListener(categoryListener);
    }
}
