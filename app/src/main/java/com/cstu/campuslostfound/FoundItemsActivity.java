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

public class FoundItemsActivity extends AppCompatActivity {

    private RecyclerView recyclerFoundItems;
    private EditText etSearchFound;

    private Spinner spinnerCategoryFound;
    private Spinner spinnerLocationFound;

    private TextView tvFoundResult;
    private TextView tvFoundMatch;

    private FoundItemAdapter adapter;

    private List<FoundItem> foundItemList;
    private List<FoundItem> filteredFoundItemList;

    private List<String> categories;
    private List<String> locations;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_found_items);

        recyclerFoundItems =
                findViewById(R.id.recyclerFoundItems);

        etSearchFound =
                findViewById(R.id.etSearchFound);

        spinnerCategoryFound =
                findViewById(R.id.spinnerCategoryFound);

        spinnerLocationFound =
                findViewById(R.id.spinnerLocationFound);

        tvFoundResult =
                findViewById(R.id.tvFoundResult);

        tvFoundMatch =
                findViewById(R.id.tvFoundMatch);

        recyclerFoundItems.setLayoutManager(
                new LinearLayoutManager(this)
        );

        foundItemList = new ArrayList<>();
        filteredFoundItemList = new ArrayList<>();

        categories = new ArrayList<>();
        locations = new ArrayList<>();

        adapter = new FoundItemAdapter(
                filteredFoundItemList
        );

        recyclerFoundItems.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        // Search
        etSearchFound.addTextChangedListener(
                new TextWatcher() {

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
                        filterFoundItems();
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }
                }
        );

        // Category Spinner
        spinnerCategoryFound.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {
                        filterFoundItems();
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent
                    ) {
                    }
                }
        );

        // Location Spinner
        spinnerLocationFound.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {
                        filterFoundItems();
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent
                    ) {
                    }
                }
        );
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadFoundItems();
    }
    private void loadFoundItems() {

        db.collection("found_items")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    foundItemList.clear();
                    categories.clear();
                    locations.clear();

                    // Default options
                    categories.add("All Categories");
                    locations.add("All Locations");

                    for (QueryDocumentSnapshot document
                            : queryDocumentSnapshots) {

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

                        String status = document.getString("status");

                        if (status == null || status.trim().isEmpty()) {
                            status = "Active";
                        }

                        FoundItem foundItem = new FoundItem(
                                itemName,
                                category,
                                description,
                                location,
                                date,
                                contact
                        );
                        foundItem.setStatus(status);

                        foundItemList.add(foundItem);

                        addUniqueValue(
                                categories,
                                category
                        );

                        addUniqueValue(
                                locations,
                                location
                        );
                    }

                    setupSpinners();

                    filteredFoundItemList.clear();
                    filteredFoundItemList.addAll(
                            foundItemList
                    );

                    adapter.notifyDataSetChanged();

                    updateResultCount();

                    loadRelatedLostMatches();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            FoundItemsActivity.this,
                            "Failed to load found items: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // Add unique category/location
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

            if (existing.equalsIgnoreCase(
                    cleanValue
            )) {
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

        spinnerCategoryFound.setAdapter(
                categoryAdapter
        );


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

        spinnerLocationFound.setAdapter(
                locationAdapter
        );
    }

    private void filterFoundItems() {

        String searchText =
                etSearchFound.getText()
                        .toString()
                        .trim()
                        .toLowerCase();

        String selectedCategory =
                spinnerCategoryFound.getSelectedItem() == null
                        ? "All Categories"
                        : spinnerCategoryFound
                        .getSelectedItem()
                        .toString();

        String selectedLocation =
                spinnerLocationFound.getSelectedItem() == null
                        ? "All Locations"
                        : spinnerLocationFound
                        .getSelectedItem()
                        .toString();

        filteredFoundItemList.clear();

        for (FoundItem item : foundItemList) {

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
                    selectedCategory.equals(
                            "All Categories"
                    )
                            || category.equals(
                            selectedCategory.toLowerCase()
                    );

            boolean locationMatch =
                    selectedLocation.equals(
                            "All Locations"
                    )
                            || location.equals(
                            selectedLocation.toLowerCase()
                    );

            if (searchMatch
                    && categoryMatch
                    && locationMatch) {

                filteredFoundItemList.add(item);
            }
        }

        adapter.notifyDataSetChanged();

        updateResultCount();
    }

    private void updateResultCount() {

        if (tvFoundResult != null) {

            tvFoundResult.setText(
                    "✨ "
                            + filteredFoundItemList.size()
                            + " found item(s) available"
            );
        }
    }

    private void loadRelatedLostMatches() {

        db.collection("lost_items")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    int matchCount = 0;

                    for (FoundItem foundItem
                            : foundItemList) {

                        String foundCategory =
                                safeValue(
                                        foundItem.getCategory()
                                ).toLowerCase();

                        String foundLocation =
                                safeValue(
                                        foundItem.getLocation()
                                ).toLowerCase();

                        for (
                                QueryDocumentSnapshot document
                                : queryDocumentSnapshots
                        ) {

                            String lostCategory =
                                    safeValue(
                                            document.getString(
                                                    "category"
                                            )
                                    ).toLowerCase();

                            String lostLocation =
                                    safeValue(
                                            document.getString(
                                                    "location"
                                            )
                                    ).toLowerCase();

                            boolean categoryMatch =
                                    !foundCategory.isEmpty()
                                            && foundCategory.equals(
                                            lostCategory
                                    );

                            boolean locationMatch =
                                    !foundLocation.isEmpty()
                                            && foundLocation.equals(
                                            lostLocation
                                    );

                            if (categoryMatch
                                    || locationMatch) {

                                matchCount++;
                                break;
                            }
                        }
                    }

                    if (tvFoundMatch != null) {

                        tvFoundMatch.setVisibility(
                                View.VISIBLE
                        );

                        if (matchCount > 0) {

                            tvFoundMatch.setText(
                                    "🔗 "
                                            + matchCount
                                            + " related lost match(es) available"
                            );

                        } else {

                            tvFoundMatch.setText(
                                    "🔗 No related lost match yet"
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