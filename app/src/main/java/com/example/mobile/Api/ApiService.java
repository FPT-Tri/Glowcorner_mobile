package com.example.mobile.Api;


import com.example.mobile.Models.LoginRequest;
import com.example.mobile.Models.Product;
import com.example.mobile.Models.ProductResponse;
import com.example.mobile.Models.RegisterRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("auth/login")
    Call<ApiResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/register")
    Call<ApiResponse> register(@Body RegisterRequest registerRequest);

    @GET("api/products")
    Call<ProductResponse> getProducts();
}
