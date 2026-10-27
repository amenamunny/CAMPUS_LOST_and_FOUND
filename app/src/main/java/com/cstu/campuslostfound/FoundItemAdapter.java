package com.cstu.campuslostfound;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FoundItemAdapter
        extends RecyclerView.Adapter<FoundItemAdapter.FoundItemViewHolder> {

    private List<FoundItem> foundItemList;

    public FoundItemAdapter(List<FoundItem> foundItemList) {
        this.foundItemList = foundItemList;
    }

    @NonNull
    @Override
    public FoundItemViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_found, parent, false);

        return new FoundItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull FoundItemViewHolder holder,
            int position) {

        FoundItem item = foundItemList.get(position);

        holder.tvFoundItemName.setText(
                safeValue(item.getItemName()));

        holder.tvFoundCategory.setText(
                "Category: " + safeValue(item.getCategory()));

        holder.tvFoundLocation.setText(
                "Found Location: " + safeValue(item.getLocation()));

        holder.tvFoundDate.setText(
                "Found Date: " + safeValue(item.getDate()));

        // Display the current item status
        String status = item.getStatus();

        if ("Resolved".equalsIgnoreCase(status)) {

            holder.tvFoundItemStatus.setText("✅ Resolved");

        } else {

            holder.tvFoundItemStatus.setText("🟢 Active");
        }

        holder.tvFoundItemStatus.setTextColor(
                Color.parseColor("#1F5C45"));

        holder.tvFoundItemStatus.setBackgroundColor(
                Color.parseColor("#EAF3EB"));

        // Open Item Details when clicked
        holder.itemView.setOnClickListener(v -> {

            Context context = v.getContext();

            Intent intent = new Intent(
                    context,
                    ItemDetailsActivity.class
            );

            intent.putExtra("itemName", item.getItemName());
            intent.putExtra("category", item.getCategory());
            intent.putExtra("description", item.getDescription());
            intent.putExtra("location", item.getLocation());
            intent.putExtra("date", item.getDate());
            intent.putExtra("contact", item.getContact());
            intent.putExtra("itemType", "Found");

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return foundItemList.size();
    }

    // Handle missing values
    private String safeValue(String value) {

        if (value == null || value.trim().isEmpty()) {
            return "Not specified";
        }

        return value;
    }

    public static class FoundItemViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvFoundItemName;
        TextView tvFoundCategory;
        TextView tvFoundLocation;
        TextView tvFoundDate;
        TextView tvFoundItemStatus;

        public FoundItemViewHolder(
                @NonNull View itemView) {

            super(itemView);

            tvFoundItemName =
                    itemView.findViewById(R.id.tvFoundItemName);

            tvFoundCategory =
                    itemView.findViewById(R.id.tvFoundCategory);

            tvFoundLocation =
                    itemView.findViewById(R.id.tvFoundLocation);

            tvFoundDate =
                    itemView.findViewById(R.id.tvFoundDate);

            // New status TextView
            tvFoundItemStatus =
                    itemView.findViewById(R.id.tvFoundItemStatus);
        }
    }
}
