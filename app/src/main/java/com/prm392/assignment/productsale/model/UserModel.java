package com.prm392.assignment.productsale.model;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

public class UserModel {

    private final String image;
    @Setter
    @Getter
    @SerializedName("userId")
    private int id;

    @Setter
    @Getter
    @SerializedName("username")
    private String userName;

    @Setter
    @Getter
    @SerializedName("email")
    private String email;
    @Setter
    @Getter
    @SerializedName("phoneNumber")
    private String phone;
    @Setter
    @Getter
    @SerializedName("address")
    private String address;
    @Setter
    @Getter
    @SerializedName("role")
    private String role;

    @Setter
    @Getter
    @SerializedName("lastSeen")
    private String lastSeen;

    public UserModel() {
        image = "https://img.freepik.com/free-vector/blue-circle-with-white-user_78370-4707.jpg";
    }

    public String getImageLink() {
        return image;
    }

    public boolean hasStore() {
        return role.equals("Admin");
    }

    public String getAccountType() {
        return role;
    }
}
