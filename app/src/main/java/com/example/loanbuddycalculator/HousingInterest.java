package com.example.loanbuddycalculator;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class HousingInterest extends AppCompatActivity {
    private Button backPrevious;
    private TableLayout tableLayout;
    private TextView totalInterestTextView;
    private TextView loanAmountTextView;
    private TextView interestRateTextView;
    private TextView numRepaymentsTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_housing_interest);
        backPrevious = findViewById(R.id.back_previous);
        tableLayout = findViewById(R.id.tableLayout);
        totalInterestTextView = findViewById(R.id.totalInterest);
        loanAmountTextView = findViewById(R.id.loanAmount);
        interestRateTextView = findViewById(R.id.interestRate);
        numRepaymentsTextView = findViewById(R.id.numRepayments);

        double principal = getIntent().getDoubleExtra("principal", 0);
        double annualRate = getIntent().getDoubleExtra("interestRate", 0); // Expect 0.04 for 4%
        int numberOfRepayments = getIntent().getIntExtra("numberOfRepayments", 0);
        double monthlyInstalment = getIntent().getDoubleExtra("monthlyInstalment", 0);

        double monthlyRate = annualRate / 12;
        double totalInterest = 0;

        // Set details at the top
        loanAmountTextView.setText(String.format("Loan Amount: %.2f", principal));
        interestRateTextView.setText(String.format("Interest: %.2f%%", annualRate * 100));
        numRepaymentsTextView.setText(String.format("Number of Repayments: %d", numberOfRepayments));

        // Remove existing rows, if any
        tableLayout.removeAllViews();

        // Add header row
        TableRow headerRow = new TableRow(this);
        headerRow.setBackgroundColor(0xFF01b695);

        TextView monthHeader = new TextView(this);
        monthHeader.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
        monthHeader.setPadding(8, 8, 8, 8);
        monthHeader.setText("Month");
        monthHeader.setTextColor(0xFFFFFFFF);
        monthHeader.setGravity(android.view.Gravity.CENTER);
        headerRow.addView(monthHeader);

        TextView interestHeader = new TextView(this);
        interestHeader.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
        interestHeader.setPadding(8, 8, 8, 8);
        interestHeader.setText("Interest");
        interestHeader.setTextColor(0xFFFFFFFF);
        interestHeader.setGravity(android.view.Gravity.CENTER);
        headerRow.addView(interestHeader);

        tableLayout.addView(headerRow);

        // Add data rows
        for (int month = 1; month <= numberOfRepayments; month++) {
            double monthlyInterest = principal * monthlyRate; // Calculate interest for this month
            TableRow row = new TableRow(this);

            TextView monthTextView = new TextView(this);
            monthTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
            monthTextView.setPadding(8, 8, 8, 8);
            monthTextView.setText(String.valueOf(month));
            monthTextView.setGravity(android.view.Gravity.CENTER);

            TextView interestTextView = new TextView(this);
            interestTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
            interestTextView.setPadding(8, 8, 8, 8);
            interestTextView.setText(String.format("%.2f", monthlyInterest));
            interestTextView.setGravity(android.view.Gravity.CENTER);

            row.addView(monthTextView);
            row.addView(interestTextView);

            tableLayout.addView(row);

            totalInterest += monthlyInterest;
            principal -= (monthlyInstalment - monthlyInterest);
        }

        // Set total interest paid
        totalInterestTextView.setText(String.format("Total Interest Paid: %.2f", totalInterest));

        backPrevious.setOnClickListener(view -> {
            Intent intent = new Intent(HousingInterest.this, HousingLoan.class);
            startActivity(intent);
        });
    }
}
