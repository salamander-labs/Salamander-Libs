package com.salamander.location;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;

import com.salamander.core.Utils;

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
    private String LAST_POSITION_IS_MOCK = "";

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
        this.LAST_POSITION_IS_MOCK = SPF_NAME + "_is_mock";

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
            Utils.showLog(e);
        }
        return list_blacklist_app;
    }

    public void setMockLocation(Location location) {
        LocationInfo locationInfo = new LocationInfo();
        locationInfo.set(location);
        setLastMockPosition(locationInfo);
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
        locationInfo.setLongitude(getDouble(LAST_POSITION_LONGITUDE, 0));
        locationInfo.setTime(spf.getLong(LAST_POSITION_TIMESTAMP, 0));
        locationInfo.setFromMock(spf.getBoolean(LAST_POSITION_IS_MOCK, false));
        return locationInfo;
    }

    public void setLocation(Location location) {
        boolean isFromMock = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && location.isFromMockProvider();
        setLastLocation(location.getTime(), location.getLatitude(), location.getLongitude(), isFromMock);
    }

    public void setLastLocation(long timestamp, double latitude, double longitude, boolean isFromMock) {
        editor = spf.edit();
        editor.putLong(LAST_POSITION_TIMESTAMP, timestamp);
        putDouble(LAST_POSITION_LATITUDE, latitude);
        putDouble(LAST_POSITION_LONGITUDE, longitude);
        editor.putBoolean(LAST_POSITION_IS_MOCK, isFromMock);
        editor.apply();
    }

    private void putDouble(final String key, final double value) {
        editor.putLong(key, Double.doubleToRawLongBits(value));
    }

    private double getDouble(final String key, final double defaultValue) {
        return Double.longBitsToDouble(spf.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

}
