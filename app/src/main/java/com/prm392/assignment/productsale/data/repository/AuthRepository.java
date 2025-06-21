package com.prm392.assignment.productsale.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;

import com.prm392.assignment.productsale.data.remote.RetrofitClient;
import com.prm392.assignment.productsale.data.service.AuthService;
import com.prm392.assignment.productsale.model.BaseResponseModel;
import com.prm392.assignment.productsale.model.SignInModel;
import com.prm392.assignment.productsale.model.SignUpModel;
import com.prm392.assignment.productsale.model.UserResponseModel;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AuthRepository {

    private final Retrofit mainClient;

    public AuthRepository() {
        mainClient = RetrofitClient.getMainInstance();
    }

    public LiveData<Response<UserResponseModel>> signIn(SignInModel signInModel) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(AuthService.class)
                        .signIn(signInModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            MediaType mediaType = MediaType.parse("application/json");
                            ResponseBody responseBody = ResponseBody.create(mediaType, "");

                            if (exception instanceof HttpException) {
                                return Response.error(((HttpException) exception).code(), responseBody);
                            }

                            return Response.error(
                                    BaseResponseModel.FAILED_REQUEST_FAILURE, responseBody);
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<UserResponseModel>> signUp(SignUpModel signUpModel) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(AuthService.class)
                        .signUp(signUpModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            MediaType mediaType = MediaType.parse("application/json");
                            ResponseBody responseBody = ResponseBody.create(mediaType, "");

                            if (exception instanceof HttpException) {
                                return Response.error(((HttpException) exception).code(), responseBody);
                            }

                            return Response.error(
                                    BaseResponseModel.FAILED_REQUEST_FAILURE, responseBody);
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

}
