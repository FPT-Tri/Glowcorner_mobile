package com.prm392.assignment.productsale.data.remote;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String mainClientUrl = "http://localhost:8080/";
//    private static final String mainClientUrl = "http://10.0.2.2:5120/api/";
//    private static final String mainClientUrl = "https://productsales-fda2fmhsdzd4dsa2.southeastasia-01.azurewebsites.net/api/";
    private static Retrofit mainClient;

    private RetrofitClient() { // apply singleton pattern
    }

    public static Retrofit getMainInstance() {
        if (mainClient == null) {
            mainClient = new Retrofit.Builder()
                    .baseUrl(mainClientUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build();
        }

        return mainClient;
    }

}
