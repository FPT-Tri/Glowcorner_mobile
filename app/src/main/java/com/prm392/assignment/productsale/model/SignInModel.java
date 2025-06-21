package com.prm392.assignment.productsale.model;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignInModel {

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

}
