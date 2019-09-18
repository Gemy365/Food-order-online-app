package com.example.android.eatit.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import com.example.android.eatit.Model.User;

public class Common {
    // Store The Current User.
    public static User currentUser;

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
