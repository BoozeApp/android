package com.boozefy.android.gcm;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.boozefy.android.OrderActivity;
import com.boozefy.android.R;
import com.boozefy.android.ReplyActivity;
import com.boozefy.android.model.Order;
import com.boozefy.android.model.User;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mauricio Giordano on 1/22/16.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Booze, 2016 - All rights reserved.
 */
public class GcmIntentService extends IntentService {
    public static final String TAG = "GcmIntentService";

    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    private int lastHash = -1;

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                //sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                // sendNotification("Deleted messages on server: " +
                //         extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                String code = extras.getString("code");

                if (code.equals("order-in-transit")) {
                    orderInTransitNotification(Long.parseLong(extras.getString("orderId", "0")));
                } else if (code.equals("order-placed")) {
                    orderPlacedNotification(Long.parseLong(extras.getString("orderId", "0")));
                } else if (code.equals("order-message")) {
                    orderMessageNotification(Long.parseLong(extras.getString("orderId", "0")),
                                             extras.getString("message", ""));
                }

                Log.d("GCM", "NEW GCM MESSAGE WITH CODE " + code);
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        //GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void orderInTransitNotification(final long orderId) {
        User user = User._.load(this);

        if (user == null) return;

        Order.getService().get(orderId, user.getAccessToken()).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Response<Order> response) {
                Order order = response.body();

                if (order == null) return;

                Intent intentToApp = new Intent(getApplicationContext(), OrderActivity.class);
                intentToApp.putExtra("orderId", orderId);

                PendingIntent pIntentToApp = PendingIntent.getActivity(getApplicationContext(),
                        (int) orderId, intentToApp, 0);

                Notification n = new NotificationCompat.Builder(GcmIntentService.this)
                    .setContentTitle(getString(R.string.push_title_in_transit))
                    .setContentIntent(pIntentToApp)
                    .setContentText(getString(R.string.push_message_in_transit))
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setAutoCancel(true)
                    .build();

                NotificationManager notificationManager =
                        (NotificationManager) GcmIntentService.this.getSystemService(Activity.NOTIFICATION_SERVICE);

                notificationManager.notify((int) orderId, n);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

    }

    private void orderPlacedNotification(final long orderId) {
        User user = User._.load(this);

        if (user == null) return;

        Order.getService().get(orderId, user.getAccessToken()).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Response<Order> response) {
                Order order = response.body();

                if (order == null) return;

                Intent intentToApp = new Intent(getApplicationContext(), OrderActivity.class);
                intentToApp.putExtra("orderId", orderId);

                PendingIntent pIntentToApp = PendingIntent.getActivity(getApplicationContext(),
                        (int) orderId, intentToApp, 0);

                Notification n = new NotificationCompat.Builder(GcmIntentService.this)
                        .setContentTitle(getString(R.string.push_title_placed))
                        .setContentIntent(pIntentToApp)
                        .setContentText(getString(R.string.push_message_placed))
                        .setSmallIcon(R.drawable.ic_stat_name)
                        .setAutoCancel(true)
                        .build();

                NotificationManager notificationManager =
                        (NotificationManager) GcmIntentService.this.getSystemService(Activity.NOTIFICATION_SERVICE);

                notificationManager.notify((int) orderId, n);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

    }

    private void orderMessageNotification(final long orderId, final String message) {
        User user = User._.load(this);

        if (user == null) return;

        Order.getService().get(orderId, user.getAccessToken()).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Response<Order> response) {
                Order order = response.body();

                if (order == null) return;

                Intent intentToAnswer = new Intent(getApplicationContext(), ReplyActivity.class);
                intentToAnswer.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intentToAnswer.putExtra("orderId", orderId);

                TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
                taskStackBuilder.addParentStack(ReplyActivity.class);
                taskStackBuilder.addNextIntent(intentToAnswer);

                PendingIntent pIntentToAnswer = taskStackBuilder.getPendingIntent(0,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                Notification n = new NotificationCompat.Builder(GcmIntentService.this)
                        .setContentTitle(getString(R.string.push_title_message))
                        .setContentIntent(pIntentToAnswer)
                        .setContentText(message)
                        .addAction(R.drawable.ic_reply, getString(R.string.button_reply), pIntentToAnswer)
                        .setSmallIcon(R.drawable.ic_stat_name)
                        .setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle()
                            .bigText(message))
                        .setAutoCancel(true)
                        .setWhen(0)
                        .build();

                NotificationManager notificationManager =
                        (NotificationManager) GcmIntentService.this.getSystemService(Activity.NOTIFICATION_SERVICE);

                notificationManager.notify((int) orderId, n);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

    }
}