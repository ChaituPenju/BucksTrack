package com.chaitupenjudcoder;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chaitupenjudcoder.buckstrack.R;
import com.chaitupenjudcoder.buckstrack.databinding.ActivityCategoriesBinding;
import com.chaitupenjudcoder.buckstrack.databinding.AddCategoryCustomDialogBinding;
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
    AddCategoryCustomDialogBinding addCategoryBind;

    FirebaseUser mUser;
    DatabaseReference incomeExpenseRef, addCategoryRef;

    RecyclerView income, expense;

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

        initIncomeExpenseButtons();

    }

    //  initialize/set on click functionality for income and expense adding buttons
    public void initIncomeExpenseButtons() {
        catBind.btnAddIncomeCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCategoryAddCustomDialog("income");
            }
        });

        catBind.btnAddExpenseCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCategoryAddCustomDialog("expense");
            }
        });
    }

    public void initCategoryAddCustomDialog(final String type) {
        addCategoryBind = AddCategoryCustomDialogBinding.inflate(getLayoutInflater());

        AlertDialog.Builder catAddBuilder = new AlertDialog.Builder(CategoriesActivity.this);
        addCategoryBind.setCategoryType(type);
        View mView = addCategoryBind.getRoot();

        catAddBuilder.setView(mView);

        final AlertDialog addCatDialog = catAddBuilder.create();
        addCatDialog.setCanceledOnTouchOutside(true);

        addCategoryBind.btnCategoryCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCatDialog.dismiss();
            }
        });

        addCategoryBind.btnCategoryOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = addCategoryBind.etAddCategory.getText().toString();
                addIncomeExpenseCategory(type, category);
                addCatDialog.dismiss();
            }
        });

        addCatDialog.show();
    }

    //  adding category to corresponding child(income/expense) on button click
    public void addIncomeExpenseCategory(String type, String category) {
        addCategoryRef = FirebaseDatabase.getInstance().getReference("data/" + mUser.getUid() + "/categories/" + type);
        addCategoryRef.push().setValue(category);
    }

    private void getIncomeExpenseCategories() {
        incomeExpenseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> incomes = new ArrayList<>();
                ArrayList<String> expenses = new ArrayList<>();

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
