package com.example.android.eatit.Remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

// This For Notification.
public class RetrofitClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient(String baseURL){

        if (retrofit == null){

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // This For Location.
    private static Retrofit retrofit2 = null; //*

    public static Retrofit getGoogleClient(String baseURL){

        if (retrofit2 == null){

            retrofit2 = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofit2;
    }
}
