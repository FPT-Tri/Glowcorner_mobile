package com.example.mobile.Models;

import java.util.List;

public class Product {
    private String productID;
    private String productName;
    private String description;
    private double price;
    private double discountedPrice;
    private List<String> skinTypes;
    private String category;
    private double rating;
    private String image_url;
    private String status;

    // Getters
    public String getProductID() { return productID; }
    public String getProductName() { return productName; }
    public double getPrice() { return price; }
    public List<String> getSkinTypes() { return skinTypes; }
    public String getCategory() { return category; }
    public double getRating() { return rating; }
    public String getImageUrl() { return image_url; }

    public Product(String productID, String productName, String description, double price,
                   String imageUrl, String category, double rating) {
        this.productID = productID;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.image_url = imageUrl;
        this.category = category;
        this.rating = rating;
    }

}
