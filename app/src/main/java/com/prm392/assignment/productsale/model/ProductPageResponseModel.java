package com.prm392.assignment.productsale.model;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductPageResponseModel extends BaseResponseModel {

    @SerializedName("product")
    private ProductPageModel product;

}
