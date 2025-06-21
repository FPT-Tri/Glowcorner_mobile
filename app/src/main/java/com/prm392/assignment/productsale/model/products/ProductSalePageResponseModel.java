package com.prm392.assignment.productsale.model.products;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSalePageResponseModel {
    @SerializedName("product")
    private ProductSaleModel product;

    @SerializedName("storeLocation")
    private StoreLocation storeLocation;
}
