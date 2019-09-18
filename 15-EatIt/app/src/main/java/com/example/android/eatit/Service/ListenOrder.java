package com.example.android.eatit.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.example.android.eatit.Common.Common;
import com.example.android.eatit.Model.Request;
import com.example.android.eatit.OrderStatus;
import com.example.android.eatit.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ListenOrder extends Service implements ChildEventListener {
    // For Data Base.
    FirebaseDatabase database;
    // Get DataBase By Reference.
    DatabaseReference requests;

    public ListenOrder() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Init FireBase.
        database = FirebaseDatabase.getInstance();
        // Reference Or Name Of Our DataBase [Root], In This Case Called Request For Orders.
        requests = database.getReference("Request");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // When Click On Item Into Request.
        requests.addChildEventListener(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    // When There's Change In Info Of Order Like Status.
    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        // Get Values Of Request Order And Put It Into Request Class.
        Request request = dataSnapshot.getValue(Request.class);

        // Call showNotification Method, Take Two Prams
        // Key From FireBase DataBase , Value From Request Class.
        showNotification(dataSnapshot.getKey(), request);
    }

    // When Call showNotification Method.
    private void showNotification(String key, Request request) {
        // GoTo OrderStatus Class.
        Intent intent = new Intent(ListenOrder.this, OrderStatus.class);
        // We Need Put User Phone, To Send Notification To This Number.
        intent.putExtra("userPhone", request.getPhone());


        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set Notification Info.
        NotificationCompat.Builder  builder = new NotificationCompat.Builder(getBaseContext());
        // Notification Info.
        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("Gemy")
                .setContentInfo("Your order was updated")
                .setContentText("Order #" + key + " was update status to " + Common.convertCodeToStatus(request.getStatus()))
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.black_box)
                .setContentInfo("Info");

        NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
