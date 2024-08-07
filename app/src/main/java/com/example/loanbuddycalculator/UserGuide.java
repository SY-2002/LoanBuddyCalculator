package com.example.loanbuddycalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class UserGuide extends AppCompatActivity {

    private ImageView userGuideImage;
    private Button backButton, homeButton, nextButton;

    private int[] guideImages = {
            R.drawable.guide_image_1,
            R.drawable.guide_image_2,
            R.drawable.guide_image_3,
            R.drawable.guide_image_4,
            R.drawable.guide_image_5,

    };

    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_guide);
        userGuideImage = findViewById(R.id.userGuideIcon);
        backButton = findViewById(R.id.backButton1);
        homeButton = findViewById(R.id.homeButton1);
        nextButton = findViewById(R.id.nextButton1);

        // Initialize with the first image
        userGuideImage.setImageResource(guideImages[currentIndex]);

        // Set up button click listeners
        backButton.setOnClickListener(v -> showPreviousImage());
        homeButton.setOnClickListener(v -> navigateHome());
        nextButton.setOnClickListener(v -> showNextImage());

        // Update button visibility
        updateButtons();
    }

    private void showPreviousImage() {
        if (currentIndex > 0) {
            currentIndex--;
            userGuideImage.setImageResource(guideImages[currentIndex]);
            updateButtons();
        }
    }

    private void showNextImage() {
        if (currentIndex < guideImages.length - 1) {
            currentIndex++;
            userGuideImage.setImageResource(guideImages[currentIndex]);
            updateButtons();
        }
    }

    private void navigateHome() {
        // Navigate to the home activity (MainActivity)
        Intent intent = new Intent(UserGuide.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateButtons() {
        backButton.setVisibility(currentIndex == 0 ? View.GONE : View.VISIBLE);
        nextButton.setVisibility(currentIndex == guideImages.length - 1 ? View.GONE : View.VISIBLE);
    }
}
