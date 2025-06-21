package com.prm392.assignment.productsale.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;

import com.prm392.assignment.productsale.data.remote.RetrofitClient;
import com.prm392.assignment.productsale.data.service.ProductSaleService;
import com.prm392.assignment.productsale.model.BaseResponseModel;
import com.prm392.assignment.productsale.model.cart.AddProductCartModel;
import com.prm392.assignment.productsale.model.cart.CartTotalResponse;
import com.prm392.assignment.productsale.model.products.ProductSalePageResponseModel;
import com.prm392.assignment.productsale.model.products.ProductsSaleResponseModel;

import java.util.List;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProductsSaleRepository {

    // Headers
    private static final String AUTHORIZATION = "Authorization";
    private final Retrofit mainClient;

    public ProductsSaleRepository() {
        mainClient = RetrofitClient.getMainInstance();
    }

    public LiveData<Response<ProductsSaleResponseModel>> getProducts(String token, Integer pageIndex, Integer pageSize, String search, String sortBy, Boolean sortDescending, Double minPrice, Double maxPrice, List<Integer> categoryIds) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(ProductSaleService.class)
                        .getProducts(token, pageIndex, pageSize, search, sortBy, sortDescending, minPrice, maxPrice, categoryIds)
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
    public LiveData<Response<ProductSalePageResponseModel>> getProductSale(String token, long productId) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(ProductSaleService.class)
                        .getProductSale(token, productId)
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

    public LiveData<Response<BaseResponseModel>> addProductToCart(String token, AddProductCartModel addProductCartModel) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(ProductSaleService.class)
                        .addToCart(token, addProductCartModel)
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
