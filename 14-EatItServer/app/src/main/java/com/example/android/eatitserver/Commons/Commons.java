package com.example.android.eatitserver.Commons;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.example.android.eatitserver.Model.Request;
import com.example.android.eatitserver.Model.User;
import com.example.android.eatitserver.Remote.IGeoCoordinates;
import com.example.android.eatitserver.Remote.RetrofitClient;

import retrofit2.Retrofit;

public class Commons {
    public static User currentUser;

    public static Request currentRequest;

    public static final  String UPDATE = "Update";

    public static final  String DELETE = "Delete";

    public static final  String baseUrl = "https://maps.googleapis.com";

    public static String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return "placed";

        else if(status.equals("1"))
            return "On my way";

        else
            return "Shipped";
    }

    public static IGeoCoordinates getGeoCodeService(){
        return RetrofitClient.getClient(baseUrl).create(IGeoCoordinates.class);
    }

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
}
