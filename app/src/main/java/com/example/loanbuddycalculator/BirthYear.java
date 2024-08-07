package com.example.loanbuddycalculator;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import java.util.Calendar;

public class BirthYear extends AppCompatActivity {
    private TextView birthYear;
    private Button saveButton;

    private static final String PREFS_NAME = "LoanBuddy";
    private static final String BIRTH_YEAR_KEY = "birthYear";
    private static final String FIRST_TIME_KEY = "firstTime";
    private static final String USER_GUIDE_SHOWN_KEY = "userGuideShown";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birth_year);

        birthYear = findViewById(R.id.textViewBirthYear);
        saveButton = findViewById(R.id.buttonSave);

        birthYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String birthYearStr = birthYear.getText().toString();
                if (!birthYearStr.isEmpty() && !birthYearStr.equals("year/month/day")) {
                    SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(BIRTH_YEAR_KEY, birthYearStr);
                    editor.putBoolean(FIRST_TIME_KEY, false);
                    editor.putBoolean(USER_GUIDE_SHOWN_KEY, false); // Set user guide shown to false
                    editor.apply();

                    Intent intent = new Intent(BirthYear.this, UserGuide.class); // Change to your user guide activity
                    startActivity(intent);
                    finish();
                } else {
                    birthYear.setError("Please enter your birth year");
                }
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                BirthYear.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        // Save in yyyy/MM/dd format
                        String date = selectedYear + "/" + (selectedMonth + 1) + "/" + selectedDay;
                        birthYear.setText(date);
                    }
                }, year, month, day);

        datePickerDialog.show();
    }
}
