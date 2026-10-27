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

public class LostItemAdapter
        extends RecyclerView.Adapter<LostItemAdapter.LostItemViewHolder> {

    private List<LostItem> lostItemList;

    public LostItemAdapter(List<LostItem> lostItemList) {
        this.lostItemList = lostItemList;
    }

    @NonNull
    @Override
    public LostItemViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lost, parent, false);

        return new LostItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull LostItemViewHolder holder, int position) {

        LostItem item = lostItemList.get(position);

        holder.tvItemName.setText(safeValue(item.getItemName()));
        holder.tvCategory.setText(
                "Category: " + safeValue(item.getCategory()));
        holder.tvLocation.setText(
                "Lost Location: " + safeValue(item.getLocation()));
        holder.tvDate.setText(
                "Lost Date: " + safeValue(item.getDate()));

        // Display item status
        String status = item.getStatus();

        if ("Resolved".equalsIgnoreCase(status)) {

            holder.tvItemStatus.setText("✅ Resolved");
            holder.tvItemStatus.setTextColor(
                    Color.parseColor("#1F5C45"));
            holder.tvItemStatus.setBackgroundColor(
                    Color.parseColor("#EAF3EB"));

        } else {

            holder.tvItemStatus.setText("🟢 Active");
            holder.tvItemStatus.setTextColor(
                    Color.parseColor("#1F5C45"));
            holder.tvItemStatus.setBackgroundColor(
                    Color.parseColor("#EAF3EB"));
        }

        // Open Item Details when card is clicked
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
            intent.putExtra("itemType", "Lost");

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lostItemList.size();
    }

    // Handle null or empty values
    private String safeValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Not specified";
        }
        return value;
    }

    public static class LostItemViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvItemName;
        TextView tvCategory;
        TextView tvLocation;
        TextView tvDate;
        TextView tvItemStatus;

        public LostItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDate = itemView.findViewById(R.id.tvDate);

            // New status TextView
            tvItemStatus = itemView.findViewById(R.id.tvItemStatus);
        }
    }
}
