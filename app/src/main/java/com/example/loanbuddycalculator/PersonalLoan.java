package com.example.loanbuddycalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PersonalLoan extends AppCompatActivity {
    private EditText loanAmountEditText;
    private EditText interestRateEditText;
    private EditText numberOfRepaymentsEditText;
    private TextView loanStartDateTextView;
    private Button calculateButton;
    private Button backHome;
    private Button resetButton;
    private Button amortizationScheduleButton;
    private Button buttonInterest;
    private TextView resultsTextView;
    private Calendar calendar = Calendar.getInstance();
    private boolean hasError = false; // Flag to track error state

    private static final String PREFS_NAME = "LoanBuddy";
    private static final String BIRTH_YEAR_KEY = "birthYear";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_loan);

        loanAmountEditText = findViewById(R.id.editTextLoanAmount);
        interestRateEditText = findViewById(R.id.editTextInterestRate);
        numberOfRepaymentsEditText = findViewById(R.id.editTextNumberOfRepayments);
        loanStartDateTextView = findViewById(R.id.editTextLoanStartDate);
        calculateButton = findViewById(R.id.buttonCalculate);
        resultsTextView = findViewById(R.id.textViewResults);

        backHome = findViewById(R.id.button_back_home);
        resetButton = findViewById(R.id.buttonReset);
        amortizationScheduleButton = findViewById(R.id.buttonAmortizationSchedule);
        buttonInterest = findViewById(R.id.buttonInterest);

        // Load saved data
        loadData();

        amortizationScheduleButton.setOnClickListener(v -> {
            if (!hasError) { // Only proceed if no error
                try {
                    double principal = Double.parseDouble(loanAmountEditText.getText().toString());
                    double interestRate = Double.parseDouble(interestRateEditText.getText().toString());
                    int numberOfRepayments = Integer.parseInt(numberOfRepaymentsEditText.getText().toString());

                    // Create an Intent to start AmortizationScheduleActivity
                    Intent intent = new Intent(PersonalLoan.this, PersonalAmortization.class);
                    intent.putExtra("principal", principal);
                    intent.putExtra("interestRate", interestRate);
                    intent.putExtra("numberOfRepayments", numberOfRepayments);

                    startActivity(intent);
                } catch (NumberFormatException e) {
                    resultsTextView.setText("Error: Please check your input values.");
                }
            }
        });

        buttonInterest.setOnClickListener(v -> {
            if (!hasError) { // Only proceed if no error
                try {
                    double principal = Double.parseDouble(loanAmountEditText.getText().toString());
                    double annualRate = Double.parseDouble(interestRateEditText.getText().toString()) / 100;
                    int numberOfRepayments = Integer.parseInt(numberOfRepaymentsEditText.getText().toString());

                    double monthlyRate = annualRate / 12;
                    // Monthly installment calculation
                    double monthlyInstallment = (principal * (1 + monthlyRate * numberOfRepayments)) / numberOfRepayments;

                    // Create an Intent to start PersonalInterestAtMonthActivity
                    Intent intent = new Intent(PersonalLoan.this, PersonalInterest.class);
                    intent.putExtra("principal", principal);
                    intent.putExtra("annualRate", annualRate);
                    intent.putExtra("numberOfRepayments", numberOfRepayments);
                    intent.putExtra("monthlyInstallment", monthlyInstallment);

                    startActivity(intent);
                } catch (NumberFormatException e) {
                    resultsTextView.setText("Error: Please check your input values.");
                }
            }
        });

        loanStartDateTextView.setOnClickListener(v -> showDatePickerDialog());

        calculateButton.setOnClickListener(v -> {
            calculateLoan();
            // Save data when calculating
            if (!hasError) {
                saveData();
            }
        });

        backHome.setOnClickListener(view -> {
            // Set a flag indicating the return from MainActivity
            SharedPreferences sharedPreferences = getSharedPreferences("LoanData", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("clearData", true);
            editor.apply();

            Intent intent = new Intent(PersonalLoan.this, MainActivity.class);
            startActivity(intent);
        });

        resetButton.setOnClickListener(v -> {
            // Clear input fields and results
            loanAmountEditText.setText("");
            interestRateEditText.setText("");
            numberOfRepaymentsEditText.setText("");
            loanStartDateTextView.setText("");
            resultsTextView.setText("");

            // Reset error flag
            hasError = false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences("LoanData", MODE_PRIVATE);
        boolean clearData = sharedPreferences.getBoolean("clearData", false);

        if (clearData) {
            // Clear input fields when returning from MainActivity
            loanAmountEditText.setText("");
            interestRateEditText.setText("");
            numberOfRepaymentsEditText.setText("");
            loanStartDateTextView.setText("");
            resultsTextView.setText("");

            // Reset flag
            hasError = false;

            // Reset flag
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("clearData", false);
            editor.apply();
        } else {
            // Load saved data if not clearing data
            loadData();
        }
    }

    private void showDatePickerDialog() {
        new DatePickerDialog(this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
        calendar.set(year, month, dayOfMonth);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        loanStartDateTextView.setText(dateFormat.format(calendar.getTime()));
    };

    private void calculateLoan() {
        try {
            double principal = Double.parseDouble(loanAmountEditText.getText().toString());
            double annualRate = Double.parseDouble(interestRateEditText.getText().toString()) / 100; // Convert annual rate to decimal
            double monthlyRate = annualRate / 12; // Monthly interest rate
            int numberOfRepayments = Integer.parseInt(numberOfRepaymentsEditText.getText().toString());
            String startDateStr = loanStartDateTextView.getText().toString();

            // Get birth year
            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String birthYearStr = sharedPreferences.getString(BIRTH_YEAR_KEY, "");
            if (birthYearStr.isEmpty()) {
                resultsTextView.setText("Error: Please enter your birth year.");
                return;
            }

            int birthYear = Integer.parseInt(birthYearStr.split("/")[0]);
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            int age = currentYear - birthYear;

            // Maximum loan tenure calculations
            int maxTenureYears = Math.min(10, 60 - age);

            // Check if the number of repayments exceeds the maximum tenure
            if (numberOfRepayments > maxTenureYears * 12) { // Convert years to months
                resultsTextView.setText("Error: The number of repayments exceeds the maximum loan tenure.");
                hasError = true; // Set error flag
                return;
            }

            // Corrected monthly installment calculation
            double monthlyInstallment = (principal * (1 + monthlyRate * numberOfRepayments)) / numberOfRepayments;

            // Calculate last payment date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date startDate = dateFormat.parse(startDateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            calendar.add(Calendar.MONTH, numberOfRepayments);
            Date lastPaymentDate = calendar.getTime();

            // Total amount repaid
            double totalRepaid = monthlyInstallment * numberOfRepayments;

            // Format results
            String results = String.format(
                    "Monthly Installment: %.2f\n" +
                            "Last Payment Date: %s\n" +
                            "Total Amount Repaid: %.2f",
                    monthlyInstallment,
                    dateFormat.format(lastPaymentDate),
                    totalRepaid
            );

            resultsTextView.setText(results);

            // Reset error flag after successful calculation
            hasError = false;

        } catch (NumberFormatException | ParseException e) {
            resultsTextView.setText("Error: Please check your input values.");
            hasError = true; // Set error flag
        }
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoanData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("loanAmount", loanAmountEditText.getText().toString());
        editor.putString("interestRate", interestRateEditText.getText().toString());
        editor.putString("numberOfRepayments", numberOfRepaymentsEditText.getText().toString());
        editor.putString("loanStartDate", loanStartDateTextView.getText().toString());
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoanData", MODE_PRIVATE);
        loanAmountEditText.setText(sharedPreferences.getString("loanAmount", ""));
        interestRateEditText.setText(sharedPreferences.getString("interestRate", ""));
        numberOfRepaymentsEditText.setText(sharedPreferences.getString("numberOfRepayments", ""));
        loanStartDateTextView.setText(sharedPreferences.getString("loanStartDate", ""));
    }
}
