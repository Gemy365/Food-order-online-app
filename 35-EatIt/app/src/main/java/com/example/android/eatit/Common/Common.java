package com.example.android.eatit.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import com.example.android.eatit.Model.User;
import com.example.android.eatit.Remote.APIService;
import com.example.android.eatit.Remote.IGoogleService;
import com.example.android.eatit.Remote.RetrofitClient;

public class Common {
    // Store The Current User Into [Static] User To Use In Every Where In This Project.
    public static User currentUser;

    private static final String BASE_URL = "https://fcm.googleapis.com/";

    private static final String GOOGLE_API_URL = "https://maps.googleapis.com/";

    // This For Notification.
    public static APIService getFCMService()
    {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    // This For Location.
    public static IGoogleService getGoogleMapAPI()
    {
        return RetrofitClient.getGoogleClient(GOOGLE_API_URL).create(IGoogleService.class);
    }

    public static final  String DELETE = "Delete";

    public static final  String USER_KEY = "User";

    public static final  String PWD_KEY = "Password";

    public static String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return "placed";

        else if(status.equals("1"))
            return "On my way";

        else
            return "Shipped";
    }

    // Method To Check InterNet.
    public  static boolean isConnectedToInternet(Context context){

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null)
        {
            NetworkInfo info[] = connectivityManager.getAllNetworkInfo();

            if(info != null){

                // Infinity info, When Network Is Connected, & Always Return True.
                for(int i = 0; i < info.length; i++){
                    if(info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        // If No Connection.
        return false;
    }
}
