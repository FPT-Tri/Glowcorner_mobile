package com.example.mobile.Models;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

public class Product {
    @SerializedName("productID")
    private String productID;

    @SerializedName("productName")
    private String productName;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private double price;

    @SerializedName("discountedPrice")
    private Double discountedPrice;

    @SerializedName("skinTypes")
    private List<String> skinTypes;

    @SerializedName("category")
    private String category;

    @SerializedName("rating")
    private double rating;

    @SerializedName("image_url")
    private String image_url;

    @SerializedName("status")
    private String status;

    // Constructor for backward compatibility
    public Product(String productID, String productName, String description, double price,
                   String image_url, String category, double rating) {
        this.productID = productID;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.image_url = image_url;
        this.category = category;
        this.rating = rating;
        this.discountedPrice = null;
        this.skinTypes = Collections.emptyList();
        this.status = "";
    }

    // Constructor to match EditProductActivity usage
    public Product(String productID, String productName, String description, float price,
                   String category, List<String> skinTypes, float rating, String imageUrl) {
        this.productID = productID;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.category = category;
        this.skinTypes = skinTypes != null ? skinTypes : Collections.emptyList();
        this.rating = rating;
        this.image_url = imageUrl;
        this.discountedPrice = null;
        this.status = "";
    }

    // Full constructor
    public Product(String productID, String productName, String description, double price,
                   Double discountedPrice, List<String> skinTypes, String category,
                   double rating, String image_url, String status) {
        this.productID = productID;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.discountedPrice = discountedPrice;
        this.skinTypes = skinTypes != null ? skinTypes : Collections.emptyList();
        this.category = category;
        this.rating = rating;
        this.image_url = image_url;
        this.status = status;
    }

    // Getters with null checks
    public String getProductID() { return productID != null ? productID : ""; }
    public String getProductName() { return productName != null ? productName : ""; }
    public String getDescription() { return description != null ? description : ""; }
    public double getPrice() { return price; }
    public Double getDiscountedPrice() { return discountedPrice; }
    public List<String> getSkinTypes() { return skinTypes != null ? skinTypes : Collections.emptyList(); }
    public String getCategory() { return category != null ? category : ""; }
    public double getRating() { return rating; }
    public String getImageUrl() { return image_url != null ? image_url : ""; }
    public String getStatus() { return status != null ? status : ""; }
}