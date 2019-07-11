package com.chaitupenjudcoder;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.chaitupenjudcoder.buckstrack.R;
import com.chaitupenjudcoder.buckstrack.databinding.ActivityAddIncomeExpenseBinding;
import com.chaitupenjudcoder.datapojos.IncomeExpense;
import com.chaitupenjudcoder.firebasehelpers.FirebaseCategoriesHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

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
            }
        }, BUCKS_STRING);
    }

    private void initDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(AddIncomeExpenseActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date.setText(getResources().getString(R.string.date_format_string, dayOfMonth ,(month + 1), year));
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
        mReference = mDatabase.getReference("data/"+uId+"/spendings");
        //push the object into it
        mReference.push().setValue(income);
        String all = titleStr + " \n" + amountStr + " \n" + dateStr + " \n" + descriptionStr + " \n" + categoryStr;
        Toast.makeText(this, "String is :\n"+all, Toast.LENGTH_SHORT).show();
    }

}
