package com.optimus.anymo.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import com.optimus.anymo.AppActivity;
import com.optimus.anymo.ChatFragment;
import com.optimus.anymo.DialogsActivity;
import com.optimus.anymo.MainActivity;
import com.optimus.anymo.R;
import com.optimus.anymo.app.App;
import com.optimus.anymo.constants.Constants;

public class MyFcmListenerService extends FirebaseMessagingService implements Constants {

    private int flag;

    public MyFcmListenerService () {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            flag = PendingIntent.FLAG_IMMUTABLE;

        } else {

            flag =  PendingIntent.FLAG_UPDATE_CURRENT;
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {

        String from = message.getFrom();
        Map data = message.getData();

        Log.e("Message", "Could not parse malformed JSON: \"" + data.toString() + "\"");

        generateNotification(getApplicationContext(), data);
    }

    @Override
    public void onNewToken(String token) {

        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        App.getInstance().getSettings().setFcmToken(token);
    }

    @Override
    public void onDeletedMessages() {
        sendNotification("Deleted messages on server");
    }

    @Override
    public void onMessageSent(String msgId) {

        sendNotification("Upstream message sent. Id=" + msgId);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {

        Log.e("Message", "Could not parse malformed JSON: \"" + msg + "\"");
    }

    /**
     * Create a notification to inform the user that server has sent a message.
     */
    private void generateNotification(Context context, Map data) {

        String CHANNEL_ID = "my_channel_01"; // id for channel.

        CharSequence name = context.getString(R.string.channel_name);     // user visible name of channel.

        NotificationChannel mChannel;

        String msgId = "0";
        String msgFromUserId = "0";
        String msgMessage = "";
        String msgImgUrl = "";
        String msgCreateAt = "0";
        String msgDate = "";
        String msgTimeAgo = "";
        String msgRemoveAt = "0";

        String message = data.get("msg").toString();
        String type = data.get("type").toString();
        String actionId = data.get("id").toString();
        String accountId = data.get("accountId").toString();

        if (Integer.valueOf(type) == GCM_NOTIFY_MESSAGE) {

            msgId = data.get("msgId").toString();
            msgFromUserId = data.get("msgFromUserId").toString();

            if (data.containsKey("msgMessage")) {

                msgMessage = data.get("msgMessage").toString();
            }

            if (data.containsKey("msgImgUrl")) {

                msgImgUrl = data.get("msgImgUrl").toString();
            }

            msgCreateAt = data.get("msgCreateAt").toString();
            msgDate = data.get("msgDate").toString();
            msgTimeAgo = data.get("msgTimeAgo").toString();
            msgRemoveAt = data.get("msgRemoveAt").toString();
        }

        int icon = R.drawable.ic_action_push_notification;
        long when = System.currentTimeMillis();
        String title = context.getString(R.string.app_name);

        switch (Integer.valueOf(type)) {

            case GCM_NOTIFY_SYSTEM: {

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context, CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_action_push_notification)
                                .setContentTitle(title)
                                .setContentText(message);

                Intent resultIntent;

                if (App.getInstance().getAccount().getId() != 0) {

                    resultIntent = new Intent(context, MainActivity.class);

                } else {

                    resultIntent = new Intent(context, AppActivity.class);
                }

                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, flag);
                mBuilder.setContentIntent(resultPendingIntent);

                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    int importance = NotificationManager.IMPORTANCE_HIGH;

                    mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                    mNotificationManager.createNotificationChannel(mChannel);
                }

                mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                mBuilder.setAutoCancel(true);
                mNotificationManager.notify(0, mBuilder.build());

                break;
            }

            case GCM_NOTIFY_CUSTOM: {

                if (App.getInstance().getAccount().getId() != 0) {

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_action_push_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);

                    Intent resultIntent = new Intent(context, MainActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(MainActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, flag);
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        int importance = NotificationManager.IMPORTANCE_HIGH;

                        mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                        mNotificationManager.createNotificationChannel(mChannel);
                    }

                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }

                break;
            }

            case GCM_NOTIFY_PERSONAL: {

                if (App.getInstance().getAccount().getId() != 0 && Long.toString(App.getInstance().getAccount().getId()).equals(accountId)) {

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_action_push_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);

                    Intent resultIntent = new Intent(context, MainActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(MainActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, flag);
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        int importance = NotificationManager.IMPORTANCE_HIGH;

                        mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                        mNotificationManager.createNotificationChannel(mChannel);
                    }

                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }

                break;
            }

            case GCM_NOTIFY_LIKE: {

                if (App.getInstance().getAccount().getId() != 0 && Long.toString(App.getInstance().getAccount().getId()).equals(accountId)) {

                    App.getInstance().getSettings().setNotificationsCount(App.getInstance().getSettings().getNotificationsCount() + 1);

                    if (App.getInstance().getFcmSettings().getNewLikes() == ENABLED) {

                        message = context.getString(R.string.label_gcm_like);

                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(context, CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_action_push_notification)
                                        .setContentTitle(title)
                                        .setContentText(message);

                        Intent resultIntent = new Intent(context, MainActivity.class);
                        resultIntent.putExtra("pageId", 3); // 3 = notifications page
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addParentStack(MainActivity.class);
                        stackBuilder.addNextIntent(resultIntent);
                        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, flag);
                        mBuilder.setContentIntent(resultPendingIntent);

                        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                            int importance = NotificationManager.IMPORTANCE_HIGH;

                            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                            mNotificationManager.createNotificationChannel(mChannel);
                        }

                        mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                        mBuilder.setAutoCancel(true);
                        mNotificationManager.notify(0, mBuilder.build());
                    }
                }

                break;
            }

            case GCM_NOTIFY_COMMENT: {

                if (App.getInstance().getAccount().getId() != 0 && Long.toString(App.getInstance().getAccount().getId()).equals(accountId)) {

                    App.getInstance().getSettings().setNotificationsCount(App.getInstance().getSettings().getNotificationsCount() + 1);

                    if (App.getInstance().getFcmSettings().getNewComments() == ENABLED) {

                        message = context.getString(R.string.label_gcm_comment);

                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(context, CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_action_push_notification)
                                        .setContentTitle(title)
                                        .setContentText(message);

                        Intent resultIntent = new Intent(context, MainActivity.class);
                        resultIntent.putExtra("pageId", 3); // 3 = notifications page
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addParentStack(MainActivity.class);
                        stackBuilder.addNextIntent(resultIntent);
                        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, flag);
                        mBuilder.setContentIntent(resultPendingIntent);

                        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                            int importance = NotificationManager.IMPORTANCE_HIGH;

                            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                            mNotificationManager.createNotificationChannel(mChannel);
                        }

                        mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                        mBuilder.setAutoCancel(true);
                        mNotificationManager.notify(0, mBuilder.build());
                    }
                }

                break;
            }

            case GCM_NOTIFY_COMMENT_REPLY: {

                if (App.getInstance().getAccount().getId() != 0 && Long.toString(App.getInstance().getAccount().getId()).equals(accountId)) {

                    App.getInstance().getSettings().setNotificationsCount(App.getInstance().getSettings().getNotificationsCount() + 1);

                    if (App.getInstance().getFcmSettings().getNewComments() == ENABLED) {

                        message = context.getString(R.string.label_gcm_comment_reply);

                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(context, CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_action_push_notification)
                                        .setContentTitle(title)
                                        .setContentText(message);

                        Intent resultIntent = new Intent(context, MainActivity.class);
                        resultIntent.putExtra("pageId", 3); // 3 = notifications page
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addParentStack(MainActivity.class);
                        stackBuilder.addNextIntent(resultIntent);
                        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, flag);
                        mBuilder.setContentIntent(resultPendingIntent);

                        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                            int importance = NotificationManager.IMPORTANCE_HIGH;

                            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                            mNotificationManager.createNotificationChannel(mChannel);
                        }

                        mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                        mBuilder.setAutoCancel(true);
                        mNotificationManager.notify(0, mBuilder.build());
                    }
                }

                break;
            }

            case GCM_NOTIFY_MESSAGE: {

                Log.e("FCM", "New Message");

                if (App.getInstance().getAccount().getId() != 0 && Long.valueOf(accountId) == App.getInstance().getAccount().getId()) {

                    if (App.getInstance().getSettings().getCurrentChatId() == Integer.valueOf(actionId)) {

                        Intent i = new Intent(ChatFragment.BROADCAST_ACTION);
                        i.putExtra(ChatFragment.PARAM_TASK, 0);
                        i.putExtra(ChatFragment.PARAM_STATUS, ChatFragment.STATUS_START);

                        i.putExtra("msgId", Integer.valueOf(msgId));
                        i.putExtra("msgFromUserId", Long.valueOf(msgFromUserId));
                        i.putExtra("msgMessage", String.valueOf(msgMessage));
                        i.putExtra("msgImgUrl", String.valueOf(msgImgUrl));
                        i.putExtra("msgCreateAt", Integer.valueOf(msgCreateAt));
                        i.putExtra("msgDate", String.valueOf(msgDate));
                        i.putExtra("msgTimeAgo", String.valueOf(msgTimeAgo));

                        context.sendBroadcast(i);

                    } else {

                        if (App.getInstance().getSettings().getMessagesCount() == 0) App.getInstance().getSettings().setMessagesCount(App.getInstance().getSettings().getMessagesCount() + 1);

                        if (App.getInstance().getFcmSettings().getNewMessages() == ENABLED) {

                             message = context.getString(R.string.label_gcm_message);

                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(context, CHANNEL_ID)
                                            .setSmallIcon(R.drawable.ic_action_push_notification)
                                            .setContentTitle(title)
                                            .setContentText(message);

                            Intent resultIntent = new Intent(context, DialogsActivity.class);
                            resultIntent.putExtra("fcm", true);
                            resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                            stackBuilder.addParentStack(DialogsActivity.class);
                            stackBuilder.addNextIntent(resultIntent);
                            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, flag);
                            mBuilder.setContentIntent(resultPendingIntent);

                            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                                int importance = NotificationManager.IMPORTANCE_HIGH;

                                mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                                mNotificationManager.createNotificationChannel(mChannel);
                            }

                            mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                            mBuilder.setAutoCancel(true);
                            mNotificationManager.notify(0, mBuilder.build());
                        }
                    }
                }

                break;
            }

            case GCM_NOTIFY_SEEN: {

                if (App.getInstance().getAccount().getId() != 0 && Long.valueOf(accountId) == App.getInstance().getAccount().getId()) {

                    Log.e("SEEN", "IN LISTENER");

                    if (App.getInstance().getSettings().getCurrentChatId() == Integer.valueOf(actionId)) {

                        Intent i = new Intent(ChatFragment.BROADCAST_ACTION_SEEN);
                        i.putExtra(ChatFragment.PARAM_TASK, 0);
                        i.putExtra(ChatFragment.PARAM_STATUS, ChatFragment.STATUS_START);
                        context.sendBroadcast(i);
                    }

                    break;
                }
            }

            case GCM_NOTIFY_TYPING_START: {

                if (App.getInstance().getAccount().getId() != 0 && Long.valueOf(accountId) == App.getInstance().getAccount().getId()) {

                    if (App.getInstance().getSettings().getCurrentChatId() == Integer.valueOf(actionId)) {

                        Intent i = new Intent(ChatFragment.BROADCAST_ACTION_TYPING_START);
                        i.putExtra(ChatFragment.PARAM_TASK, 0);
                        i.putExtra(ChatFragment.PARAM_STATUS, ChatFragment.STATUS_START);
                        context.sendBroadcast(i);
                    }

                    break;
                }
            }

            case GCM_NOTIFY_TYPING_END: {

                if (App.getInstance().getAccount().getId() != 0 && Long.valueOf(accountId) == App.getInstance().getAccount().getId()) {

                    if (App.getInstance().getSettings().getCurrentChatId() == Integer.valueOf(actionId)) {

                        Intent i = new Intent(ChatFragment.BROADCAST_ACTION_TYPING_END);
                        i.putExtra(ChatFragment.PARAM_TASK, 0);
                        i.putExtra(ChatFragment.PARAM_STATUS, ChatFragment.STATUS_START);
                        context.sendBroadcast(i);
                    }

                    break;
                }
            }

            default: {

                break;
            }
        }
    }
}