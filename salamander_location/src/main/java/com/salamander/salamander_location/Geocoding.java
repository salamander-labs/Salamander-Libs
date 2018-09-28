package com.salamander.salamander_location;

import android.content.Context;
import android.location.Address;
import android.location.Location;

import com.salamander.salamander_base_module.Utils;
import com.salamander.salamander_network.JSON;
import com.salamander.salamander_network.RetroData;
import com.salamander.salamander_network.RetroResp;
import com.salamander.salamander_network.RetroStatus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.nlopez.smartlocation.OnReverseGeocodingListener;
import io.nlopez.smartlocation.SmartLocation;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class Geocoding {

    public static final String RESULT_OK = "OK";
    public static final String ZERO_RESULTS = "ZERO_RESULTS";
    public static final String OVER_DAILY_LIMIT = "OVER_DAILY_LIMIT";
    public static final String OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT";
    public static final String REQUEST_DENIED = "REQUEST_DENIED";
    public static final String INVALID_REQUEST = "INVALID_REQUEST";
    public static final String UNKNOWN_ERROR = "UNKNOWN_ERROR";

    public static void getLocationAddress(final Context context, final double latitude, final double longitude, final String apiKey, final OnCB onCB) {
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        SmartLocation.with(context).geocoding().reverse(location, new OnReverseGeocodingListener() {
            @Override
            public void onAddressResolved(Location location, List<Address> list) {
                if (list.size() > 0) {
                    RetroStatus retroStatus = new RetroStatus();
                    retroStatus.setSuccess(true);
                    retroStatus.setMessage(list.get(0).getAddressLine(0));
                    onCB.onCB(retroStatus);
                } else getLocationAddressFromGoogleAPI(context, latitude, longitude, apiKey, onCB);
            }
        });
    }
    private static void getLocationAddressFromGoogleAPI(final Context context, final double latitude, final double longitude, final String apiKey, final OnCB onCB) {
        IC ic = createRetrofit().create(IC.class);
        String formatLatLng = String.valueOf(latitude) + "," + String.valueOf(longitude);
        ic.getLocation(formatLatLng, apiKey).enqueue(new RetroResp.SuccessCallback<ResponseBody>(context) {
            @Override
            public void onCall(RetroData retroData) {
                super.onCall(retroData);
                JSONObject jsonObject = JSON.toJSONObject(retroData.getResult());
                String status = JSON.getString(jsonObject, "status");
                retroData.getRetroStatus().setSuccess(!Utils.isEmpty(status) && status.equals("OK"));
                if (retroData.isSuccess()) {
                    try {
                        JSONArray jsonArray = JSON.getJSONArray(jsonObject, "results");
                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                        retroData.getRetroStatus().setMessage(JSON.getString(jsonObject1, "formatted_address"));
                    } catch (Exception e) {
                        retroData.getRetroStatus().setSuccess(false);
                        retroData.getRetroStatus().setMessage(e.toString());
                    }
                } else {
                    String errorMessage = JSON.getStringOrNull(jsonObject, "error_message");
                    if (Utils.isEmpty(errorMessage))
                        retroData.getRetroStatus().setMessage("Address not found");
                    else retroData.getRetroStatus().setMessage(errorMessage);
                }
                onCB.onCB(retroData.getRetroStatus());
            }
        });
    }

    public interface OnCB {
        void onCB(RetroStatus retroStatus);
    }

    interface IC {
        @GET("maps/api/geocode/json")
        Call<ResponseBody> getLocation(@Query("latlng") String latlng, @Query("key") String key);
    }

    public static Retrofit createRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES);
        return new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .client(client.build())
                .build();
    }
}