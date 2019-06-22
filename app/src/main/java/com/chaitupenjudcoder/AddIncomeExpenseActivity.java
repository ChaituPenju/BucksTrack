package com.chaitupenjudcoder;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.chaitupenjudcoder.buckstrack.R;
import com.chaitupenjudcoder.buckstrack.databinding.ActivityAddIncomeExpenseBinding;
import com.chaitupenjudcoder.datapojos.IncomeExpense;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.chaitupenjudcoder.BucksActivity.BUCKS_STRING_IS_INCOME_EXTRA;

public class AddIncomeExpenseActivity extends AppCompatActivity {

    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
    FirebaseAuth mAuth;
    FirebaseUser muser;
    ActivityAddIncomeExpenseBinding addIncome;
    boolean isIncome;
    String BUCKS_STRING;
    String uId;
    EditText title, amount, date, description;
    String titleStr, amountStr, dateStr, descriptionStr, categoryStr;

    IncomeExpense income;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addIncome = DataBindingUtil.setContentView(this, R.layout.activity_add_income_expense);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        muser = mAuth.getCurrentUser();
        uId = muser.getUid();
        mReference = mDatabase.getReference("data/"+uId+"/categories");
        Intent in = getIntent();
        if (in.getExtras() != null && in.getExtras().containsKey(BUCKS_STRING_IS_INCOME_EXTRA)) {
            isIncome = in.getExtras().getBoolean(BUCKS_STRING_IS_INCOME_EXTRA);
            BUCKS_STRING = isIncome ? "income" : "expense";
        }
        addIncome.setIsBucksIncome(isIncome);
        title = addIncome.etTitle;
        amount = addIncome.etAmount;
        date = addIncome.etDate;
        description = addIncome.etDescription;
        getAllCategories(new activitiesCallback() {
            @Override
            public void mCallback(ArrayList<String> categories) {
                String[] categorie = new String[categories.size()];
                categorie = categories.toArray(categorie);
                ArrayAdapter<String> cats = new ArrayAdapter<>(getApplication(), android.R.layout.simple_spinner_dropdown_item, categorie);
                addIncome.spiCategories.setAdapter(cats);
            }
        });
    }

    public void saveIncomeData(View view) {
        titleStr = title.getText().toString();
        amountStr = amount.getText().toString();
        dateStr = date.getText().toString();
        descriptionStr = description.getText().toString();
        categoryStr = (String) addIncome.spiCategories.getSelectedItem();
        //create an instance of addincomeexpense object to push
        income = new IncomeExpense(titleStr, amountStr, dateStr, descriptionStr, categoryStr, BUCKS_STRING);
        //goto spendings reference
        mReference = mDatabase.getReference("data/"+uId+"/spendings");
        //push the object into it
        mReference.push().setValue(income);
        String all = titleStr + " \n" + amountStr + " \n" + dateStr + " \n" + descriptionStr + " \n" + categoryStr;
        Toast.makeText(this, "String is :\n"+all, Toast.LENGTH_SHORT).show();
    }

    public interface activitiesCallback{
        void mCallback(ArrayList<String> categories);
    }

    private void getAllCategories(final activitiesCallback callback) {
        ValueEventListener postListener = new ValueEventListener() {
            ArrayList<String> categories = new ArrayList<>();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot categorieShot:dataSnapshot.child(BUCKS_STRING).getChildren()) {
                    String category = categorieShot.getValue(String.class);
                    categories.add(category);
                }
                callback.mCallback(categories);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AddIncomeExpenseActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
            }
        };
        mReference.addValueEventListener(postListener);
    }
}
