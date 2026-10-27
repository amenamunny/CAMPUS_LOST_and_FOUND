package com.cstu.campuslostfound;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    private Button btnLostItems;
    private Button btnFoundItems;
    private Button btnReportLost;
    private Button btnReportFound;
    private Button btnProfile;

    private TextView navLost;
    private TextView navFound;
    private TextView navProfile;

    private MaterialCardView welcomeCard;
    private MaterialCardView cardReportLost;
    private MaterialCardView cardReportFound;
    private MaterialCardView cardLostItems;
    private MaterialCardView cardFoundItems;

    // Home Search Card
    private MaterialCardView searchCard;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Check if user is logged in
        if (mAuth.getCurrentUser() == null) {

            Intent intent = new Intent(
                    MainActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);
            finish();

            return;
        }

        setContentView(R.layout.activity_main);

        // -----------------------------
        // Connect buttons
        // -----------------------------

        btnLostItems = findViewById(R.id.btnLostItems);
        btnFoundItems = findViewById(R.id.btnFoundItems);
        btnReportLost = findViewById(R.id.btnReportLost);
        btnReportFound = findViewById(R.id.btnReportFound);
        btnProfile = findViewById(R.id.btnProfile);

        // -----------------------------
        // Connect cards
        // -----------------------------

        welcomeCard = findViewById(R.id.welcomeCard);
        cardReportLost = findViewById(R.id.cardReportLost);
        cardReportFound = findViewById(R.id.cardReportFound);
        cardLostItems = findViewById(R.id.cardLostItems);
        cardFoundItems = findViewById(R.id.cardFoundItems);

        // Home Search Card
        searchCard = findViewById(R.id.searchCard);

        // -----------------------------
        // Bottom navigation
        // -----------------------------

        navLost = findViewById(R.id.navLost);
        navFound = findViewById(R.id.navFound);
        navProfile = findViewById(R.id.navProfile);


        // -----------------------------
        // Report Lost
        // -----------------------------

        btnReportLost.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    ReportLostActivity.class
            );

            startActivity(intent);
        });


        // -----------------------------
        // Report Found
        // -----------------------------

        btnReportFound.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    ReportFoundActivity.class
            );

            startActivity(intent);
        });


        // -----------------------------
        // Lost Items
        // -----------------------------

        btnLostItems.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    LostItemsActivity.class
            );

            startActivity(intent);
        });


        // -----------------------------
        // Found Items
        // -----------------------------

        btnFoundItems.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    FoundItemsActivity.class
            );

            startActivity(intent);
        });


        // -----------------------------
        // Profile
        // -----------------------------

        btnProfile.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    ProfileActivity.class
            );

            startActivity(intent);
        });


        // -----------------------------
        // HOME SEARCH
        // -----------------------------

        searchCard.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    SearchActivity.class
            );

            startActivity(intent);
        });


        // -----------------------------
        // Bottom Navigation
        // -----------------------------

        navLost.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    LostItemsActivity.class
            );

            startActivity(intent);
        });


        navFound.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    FoundItemsActivity.class
            );

            startActivity(intent);
        });


        navProfile.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    ProfileActivity.class
            );

            startActivity(intent);
        });


        // -----------------------------
        // Home Screen Animation
        // -----------------------------

        playHomeAnimations();
    }


    private void playHomeAnimations() {

        Animation fadeSlide =
                AnimationUtils.loadAnimation(
                        this,
                        R.anim.fade_slide_up
                );

        Animation logoAnimation =
                AnimationUtils.loadAnimation(
                        this,
                        R.anim.logo_fade
                );


        welcomeCard.startAnimation(fadeSlide);

        cardReportLost.startAnimation(fadeSlide);

        cardReportFound.startAnimation(fadeSlide);

        cardLostItems.startAnimation(fadeSlide);

        cardFoundItems.startAnimation(fadeSlide);

        findViewById(R.id.ivHomeLogo)
                .startAnimation(logoAnimation);
    }
}