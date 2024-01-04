package com.mad.localist;

import androidx.annotation.Nullable;

public class GroceryEntry {
    private String name;
    private String quantity;
    private String price;
    private String details;
    private String category;
    private String date;
    private String photoPath;
    private String location;

    public GroceryEntry(String name, String quantity, String price, String details, String date, String photoPath, String location) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.details = details;
        this.date = date;
        this.photoPath = photoPath;
        this.location = location;
    }

    public void setData(GroceryEntry groceryEntry) {
        this.name = groceryEntry.getName();
        this.price = groceryEntry.getPrice();
        this.details = groceryEntry.getDetails();
        this.quantity = groceryEntry.getQuantity();
        this.photoPath = groceryEntry.getPhotoPath();
        this.location = groceryEntry.getLocation();
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

    public String getDetails(){return details;}
    public String getLocation(){return location; }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof GroceryEntry){
            GroceryEntry groceryEntry = (GroceryEntry) obj;
            return groceryEntry.getName().equals(this.getName()) &&
                    groceryEntry.getQuantity().equals(this.getQuantity()) &&
                    groceryEntry.getPrice().equals(this.getPrice()) &&
                    groceryEntry.getDetails().equals(this.getDetails()) &&
                    groceryEntry.getLocation().equals(this.getLocation());
        }
        return false;
    }
}
