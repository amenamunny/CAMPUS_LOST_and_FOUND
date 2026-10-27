package com.cstu.campuslostfound;

public class LostItem {

    private String itemName;
    private String category;
    private String description;
    private String location;
    private String date;
    private String contact;
    private String status;

    // Constructor
    public LostItem(String itemName, String category,
                    String description, String location,
                    String date, String contact) {

        this.itemName = itemName;
        this.category = category;
        this.description = description;
        this.location = location;
        this.date = date;
        this.contact = contact;
        this.status = "Active";
    }

    // Get item name
    public String getItemName() {
        return itemName;
    }

    // Get category
    public String getCategory() {
        return category;
    }

    // Get description
    public String getDescription() {
        return description;
    }

    // Get location
    public String getLocation() {
        return location;
    }

    // Get date
    public String getDate() {
        return date;
    }

    // Get contact
    public String getContact() {
        return contact;
    }

    // Get status
    public String getStatus() {
        return status;
    }

    // Set status
    public void setStatus(String status) {
        this.status = status;
    }
}
