package com.cstu.campuslostfound;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2200;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        Window window = getWindow();

        // Status bar and navigation bar
        window.setStatusBarColor(Color.WHITE);
        window.setNavigationBarColor(Color.WHITE);

        View splashContent = findViewById(R.id.splashContent);
        View ivSplashLogo = findViewById(R.id.ivSplashLogo);
        View tvSplashTitle = findViewById(R.id.tvSplashTitle);
        View tvSplashTagline = findViewById(R.id.tvSplashTagline);
        View blackOverlay = findViewById(R.id.blackOverlay);

        // Initially invisible
        splashContent.setAlpha(1f);

        ivSplashLogo.setAlpha(0f);
        ivSplashLogo.setScaleX(0.85f);
        ivSplashLogo.setScaleY(0.85f);

        tvSplashTitle.setAlpha(0f);
        tvSplashTitle.setTranslationY(25f);

        tvSplashTagline.setAlpha(0f);
        tvSplashTagline.setTranslationY(20f);

        blackOverlay.setAlpha(0f);

        // -----------------------------
        // 1. Logo animation
        // -----------------------------
        ivSplashLogo.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(700)
                .start();

        // -----------------------------
        // 2. Title animation
        // -----------------------------
        tvSplashTitle.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(350)
                .setDuration(650)
                .start();

        // -----------------------------
        // 3. Tagline animation
        // -----------------------------
        tvSplashTagline.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(500)
                .setDuration(650)
                .start();

        // -----------------------------
        // 4. After splash → black fade
        // -----------------------------
        handler.postDelayed(() -> {

            window.setStatusBarColor(Color.BLACK);
            window.setNavigationBarColor(Color.BLACK);

            blackOverlay.animate()
                    .alpha(1f)
                    .setDuration(500)
                    .withEndAction(() -> {

                        // Open Dashboard/Home
                        Intent intent = new Intent(
                                SplashActivity.this,
                                MainActivity.class
                        );

                        startActivity(intent);

                        // Smooth transition
                        overridePendingTransition(
                                android.R.anim.fade_in,
                                android.R.anim.fade_out
                        );

                        finish();
                    })
                    .start();

        }, SPLASH_DELAY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}