package com.salamander.salamander_logger;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.salamander.salamander_network.retro.RetroData;
import com.salamander.salamander_network.retro.RetroResp;
import com.salamander.salamander_network.retro.RetroStatus;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class LogUtils {

    public static void reportError(final Context context, int userID) {
        ArrayList<ErrorLog> list_error = new LogSQLite(context).getAll();
        if (list_error.size() > 0)
            sendError(context, userID, makeJSON(context, list_error).toString());
    }

    private static JSONArray makeJSON(Context context, ArrayList<ErrorLog> list_error) {
        JSONArray jsonArray = new JSONArray();
        for (ErrorLog errorLog : list_error) {
            jsonArray.put(errorLog.getAsJSON(context));
        }
        return jsonArray;
    }

    private static File dataFolder = Environment.getDataDirectory();
    private static final File backupFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/");

    public static File getBackupDatabaseFile(Context context, String applicationName) {
        String backupDBPath = LogDBHelper.getDBName(context);
        return new File(backupFolder + applicationName, backupDBPath);
    }

    public static void backupDatabase(Context context, String applicationName) {
        try {
            String currentDBPath = "//data//" + context.getApplicationContext().getPackageName() + "//databases//" + LogDBHelper.getDBName(context);
            String backupDBPath = LogDBHelper.getDBName(context);

            File currentDB = new File(dataFolder, currentDBPath);
            File backupDB = new File(backupFolder + applicationName, backupDBPath);

            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream(backupDB).getChannel();

            dst.transferFrom(src, 0, src.size());

            src.close();
            dst.close();
        } catch (Exception e) {
        }
    }

    //private static final String URL = "http://dtswebapi.datascrip.co.id/demo/gosam_1000_salesman/api/v1/";
    private static final String URL = "https://salamander-app.com/crashlog/api/v1/";

    public static void sendError(final Context context, final int userID, final String json) {
        IC_Error IC = createRetrofit().create(IC_Error.class);
        IC.sendError(userID, json).enqueue(new RetroResp.SuccessCallback<ResponseBody>(context) {
            @Override
            public void onCall(RetroData retroData) {
                super.onCall(retroData);
                if (retroData.isSuccess()) {
                    new LogSQLite(context).clear();
                }
            }
        });
    }

    public static String readLogs() {
        String logText = "";
        try {
            Process process = Runtime.getRuntime().exec("logcat -d -v time");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("AndroidRuntime") && !line.contains("OkHttp")) {
                    if (line.contains("Exception")) {
                        String lineText = "";
                        logText += "<span style=\"color:red;\"><b>" +
                                line +
                                "</b></span><br>";
                    } else
                        logText += line + "<br>";
                }
            }
            Runtime.getRuntime().exec("logcat -c");
        } catch (IOException e) {
            Log.e("readLogs", e.toString());
        }
        return logText;
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

    public interface IC_Error {
        @FormUrlEncoded
        @POST("send_error")
        Call<ResponseBody> sendError(@Field("user") int user, @Field("json") String json);
    }
}