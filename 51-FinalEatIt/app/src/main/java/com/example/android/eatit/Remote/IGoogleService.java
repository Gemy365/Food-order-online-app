package com.example.android.eatit.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

// For Location.
public interface IGoogleService {
    @GET
    Call<String> getAddressName(@Url String url);
}
