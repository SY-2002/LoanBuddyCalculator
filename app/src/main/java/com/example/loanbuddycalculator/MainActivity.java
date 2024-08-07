package com.example.loanbuddycalculator;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {
    private LinearLayout personalLoanLayout;
    private LinearLayout housingLoanLayout;
    private LinearLayout exitButtonLayout;
    private ImageView userGuideIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize views
        personalLoanLayout = findViewById(R.id.personalLoan);
        housingLoanLayout = findViewById(R.id.housingLoan);
        exitButtonLayout = findViewById(R.id.exitButton);
        userGuideIcon = findViewById(R.id.userGuideIcon);  // Initialize the user guide icon

        // Set click listeners for the layouts
        personalLoanLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start activity for personal loan
                Intent intent = new Intent(MainActivity.this, PersonalLoan.class);
                startActivity(intent);
            }
        });

        housingLoanLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start activity for housing loan
                Intent intent = new Intent(MainActivity.this, HousingLoan.class);
                startActivity(intent);
            }
        });

        exitButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Exit the application
                finishAffinity();
            }
        });

        // Set click listener for the user guide icon
        userGuideIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start UserGuideActivity
                Intent intent = new Intent(MainActivity.this, UserGuide.class);
                startActivity(intent);
            }
        });
    }
}