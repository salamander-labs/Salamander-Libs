package com.salamander.location;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.LocationResult;

public class LocationUpdateService extends IntentService {

    public static final String BROADCAST_ACTION_UPDATE_LOCATION_PASSIVE = "LOCATION_UPDATED_PASSIVE";

    public LocationUpdateService() {
        super("LocationUpdateService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForeground();
        else startForeground(11, new Notification());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (LocationResult.hasResult(intent)) {
            LocationResult locationResult = LocationResult.extractResult(intent);
            Location location = locationResult.getLastLocation();
            if (location != null) {
                SalamanderLocation.getLocationManager(this).setLocation(location);
                Intent locationIntentPassive = new Intent(BROADCAST_ACTION_UPDATE_LOCATION_PASSIVE);
                locationIntentPassive.putExtra("location", location);
                sendBroadcast(locationIntentPassive);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void startForeground() {
        String channelName = "Location";
        String SERVICE_CHANNEL_ID = "Location Service";
        NotificationChannel channel = new NotificationChannel(SERVICE_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(channel);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, SERVICE_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("Location Service")
                .setPriority(NotificationManager.IMPORTANCE_MAX)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(12, notification);
    }
}