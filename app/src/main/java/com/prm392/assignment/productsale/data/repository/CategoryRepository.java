package com.prm392.assignment.productsale.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;

import com.prm392.assignment.productsale.data.remote.RetrofitClient;
import com.prm392.assignment.productsale.data.service.CategoryService;
import com.prm392.assignment.productsale.model.BaseResponseModel;
import com.prm392.assignment.productsale.model.categories.CategoriesResponseModel;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CategoryRepository {
    private final Retrofit mainClient;

    public CategoryRepository() {
        mainClient = RetrofitClient.getMainInstance();
    }

    public LiveData<Response<CategoriesResponseModel>> getCategories() {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(CategoryService.class)
                        .getCategories()
                        .subscribeOn(Schedulers.io())
                        .doOnComplete(() -> Log.d("CategoryRepository", "Complete"))
                        .doOnNext(response -> {
                            if (response.code() == 200) {
                                Log.d("CategoryRepository", "Success");
                            } else {
                                Log.d("CategoryRepository", "Failed");
                            }
                        })
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
