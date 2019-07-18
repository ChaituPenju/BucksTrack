package com.chaitupenjudcoder;

import android.app.DatePickerDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.chaitupenjudcoder.buckstrack.R;
import com.chaitupenjudcoder.buckstrack.databinding.ActivityDateChooserBinding;

import java.util.Calendar;

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
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();

        });
    }

    public void initDatePickerDialog(EditText et1) {
        Calendar c = Calendar.getInstance();
        final int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        final int month = c.get(Calendar.MONTH);
        final int year = c.get(Calendar.YEAR);

        DatePickerDialog datePick = new DatePickerDialog(DateChooserActivity.this, (view, year1, month1, dayOfMonth1) -> et1.setText(getResources().getString(R.string.date_format_string, dayOfMonth1, (month1 + 1), year1)), year, month, dayOfMonth);
        datePick.show();
    }
}
