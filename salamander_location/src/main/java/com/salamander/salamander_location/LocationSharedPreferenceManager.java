package com.salamander.salamander_location;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.salamander.salamander_base_module.Utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class LocationSharedPreferenceManager {

    private SharedPreferences spf;
    private SharedPreferences.Editor editor;
    private Context context;

    private String SPF_NAME = "";

    private String LAST_POSITION_LATITUDE = "";
    private String LAST_POSITION_LONGITUDE = "";
    private String LAST_POSITION_TIMESTAMP = "";
    private String LAST_POSITION_PROVIDER = "";

    private String LAST_MOCK_POSITION_LATITUDE = "";
    private String LAST_MOCK_POSITION_LONGITUDE = "";
    private String LAST_MOCK_POSITION_TIMESTAMP = "";
    private String LAST_MOCK_POSITION_PROVIDER = "";

    private String LIST_BLACKLIST_APK = "";

    public LocationSharedPreferenceManager(Context context) {
        this.context = context;
        this.SPF_NAME = context.getPackageName();

        this.LAST_POSITION_LATITUDE = SPF_NAME + "_latitude";
        this.LAST_POSITION_LONGITUDE = SPF_NAME + "_longitude";
        this.LAST_POSITION_TIMESTAMP = SPF_NAME + "_timestamp";
        this.LAST_POSITION_PROVIDER = SPF_NAME + "_provider";

        this.LAST_MOCK_POSITION_LATITUDE = SPF_NAME + "_mock_latitude";
        this.LAST_MOCK_POSITION_LONGITUDE = SPF_NAME + "_mock_longitude";
        this.LAST_MOCK_POSITION_TIMESTAMP = SPF_NAME + "_mock_timestamp";
        this.LAST_MOCK_POSITION_PROVIDER = SPF_NAME + "_mock_provider";

        this.LIST_BLACKLIST_APK = SPF_NAME + "_blacklist_apk";

        this.spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
    }

    public void setListBlacklistApp(JSONArray jsonArray) {
        editor = spf.edit();
        editor.putString(LIST_BLACKLIST_APK, jsonArray.toString());
        editor.apply();
    }

    public void setListBlacklistApp(ArrayList<String> list_blacklist_app) {
        JSONArray jsonArray = new JSONArray();
        for (String apk_name : list_blacklist_app) {
            jsonArray.put(apk_name);
        }
        setListBlacklistApp(jsonArray);
    }

    public ArrayList<String> getListBlacklistApp() {
        ArrayList<String> list_blacklist_app = new ArrayList<>();
        String spfString = spf.getString(SPF_NAME, "");
        try {
            JSONArray jsonArray = new JSONArray(spfString);
            for (int i=0; i < jsonArray.length(); i++) {
                list_blacklist_app.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            //Log.e("getListBlacklistApp", spfString + " => " + e.toString());
            Utils.showLog(LocationSharedPreferenceManager.class.getSimpleName(), "getListBlacklistApp", e.toString());
        }
        return list_blacklist_app;
    }

    public void setLastMockPosition(LocationInfo locationInfo) {
        editor = spf.edit();
        putDouble(LAST_MOCK_POSITION_LATITUDE, locationInfo.getLatitude());
        putDouble(LAST_MOCK_POSITION_LONGITUDE, locationInfo.getLongitude());
        editor.putLong(LAST_MOCK_POSITION_TIMESTAMP, locationInfo.getTime());
        editor.putString(LAST_MOCK_POSITION_PROVIDER, locationInfo.getProvider());
        editor.apply();
    }

    public LocationInfo getLastMockPosition() {
        LocationInfo locationInfo = new LocationInfo("LAST_MOCK_LOCATION");
        locationInfo.setLatitude(getDouble(LAST_MOCK_POSITION_LATITUDE, 0));
        locationInfo.setLongitude(getDouble(LAST_MOCK_POSITION_LONGITUDE, 0));
        locationInfo.setTime(spf.getLong(LAST_MOCK_POSITION_TIMESTAMP, 0));
        return locationInfo;
    }

    public LocationInfo getLastLocation() {
        LocationInfo locationInfo = new LocationInfo("LAST_LOCATION");
        locationInfo.setLatitude(getDouble(LAST_POSITION_LATITUDE, 0));
        locationInfo.setLongitude(getDouble(LAST_POSITION_LONGITUDE, 0));
        locationInfo.setTime(spf.getLong(LAST_POSITION_TIMESTAMP, 0));
        return locationInfo;
    }

    public void setLocation(Location location) {
        setLastLocation(location.getTime(), location.getLatitude(), location.getLongitude());
    }

    public void setLastLocation(long timestamp, double latitude, double longitude) {
        editor = spf.edit();
        editor.putLong(LAST_POSITION_TIMESTAMP, timestamp);
        putDouble(LAST_POSITION_LATITUDE, latitude);
        putDouble(LAST_POSITION_LONGITUDE, longitude);
        editor.apply();
    }

    private void putDouble(final String key, final double value) {
        editor.putLong(key, Double.doubleToRawLongBits(value));
    }

    private double getDouble(final String key, final double defaultValue) {
        return Double.longBitsToDouble(spf.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

}
