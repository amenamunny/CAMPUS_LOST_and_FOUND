package com.cstu.campuslostfound;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearchAll;

    private View btnClearSearch;

    private RecyclerView recyclerSearchLost;
    private RecyclerView recyclerSearchFound;

    private TextView tvLostTitle;
    private TextView tvFoundTitle;
    private TextView tvNoResults;
    private TextView tvSearchResultCount;

    private LostItemAdapter lostAdapter;
    private FoundItemAdapter foundAdapter;

    private List<LostItem> allLostItems;
    private List<FoundItem> allFoundItems;

    private List<LostItem> filteredLostItems;
    private List<FoundItem> filteredFoundItems;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        TextView btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        // Connect views
        etSearchAll = findViewById(R.id.etSearchAll);
        btnClearSearch = findViewById(R.id.btnClearSearch);

        recyclerSearchLost = findViewById(R.id.recyclerSearchLost);
        recyclerSearchFound = findViewById(R.id.recyclerSearchFound);

        tvLostTitle = findViewById(R.id.tvLostTitle);
        tvFoundTitle = findViewById(R.id.tvFoundTitle);
        tvNoResults = findViewById(R.id.tvNoResults);
        tvSearchResultCount = findViewById(R.id.tvSearchResultCount);

        // RecyclerView setup
        recyclerSearchLost.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerSearchFound.setLayoutManager(
                new LinearLayoutManager(this)
        );

        // Initialize lists
        allLostItems = new ArrayList<>();
        allFoundItems = new ArrayList<>();

        filteredLostItems = new ArrayList<>();
        filteredFoundItems = new ArrayList<>();

        // Initialize adapters
        lostAdapter = new LostItemAdapter(filteredLostItems);
        foundAdapter = new FoundItemAdapter(filteredFoundItems);

        recyclerSearchLost.setAdapter(lostAdapter);
        recyclerSearchFound.setAdapter(foundAdapter);

        // Firebase
        db = FirebaseFirestore.getInstance();

        // Clear button visibility
        btnClearSearch.setVisibility(View.GONE);

        // Clear search when ✕ is clicked
        btnClearSearch.setOnClickListener(v ->
                etSearchAll.setText("")
        );

        // Search text listener
        etSearchAll.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {

                btnClearSearch.setVisibility(
                        s.length() > 0 ? View.VISIBLE : View.GONE
                );

                filterAllItems();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Load data
        loadLostItems();
        loadFoundItems();
    }

    // Load Lost Items
    private void loadLostItems() {

        db.collection("lost_items")
                .get()
                .addOnSuccessListener(snapshot -> {

                    allLostItems.clear();

                    for (QueryDocumentSnapshot document : snapshot) {

                        LostItem item = new LostItem(
                                document.getString("itemName"),
                                document.getString("category"),
                                document.getString("description"),
                                document.getString("location"),
                                document.getString("date"),
                                document.getString("contact")
                        );

                        allLostItems.add(item);
                    }

                    filterAllItems();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Failed to load lost items",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    // Load Found Items
    private void loadFoundItems() {

        db.collection("found_items")
                .get()
                .addOnSuccessListener(snapshot -> {

                    allFoundItems.clear();

                    for (QueryDocumentSnapshot document : snapshot) {

                        FoundItem item = new FoundItem(
                                document.getString("itemName"),
                                document.getString("category"),
                                document.getString("description"),
                                document.getString("location"),
                                document.getString("date"),
                                document.getString("contact")
                        );

                        allFoundItems.add(item);
                    }

                    filterAllItems();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Failed to load found items",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    // Search Lost and Found Items
    private void filterAllItems() {

        String searchText = etSearchAll.getText()
                .toString()
                .trim()
                .toLowerCase(Locale.ROOT);

        filteredLostItems.clear();
        filteredFoundItems.clear();

        // Filter Lost Items
        for (LostItem item : allLostItems) {

            boolean match =
                    searchText.isEmpty()
                            || safeValue(item.getItemName())
                            .toLowerCase(Locale.ROOT).contains(searchText)
                            || safeValue(item.getCategory())
                            .toLowerCase(Locale.ROOT).contains(searchText)
                            || safeValue(item.getLocation())
                            .toLowerCase(Locale.ROOT).contains(searchText)
                            || safeValue(item.getDescription())
                            .toLowerCase(Locale.ROOT).contains(searchText);

            if (match) {
                filteredLostItems.add(item);
            }
        }

        // Filter Found Items
        for (FoundItem item : allFoundItems) {

            boolean match =
                    searchText.isEmpty()
                            || safeValue(item.getItemName())
                            .toLowerCase(Locale.ROOT).contains(searchText)
                            || safeValue(item.getCategory())
                            .toLowerCase(Locale.ROOT).contains(searchText)
                            || safeValue(item.getLocation())
                            .toLowerCase(Locale.ROOT).contains(searchText)
                            || safeValue(item.getDescription())
                            .toLowerCase(Locale.ROOT).contains(searchText);

            if (match) {
                filteredFoundItems.add(item);
            }
        }

        // Refresh lists
        lostAdapter.notifyDataSetChanged();
        foundAdapter.notifyDataSetChanged();

        updateUI();
    }

    // Update result count and visibility
    private void updateUI() {

        int lostCount = filteredLostItems.size();
        int foundCount = filteredFoundItems.size();
        int totalCount = lostCount + foundCount;

        // Show total result count
        tvSearchResultCount.setText(
                "📊 " + totalCount + " results found"
                        + "  •  Lost: " + lostCount
                        + "  •  Found: " + foundCount
        );

        // Lost section
        if (lostCount > 0) {

            tvLostTitle.setVisibility(View.VISIBLE);
            recyclerSearchLost.setVisibility(View.VISIBLE);

            tvLostTitle.setText("🎒 Lost Items (" + lostCount + ")");

        } else {

            tvLostTitle.setVisibility(View.GONE);
            recyclerSearchLost.setVisibility(View.GONE);
        }

        // Found section
        if (foundCount > 0) {

            tvFoundTitle.setVisibility(View.VISIBLE);
            recyclerSearchFound.setVisibility(View.VISIBLE);

            tvFoundTitle.setText("🎁 Found Items (" + foundCount + ")");

        } else {

            tvFoundTitle.setVisibility(View.GONE);
            recyclerSearchFound.setVisibility(View.GONE);
        }

        // Empty state
        if (totalCount == 0) {

            tvNoResults.setVisibility(View.VISIBLE);

            if (etSearchAll.getText().toString().trim().isEmpty()) {
                tvNoResults.setText(
                        "📭 No items available yet."
                );
            } else {
                tvNoResults.setText(
                        "🔍 No matching items found.\nTry another keyword!"
                );
            }

        } else {

            tvNoResults.setVisibility(View.GONE);
        }
    }

    // Handle null values safely
    private String safeValue(String value) {

        return value == null ? "" : value.trim();
    }
}
