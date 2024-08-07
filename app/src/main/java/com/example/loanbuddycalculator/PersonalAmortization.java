package com.example.loanbuddycalculator;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.graphics.Color;


public class PersonalAmortization extends AppCompatActivity {
    private TableLayout amortizationScheduleTable;
    private Button backPrevious;
    private TextView loanAmountTextView;
    private TextView interestRateTextView;
    private TextView numRepaymentsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_amortization);

        amortizationScheduleTable = findViewById(R.id.tableLayout);
        backPrevious = findViewById(R.id.back_previous);
        loanAmountTextView = findViewById(R.id.loanAmount);
        interestRateTextView = findViewById(R.id.interestRate);
        numRepaymentsTextView = findViewById(R.id.numRepayments);

        backPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonalAmortization.this, PersonalLoan.class);
                startActivity(intent);
            }
        });

        // Get data from Intent
        double principal = getIntent().getDoubleExtra("principal", 0);
        double interestRate = getIntent().getDoubleExtra("interestRate", 0);
        int numberOfRepayments = getIntent().getIntExtra("numberOfRepayments", 0);

        // Display the details
        loanAmountTextView.setText(String.format("Loan Amount: %.2f", principal));
        interestRateTextView.setText(String.format("Interest: %.2f%%", interestRate));
        numRepaymentsTextView.setText(String.format("Number of Repayments: %d", numberOfRepayments));

        // Calculate and display amortization schedule
        populateAmortizationSchedule(principal, interestRate, numberOfRepayments);
    }

    private void populateAmortizationSchedule(double principal, double interestRate, int numberOfRepayments) {
        amortizationScheduleTable.removeAllViews();  // Clear previous rows if any

        // Add header row
        TableRow headerRow = new TableRow(this);
        headerRow.setBackgroundColor(Color.parseColor("#01b695"));
        String[] headers = {"Payment No", "Beginning Balance", "Monthly Repayment", "Interest Paid", "Principal Paid"};
        for (String header : headers) {
            TextView headerText = new TextView(this);
            headerText.setText(header);
            headerText.setPadding(8, 8, 8, 8);
            headerText.setTextColor(getResources().getColor(android.R.color.white));
            headerText.setGravity(Gravity.CENTER);
            headerRow.addView(headerText);
        }
        amortizationScheduleTable.addView(headerRow);

        // Calculate and add rows
        double monthlyRepayment = principal / numberOfRepayments;
        double beginningBalance = principal;
        double totalInterestRate = interestRate / 100;
        double interestPaid = (beginningBalance * totalInterestRate) / numberOfRepayments;
        double principalPaid = principal / numberOfRepayments;

        for (int i = 1; i <= numberOfRepayments; i++) {
            TableRow row = new TableRow(this);
            String[] values = {
                    String.valueOf(i),
                    String.format("%.2f", beginningBalance),
                    String.format("%.2f", monthlyRepayment + interestPaid),
                    String.format("%.2f", interestPaid),
                    String.format("%.2f", principalPaid)
            };
            for (String value : values) {
                TextView cell = new TextView(this);
                cell.setText(value);
                cell.setPadding(8, 8, 8, 8);
                cell.setGravity(Gravity.CENTER);
                row.addView(cell);
            }
            amortizationScheduleTable.addView(row);

            beginningBalance -= principalPaid;
        }
    }
}
