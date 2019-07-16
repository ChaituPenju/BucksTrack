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
import com.chaitupenjudcoder.firebasehelpers.FirebaseCategoriesHelper;
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

    RecyclerView rvIncome, rvExpense;

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

        //retrieve the list of rvIncome and rvExpense categories
        getIncomeExpenseCategories();

        rvIncome = catBind.rvIncome;
        rvExpense = catBind.rvExpense;

        //create and set layout manager for each RecyclerView
        RecyclerView.LayoutManager firstLayoutManager = new LinearLayoutManager(this);
        RecyclerView.LayoutManager secondLayoutManager = new LinearLayoutManager(this);

        rvIncome.setLayoutManager(firstLayoutManager);
        rvExpense.setLayoutManager(secondLayoutManager);

        initIncomeExpenseButtons();

    }

    //  initialize/set on click functionality for rvIncome and rvExpense adding buttons
    public void initIncomeExpenseButtons() {
        catBind.btnAddIncomeCategory.setOnClickListener(v -> initCategoryAddCustomDialog("rvIncome"));

        catBind.btnAddExpenseCategory.setOnClickListener(v -> initCategoryAddCustomDialog("rvExpense"));
    }

    public void initCategoryAddCustomDialog(final String type) {
        addCategoryBind = AddCategoryCustomDialogBinding.inflate(getLayoutInflater());

        AlertDialog.Builder catAddBuilder = new AlertDialog.Builder(CategoriesActivity.this);
        addCategoryBind.setCategoryType(type);
        View mView = addCategoryBind.getRoot();

        catAddBuilder.setView(mView);

        final AlertDialog addCatDialog = catAddBuilder.create();
        addCatDialog.setCanceledOnTouchOutside(true);

        addCategoryBind.btnCategoryCancel.setOnClickListener(v -> addCatDialog.dismiss());

        addCategoryBind.btnCategoryOk.setOnClickListener(v -> {
            String category = addCategoryBind.etAddCategory.getText().toString();
            addIncomeExpenseCategory(type, category);
            addCatDialog.dismiss();
        });

        addCatDialog.show();
    }

    //  adding category to corresponding child(rvIncome/rvExpense) on button click
    public void addIncomeExpenseCategory(String type, String category) {
        addCategoryRef = FirebaseDatabase.getInstance().getReference("data/" + mUser.getUid() + "/categories/" + type);
        addCategoryRef.push().setValue(category);
    }

    private void getIncomeExpenseCategories() {
        FirebaseCategoriesHelper incExpCats1 = new FirebaseCategoriesHelper();
        FirebaseCategoriesHelper incExpCats2 = new FirebaseCategoriesHelper();

        incExpCats1.getAllCategories((allCategory) -> {
            BucksCategoriesRecycler firstAdapter = new BucksCategoriesRecycler(allCategory);
            rvIncome.setAdapter(firstAdapter);
        }, "income");

        incExpCats2.getAllCategories((allCategory) -> {
            BucksCategoriesRecycler firstAdapter = new BucksCategoriesRecycler(allCategory);
            rvExpense.setAdapter(firstAdapter);
        }, "expense");
    }
}
