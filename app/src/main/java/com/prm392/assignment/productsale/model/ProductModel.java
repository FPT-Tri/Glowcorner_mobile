package com.prm392.assignment.productsale.model;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

public class ProductModel {
    @Getter
    @Setter
    @SerializedName(value = "product_id",alternate = {"id"})
    private long id;

    @Setter
    @Getter
    @SerializedName(value = "product_title",alternate = {"title"})
    private String name;

    @Setter
    @Getter
    @SerializedName(value = "product_title_ar",alternate = {"title_ar"})
    private String nameArabic;

    @Getter
    @SerializedName(value = "product_price",alternate = {"price"})
    private double originalPrice;

    @Getter
    @Setter
    @SerializedName(value = "product_rating",alternate = {"rating"})
    private double rate;

    @Getter
    @Setter
    @SerializedName("sale")
    private int salePercent;

    @Getter
    @Setter
    @SerializedName(value = "product_brand",alternate = {"brand"})
    private String brand;

    @Getter
    @Setter
    @SerializedName(value = "product_category",alternate = {"category"})
    private String category;

    @Getter
    @Setter
    @SerializedName(value = "product_category_ar",alternate = {"category_ar"})
    private String categoryArabic;

    @Setter
    @SerializedName(value = "image_url",alternate = {"image"})
    private String image;

    @SerializedName("is_favourite")
    private int favorite;

    @Getter
    @Setter
    @SerializedName("store_id")
    private long storeId;

    @Getter
    @Setter
    @SerializedName("store_name")
    private String storeName;

    @Getter
    @Setter
    @SerializedName("store_type")
    private String storeType;

    @Setter
    @SerializedName("store_logo")
    private String storeLogo;

    @Getter
    @Setter
    @SerializedName("store_latitude")
    private Double storeLatitude;

    @Getter
    @SerializedName("store_longitude")
    private Double storeLongitude;

    @Getter
    @Setter
    @SerializedName(value = "viewed_at",alternate = {"favourite_date"})
    private String date;

    @Getter
    @Setter
    @SerializedName("description")
    private String description;

    @Getter
    @Setter
    @SerializedName("description_ar")
    private String descriptionArabic;

    public static final String ONLINE_STORE = "online";

    public static final String LOCAL_STORE = "local";

    public double getPrice() {
        return Double.parseDouble(String.format("%.2f",originalPrice-(originalPrice*salePercent/100)));
    }

    public void setPrice(double price) {
        this.originalPrice = Double.parseDouble(String.format("%.2f",price));
    }

    public String getImage() {
        if(image == null) return "";
        return image.replace("http:","https:");
    }

    public void setFavorite(boolean favorite) {
        if(favorite) this.favorite = 1;
        else this.favorite = 0;
    }

    public boolean isFavorite() {
        return favorite == 1;
    }

    public String getStoreLogo() {
        if(storeLogo==null) return "";
        return storeLogo.replace("http:","https:");
    }

}
