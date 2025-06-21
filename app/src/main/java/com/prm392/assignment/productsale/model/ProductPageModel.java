package com.prm392.assignment.productsale.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

public class ProductPageModel {
    @Setter
    @Getter
    @SerializedName("basic")
    private MainInfo mainInfo;

    @SerializedName("is_favourite")
    private int favorite;

    @Setter
    @Getter
    @SerializedName("user_rating")
    private int userRating;

    @Setter
    @Getter
    @SerializedName("images")
    private ArrayList<ProductImage> images;

    @Setter
    @Getter
    @SerializedName("prices")
    private ArrayList<ProductPrice> prices;

    @Setter
    @Getter
    @SerializedName("rating")
    private ProductRating productRating;

    @Setter
    @Getter
    @SerializedName("views")
    private ProductViews views;

    @Setter
    @Getter
    @SerializedName("store")
    private Store store;

    public static class MainInfo{
        private final String WEBSITE_URL = "https://sale-hunter.vercel.app/";

        @Setter
        @Getter
        @SerializedName("product_id")
        private long id;

        @Setter
        @Getter
        @SerializedName("product_title")
        private String name;

        @Setter
        @Getter
        @SerializedName("product_title_ar")
        private String nameArabic;

        @Setter
        @Getter
        @SerializedName("product_description")
        private String description;

        @Setter
        @Getter
        @SerializedName("product_description_ar")
        private String descriptionArabic;

        @Setter
        @Getter
        @SerializedName("product_sale")
        private int salePercent;

        @Setter
        @Getter
        @SerializedName("product_brand")
        private String brand;

        @Setter
        @Getter
        @SerializedName("product_category")
        private String category;

        @Setter
        @Getter
        @SerializedName("product_category_ar")
        private String categoryArabic;

        @Setter
        @SerializedName("product_url")
        private String url;


        public String getSourceUrl() {
            return url;
        }

        public String getShareableUrl(){
            return WEBSITE_URL+"pid="+id;
        }
    }

    @Setter
    public static class ProductImage{
        @Getter
        @SerializedName("img_id")
        private long imageId;

        @SerializedName("img_url")
        private String imageUrl;

        public String getImageUrl() {
            return imageUrl.replace("http:","https:");
        }

    }

    @Setter
    @Getter
    public static class ProductPrice{
        @SerializedName("price")
        private double price;

        @SerializedName("created_at")
        private String creationDate;

    }

    @Setter
    @Getter
    public static class ProductViews{
        @SerializedName("number_of_views")
        private int count;

    }

    @Setter
    public static class ProductRating{
        @Getter
        @SerializedName("number_of_ratings")
        private int count;

        @SerializedName("rating_average")
        private String rating;

        public String getRating() {
            if(rating==null) rating = "0.0";
            return rating;
        }

    }

    @Setter
    public static class Store{
        @Getter
        @SerializedName("store_id")
        private long storeId;

        @Getter
        @SerializedName("store_name")
        private String storeName;

        @Getter
        @SerializedName("store_type")
        private String storeType;

        @SerializedName("store_logo")
        private String storeLogo;

        @Getter
        @SerializedName("store_latitude")
        private Double storeLatitude;

        @Getter
        @SerializedName("store_longitude")
        private Double storeLongitude;

        public String getStoreLogo() {
            return storeLogo.replace("http:","https:");
        }

    }

    public void setFavorite(boolean favorite) {
        if(favorite) this.favorite = 1;
        else this.favorite = 0;
    }

    public boolean isFavorite() {
        return favorite != 0;
    }

}
