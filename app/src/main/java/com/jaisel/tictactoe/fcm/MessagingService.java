package com.jaisel.tictactoe.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.jaisel.tictactoe.MainActivity;
import com.jaisel.tictactoe.R;
import com.jaisel.tictactoe.app.AppController;

import java.util.Map;

/**
 * Created by jaisel on 10/6/17.
 */

public class MessagingService extends FirebaseMessagingService {
    private final static String TAG = MessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendNotification(remoteMessage);
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        Log.i(TAG, "Data " + remoteMessage.getData());

        Bundle bundle = convertMapToBundle(remoteMessage.getData());

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(bundle);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplication(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, AppController.NOTIFY_CHANNEL_PLAY_REQUEST);
        notificationBuilder.setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        Map<String, String> data = remoteMessage.getData();
        if(!data.isEmpty()) {
            notificationBuilder.setContentTitle(data.get("title"))
                    .setContentText(data.get("body"))
                    .setSubText("Click to play");
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager != null) notificationManager.notify(data.get("userId").hashCode(), notificationBuilder.build());
    }

    public Bundle convertMapToBundle(Map<String, String> data){
        Bundle bundle = new Bundle();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            bundle.putString(entry.getKey(), entry.getValue());
        }
        return bundle;
    }
}
