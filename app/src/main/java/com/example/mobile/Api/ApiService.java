package com.example.mobile.Api;


import com.example.mobile.Models.AddToCartRequest;
import com.example.mobile.Models.AddToCartResponse;
import com.example.mobile.Models.CartResponse;
import com.example.mobile.Models.LoginRequest;
import com.example.mobile.Models.OrderResponse;
import com.example.mobile.Models.Product;
import com.example.mobile.Models.ProductResponse;
import com.example.mobile.Models.QuizResponse;
import com.example.mobile.Models.RegisterRequest;
import com.example.mobile.Models.RoutineResponse;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("auth/login")
    Call<ApiResponse> login(@Body LoginRequest loginRequest);
    @POST("auth/register")
    Call<ApiResponse> register(@Body RegisterRequest registerRequest);

    // Get products
    @GET("api/products")
    Call<ProductResponse> getProducts();
    @GET("api/customer/products/skinType/{skinType}")
    Call<ProductResponse> getProductsBySkinType(@Path("skinType") String skinType);
    @GET("api/customer/products/category/{category}")
    Call<ProductResponse> getProductsByCategory(@Path("category") String category);
    @GET("api/customer/products/{productID}")
    Call<ProductResponse> getProductById(@Path("productID") String productID);
    @GET("api/customer/products/name/{productName}")
    Call<ProductResponse> getProductsByName(@Path("productName") String productName);

    // In ApiService.java
    // Add to cart
    @POST("api/cart/{userID}/add/{productID}")
    Call<ResponseBody> addToCart(@Path("userID") String userID, @Path("productID") String productID, @Query("quantity") int quantity);

    // Show cart
    @GET("api/cart/{userID}") // Adjust endpoint as needed
    Call<CartResponse> getCart(@Path("userID") String userID);

    // Get user profile
    @GET("api/user/{userID}")
    Call<ResponseBody> getUserProfile(@Path("userID") String userID);

    @POST("/api/orders/customer/{userID}/create")
    Call<OrderResponse> createOrder(@Header("Authorization") String token, @Path("userID") String userID, @Body JsonObject orderData);
    // Get orders by user ID
    @GET("api/orders/customer/{userID}")
    Call<ResponseBody> getCustomerOrders(@Header("Authorization") String token, @Path("userID") String userID);
    @GET("api/orders/staff/{orderId}")
    Call<ResponseBody> getOrderDetails(@Path("orderId") String orderId);

    // Get Quizzes
    @GET("api/quizzes")
    Call<ResponseBody> getQuizzes();
    // Get Skintype by Quiz ID
    @GET("api/skin-care-routines")
    Call<ResponseBody> getSkinCareRoutines();
    @GET("api/skin-care-routines/{routineId}")
    Call<RoutineResponse> getRoutineById(@Path("routineId") String routineId);
    @POST("api/skin-care-routines/{routineId}/apply-to-user/{userId}")
    Call<RoutineResponse> applyRoutineToUser(
            @Path("routineId") String routineId,
            @Path("userId") String userId
    );
}
