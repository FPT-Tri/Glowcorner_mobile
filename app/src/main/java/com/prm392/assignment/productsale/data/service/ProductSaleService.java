package com.prm392.assignment.productsale.data.service;

import com.prm392.assignment.productsale.model.BaseResponseModel;
import com.prm392.assignment.productsale.model.cart.AddProductCartModel;
import com.prm392.assignment.productsale.model.cart.CartModel;
import com.prm392.assignment.productsale.model.cart.CartTotalResponse;
import com.prm392.assignment.productsale.model.products.ProductSalePageResponseModel;
import com.prm392.assignment.productsale.model.products.ProductsSaleResponseModel;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductSaleService {

//    @Headers({"client: mobile"})
//    @GET("product/demo")
//    Observable<Response<ProductsSaleResponseModel>> getDemoProducts(@Header("Authorization") String token);

    @Headers({"client: mobile"})
    @GET("product")
    Observable<Response<ProductsSaleResponseModel>> getProducts(@Header("Authorization") String token,
                                                                @Query("pageIndex") Integer pageIndex,
                                                                @Query("pageSize") Integer pageSize,
                                                                @Query("search") String search,
                                                                @Query("sortBy") String sortBy,
                                                                @Query("sortDescending") Boolean sortDescending,
                                                                @Query("minPrice") Double minPrice,
                                                                @Query("maxPrice") Double maxPrice,
                                                                @Query("categoryIds") List<Integer> categoryIds);
    @GET("product/demo")
    Observable<Response<ProductsSaleResponseModel>> getDemoProducts(@Header("Authorization") String token);

    @Headers({"client: mobile"})
    @GET("product/{productId}")
    Observable<Response<ProductSalePageResponseModel>> getProductSale(@Header("Authorization") String token, @Path("productId") long productId);

    @Headers({"client: mobile"})
    @POST("cart")
    Observable<Response<BaseResponseModel>> addToCart(@Header("Authorization") String token, @Body AddProductCartModel addProductCartModel);
    @Headers({"client: mobile"})
    @GET("cart/{userId}")
    Observable<Response<CartModel>> getCart(@Header("Authorization") String token, @Path("userId") int userId);

    @Headers({"client: mobile"})
    @DELETE("cart/remove-item")
    Observable<Response<BaseResponseModel>> removeItemFromCart(@Header("Authorization") String token, @Query("userId") int userId, @Query("productId") int productId);

    @Headers({"client: mobile"})
    @PUT("cart/update-item")
    Observable<Response<BaseResponseModel>> updateCartItemQuantity(
            @Header("Authorization") String token,
            @Query("userId") int userId,
            @Query("productId") int productId,
            @Query("quantity") int quantity
    );

    @Headers({"client: mobile"})
    @DELETE("cart/clear")
    Observable<Response<BaseResponseModel>> clearCart(
            @Header("Authorization") String token,
            @Query("userId") int userId
    );

    @Headers({"client: mobile"})
    @GET("cart/total")
    Observable<Response<CartTotalResponse>> getCartTotal(@Header("Authorization") String token, @Query("userId") int userId);

    @Headers({"client: mobile"})
    @POST("cart/complete-payment")
    Observable<Response<BaseResponseModel>> completePaymentAndConvertCartToOrder(
            @Header("Authorization") String token,
            @Query("userId") int userId,
            @Query("PaymentMethod") String paymentMethod,
            @Query("BillingAddress") String billingAddress
    );
}
