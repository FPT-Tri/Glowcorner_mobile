package com.prm392.assignment.productsale.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;

import com.prm392.assignment.productsale.data.remote.RetrofitClient;
import com.prm392.assignment.productsale.data.service.ProductSaleService;
import com.prm392.assignment.productsale.model.BaseResponseModel;
import com.prm392.assignment.productsale.model.cart.CartModel;
import com.prm392.assignment.productsale.model.cart.CartTotalResponse;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CartRepository {

    private final Retrofit mainClient;

    // Headers
    private static final String AUTHORIZATION = "Authorization";

    public CartRepository() {
        mainClient = RetrofitClient.getMainInstance();
    }

    public LiveData<Response<CartModel>> getCart(String token, int userId) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(ProductSaleService.class)
                        .getCart(token, userId)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel>> removeItemFromCart(String token, int userId, int productId) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(ProductSaleService.class)
                        .removeItemFromCart(token, userId, productId)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel>> updateCartItemQuantity(String token, int userId, int productId, int quantity) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(ProductSaleService.class)
                        .updateCartItemQuantity(token, userId, productId, quantity) // Call the service method
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel>> clearCart(String token, int userId) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(ProductSaleService.class)
                        .clearCart(token, userId) // Gọi API clearCart
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<CartTotalResponse>> getCartTotal(String token, int userId) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(ProductSaleService.class)
                        .getCartTotal(token, userId)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel>> completePaymentAndConvertCartToOrder(
            String token, int userId, String paymentMethod, String billingAddress) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(ProductSaleService.class)
                        .completePaymentAndConvertCartToOrder(token, userId, paymentMethod, billingAddress)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }
}
