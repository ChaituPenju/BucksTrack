package com.chaitupenjudcoder;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.chaitupenjudcoder.buckstrack.R;
import com.chaitupenjudcoder.buckstrack.databinding.ActivityAddIncomeExpenseBinding;
import com.chaitupenjudcoder.datapojos.IncomeExpense;
import com.chaitupenjudcoder.firebasehelpers.FirebaseCategoriesHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

import static com.chaitupenjudcoder.BucksActivity.BUCKS_STRING_IS_INCOME_EXTRA;
import static com.chaitupenjudcoder.BucksActivity.INCOME_EXPENSE_OBJECT_EXTRA;

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
        mReference = mDatabase.getReference("data/" + uId + "/categories");

        title = addIncome.etTitle;
        amount = addIncome.etAmount;
        date = addIncome.etDate;
        description = addIncome.etDescription;

        Intent in = getIntent();
        //  set default value for bucks string
        BUCKS_STRING = "income";
        if (in.getExtras() != null) {
            if (in.getExtras().containsKey(BUCKS_STRING_IS_INCOME_EXTRA)) {
                isIncome = in.getExtras().getBoolean(BUCKS_STRING_IS_INCOME_EXTRA);
                BUCKS_STRING = isIncome ? "income" : "expense";
            }

            if (in.getExtras().containsKey(INCOME_EXPENSE_OBJECT_EXTRA)) {
                IncomeExpense ie = in.getParcelableExtra(INCOME_EXPENSE_OBJECT_EXTRA);
                addIncome.setIncExpTransac(ie);
            }
        }
        addIncome.setIsBucksIncome(isIncome);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //initialize date picker dialog with current date and update selected date
                initDatePickerDialog();
            }
        });

        FirebaseCategoriesHelper categoryHelper = new FirebaseCategoriesHelper();
        categoryHelper.getAllCategories(new FirebaseCategoriesHelper.GetAllCategory() {
            @Override
            public void categories(ArrayList<String> categoriesList) {
                String[] categorie = new String[categoriesList.size()];
                categorie = categoriesList.toArray(categorie);
                ArrayAdapter<String> cats = new ArrayAdapter<>(getApplication(), android.R.layout.simple_spinner_dropdown_item, categorie);
                addIncome.spiCategories.setAdapter(cats);
                if (getIntent().getExtras().containsKey(INCOME_EXPENSE_OBJECT_EXTRA)) {
                    IncomeExpense ie = getIntent().getParcelableExtra(INCOME_EXPENSE_OBJECT_EXTRA);
                    setSelectSpinnerValue(ie.getCategory());
                }
            }
        }, BUCKS_STRING);
    }

    private void setSelectSpinnerValue(String category)
    {
        Spinner spinner = addIncome.spiCategories;
        int index = 0;
        for(int i = 0; i < spinner.getCount(); i++){
            if(spinner.getItemAtPosition(i).toString().equals(category)){
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void initDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(AddIncomeExpenseActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date.setText(getResources().getString(R.string.date_format_string, dayOfMonth, (month + 1), year));
            }
        }, mYear, mMonth, mDay);
        dialog.show();
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
        mReference = mDatabase.getReference("data/" + uId + "/spendings");
        //push the object into it
        mReference.push().setValue(income).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AddIncomeExpenseActivity.this, "Your Data is Saved!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

}
