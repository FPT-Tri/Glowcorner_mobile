package com.prm392.assignment.productsale.data.service;

import com.prm392.assignment.productsale.model.SignInModel;
import com.prm392.assignment.productsale.model.SignUpModel;
import com.prm392.assignment.productsale.model.UserResponseModel;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AuthService {
    @Headers({"client: mobile"})
    @POST("Auth/login")
    Observable<Response<UserResponseModel>> signIn(@Body SignInModel signInModel);

    @Headers({"client: mobile"})
    @POST("Auth/register")
    Observable<Response<UserResponseModel>> signUp(@Body SignUpModel signUpModel);
}
