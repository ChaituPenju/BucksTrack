package com.chaitupenjudcoder;

import android.app.DatePickerDialog;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.chaitupenjudcoder.buckstrack.R;
import com.chaitupenjudcoder.buckstrack.databinding.ActivityAddIncomeExpenseBinding;
import com.chaitupenjudcoder.datapojos.IncomeExpense;
import com.chaitupenjudcoder.firebasehelpers.BucksInputValidationHelper;
import com.chaitupenjudcoder.firebasehelpers.FirebaseCategoriesHelper;
import com.chaitupenjudcoder.firebasehelpers.FirebaseTransactionsHelper;
import com.chaitupenjudcoder.firebasehelpers.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.Calendar;

import static com.chaitupenjudcoder.BucksActivity.BUCKS_STRING_IS_INCOME_EXTRA;
import static com.chaitupenjudcoder.BucksActivity.INCOME_EXPENSE_OBJECT_EXTRA;

public class AddIncomeExpenseActivity extends AppCompatActivity {

    ActivityAddIncomeExpenseBinding addIncomeExpense;
    boolean isIncome;
    String BUCKS_STRING;
    EditText title, amount, date, description;
    TextInputLayout titleLout, amountLout, dateLout, descriptionLout;
    String titleStr, amountStr, dateStr, descriptionStr, categoryStr;

    IncomeExpense incExp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addIncomeExpense = DataBindingUtil.setContentView(this, R.layout.activity_add_income_expense);

        //  initialize all edit texts
        title = addIncomeExpense.etTitle;
        amount = addIncomeExpense.etAmount;
        date = addIncomeExpense.etDate;
        description = addIncomeExpense.etDescription;

        //  initialize all text input layouts
        titleLout = addIncomeExpense.etTitleWrapper;
        amountLout = addIncomeExpense.etAmountWrapper;
        dateLout = addIncomeExpense.etDateWrapper;
        descriptionLout = addIncomeExpense.etDescriptionWrapper;

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
                addIncomeExpense.setIncExpTransac(ie);
            }
        }
        addIncomeExpense.setIsBucksIncome(isIncome);

        //  initialize date picker dialog with current date and update selected date
        date.setOnClickListener(v -> initDatePickerDialog());

        FirebaseCategoriesHelper categoryHelper = new FirebaseCategoriesHelper();
        categoryHelper.getAllCategories(this::setSpinnerAdapter, BUCKS_STRING);
    }

    private void setSpinnerAdapter(ArrayList<String> categoriesList) {
        String[] categorie = new String[categoriesList.size()];
        categorie = categoriesList.toArray(categorie);
        ArrayAdapter<String> cats = new ArrayAdapter<>(getApplication(), android.R.layout.simple_spinner_dropdown_item, categorie);
        addIncomeExpense.spiCategories.setAdapter(cats);

        if (getIntent().getExtras().containsKey(INCOME_EXPENSE_OBJECT_EXTRA)) {
            IncomeExpense ie = getIntent().getParcelableExtra(INCOME_EXPENSE_OBJECT_EXTRA);
            setSelectSpinnerValue(ie.getCategory());
        }
    }

    private void setSelectSpinnerValue(String category) {
        Spinner spinner = addIncomeExpense.spiCategories;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(category)) {
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

        DatePickerDialog dialog = new DatePickerDialog(AddIncomeExpenseActivity.this, (view, year, month, dayOfMonth) -> date.setText(getResources().getString(R.string.date_format_string, dayOfMonth, (month + 1), year)), mYear, mMonth, mDay);
        dialog.show();
    }

    public void updateIncomeExpenseData(IncomeExpense incExp) {
        String key = addIncomeExpense.getIncExpTransac().getId();
        FirebaseTransactionsHelper tHelper = new FirebaseTransactionsHelper();
        tHelper.updateATransaction(response -> {
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
            finish();
        }, key, incExp);
    }

    public void saveIncomeExpenseData(View view) {
        titleStr = title.getText().toString();
        amountStr = amount.getText().toString();
        dateStr = date.getText().toString();
        descriptionStr = description.getText().toString();
        categoryStr = (String) addIncomeExpense.spiCategories.getSelectedItem();

        BucksInputValidationHelper validationHelper = new BucksInputValidationHelper();

        //  input validation
        if (!validationHelper.inputValidator(titleLout, titleLout.getCounterMaxLength()) | !validationHelper.inputValidator(amountLout, amountLout.getCounterMaxLength()) | !validationHelper.inputValidator(dateLout, 10) | !validationHelper.inputValidator(descriptionLout, descriptionLout.getCounterMaxLength())) {
            return;
        }

        //  create an instance of addincomeexpense object to push
        incExp = new IncomeExpense(titleStr, amountStr, dateStr, descriptionStr, categoryStr, BUCKS_STRING);
        incExp.setDateFormat(new SharedPreferencesHelper(AddIncomeExpenseActivity.this).getDateFormatPref("dd-MM-yyyy"));

        //  update transaction if it shows from update
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(INCOME_EXPENSE_OBJECT_EXTRA)) {
            updateIncomeExpenseData(incExp);
        } else {
            new FirebaseTransactionsHelper().addATransaction(response -> {
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                finish();
            }, incExp);
        }
    }

}
