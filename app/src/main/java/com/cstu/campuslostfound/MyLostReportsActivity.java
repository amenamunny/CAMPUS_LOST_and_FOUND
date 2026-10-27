package com.cstu.campuslostfound;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyLostReportsActivity extends AppCompatActivity {

    private LinearLayout reportsContainer;
    private TextView tvNoReports;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_lost_reports);

        reportsContainer = findViewById(R.id.reportsContainer);
        tvNoReports = findViewById(R.id.tvNoReports);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadMyLostReports();
    }

    private void loadMyLostReports() {

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

        db.collection("lost_items")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    reportsContainer.removeAllViews();

                    if (queryDocumentSnapshots.isEmpty()) {
                        tvNoReports.setVisibility(View.VISIBLE);
                        return;
                    }

                    tvNoReports.setVisibility(View.GONE);

                    for (com.google.firebase.firestore.DocumentSnapshot document
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
                                (savedStatus == null || savedStatus.trim().isEmpty())
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
                                reportView.findViewById(R.id.tvReportItemName);

                        TextView categoryView =
                                reportView.findViewById(R.id.tvReportCategory);

                        TextView locationView =
                                reportView.findViewById(R.id.tvReportLocation);

                        TextView dateView =
                                reportView.findViewById(R.id.tvReportDate);

                        TextView deleteButton =
                                reportView.findViewById(R.id.btnDeleteReport);

                        TextView statusView =
                                reportView.findViewById(R.id.tvReportStatus);

                        TextView resolveButton =
                                reportView.findViewById(R.id.btnResolveReport);

                        TextView editButton =
                                reportView.findViewById(R.id.btnEditReport);

                        // Display report information
                        icon.setText("🎒");
                        name.setText(safeValue(itemName));
                        categoryView.setText("📁 " + safeValue(category));
                        locationView.setText("📍 " + safeValue(location));
                        dateView.setText("📅 " + safeValue(date));

                        // Display status
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
                                    MyLostReportsActivity.this,
                                    ItemDetailsActivity.class
                            );

                            intent.putExtra("itemName", itemName);
                            intent.putExtra("category", category);
                            intent.putExtra("description", description);
                            intent.putExtra("location", location);
                            intent.putExtra("date", date);
                            intent.putExtra("contact", contact);
                            intent.putExtra("itemType", "Lost");

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

                        // Mark as resolved
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
                            MyLostReportsActivity.this,
                            "Failed to load your lost reports: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // Show dialog to edit a report
    private void showEditDialog(
            String documentId,
            String oldName,
            String oldCategory,
            String oldDescription,
            String oldLocation,
            String oldDate,
            String oldContact) {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        int padding = (int) (20 * getResources()
                .getDisplayMetrics().density);

        layout.setPadding(padding, padding, padding, 0);

        EditText etName = createEditText(
                "Item Name", oldName);

        EditText etCategory = createEditText(
                "Category", oldCategory);

        EditText etDescription = createEditText(
                "Description", oldDescription);

        EditText etLocation = createEditText(
                "Location", oldLocation);

        EditText etDate = createEditText(
                "Date", oldDate);

        EditText etContact = createEditText(
                "Contact", oldContact);

        layout.addView(etName);
        layout.addView(etCategory);
        layout.addView(etDescription);
        layout.addView(etLocation);
        layout.addView(etDate);
        layout.addView(etContact);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("✏️ Edit Lost Report")
                .setView(layout)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Update", null)
                .create();

        dialog.setOnShowListener(d -> {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {

                        String newName =
                                etName.getText().toString().trim();

                        String newCategory =
                                etCategory.getText().toString().trim();

                        String newDescription =
                                etDescription.getText().toString().trim();

                        String newLocation =
                                etLocation.getText().toString().trim();

                        String newDate =
                                etDate.getText().toString().trim();

                        String newContact =
                                etContact.getText().toString().trim();

                        if (newName.isEmpty()
                                || newCategory.isEmpty()
                                || newDescription.isEmpty()
                                || newLocation.isEmpty()
                                || newDate.isEmpty()
                                || newContact.isEmpty()) {

                            Toast.makeText(
                                    MyLostReportsActivity.this,
                                    "Please fill in all fields",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        updateLostReport(
                                documentId,
                                newName,
                                newCategory,
                                newDescription,
                                newLocation,
                                newDate,
                                newContact,
                                dialog
                        );
                    });
        });

        dialog.show();
    }

    // Create an input field
    private EditText createEditText(
            String hint,
            String value) {

        EditText editText = new EditText(this);

        editText.setHint(hint);
        editText.setText(value == null ? "" : value);
        editText.setSingleLine(false);

        return editText;
    }

    // Update the existing Firestore document
    private void updateLostReport(
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
                    "Please login again",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        // Verify ownership before updating.
        db.collection("lost_items")
                .document(documentId)
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()
                            || !userId.equals(document.getString("userId"))) {

                        Toast.makeText(
                                MyLostReportsActivity.this,
                                "You cannot edit this report",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    java.util.Map<String, Object> updates =
                            new java.util.HashMap<>();

                    updates.put("itemName", itemName);
                    updates.put("category", category);
                    updates.put("description", description);
                    updates.put("location", location);
                    updates.put("date", date);
                    updates.put("contact", contact);

                    db.collection("lost_items")
                            .document(documentId)
                            .update(updates)
                            .addOnSuccessListener(unused -> {

                                dialog.dismiss();

                                Toast.makeText(
                                        MyLostReportsActivity.this,
                                        "✅ Lost report updated successfully",
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadMyLostReports();
                            })
                            .addOnFailureListener(e -> {

                                Toast.makeText(
                                        MyLostReportsActivity.this,
                                        "Failed to update report: "
                                                + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            });
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            MyLostReportsActivity.this,
                            "Failed to verify report: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // Confirmation before marking a report as resolved
    private void showResolveConfirmation(
            String documentId,
            TextView statusView,
            TextView resolveButton) {

        new AlertDialog.Builder(this)
                .setTitle("Mark as Resolved?")
                .setMessage(
                        "Have you found this lost item? "
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

    // Update the report status in Firestore
    private void markReportAsResolved(
            String documentId,
            TextView statusView,
            TextView resolveButton) {

        resolveButton.setEnabled(false);

        db.collection("lost_items")
                .document(documentId)
                .update("status", "Resolved")
                .addOnSuccessListener(unused -> {

                    statusView.setText("✅ Resolved");
                    resolveButton.setText("✓ Resolved");
                    resolveButton.setAlpha(0.65f);
                    resolveButton.setEnabled(false);

                    Toast.makeText(
                            MyLostReportsActivity.this,
                            "✅ Lost report marked as resolved!",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .addOnFailureListener(e -> {

                    resolveButton.setEnabled(true);

                    Toast.makeText(
                            MyLostReportsActivity.this,
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
                        "Are you sure you want to delete this lost report?"
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

        db.collection("lost_items")
                .document(documentId)
                .delete()
                .addOnSuccessListener(unused -> {

                    reportsContainer.removeView(reportView);

                    if (reportsContainer.getChildCount() == 0) {
                        tvNoReports.setVisibility(View.VISIBLE);
                    }

                    Toast.makeText(
                            MyLostReportsActivity.this,
                            "🗑️ Lost report deleted successfully",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            MyLostReportsActivity.this,
                            "Failed to delete report: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // Handle missing or empty values
    private String safeValue(String value) {

        if (value == null || value.trim().isEmpty()) {
            return "Not specified";
        }

        return value;
    }
}

