package com.cstu.campuslostfound;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ReportLostActivity extends AppCompatActivity {

    private EditText etItemName;
    private EditText etCategory;
    private EditText etDescription;
    private EditText etLocation;
    private EditText etDate;
    private EditText etContact;

    private Button btnSubmit;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_report_lost);

        // Connect XML fields
        etItemName = findViewById(R.id.etItemName);
        etCategory = findViewById(R.id.etCategory);
        etDescription = findViewById(R.id.etDescription);
        etLocation = findViewById(R.id.etLocation);
        etDate = findViewById(R.id.etDate);
        etContact = findViewById(R.id.etContact);

        btnSubmit = findViewById(R.id.btnSubmit);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Submit button
        btnSubmit.setOnClickListener(v -> {

            String itemName =
                    etItemName.getText().toString().trim();

            String category =
                    etCategory.getText().toString().trim();

            String description =
                    etDescription.getText().toString().trim();

            String location =
                    etLocation.getText().toString().trim();

            String date =
                    etDate.getText().toString().trim();

            String contact =
                    etContact.getText().toString().trim();

            // Check required fields
            if (itemName.isEmpty()
                    || category.isEmpty()
                    || location.isEmpty()) {

                Toast.makeText(
                        ReportLostActivity.this,
                        "Please fill in Item Name, Category and Location",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Get currently logged-in user
            FirebaseUser currentUser = mAuth.getCurrentUser();

            if (currentUser == null) {

                Toast.makeText(
                        ReportLostActivity.this,
                        "Please login first",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Get user's Firebase UID
            String userId = currentUser.getUid();

            // Create data map
            Map<String, Object> lostItem = new HashMap<>();

            lostItem.put("itemName", itemName);
            lostItem.put("category", category);
            lostItem.put("description", description);
            lostItem.put("location", location);
            lostItem.put("date", date);
            lostItem.put("contact", contact);

            // Save current user's UID
            lostItem.put("userId", userId);

            // Save data to Firestore
            db.collection("lost_items")
                    .add(lostItem)
                    .addOnSuccessListener(documentReference -> {

                        Toast.makeText(
                                ReportLostActivity.this,
                                "Lost item report saved successfully!",
                                Toast.LENGTH_LONG
                        ).show();

                        // Clear form
                        etItemName.setText("");
                        etCategory.setText("");
                        etDescription.setText("");
                        etLocation.setText("");
                        etDate.setText("");
                        etContact.setText("");
                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(
                                ReportLostActivity.this,
                                "Failed to save report: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    });
        });
    }
}