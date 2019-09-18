package com.example.android.eatit.Remote;



import com.example.android.eatit.Model.MyResponse;
import com.example.android.eatit.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface APIService {
    @Headers(
            {
                    // Type Of Application.
                    "Content-Type:application/json",
//                    Authorization:key= From Firebase Project Overview /Project Settings / Cloud Messaging / Copy Server Key.
                    "Authorization:key=AAAARuWv1yE:APA91bFuB0IOkkRsBc-VFSZ9S7HGJy8flSD-g_kRSuWoFNcYSpncZV1EUFFTmeuO68M7nbYJeoiXcpjIoU548bxxIXYVEw3gi5316uoR35oulsdsamp6icWcwHt2ZpX5SDc6HbUiFqe8r20m1OE7QGKnnC0m-Cnc6g"

            }

    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
