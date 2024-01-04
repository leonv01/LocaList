package com.mad.localist;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;

public class GroceryNotifier {
    private static final int NOTIFICATION_ID = 187;

    private static String CHANNEL_ID = "grocery_notification_channel";
    private static String CHANNEL_NAME = "Grocery Notification";

    NotificationCompat.Builder notificationBuilder;
    NotificationManager notificationManager;

    public GroceryNotifier(Context context) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if(notificationChannel == null) {
                notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(notificationChannel);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Grocery Notification")
                .setContentText("You have a grocery to buy!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_IMMUTABLE);
        notificationBuilder.setContentIntent(resultPendingIntent);
    }

    public void showOrUpdateNotification(ArrayList<GroceryEntry> groceryEntries) {
        if(groceryEntries.size() > 0) {
            notificationBuilder.setContentText("You have " + groceryEntries.size() + " groceries to buy!");
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
        } else {
            notificationManager.cancel(NOTIFICATION_ID);
        }
    }

    public void removeNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }
}
