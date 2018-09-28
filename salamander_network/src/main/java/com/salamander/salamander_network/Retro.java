package com.salamander.salamander_network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Html;

import com.salamander.salamander_base_module.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import retrofit2.Response;

public class Retro {

    private static final String STATUS_SUCCESS = "success";
    private static final int STATUS_SUCCESS_CODE = 1;

    public static String getString(Response<ResponseBody> response) {
        //Response<ResponseBody> bodyResponse = response;
        try {
            if (response.isSuccessful()) {
                String respon = getResponseBody(response).string().trim();
                if (!(Utils.isEmpty(respon)))
                    return respon;
                else
                    return null;
            } else {
                InputStream i = getErrorBody(response).byteStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(i));
                StringBuilder errorResult = new StringBuilder();
                String line;
                try {
                    while ((line = r.readLine()) != null) {
                        errorResult.append(line).append('\n');
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return errorResult.toString();
            }
        } catch (Exception e) {
            //new Function().writeToText("getString", e.toString());
            //Log.e("R.R.->getString", e.toString());
            Utils.showLog(Retro.class.getSimpleName(), "getString", e.toString());
            return null;
        }
    }

    public static String getErrorMsg(Response<RetroStatus> response) {
        try {
            return response.errorBody().string();
        } catch (Exception e) {
            return null;
        }
    }

    public static RetroStatus getRetroStatus(Response<ResponseBody> response, String json) {
        //Log.e(Salamander.SHORT_PACKAGE_NAME, Retro.class.getSimpleName() + " => getStatus([response, json])  => " + json);
        Utils.showLog(Retro.class.getSimpleName(), "getStatus", json);
        if (Utils.isEmpty(json)) {
            try {
                ResponseBody errorBody = getErrorBody(response);
                String errorMsg = errorBody.string();
                /*
                if (response != null && response.code() != 200)
                    return new RetroStatus(false, response.code() + " " + response.message(), errorMsg.substring(errorMsg.indexOf("<h1>") + "<h1>".length(), errorMsg.indexOf("</h1>")) + "<br/>" + errorMsg.substring(errorMsg.indexOf("<p>") + "<p>".length(), errorMsg.indexOf("</p>")));
                return new RetroStatus(false, "Network Error.", errorMsg.substring(errorMsg.indexOf("<h1>") + "<h1>".length(), errorMsg.indexOf("</h1>")) + "<br/>" + errorMsg.substring(errorMsg.indexOf("<p>") + "<p>".length(), errorMsg.indexOf("</p>")));
                */
                if (response != null && response.code() != 200)
                    return new RetroStatus(false, response.code() + " " + response.message(), errorMsg);
                return new RetroStatus(false, "Network Error.", errorMsg);
            } catch (Exception e) {
                if (response != null && response.code() != 200)
                    return new RetroStatus(false, response.code() + " " + response.message(), null);
                return new RetroStatus(false, "Network Error.", null);
            }
        } else {
            RetroStatus retroStatus = new RetroStatus();
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(json);
                if (jsonObject.has("status"))
                    retroStatus.setSuccess(jsonObject.getString("status").trim().toLowerCase().equals(STATUS_SUCCESS));
                else if (jsonObject.has("retroStatus"))
                    retroStatus.setSuccess(jsonObject.getInt("retroStatus") == STATUS_SUCCESS_CODE);
                if (jsonObject.has("title"))
                    retroStatus.setMessage(jsonObject.getString("title").trim());
                if (jsonObject.has("msg"))
                    retroStatus.setMessage(jsonObject.getString("msg").trim());
                else if (jsonObject.has("message"))
                    retroStatus.setMessage(jsonObject.getString("message").trim());
                if (jsonObject.has("sql"))
                    retroStatus.setQuery(jsonObject.getString("sql").trim());
            } catch (JSONException e) {
                Utils.showLog(Retro.class.getSimpleName(), "getStatus", e.toString());
                //if (json.contains("<h1>") && json.contains("</h1>"))
                //    retroStatus.setMessage(json.substring(json.indexOf("<h1>"), json.indexOf("</h1>") + "</h1>".length()));
                //else
                retroStatus.setMessage(json);
            }
            return retroStatus;
        }
    }

    public static void showError(final Context context, String title, String message) {
        if (message != null && title != null)
            new AlertDialog.Builder(context)
                    .setTitle(Html.fromHtml(title))
                    .setMessage(Html.fromHtml(message))
                    .setPositiveButton("OK", null)
                    .create()
                    .show();
    }

    public static boolean isSuccess(Response<ResponseBody> response, String json) {
        return getRetroStatus(response, json).isSuccess();
    }

    private static ResponseBody getResponseBody(final Response<ResponseBody> response) {
        return new ResponseBody() {
            @Override
            public MediaType contentType() {
                return response.body().contentType();
            }

            @Override
            public long contentLength() {
                return response.body().contentLength();
            }

            @Override
            public BufferedSource source() {
                return response.body().source();
            }
        };
    }

    private static ResponseBody getErrorBody(final Response<ResponseBody> response) {
        return new ResponseBody() {
            @Override
            public MediaType contentType() {
                return response.errorBody().contentType();
            }

            @Override
            public long contentLength() {
                return response.errorBody().contentLength();
            }

            @Override
            public BufferedSource source() {
                return response.errorBody().source();
            }
        };
    }

    @NonNull
    public static RequestBody createPartFromString(String description) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), description);
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
