package com.example.android.eatitserver.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.android.eatitserver.MainActivity;
import com.example.android.eatitserver.R;
import com.google.android.gms.common.internal.service.Common;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

// For Notification.
public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Call sendNotification Method.
        sendNotification(remoteMessage);
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        // Copy From Cart.java Client App.
        // Send Notifications For All Users.
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String message = data.get("message");

        Intent intent = new Intent(MyFirebaseMessaging.this, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext())
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.black_box)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis());

        NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        noti.notify(0, builder.build());
    }
}

