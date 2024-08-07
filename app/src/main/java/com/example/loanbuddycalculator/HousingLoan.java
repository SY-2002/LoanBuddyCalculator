package com.example.loanbuddycalculator;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HousingLoan extends AppCompatActivity {

    private Calendar calendar;
    private TextView loanStartDateTextView;
    private EditText loanAmountEditText;
    private EditText interestRateEditText;
    private EditText numberOfRepaymentsEditText;
    private TextView resultsTextView;
    private Button calculateButton;
    private Button backHomeButton;
    private Button resetButton;
    private Button buttonHousingAmortizationScheduleButton;
    private Button buttonInterest;

    private static final String PREFS_NAME = "LoanBuddy";
    private static final String KEY_LOAN_AMOUNT = "loanAmount";
    private static final String KEY_INTEREST_RATE = "interestRate";
    private static final String KEY_REPAYMENTS = "numberOfRepayments";
    private static final String KEY_LOAN_START_DATE = "loanStartDate";
    private static final String KEY_BIRTH_YEAR = "birthYear";
    private static final String KEY_CLEAR_DATA = "clearData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_housing_loan);

        loanAmountEditText = findViewById(R.id.editTextLoanAmount);
        interestRateEditText = findViewById(R.id.editTextInterestRate);
        numberOfRepaymentsEditText = findViewById(R.id.editTextNumberOfRepayments);
        loanStartDateTextView = findViewById(R.id.editTextLoanStartDate);
        resultsTextView = findViewById(R.id.textViewResults);
        calculateButton = findViewById(R.id.buttonCalculate);
        resetButton = findViewById(R.id.buttonReset);
        backHomeButton = findViewById(R.id.backHome);
        buttonHousingAmortizationScheduleButton = findViewById(R.id.buttonHousingAmortizationSchedule);
        buttonInterest = findViewById(R.id.buttonInterest);

        calendar = Calendar.getInstance();

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean clearData = sharedPreferences.getBoolean(KEY_CLEAR_DATA, false);

        // Restore input data from SharedPreferences
        loanAmountEditText.setText(sharedPreferences.getString(KEY_LOAN_AMOUNT, ""));
        interestRateEditText.setText(sharedPreferences.getString(KEY_INTEREST_RATE, ""));
        numberOfRepaymentsEditText.setText(sharedPreferences.getString(KEY_REPAYMENTS, ""));
        loanStartDateTextView.setText(sharedPreferences.getString(KEY_LOAN_START_DATE, ""));
        buttonHousingAmortizationScheduleButton.setOnClickListener(v -> {
            try {
                double principal = Double.parseDouble(loanAmountEditText.getText().toString());
                double interestRate = Double.parseDouble(interestRateEditText.getText().toString());
                int numberOfRepayments = Integer.parseInt(numberOfRepaymentsEditText.getText().toString());

                Intent intent = new Intent(HousingLoan.this, HousingAmortization.class);
                intent.putExtra("principal", principal);
                intent.putExtra("interestRate", interestRate);
                intent.putExtra("numberOfRepayments", numberOfRepayments);

                startActivity(intent);
            } catch (NumberFormatException e) {
                resultsTextView.setText("Error: Please check your input values.");
            }
        });

        buttonInterest.setOnClickListener(v -> {
            try {
                double principal = Double.parseDouble(loanAmountEditText.getText().toString());
                double annualRate = Double.parseDouble(interestRateEditText.getText().toString()) / 100;
                int numberOfRepayments = Integer.parseInt(numberOfRepaymentsEditText.getText().toString());

                double monthlyRate = annualRate / 12;
                double monthlyInstalment = (principal * monthlyRate * Math.pow(1 + monthlyRate, numberOfRepayments)) /
                        (Math.pow(1 + monthlyRate, numberOfRepayments) - 1);

                Intent intent = new Intent(HousingLoan.this, HousingInterest.class);
                intent.putExtra("principal", principal);
                intent.putExtra("interestRate", annualRate);
                intent.putExtra("numberOfRepayments", numberOfRepayments);
                intent.putExtra("monthlyInstalment", monthlyInstalment);

                startActivity(intent);
            } catch (NumberFormatException e) {
                resultsTextView.setText("Error: Please check your input values.");
            }
        });

        loanStartDateTextView.setOnClickListener(view ->
                new DatePickerDialog(HousingLoan.this, dateSetListener,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show()
        );

        backHomeButton.setOnClickListener(view -> {
            Intent intent = new Intent(HousingLoan.this, MainActivity.class);
            // Clear SharedPreferences data when navigating back to home
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(KEY_LOAN_AMOUNT);
            editor.remove(KEY_INTEREST_RATE);
            editor.remove(KEY_REPAYMENTS);
            editor.remove(KEY_LOAN_START_DATE);
            editor.putBoolean(KEY_CLEAR_DATA, true);
            editor.apply();
            startActivity(intent);
            finish();
        });

        calculateButton.setOnClickListener(v -> {
            try {
                double principal = Double.parseDouble(loanAmountEditText.getText().toString());
                double annualRate = Double.parseDouble(interestRateEditText.getText().toString()) / 100;
                int numberOfRepayments = Integer.parseInt(numberOfRepaymentsEditText.getText().toString());
                String startDateStr = loanStartDateTextView.getText().toString();
                String birthDateStr = sharedPreferences.getString(KEY_BIRTH_YEAR, "");

                // Log inputs
                Log.d("HousingLoan", "Principal: " + principal);
                Log.d("HousingLoan", "Annual Rate: " + annualRate);
                Log.d("HousingLoan", "Number of Repayments: " + numberOfRepayments);
                Log.d("HousingLoan", "Start Date: " + startDateStr);
                Log.d("HousingLoan", "Birth Date: " + birthDateStr);

                // Validate birth date
                if (birthDateStr.isEmpty()) {
                    resultsTextView.setText("Error: Birth year not set.");
                    return;
                }

                // Calculate the maximum allowed loan tenure
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Date birthDate;
                try {
                    birthDate = dateFormat.parse(birthDateStr);
                } catch (ParseException e) {
                    resultsTextView.setText("Error: Invalid birth date format.");
                    return;
                }

                Calendar birthCalendar = Calendar.getInstance();
                birthCalendar.setTime(birthDate);
                int birthYear = birthCalendar.get(Calendar.YEAR);
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                int age = currentYear - birthYear;
                int maxTenureYears = Math.min(35, 70 - age); // 35 years or up to age 70

                if (numberOfRepayments > maxTenureYears * 12) {
                    resultsTextView.setText("Error: Loan tenure exceeds the maximum allowed period.");
                    return;
                }

                double monthlyRate = annualRate / 12;
                double monthlyInstalment = (principal * monthlyRate * Math.pow(1 + monthlyRate, numberOfRepayments)) /
                        (Math.pow(1 + monthlyRate, numberOfRepayments) - 1);

                Date startDate = dateFormat.parse(startDateStr);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);
                calendar.add(Calendar.MONTH, numberOfRepayments);
                Date lastPaymentDate = calendar.getTime();

                double totalRepaid = monthlyInstalment * numberOfRepayments;

                String results = String.format(
                        "Monthly Installment: %.2f\n" +
                                "Last Payment Date: %s\n" +
                                "Total Repaid: %.2f",
                        monthlyInstalment,
                        dateFormat.format(lastPaymentDate),
                        totalRepaid
                );
                resultsTextView.setText(results);

                // Save data to SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(KEY_LOAN_AMOUNT, loanAmountEditText.getText().toString());
                editor.putString(KEY_INTEREST_RATE, interestRateEditText.getText().toString());
                editor.putString(KEY_REPAYMENTS, numberOfRepaymentsEditText.getText().toString());
                editor.putString(KEY_LOAN_START_DATE, loanStartDateTextView.getText().toString());
                editor.putBoolean(KEY_CLEAR_DATA, false);
                editor.apply();
            } catch (NumberFormatException e) {
                resultsTextView.setText("Error: Please check your input values.");
            } catch (ParseException e) {
                resultsTextView.setText("Error: Invalid date format.");
            }
        });

        resetButton.setOnClickListener(view -> {
            resetInputs();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_CLEAR_DATA, false);
            editor.apply();
        });


        resetButton.setOnClickListener(view -> {
            resetInputs();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_CLEAR_DATA, false);
            editor.apply();
        });
    }

    private final DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
        String date = year + "/" + (month + 1) + "/" + dayOfMonth;
        loanStartDateTextView.setText(date);
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_LOAN_AMOUNT, loanAmountEditText.getText().toString());
        outState.putString(KEY_INTEREST_RATE, interestRateEditText.getText().toString());
        outState.putString(KEY_REPAYMENTS, numberOfRepaymentsEditText.getText().toString());
        outState.putString(KEY_LOAN_START_DATE, loanStartDateTextView.getText().toString());
    }

    private void resetInputs() {
        loanAmountEditText.setText("");
        interestRateEditText.setText("");
        numberOfRepaymentsEditText.setText("");
        loanStartDateTextView.setText("yyyy/MM/dd");
    }
}
