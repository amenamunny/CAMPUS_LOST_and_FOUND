package com.cstu.campuslostfound;

public class FoundItem {

    private String itemName;
    private String category;
    private String description;
    private String location;
    private String date;
    private String contact;
    private String status;

    // Constructor
    public FoundItem(String itemName,
                     String category,
                     String description,
                     String location,
                     String date,
                     String contact) {

        this.itemName = itemName;
        this.category = category;
        this.description = description;
        this.location = location;
        this.date = date;
        this.contact = contact;
        this.status = "Active";
    }

    public String getItemName() {
        return itemName;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getContact() {
        return contact;
    }

    // Get current status
    public String getStatus() {
        return status;
    }

    // Update status
    public void setStatus(String status) {
        this.status = status;
    }
}
