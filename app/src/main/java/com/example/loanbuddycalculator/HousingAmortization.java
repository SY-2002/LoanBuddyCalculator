package com.example.loanbuddycalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class HousingAmortization extends AppCompatActivity {
    private LinearLayout tableAndTotalContainer;
    private TableLayout tableLayout;
    private Button backToPrevious;
    private TextView loanAmountTextView, interestRateTextView, numRepaymentsTextView;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_housing_amortization);
        tableAndTotalContainer = findViewById(R.id.tableAndTotalContainer);
        tableLayout = findViewById(R.id.tableLayout);
        backToPrevious = findViewById(R.id.back_previous);

        loanAmountTextView = findViewById(R.id.loanAmount);
        interestRateTextView = findViewById(R.id.interestRate);
        numRepaymentsTextView = findViewById(R.id.numRepayments);

        // Get data from Intent
        double principal = getIntent().getDoubleExtra("principal", 0);
        double interestRate = getIntent().getDoubleExtra("interestRate", 0);
        int numberOfRepayments = getIntent().getIntExtra("numberOfRepayments", 0);

        // Set details to TextViews
        loanAmountTextView.setText(String.format("Loan Amount: %.2f", principal));
        interestRateTextView.setText(String.format("Interest: %.2f%%", interestRate));
        numRepaymentsTextView.setText(String.format("Number of Repayments: %d", numberOfRepayments));

        // Calculate and display amortization schedule
        String amortizationSchedule = calculateAmortizationSchedule(principal, interestRate, numberOfRepayments);
        displayAmortizationSchedule(amortizationSchedule);

        backToPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HousingAmortization.this, HousingLoan.class);
                startActivity(intent);
            }
        });
    }

    private String calculateAmortizationSchedule(double principal, double annualInterestRate, int numberOfRepayments) {
        StringBuilder schedule = new StringBuilder();
        schedule.append(String.format("%-15s %-20s %-20s %-20s %-20s\n",
                "Payment No", "Beginning Balance", "Monthly Repayment", "Interest Paid", "Principal Paid"));

        double monthlyInterestRate = annualInterestRate / 12 / 100;
        double monthlyRepayment = (principal * monthlyInterestRate * Math.pow(1 + monthlyInterestRate, numberOfRepayments)) /
                (Math.pow(1 + monthlyInterestRate, numberOfRepayments) - 1);

        double beginningBalance = principal;
        for (int i = 1; i <= numberOfRepayments; i++) {
            double interestPaid = beginningBalance * monthlyInterestRate;
            double principalPaid = monthlyRepayment - interestPaid;
            schedule.append(String.format("%-15d %-20.2f %-20.2f %-20.2f %-20.2f\n",
                    i, beginningBalance, monthlyRepayment, interestPaid, principalPaid));
            beginningBalance -= principalPaid;
        }

        return schedule.toString();
    }

    private void displayAmortizationSchedule(String scheduleData) {
        // Clear existing table rows
        tableLayout.removeAllViews();

        // Create header row
        TableRow headerRow = new TableRow(this);
        String[] headers = {"Payment No", "Beginning Balance", "Monthly Repayment", "Interest Paid", "Principal Paid"};
        for (String header : headers) {
            TextView headerTextView = new TextView(this);
            headerTextView.setText(header);
            headerTextView.setPadding(8, 8, 8, 8);
            headerTextView.setTextColor(Color.parseColor("#FFFFFF"));
            headerTextView.setBackgroundColor(Color.parseColor("#01b695")); // Set header color
            headerTextView.setGravity(View.TEXT_ALIGNMENT_CENTER);
            headerRow.addView(headerTextView);
        }
        tableLayout.addView(headerRow);

        // Populate table with amortization data
        String[] rows = scheduleData.split("\n");
        for (int i = 1; i < rows.length; i++) { // Skip header
            String row = rows[i];
            if (row.trim().length() > 0) {
                TableRow tableRow = new TableRow(this);
                String[] columns = row.split("\\s+");
                for (String column : columns) {
                    TextView cellTextView = new TextView(this);
                    cellTextView.setText(column);
                    cellTextView.setPadding(8, 8, 8, 8);
                    cellTextView.setTextColor(Color.BLACK);
                    cellTextView.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    tableRow.addView(cellTextView);
                }
                tableLayout.addView(tableRow);
            }
        }
    }
}
