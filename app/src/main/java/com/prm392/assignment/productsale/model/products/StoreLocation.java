package com.prm392.assignment.productsale.model.products;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

@Getter
public class StoreLocation {
    @SerializedName(value = "locationId")
    private String locationId;
    @SerializedName(value = "latitude")
    private Double latitude;
    @SerializedName(value = "longitude")
    private Double longitude;
    @SerializedName(value = "address")
    private String address;
}
