package com.mad.localist;

public class GroceryEntry {
    private String name;
    private String quantity;
    private String price;
    private String category;
    private String date;
    private String photoPath;
    private String lattitude;
    private String longitude;

    public GroceryEntry(String name, String quantity, String price, String category, String date, String photoPath) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.category = category;
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getName() {
        return name;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public String getPhotoPath() {
        return photoPath;
    }
}
