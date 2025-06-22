package com.example.mobile.Api;


import com.example.mobile.Models.AddToCartRequest;
import com.example.mobile.Models.AddToCartResponse;
import com.example.mobile.Models.CartResponse;
import com.example.mobile.Models.LoginRequest;
import com.example.mobile.Models.Product;
import com.example.mobile.Models.ProductResponse;
import com.example.mobile.Models.RegisterRequest;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("auth/login")
    Call<ApiResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/register")
    Call<ApiResponse> register(@Body RegisterRequest registerRequest);

    @GET("api/products")
    Call<ProductResponse> getProducts();

    // In ApiService.java
    @POST("api/cart/{userID}/add/{productID}")
    Call<ResponseBody> addToCart(@Path("userID") String userID, @Path("productID") String productID, @Body AddToCartRequest request);

    @GET("api/cart/{userID}") // Adjust endpoint as needed
    Call<CartResponse> getCart(@Path("userID") String userID);
}
