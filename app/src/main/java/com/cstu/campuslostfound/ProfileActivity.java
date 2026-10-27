package com.cstu.campuslostfound;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName;
    private TextView tvEmail;
    private TextView tvDepartment;

    private com.google.android.material.card.MaterialCardView btnMyLostReports;
    private com.google.android.material.card.MaterialCardView btnMyFoundReports;
    private Button btnLogout;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvDepartment = findViewById(R.id.tvDepartment);

        btnMyLostReports = findViewById(R.id.btnMyLostReports);
        btnMyFoundReports = findViewById(R.id.btnMyFoundReports);
        btnLogout = findViewById(R.id.btnLogout);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadUserProfile();

        btnLogout.setOnClickListener(v -> {

            mAuth.signOut();

            Intent intent = new Intent(
                    ProfileActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);
            finish();
        });

        btnMyLostReports.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ProfileActivity.this,
                    MyLostReportsActivity.class
            );

            startActivity(intent);
        });

        btnMyFoundReports.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ProfileActivity.this,
                    MyFoundReportsActivity.class
            );

            startActivity(intent);
        });
    }

    private void loadUserProfile() {

        if (mAuth.getCurrentUser() == null) {

            Toast.makeText(
                    ProfileActivity.this,
                    "No user is currently logged in",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        String email = mAuth.getCurrentUser().getEmail();

        // Email comes directly from Firebase Authentication
        tvEmail.setText("Email: " + email);

        // Get profile data directly from Firestore server
        db.collection("users")
                .document(userId)
                .get(com.google.firebase.firestore.Source.SERVER)
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        String name =
                                documentSnapshot.getString("name");

                        String department =
                                documentSnapshot.getString("department");

                        // Check if values are actually available
                        if (name != null && !name.isEmpty()) {

                            tvName.setText("Name: " + name);

                        } else {

                            tvName.setText("Name: Not available");
                        }

                        if (department != null && !department.isEmpty()) {

                            tvDepartment.setText(
                                    "Department: " + department
                            );

                        } else {

                            tvDepartment.setText(
                                    "Department: Not specified"
                            );
                        }

                    } else {

                        tvName.setText("Name: Not available");

                        tvDepartment.setText(
                                "Department: Not specified"
                        );

                        Toast.makeText(
                                ProfileActivity.this,
                                "User profile document not found",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            ProfileActivity.this,
                            "Failed to load profile: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}