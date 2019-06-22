package com.chaitupenjudcoder;

import android.app.DatePickerDialog;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import com.chaitupenjudcoder.buckstrack.R;
import com.chaitupenjudcoder.buckstrack.databinding.ActivityDateChooserBinding;

import java.util.Calendar;

public class DateChooserActivity extends AppCompatActivity {
    ActivityDateChooserBinding dateChooser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateChooser = DataBindingUtil.setContentView(this, R.layout.activity_date_chooser);

        //
        Calendar c = Calendar.getInstance();
        final int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        final int month = c.get(Calendar.MONTH);
        final int year = c.get(Calendar.YEAR);
        dateChooser.btnDtStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog std = new DatePickerDialog(DateChooserActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateChooser.tvDateStart.setText(year+"-"+month+"-"+dayOfMonth);
                    }
                }, year, month, dayOfMonth);
                std.show();
            }
        });
    }
}
