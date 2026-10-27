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

public class ReportFoundActivity extends AppCompatActivity {

    private EditText etFoundItemName;
    private EditText etFoundCategory;
    private EditText etFoundDescription;
    private EditText etFoundLocation;
    private EditText etFoundDate;
    private EditText etFoundContact;

    private Button btnSubmitFound;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_report_found);

        etFoundItemName = findViewById(R.id.etFoundItemName);
        etFoundCategory = findViewById(R.id.etFoundCategory);
        etFoundDescription = findViewById(R.id.etFoundDescription);
        etFoundLocation = findViewById(R.id.etFoundLocation);
        etFoundDate = findViewById(R.id.etFoundDate);
        etFoundContact = findViewById(R.id.etFoundContact);

        btnSubmitFound = findViewById(R.id.btnSubmitFound);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnSubmitFound.setOnClickListener(v -> {

            String itemName =
                    etFoundItemName.getText().toString().trim();

            String category =
                    etFoundCategory.getText().toString().trim();

            String description =
                    etFoundDescription.getText().toString().trim();

            String location =
                    etFoundLocation.getText().toString().trim();

            String date =
                    etFoundDate.getText().toString().trim();

            String contact =
                    etFoundContact.getText().toString().trim();

            // Check required fields
            if (itemName.isEmpty()
                    || category.isEmpty()
                    || location.isEmpty()) {

                Toast.makeText(
                        ReportFoundActivity.this,
                        "Please fill in Item Name, Category and Location",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Get currently logged-in user
            FirebaseUser currentUser = mAuth.getCurrentUser();

            if (currentUser == null) {

                Toast.makeText(
                        ReportFoundActivity.this,
                        "Please login first",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Get user's Firebase UID
            String userId = currentUser.getUid();

            // Create Firestore data
            Map<String, Object> foundItem = new HashMap<>();

            foundItem.put("itemName", itemName);
            foundItem.put("category", category);
            foundItem.put("description", description);
            foundItem.put("location", location);
            foundItem.put("date", date);
            foundItem.put("contact", contact);

            // Save current user's UID
            foundItem.put("userId", userId);

            // Save data to Firestore
            db.collection("found_items")
                    .add(foundItem)
                    .addOnSuccessListener(documentReference -> {

                        Toast.makeText(
                                ReportFoundActivity.this,
                                "Found item report saved successfully!",
                                Toast.LENGTH_LONG
                        ).show();

                        // Clear form
                        etFoundItemName.setText("");
                        etFoundCategory.setText("");
                        etFoundDescription.setText("");
                        etFoundLocation.setText("");
                        etFoundDate.setText("");
                        etFoundContact.setText("");
                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(
                                ReportFoundActivity.this,
                                "Failed to save found report: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    });
        });
    }
}