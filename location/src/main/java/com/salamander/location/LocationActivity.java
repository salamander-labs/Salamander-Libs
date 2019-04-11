package com.salamander.location;

import android.Manifest;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.salamander.core.Utils;

import java.util.Iterator;

import static com.salamander.location.LocationUpdateService.BROADCAST_ACTION_UPDATE_LOCATION_PASSIVE;

public class LocationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnCompleteListener<LocationSettingsResponse>, GpsStatus.Listener {

    protected static final int REQ_CHECK_GPS = 1111;
    protected static final int ACCESS_FINE_LOCATION_INTENT_ID = 2222;
    protected static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private PendingIntent mRequestLocationUpdatesPendingIntent;

    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGoogleAPIClient();
        checkLocationPermissions(new OnLocationPermissionGranted() {
            @Override
            public void onLocationPermissionGranted(boolean isPermissionGranted) {
                if (isPermissionGranted)
                    checkGPS();
            }
        });
    }

    /* Initiate Google API Client  */
    protected synchronized void initGoogleAPIClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
                LocationInfo mPrevLocation = SalamanderLocation.getLastLocation(LocationActivity.this);

                if (mCurrentLocation.getLatitude() != mPrevLocation.getLatitude() || mCurrentLocation.getLongitude() != mPrevLocation.getLongitude())
                    Utils.showLog("Location Received\nLatitude : " + String.valueOf(mCurrentLocation.getLatitude()) + "\nLongitude : " + String.valueOf(mCurrentLocation.getLongitude()) + "\nMock Location : " + ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && mCurrentLocation.isFromMockProvider()) ? "true" : "false"));

                if (mCurrentLocation != null) {
                    SalamanderLocation.getLocationManager(LocationActivity.this).setLocation(mCurrentLocation);
                    Intent locationIntentPassive = new Intent(BROADCAST_ACTION_UPDATE_LOCATION_PASSIVE);
                    locationIntentPassive.putExtra("location", mCurrentLocation);
                    sendBroadcast(locationIntentPassive);
                }
            }
        };
    }

    /* Check Location Permission for Marshmallow Devices */
    protected void checkLocationPermissions(OnLocationPermissionGranted onLocationPermissionGranted) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission(onLocationPermissionGranted);
            else onLocationPermissionGranted.onLocationPermissionGranted(true);
        } else onLocationPermissionGranted.onLocationPermissionGranted(true);
    }

    /*  Show Popup to access User Permission  */
    private void requestLocationPermission(OnLocationPermissionGranted onLocationPermissionGranted) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                onLocationPermissionGranted.onLocationPermissionGranted(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
            } else onLocationPermissionGranted.onLocationPermissionGranted(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                onLocationPermissionGranted.onLocationPermissionGranted(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
            else onLocationPermissionGranted.onLocationPermissionGranted(true);
        }
    }

    /* Starting Location Update Service */
    private void startLocationUpdate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (mLocationRequest != null && mRequestLocationUpdatesPendingIntent != null)
                    LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                locationManager.addGpsStatusListener(this);
                Location location = locationManager.getLastKnownLocation("gps");
                if (location != null && location.getLatitude() != 0 && location.getLongitude() != 0)
                    SalamanderLocation.getLocationManager(this).setLocation(location);
            }
        } else {
            if (mLocationRequest != null && mRequestLocationUpdatesPendingIntent != null)
                LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, mRequestLocationUpdatesPendingIntent);
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.addGpsStatusListener(this);
            Location location = locationManager.getLastKnownLocation("gps");
            if (location != null && location.getLatitude() != 0 && location.getLongitude() != 0)
                SalamanderLocation.getLocationManager(this).setLocation(location);
        }
    }

    /* Stop Location Update Service */
    private void stopLocationUpdate() {
        if (mGoogleApiClient != null)
            LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(mRequestLocationUpdatesPendingIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdate();
    }

    /* check GPS status then show dialog */
    protected void checkGPS() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        Task<LocationSettingsResponse> pendingResult = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        pendingResult.addOnCompleteListener(this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10);
        mLocationRequest.setFastestInterval(10);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Intent mRequestLocationUpdatesIntent = new Intent(this, LocationUpdateService.class);
        mRequestLocationUpdatesPendingIntent = PendingIntent.getService(getApplicationContext(), 0, mRequestLocationUpdatesIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        checkGPS();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Toast.makeText(this,"onConnectionSuspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Toast.makeText(this,"onConnectionFailed",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkGooglePlayServices()) {
            startLocationUpdate();
        }
    }

    /* check if Google Play Service is available */
    private boolean checkGooglePlayServices() {
        int result = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (result == ConnectionResult.SUCCESS) {
            // the device has the latest Google Play services installed
            return true;
        } else {
            Dialog errorDialog = GoogleApiAvailability.getInstance().getErrorDialog(this, result, 8964,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            finish();
                        }
                    });
            if (errorDialog != null) {
                errorDialog.show();
            } else {
                Toast.makeText(this, "Unknown error occured", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CHECK_GPS:
                if (data != null) {
                    final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
                    if (states != null && !states.isGpsUsable())
                        checkGPS();
                }
                break;
        }
    }

    @Override
    public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
        try {
            //LocationSettingsResponse response = task.getResult(ApiException.class);
            task.getResult(ApiException.class);
            if (checkGooglePlayServices())
                startLocationUpdate();
        } catch (ApiException exception) {
            switch (exception.getStatusCode()) {
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    // Location settings are not satisfied. But could be fixed by showing the
                    // user a dialog.
                    try {
                        // Cast to a resolvable exception.
                        ResolvableApiException resolvable = (ResolvableApiException) exception;
                        resolvable.startResolutionForResult(LocationActivity.this, REQ_CHECK_GPS);
                    } catch (IntentSender.SendIntentException e) {
                        // Ignore the error.
                    } catch (ClassCastException e) {
                        // Ignore, should be an impossible error.
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    // Location settings are not satisfied. However, we have no way to fix the
                    // settings so we won't show the dialog.
                    break;
            }
        }
    }

    protected interface OnLocationPermissionGranted {
        void onLocationPermissionGranted(boolean isPermissionGranted);
    }

    /* On Request permission method to check the permisison is granted or not for Marshmallow+ Devices  */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_INTENT_ID: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mGoogleApiClient == null)
                        initGoogleAPIClient();
                }
                checkGPS();
                break;
            }
        }
    }


    @Override
    public void onGpsStatusChanged(int event) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            GpsStatus gpsStatus = locationManager.getGpsStatus(null);
            String strGpsStats = "";
            if (gpsStatus != null) {
                Iterable<GpsSatellite> satellites = gpsStatus.getSatellites();
                Iterator<GpsSatellite> sat = satellites.iterator();
                int i = 0;
                while (sat.hasNext()) {
                    GpsSatellite satellite = sat.next();
                    strGpsStats += (i++) + ": " + satellite.getPrn() + "," + satellite.usedInFix() + "," + satellite.getSnr() + "," + satellite.getAzimuth() + "," + satellite.getElevation() + "\n\n";
                }
                //Toast.makeText(this, strGpsStats, Toast.LENGTH_SHORT).show();
            }
        }
    }
}