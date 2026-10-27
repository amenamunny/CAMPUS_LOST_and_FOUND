package com.cstu.campuslostfound;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class LostItemsActivity extends AppCompatActivity {

    private RecyclerView recyclerLostItems;
    private EditText etSearchLost;

    private Spinner spinnerCategoryLost;
    private Spinner spinnerLocationLost;

    private TextView tvLostResult;
    private TextView tvLostMatch;

    private LostItemAdapter adapter;

    private List<LostItem> lostItemList;
    private List<LostItem> filteredLostItemList;

    private List<String> categories;
    private List<String> locations;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lost_items);

        recyclerLostItems = findViewById(R.id.recyclerLostItems);
        etSearchLost = findViewById(R.id.etSearchLost);

        spinnerCategoryLost = findViewById(R.id.spinnerCategoryLost);
        spinnerLocationLost = findViewById(R.id.spinnerLocationLost);

        tvLostResult = findViewById(R.id.tvLostResult);
        tvLostMatch = findViewById(R.id.tvLostMatch);

        recyclerLostItems.setLayoutManager(
                new LinearLayoutManager(this)
        );

        lostItemList = new ArrayList<>();
        filteredLostItemList = new ArrayList<>();

        categories = new ArrayList<>();
        locations = new ArrayList<>();

        adapter = new LostItemAdapter(filteredLostItemList);
        recyclerLostItems.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadLostItems();

        // Search
        etSearchLost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after
            ) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
            ) {
                filterLostItems();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Category Spinner
        spinnerCategoryLost.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {
                        filterLostItems();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );

        // Location Spinner
        spinnerLocationLost.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {
                        filterLostItems();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadLostItems();
    }

    private void loadLostItems() {

        db.collection("lost_items")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    lostItemList.clear();
                    categories.clear();
                    locations.clear();

                    // Default options
                    categories.add("All Categories");
                    locations.add("All Locations");

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                        String itemName =
                                document.getString("itemName");

                        String category =
                                document.getString("category");

                        String location =
                                document.getString("location");

                        String date =
                                document.getString("date");

                        String description =
                                document.getString("description");

                        String contact =
                                document.getString("contact");

                        String status =
                                document.getString("status");

                        if (status == null || status.trim().isEmpty()) {
                            status = "Active";
                        }

                        LostItem lostItem = new LostItem(
                                itemName,
                                category,
                                description,
                                location,
                                date,
                                contact

                        );
                        lostItem.setStatus(status);

                        lostItemList.add(lostItem);

                        // Add category safely
                        addUniqueValue(categories, category);

                        // Add location safely
                        addUniqueValue(locations, location);
                    }

                    setupSpinners();

                    filteredLostItemList.clear();
                    filteredLostItemList.addAll(lostItemList);

                    adapter.notifyDataSetChanged();

                    updateResultCount();

                    loadRelatedFoundMatches();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            LostItemsActivity.this,
                            "Failed to load lost items: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // Add unique values without duplicate categories/locations
    private void addUniqueValue(
            List<String> list,
            String value
    ) {

        if (value == null) {
            return;
        }

        String cleanValue = value.trim();

        if (cleanValue.isEmpty()) {
            return;
        }

        for (String existing : list) {

            if (existing.equalsIgnoreCase(cleanValue)) {
                return;
            }
        }

        list.add(cleanValue);
    }

    private void setupSpinners() {

        // Category Spinner
        ArrayAdapter<String> categoryAdapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.spinner_item,
                        categories
                );

        categoryAdapter.setDropDownViewResource(
                R.layout.spinner_dropdown_item
        );

        spinnerCategoryLost.setAdapter(categoryAdapter);


        // Location Spinner
        ArrayAdapter<String> locationAdapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.spinner_item,
                        locations
                );

        locationAdapter.setDropDownViewResource(
                R.layout.spinner_dropdown_item
        );

        spinnerLocationLost.setAdapter(locationAdapter);
    }

    private void filterLostItems() {

        String searchText =
                etSearchLost.getText()
                        .toString()
                        .trim()
                        .toLowerCase();

        String selectedCategory =
                spinnerCategoryLost.getSelectedItem() == null
                        ? "All Categories"
                        : spinnerCategoryLost
                        .getSelectedItem()
                        .toString();

        String selectedLocation =
                spinnerLocationLost.getSelectedItem() == null
                        ? "All Locations"
                        : spinnerLocationLost
                        .getSelectedItem()
                        .toString();

        filteredLostItemList.clear();

        for (LostItem item : lostItemList) {

            String itemName =
                    safeValue(item.getItemName())
                            .toLowerCase();

            String category =
                    safeValue(item.getCategory())
                            .toLowerCase();

            String location =
                    safeValue(item.getLocation())
                            .toLowerCase();

            boolean searchMatch =
                    searchText.isEmpty()
                            || itemName.contains(searchText)
                            || category.contains(searchText)
                            || location.contains(searchText);

            boolean categoryMatch =
                    selectedCategory.equals("All Categories")
                            || category.equals(
                            selectedCategory.toLowerCase()
                    );

            boolean locationMatch =
                    selectedLocation.equals("All Locations")
                            || location.equals(
                            selectedLocation.toLowerCase()
                    );

            if (searchMatch
                    && categoryMatch
                    && locationMatch) {

                filteredLostItemList.add(item);
            }
        }

        adapter.notifyDataSetChanged();

        updateResultCount();
    }

    private void updateResultCount() {

        if (tvLostResult != null) {

            tvLostResult.setText(
                    "✨ "
                            + filteredLostItemList.size()
                            + " lost item(s) found"
            );
        }
    }

    private void loadRelatedFoundMatches() {

        db.collection("found_items")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    int matchCount = 0;

                    for (LostItem lostItem : lostItemList) {

                        String lostCategory =
                                safeValue(
                                        lostItem.getCategory()
                                ).toLowerCase();

                        String lostLocation =
                                safeValue(
                                        lostItem.getLocation()
                                ).toLowerCase();

                        for (
                                QueryDocumentSnapshot document
                                : queryDocumentSnapshots
                        ) {

                            String foundCategory =
                                    safeValue(
                                            document.getString(
                                                    "category"
                                            )
                                    ).toLowerCase();

                            String foundLocation =
                                    safeValue(
                                            document.getString(
                                                    "location"
                                            )
                                    ).toLowerCase();

                            boolean categoryMatch =
                                    !lostCategory.isEmpty()
                                            && lostCategory.equals(
                                            foundCategory
                                    );

                            boolean locationMatch =
                                    !lostLocation.isEmpty()
                                            && lostLocation.equals(
                                            foundLocation
                                    );

                            if (categoryMatch
                                    || locationMatch) {

                                matchCount++;
                                break;
                            }
                        }
                    }

                    if (tvLostMatch != null) {

                        tvLostMatch.setVisibility(
                                View.VISIBLE
                        );

                        if (matchCount > 0) {

                            tvLostMatch.setText(
                                    "🔗 "
                                            + matchCount
                                            + " related found match(es) available"
                            );

                        } else {

                            tvLostMatch.setText(
                                    "🔗 No related found match yet"
                            );
                        }
                    }
                });
    }

    private String safeValue(String value) {

        if (value == null) {
            return "";
        }

        return value.trim();
    }
}