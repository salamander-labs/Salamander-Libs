package com.salamander.salamander_location;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.LocationResult;

public class LocationUpdateService extends IntentService {

    public static final String BROADCAST_ACTION_UPDATE_LOCATION_PASSIVE = "LOCATION_UPDATED_PASSIVE";

    public LocationUpdateService() {
        super("LocationUpdateService");
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
}
