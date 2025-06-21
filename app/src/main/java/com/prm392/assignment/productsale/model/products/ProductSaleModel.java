package com.prm392.assignment.productsale.model.products;

import com.google.gson.annotations.SerializedName;
import com.prm392.assignment.productsale.model.categories.CategoryModel;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductSaleModel {

    @SerializedName(value = "productId")
    private long productId;

    @SerializedName(value = "productName")
    private String productName;

    @SerializedName(value = "briefDescription")
    private String briefDescription;

    @SerializedName(value = "fullDescription")
    private String fullDescription;

    @SerializedName(value = "technicalSpecifications")
    private String technicalSpecifications;

    @SerializedName(value = "price")
    private float price;

    @SerializedName(value = "imageUrl")
    private String imageUrl;

    @SerializedName(value = "categoryName")
    private String categoryName;

    @SerializedName(value = "category")
    private CategoryModel category;

    public String getCurrencyPrice() {
        return price + "$";
    }

    public String getProductName() { return productName; }

    public String getProductImage() { return imageUrl; }
}
