package com.boozefy.android.gcm;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.boozefy.android.OrderActivity;
import com.boozefy.android.R;
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
                    .setSmallIcon(R.mipmap.ic_launcher)
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
}