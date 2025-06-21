package com.prm392.assignment.productsale.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductsResponseModel extends BaseResponseModel {

    @SerializedName("results")
    private int resultsCount;

    @SerializedName("products")
    private ArrayList<ProductModel> products;

}
