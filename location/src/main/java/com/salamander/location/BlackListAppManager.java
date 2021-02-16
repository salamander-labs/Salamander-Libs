package com.salamander.location;

import android.content.Context;

import com.salamander.network.JSON;
import com.salamander.network.retro.RetroData;
import com.salamander.network.retro.RetroResp;
import com.salamander.network.retro.RetroStatus;

import org.json.JSONArray;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;

public class BlackListAppManager {

    private static final String URL = "https://gist.githubusercontent.com/salamander-labs/1217b74564ef74b32b41216332b61881/raw";

    public static void getBlackListApp(final Context context, final OnCB CB) {
        IC_GetBlacklistApp IC = createRetrofit().create(IC_GetBlacklistApp.class);
        IC.getBlackListApp().enqueue(new RetroResp.SuccessCallback<ResponseBody>(context) {
            @Override
            public void onCall(RetroData retroData) {
                super.onCall(retroData);
                if (retroData.isSuccess()) {
                    String jsonDataStr = retroData.getData();
                    JSONArray jsonArray = JSON.toJSONArray(jsonDataStr);
                    SalamanderLocation.getLocationManager(context).setListBlacklistApp(jsonArray);
                }
                CB.onCB(retroData.getRetroStatus());
            }
        });
    }

    private static Retrofit createRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES);
        return new Retrofit.Builder()
                .baseUrl(URL)
                .client(client.build())
                .build();
    }

    public interface OnCB {
        void onCB(RetroStatus status);
    }

    public interface IC_GetBlacklistApp {
        @GET("get_blacklist_app")
        Call<ResponseBody> getBlackListApp();
    }
}