package com.cstu.campuslostfound;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MyFoundReportsActivity extends AppCompatActivity {

    private LinearLayout reportsContainer;
    private TextView tvNoReports;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_found_reports);

        reportsContainer = findViewById(R.id.reportsContainer);
        tvNoReports = findViewById(R.id.tvNoReports);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadMyFoundReports();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMyFoundReports();
    }

    // Load reports submitted by the currently logged-in user
    private void loadMyFoundReports() {

        if (mAuth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("found_items")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    reportsContainer.removeAllViews();

                    if (queryDocumentSnapshots.isEmpty()) {
                        tvNoReports.setVisibility(View.VISIBLE);
                        return;
                    }

                    tvNoReports.setVisibility(View.GONE);

                    for (DocumentSnapshot document
                            : queryDocumentSnapshots.getDocuments()) {

                        String itemName = document.getString("itemName");
                        String category = document.getString("category");
                        String description = document.getString("description");
                        String location = document.getString("location");
                        String date = document.getString("date");
                        String contact = document.getString("contact");
                        String documentId = document.getId();

                        String savedStatus = document.getString("status");

                        final String status =
                                (savedStatus == null ||
                                        savedStatus.trim().isEmpty())
                                        ? "Active"
                                        : savedStatus;

                        View reportView = LayoutInflater.from(this)
                                .inflate(
                                        R.layout.item_my_report,
                                        reportsContainer,
                                        false
                                );

                        TextView icon =
                                reportView.findViewById(R.id.tvReportIcon);

                        TextView name =
                                reportView.findViewById(
                                        R.id.tvReportItemName
                                );

                        TextView categoryView =
                                reportView.findViewById(
                                        R.id.tvReportCategory
                                );

                        TextView locationView =
                                reportView.findViewById(
                                        R.id.tvReportLocation
                                );

                        TextView dateView =
                                reportView.findViewById(
                                        R.id.tvReportDate
                                );

                        TextView deleteButton =
                                reportView.findViewById(
                                        R.id.btnDeleteReport
                                );

                        TextView statusView =
                                reportView.findViewById(
                                        R.id.tvReportStatus
                                );

                        TextView resolveButton =
                                reportView.findViewById(
                                        R.id.btnResolveReport
                                );

                        TextView editButton =
                                reportView.findViewById(
                                        R.id.btnEditReport
                                );

                        // Display report information
                        icon.setText("🎁");
                        name.setText(safeValue(itemName));
                        categoryView.setText(
                                "📁 " + safeValue(category)
                        );
                        locationView.setText(
                                "📍 " + safeValue(location)
                        );
                        dateView.setText(
                                "📅 " + safeValue(date)
                        );

                        // Display report status
                        if ("Resolved".equalsIgnoreCase(status)) {

                            statusView.setText("✅ Resolved");
                            resolveButton.setText("✓ Resolved");
                            resolveButton.setEnabled(false);
                            resolveButton.setAlpha(0.65f);

                        } else {

                            statusView.setText("🟢 Active");
                            resolveButton.setText("✅ Mark as Resolved");
                            resolveButton.setEnabled(true);
                            resolveButton.setAlpha(1.0f);
                        }

                        // Open item details
                        reportView.setOnClickListener(v -> {

                            Intent intent = new Intent(
                                    MyFoundReportsActivity.this,
                                    ItemDetailsActivity.class
                            );

                            intent.putExtra("itemName", itemName);
                            intent.putExtra("category", category);
                            intent.putExtra("description", description);
                            intent.putExtra("location", location);
                            intent.putExtra("date", date);
                            intent.putExtra("contact", contact);
                            intent.putExtra("itemType", "Found");

                            startActivity(intent);
                        });

                        // Edit report
                        editButton.setOnClickListener(v -> {

                            showEditDialog(
                                    documentId,
                                    itemName,
                                    category,
                                    description,
                                    location,
                                    date,
                                    contact
                            );
                        });

                        // Delete report
                        deleteButton.setOnClickListener(v -> {

                            showDeleteConfirmation(
                                    documentId,
                                    reportView
                            );
                        });

                        // Mark report as resolved
                        resolveButton.setOnClickListener(v -> {

                            if (!"Resolved".equalsIgnoreCase(status)) {

                                showResolveConfirmation(
                                        documentId,
                                        statusView,
                                        resolveButton
                                );
                            }
                        });

                        // Card animation
                        reportView.setAlpha(0f);
                        reportView.setTranslationY(20f);

                        reportView.animate()
                                .alpha(1f)
                                .translationY(0f)
                                .setDuration(300)
                                .start();

                        reportsContainer.addView(reportView);
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            MyFoundReportsActivity.this,
                            "Failed to load your found reports: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // Show the edit form with existing report information
    private void showEditDialog(
            String documentId,
            String itemName,
            String category,
            String description,
            String location,
            String date,
            String contact) {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        int padding = (int) (20 * getResources()
                .getDisplayMetrics().density);

        layout.setPadding(padding, padding, padding, 0);

        EditText etItemName = createEditText(
                "Item Name",
                itemName
        );

        EditText etCategory = createEditText(
                "Category",
                category
        );

        EditText etDescription = createEditText(
                "Description",
                description
        );

        EditText etLocation = createEditText(
                "Location",
                location
        );

        EditText etDate = createEditText(
                "Date",
                date
        );

        EditText etContact = createEditText(
                "Contact",
                contact
        );

        layout.addView(etItemName);
        layout.addView(etCategory);
        layout.addView(etDescription);
        layout.addView(etLocation);
        layout.addView(etDate);
        layout.addView(etContact);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Edit Found Report")
                .setView(layout)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Update", null)
                .create();

        dialog.setOnShowListener(d -> {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {

                        String updatedItemName =
                                etItemName.getText().toString().trim();

                        String updatedCategory =
                                etCategory.getText().toString().trim();

                        String updatedDescription =
                                etDescription.getText().toString().trim();

                        String updatedLocation =
                                etLocation.getText().toString().trim();

                        String updatedDate =
                                etDate.getText().toString().trim();

                        String updatedContact =
                                etContact.getText().toString().trim();

                        // Validate required fields
                        if (updatedItemName.isEmpty()
                                || updatedCategory.isEmpty()
                                || updatedDescription.isEmpty()
                                || updatedLocation.isEmpty()
                                || updatedDate.isEmpty()
                                || updatedContact.isEmpty()) {

                            Toast.makeText(
                                    MyFoundReportsActivity.this,
                                    "Please fill in all fields",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        updateFoundReport(
                                documentId,
                                updatedItemName,
                                updatedCategory,
                                updatedDescription,
                                updatedLocation,
                                updatedDate,
                                updatedContact,
                                dialog
                        );
                    });
        });

        dialog.show();
    }

    // Create an EditText field
    private EditText createEditText(
            String hint,
            String value) {

        EditText editText = new EditText(this);

        editText.setHint(hint);
        editText.setText(value == null ? "" : value);
        editText.setSingleLine(false);
        editText.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        );

        return editText;
    }

    // Update the existing report in Firestore
    private void updateFoundReport(
            String documentId,
            String itemName,
            String category,
            String description,
            String location,
            String date,
            String contact,
            AlertDialog dialog) {

        if (mAuth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String currentUserId = mAuth.getCurrentUser().getUid();

        db.collection("found_items")
                .document(documentId)
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()) {

                        Toast.makeText(
                                MyFoundReportsActivity.this,
                                "Report not found",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    String ownerId = document.getString("userId");

                    // Only the report owner can edit it
                    if (ownerId == null
                            || !ownerId.equals(currentUserId)) {

                        Toast.makeText(
                                MyFoundReportsActivity.this,
                                "You can only edit your own report",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    // Update only editable fields.
                    // Existing userId and status remain unchanged.
                    Map<String, Object> updates = new HashMap<>();

                    updates.put("itemName", itemName);
                    updates.put("category", category);
                    updates.put("description", description);
                    updates.put("location", location);
                    updates.put("date", date);
                    updates.put("contact", contact);

                    db.collection("found_items")
                            .document(documentId)
                            .update(updates)
                            .addOnSuccessListener(unused -> {

                                dialog.dismiss();

                                Toast.makeText(
                                        MyFoundReportsActivity.this,
                                        "Found report updated successfully!",
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadMyFoundReports();
                            })
                            .addOnFailureListener(e -> {

                                Toast.makeText(
                                        MyFoundReportsActivity.this,
                                        "Failed to update report: "
                                                + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            });
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            MyFoundReportsActivity.this,
                            "Could not verify report ownership: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // Confirmation before marking the report as resolved
    private void showResolveConfirmation(
            String documentId,
            TextView statusView,
            TextView resolveButton) {

        new AlertDialog.Builder(this)
                .setTitle("Mark as Resolved?")
                .setMessage(
                        "Has this found item been returned to its owner? "
                                + "Mark this report as resolved?"
                )
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Yes, Resolve", (dialog, which) -> {

                    markReportAsResolved(
                            documentId,
                            statusView,
                            resolveButton
                    );
                })
                .show();
    }

    // Update report status in Firestore
    private void markReportAsResolved(
            String documentId,
            TextView statusView,
            TextView resolveButton) {

        resolveButton.setEnabled(false);

        db.collection("found_items")
                .document(documentId)
                .update("status", "Resolved")
                .addOnSuccessListener(unused -> {

                    statusView.setText("✅ Resolved");
                    resolveButton.setText("✓ Resolved");
                    resolveButton.setAlpha(0.65f);
                    resolveButton.setEnabled(false);

                    Toast.makeText(
                            MyFoundReportsActivity.this,
                            "Found report marked as resolved!",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .addOnFailureListener(e -> {

                    resolveButton.setEnabled(true);

                    Toast.makeText(
                            MyFoundReportsActivity.this,
                            "Failed to update status: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // Confirmation before deleting a report
    private void showDeleteConfirmation(
            String documentId,
            View reportView) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Report?")
                .setMessage(
                        "Are you sure you want to delete this found report?"
                )
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {

                    deleteReport(documentId, reportView);
                })
                .show();
    }

    // Delete report from Firestore
    private void deleteReport(
            String documentId,
            View reportView) {

        db.collection("found_items")
                .document(documentId)
                .delete()
                .addOnSuccessListener(unused -> {

                    reportsContainer.removeView(reportView);

                    if (reportsContainer.getChildCount() == 0) {
                        tvNoReports.setVisibility(View.VISIBLE);
                    }

                    Toast.makeText(
                            MyFoundReportsActivity.this,
                            "Found report deleted successfully",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            MyFoundReportsActivity.this,
                            "Failed to delete report: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // Handle null or empty values
    private String safeValue(String value) {

        if (value == null || value.trim().isEmpty()) {
            return "Not specified";
        }

        return value;
    }
}
