package com.chaitupenjudcoder;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.chaitupenjudcoder.buckstrack.R;
import com.chaitupenjudcoder.buckstrack.databinding.ActivityDateChooserBinding;
import com.chaitupenjudcoder.firebasehelpers.FirebaseTransactionsHelper;
import com.chaitupenjudcoder.firebasehelpers.SharedPreferencesHelper;

import java.text.ParseException;
import java.util.Calendar;

import static com.chaitupenjudcoder.firebasehelpers.FirebaseTransactionsHelper.BETWEEN_TWO_DATES_EXTRA;
import static com.chaitupenjudcoder.firebasehelpers.FirebaseTransactionsHelper.DATE_ONE_EXTRA;
import static com.chaitupenjudcoder.firebasehelpers.FirebaseTransactionsHelper.DATE_TWO_EXTRA;

public class DateChooserActivity extends AppCompatActivity {
    ActivityDateChooserBinding dateChooser;
    EditText eStartDate, eEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateChooser = DataBindingUtil.setContentView(this, R.layout.activity_date_chooser);

        eStartDate = dateChooser.etStartDate;
        eEndDate = dateChooser.etEndDate;


        eStartDate.setOnClickListener(v -> initDatePickerDialog(eStartDate));
        eEndDate.setOnClickListener(v -> initDatePickerDialog(eEndDate));
        dateChooser.btnGetTransactions.setOnClickListener(v -> {
            String startDate = dateChooser.etStartDate.getText().toString();
            String endDate = dateChooser.etEndDate.getText().toString();

            Intent in = new Intent(this, BucksTransactions.class);
            in.putExtra(DATE_ONE_EXTRA, startDate);
            in.putExtra(DATE_TWO_EXTRA, endDate);
            startActivity(in);
        });

    }

    public void initDatePickerDialog(EditText et) {
        Calendar c = Calendar.getInstance();
        final int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        final int month = c.get(Calendar.MONTH);
        final int year = c.get(Calendar.YEAR);

        DatePickerDialog datePick = new DatePickerDialog(DateChooserActivity.this, (view, year1, month1, dayOfMonth1) -> {
            //something
            String date = new SharedPreferencesHelper(DateChooserActivity.this).convertDate(getResources().getString(R.string.date_format_string, dayOfMonth1, (month1 + 1), year1));
            et.setText(date);
        }, year, month, dayOfMonth);

        datePick.show();
    }
}
