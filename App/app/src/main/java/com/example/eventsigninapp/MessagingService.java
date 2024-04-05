package com.example.eventsigninapp;



import static com.google.firebase.messaging.Constants.MessagePayloadKeys.SENDER_ID;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {
    DatabaseController databaseController = new DatabaseController();
    UserController userController = new UserController();
    private static final String TAG = "MessagingService";
    private static final String channel_id = "channel_id";
    private final String channel_name = "channel_com.example.EventSignInApp";

    /**
     * There are two scenarios when onNewToken is called:
     * 1) When a new token is generated on initial app startup
     * 2) Whenever an existing token is changed
     * Under #2, there are three scenarios when the existing token is changed:
     * A) App is restored to a new device
     * B) User uninstalls/reinstalls the app
     * C) User clears app data
     */
    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        databaseController.addFCMTokenToUser(userController.getUser().getId(), token);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        generateNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
    }
    @Override
    public void onMessageSent(String messageId) {
        super.onMessageSent(messageId);
        Log.d(TAG, "Message sent: " + messageId);
    }

    @Override
    public void onSendError(String messageId, Exception exception) {
        super.onSendError(messageId, exception);
        Log.d(TAG, "Message send error: " + messageId + " " + exception);
    }





    public void generateNotification(String title, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // Create a notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel_id)
                .setSmallIcon(R.drawable.logo_placeholder)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);


        // Set custom layout
        builder.setContent(getRemoteView(title, message));

        // Get the notification manager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Check if the notification channel exists
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = notificationManager.getNotificationChannel(channel_id);
            if (channel == null) {
                // Create the notification channel if it doesn't exist
                channel = new NotificationChannel(channel_id, channel_name, NotificationManager.IMPORTANCE_HIGH);
                channel.enableLights(true);
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    channel.setAllowBubbles(true);
                }
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Show the notification
        notificationManager.notify(0, builder.build());
    }


    private RemoteViews getRemoteView(String title, String message) {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);

        remoteViews.setTextViewText(R.id.title, title);
        remoteViews.setTextViewText(R.id.message, message);
        remoteViews.setImageViewResource(R.id.app_logo, R.drawable.logo_placeholder);

        return remoteViews;
    }

    private void sendNotificationToUsers(String title, String message, String topic) {
        // Create a notification message
        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        fm.send(new RemoteMessage.Builder(SENDER_ID + "@fcm.googleapis.com")
                .setMessageId("1")
                .addData("my_message", "Hello World")
                .addData("my_action","SAY_HELLO")
                .build());

    }


}
