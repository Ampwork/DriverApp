package com.ampwork.driverapp.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.collection.ArrayMap;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ampwork.driverapp.R;
import com.ampwork.driverapp.Util.AppConstant;
import com.ampwork.driverapp.Util.AppUtility;
import com.ampwork.driverapp.Util.PreferencesManager;
import com.ampwork.driverapp.activity.BusStatusActivity;
import com.ampwork.driverapp.activity.NotificationActivity;
import com.ampwork.driverapp.database.DBHelper;
import com.ampwork.driverapp.model.Notification;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFCMService";
    PreferencesManager preferencesManager;

    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        preferencesManager = new PreferencesManager(getApplicationContext());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Collection<String> data = remoteMessage.getData().values();
            ArrayList<String> stringArrayList = new ArrayList<>();
            for (String s : data) {
                stringArrayList.add(s);
            }

            handleMessage(stringArrayList);


        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {

            playNotificationSound();
            Intent notificationAlertIntent = new Intent(AppConstant.STR_NOTIFICATION_FILTER);
            LocalBroadcastManager.getInstance(MyFirebaseMessagingService.this).sendBroadcast(notificationAlertIntent);

        }
    }

    private void handleMessage(ArrayList<String> stringArrayList) {
        JSONObject data = null;

        // data = json.getJSONObject("data");
        String title = stringArrayList.get(0);//data.getString("title");
        String message = stringArrayList.get(1);//data.getString("message");

        preferencesManager.setStringValue(AppConstant.PREF_RECENT_NOTIFICATION,message);


        if (AppUtility.isAppIsInBackground(getApplicationContext())) {
            preferencesManager.setBooleanValue(AppConstant.PREF_NOTIFICATION_ARRIVED, true);
            sendNotification(message);
        } else {
            playNotificationSound();
            Intent notificationAlertIntent = new Intent(AppConstant.STR_NOTIFICATION_FILTER);
            LocalBroadcastManager.getInstance(MyFirebaseMessagingService.this).sendBroadcast(notificationAlertIntent);
        }


    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    private void sendRegistrationToServer(String token) {
        PreferencesManager preferencesManager = new PreferencesManager(MyFirebaseMessagingService.this);
        String institute_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE_KEY);
        String driverKey = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_KEY);
        if (!institute_key.isEmpty() && !driverKey.isEmpty()) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(AppConstant.PREF_STR_DRIVERS_TB).child(institute_key);
            HashMap<String, Object> stringHashMap = new HashMap<>();
            stringHashMap.put(AppConstant.PREF_STR_FCMTOKEN, token);
            databaseReference.child(driverKey).updateChildren(stringHashMap);
        }
    }

    private void sendNotification(String messageBody) {

        Intent intent = new Intent(this, NotificationActivity.class);
        intent.putExtra("is_from_notification", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher_foreground)
                        .setColor(getResources().getColor(R.color.secondaryColor))
                        .setContentTitle(getString(R.string.fcm_message))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(messageBody))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    // Playing notification sound
    public void playNotificationSound() {

        //play sound if notification sound is on
        //  if (myPref.getBooleanValue(IS_NOTIFICATION_SOUND_ON)) {
        try {
            //Uri path = Uri.parse("android.resource://" + mContext.getPackageName() + "/raw/notification.mp3");
            //RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_NOTIFICATION, path);
            Uri path = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), path);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
