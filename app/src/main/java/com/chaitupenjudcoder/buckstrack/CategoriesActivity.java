package com.chaitupenjudcoder.buckstrack;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chaitupenjudcoder.buckstrack.databinding.ActivityCategoriesBinding;
import com.chaitupenjudcoder.recyclerviews.BucksCategoriesRecycler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CategoriesActivity extends AppCompatActivity {

    ActivityCategoriesBinding catBind;

    FirebaseUser mUser;
    DatabaseReference incomeExpenseRef;

    RecyclerView income, expense;
    ArrayList<String> incomeCats, expenseCats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        catBind = DataBindingUtil.setContentView(this, R.layout.activity_categories);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = "";
        if (mUser != null) {
            userId = mUser.getUid();
        }

        incomeExpenseRef = FirebaseDatabase.getInstance().getReference("data/" + userId + "/categories");

        //retrieve the list of income and expense categories
        getIncomeExpenseCategories();

        income = catBind.rvIncome;
        expense = catBind.rvExpense;

        //create and set layout manager for each RecyclerView
        RecyclerView.LayoutManager firstLayoutManager = new LinearLayoutManager(this);
        RecyclerView.LayoutManager secondLayoutManager = new LinearLayoutManager(this);

        income.setLayoutManager(firstLayoutManager);
        expense.setLayoutManager(secondLayoutManager);

    }

    private void getIncomeExpenseCategories() {
        incomeExpenseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> incomes = new ArrayList<>();
                ArrayList<String> expenses = new ArrayList<>();
                String incExp = "";
                for (DataSnapshot incomeShot : dataSnapshot.child("income").getChildren()) {
                    incomes.add(incomeShot.getValue(String.class));
                }

                for (DataSnapshot expenseShot : dataSnapshot.child("expense").getChildren()) {
                    expenses.add((expenseShot.getValue(String.class)));
                }
                //Initializing and set adapter for each RecyclerView
                BucksCategoriesRecycler firstAdapter = new BucksCategoriesRecycler(incomes);
                BucksCategoriesRecycler secondAdapter = new BucksCategoriesRecycler(expenses);

                income.setAdapter(firstAdapter);
                expense.setAdapter(secondAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
