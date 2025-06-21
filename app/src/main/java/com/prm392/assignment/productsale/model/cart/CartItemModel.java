package com.prm392.assignment.productsale.model.cart;

import com.google.gson.annotations.SerializedName;
import com.prm392.assignment.productsale.model.products.ProductSaleModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemModel {
    @SerializedName(value = "cartItemId")
    private int cartItemId;

    @SerializedName(value = "cartId")
    private int cartId;

    @SerializedName(value = "productId")
    private int productId;

    @SerializedName(value = "quantity")
    private int quantity;

    @SerializedName(value = "price")
    private float price;

    @SerializedName(value = "product")
    private ProductSaleModel product;
}


