package com.cstu.campuslostfound;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ItemDetailsActivity extends AppCompatActivity {

    private TextView tvDetailItemName;
    private TextView tvDetailCategory;
    private TextView tvDetailDescription;
    private TextView tvDetailLocation;
    private TextView tvDetailDate;
    private TextView tvDetailContact;
    private TextView tvDetailIcon;
    private TextView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_item_details);

        tvDetailItemName =
                findViewById(R.id.tvDetailItemName);

        tvDetailCategory =
                findViewById(R.id.tvDetailCategory);

        tvDetailDescription =
                findViewById(R.id.tvDetailDescription);

        tvDetailLocation =
                findViewById(R.id.tvDetailLocation);

        tvDetailDate =
                findViewById(R.id.tvDetailDate);

        tvDetailContact =
                findViewById(R.id.tvDetailContact);

        tvDetailIcon =
                findViewById(R.id.tvDetailIcon);

        btnBack =
                findViewById(R.id.btnBack);


        // Back button
        btnBack.setOnClickListener(v -> finish());


        String itemName =
                getIntent().getStringExtra("itemName");

        String category =
                getIntent().getStringExtra("category");

        String description =
                getIntent().getStringExtra("description");

        String location =
                getIntent().getStringExtra("location");

        String date =
                getIntent().getStringExtra("date");

        String contact =
                getIntent().getStringExtra("contact");

        String itemType =
                getIntent().getStringExtra("itemType");


        tvDetailItemName.setText(
                safeValue(itemName)
        );

        tvDetailCategory.setText(
                "📁  Category: " + safeValue(category)
        );

        tvDetailDescription.setText(
                "📝  Description: "
                        + safeValue(description)
        );


        if ("Found".equals(itemType)) {

            tvDetailIcon.setText("🎁");

            tvDetailLocation.setText(
                    "📍  Found Location: "
                            + safeValue(location)
            );

            tvDetailDate.setText(
                    "📅  Found Date: "
                            + safeValue(date)
            );

        } else {

            tvDetailIcon.setText("🎒");

            tvDetailLocation.setText(
                    "📍  Lost Location: "
                            + safeValue(location)
            );

            tvDetailDate.setText(
                    "📅  Lost Date: "
                            + safeValue(date)
            );
        }


        tvDetailContact.setText(
                "📞  Contact: "
                        + safeValue(contact)
        );


        // Simple entrance animation
        tvDetailItemName.setAlpha(0f);
        tvDetailItemName.setTranslationY(20f);

        tvDetailItemName.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .start();
    }


    private String safeValue(String value) {

        if (value == null || value.trim().isEmpty()) {
            return "Not specified";
        }

        return value;
    }
}