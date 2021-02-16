package com.salamander.location;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.salamander.core.Utils;

import org.json.JSONArray;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Url;

public class JobIntentServiceBlackListApp extends JobIntentService {

    private static final String URL = "https://gist.githubusercontent.com/salamander-labs/1217b74564ef74b32b41216332b61881/raw/";

    public static final int JOB_ID = 6969;
    public static final int REFRESH_DURATION_MILIS = 60 * 60 * 1000;

    public static void enqueue(Context context) {
        enqueueWork(context, JobIntentServiceBlackListApp.class, JOB_ID, new Intent());
    }

    public static void enqueue(Context context, Intent intent) {
        enqueueWork(context, JobIntentServiceBlackListApp.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        LocationSharedPreferenceManager sharedPreferenceManager = new LocationSharedPreferenceManager(getApplicationContext());
        if (new Date().getTime() - sharedPreferenceManager.getLastUpdateBlacklistApp().getTime() > REFRESH_DURATION_MILIS) {
            GetGist getGist = createRetrofit().create(GetGist.class);
            try {
                ResponseBody responseBody = getGist.getGist(URL).execute().body();
                if (responseBody != null) {
                    String gistStr = responseBody.string();
                    JSONArray jsonArray = new JSONArray(gistStr);
                    sharedPreferenceManager.setListBlacklistApp(jsonArray);
                }
            } catch (Exception e) {
                Utils.showLog(e);
            }
        }
    }

    interface GetGist {
        @GET
        Call<ResponseBody> getGist(@Url String url);
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
}
