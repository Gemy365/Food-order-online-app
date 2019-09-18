package com.example.android.eatitserver.Commons;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.android.eatitserver.Model.Request;
import com.example.android.eatitserver.Model.User;
import com.example.android.eatitserver.Remote.APIService;
import com.example.android.eatitserver.Remote.FCMRetrofitClient;
import com.example.android.eatitserver.Remote.IGeoCoordinates;
import com.example.android.eatitserver.Remote.RetrofitClient;

import retrofit2.Retrofit;

public class Commons {
    public static User currentUser;

    public static Request currentRequest;

    public static final  String UPDATE = "Update";

    public static final  String DELETE = "Delete";

    public static final  String baseUrl = "https://maps.googleapis.com";

    private static final String BASE_URL = "https://fcm.googleapis.com/";

    public static APIService getFCMClient()
    {
        return FCMRetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return "placed";

        else if(status.equals("1"))
            return "On my way";

        else
            return "Shipped";
    }

    // For Map.
    public static IGeoCoordinates getGeoCodeService(){
        return RetrofitClient.getClient(baseUrl).create(IGeoCoordinates.class);
    }

    // For Route On Map.
    public static Bitmap scaleBitamp(Bitmap bitmap, int newWidth, int newHeight){
        Bitmap scaleBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float scaleX = newWidth / (float) bitmap.getWidth();

        float scaleY = newHeight / (float) bitmap.getHeight();

        float pivotX = 0, pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

        Canvas canvas = new Canvas(scaleBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

        return  scaleBitmap;
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
