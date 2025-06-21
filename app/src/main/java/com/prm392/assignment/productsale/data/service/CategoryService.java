package com.prm392.assignment.productsale.data.service;

import com.prm392.assignment.productsale.model.categories.CategoriesResponseModel;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface CategoryService {

    @Headers({"client: mobile"})
    @GET("category")
    Observable<Response<CategoriesResponseModel>> getCategories();
}
