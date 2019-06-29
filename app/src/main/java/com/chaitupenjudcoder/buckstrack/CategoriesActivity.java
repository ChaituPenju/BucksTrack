package com.chaitupenjudcoder.buckstrack;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.chaitupenjudcoder.buckstrack.databinding.ActivityCategoriesBinding;
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
                //categories activity with sample settext for testing
                catBind.tvIncomes.setText("INCOME CATEGORIES\n"+incomes.toString());
                catBind.tvExpenses.setText("EXPENSE CATEGORIES\n"+expenses.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
