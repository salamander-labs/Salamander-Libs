package com.salamander.network.retro;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.salamander.core.Utils;
import com.salamander.core.widget.SalamanderDialog;
import com.salamander.network.gson.GsonConverterFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ConnectionSpec;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.BufferedSource;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Retro {

    public interface OnCB {
        void onCB(RetroStatus retroStatus);
    }

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
            Utils.showLog(e);
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

    private static String getJSON(String response) {
        try {
            Pattern pattern = Pattern.compile("((?s)<div(.*)div>)((?s).*)", Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(response);
            String result = response;
            if (matcher.find())
                result = matcher.group(1);
            return result;
        } catch (Exception e) {
            return response;
        }
    }

    public static RetroStatus getRetroStatus(Response<ResponseBody> response, String json) {
        Utils.showLog(json);
        json = getJSON(json);
        if (Utils.isEmpty(json)) {
            try {
                ResponseBody errorBody = getErrorBody(response);
                String errorMsg = errorBody.string();
                if (response != null && response.code() != 200)
                    return new RetroStatus(false, response.code() + " - " + response.message(), Utils.isEmpty(errorMsg) ? "No Response from Server" : errorMsg, "");
                return new RetroStatus(false, "Error.", errorMsg);
            } catch (Exception e) {
                if (response != null && response.code() != 200)
                    return new RetroStatus(false, response.code() + " - " + response.message(), "No Response from Server", null);
                return new RetroStatus(false, e.toString(), null);
            }
        } else {
            RetroStatus retroStatus = new RetroStatus();
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(json);
                if (jsonObject.has(RetroConst.RETRO_RESPONSE_STATUS)) {

                    JSONObject jsonObjectStatus = jsonObject.getJSONObject(RetroConst.RETRO_RESPONSE_STATUS);
                    retroStatus = new RetroStatus(jsonObjectStatus);
                } else if (jsonObject.has(RetroConst.RETRO_STATUS)) {

                    JSONObject jsonObjectStatus = jsonObject.getJSONObject(RetroConst.RETRO_STATUS);
                    retroStatus = new RetroStatus(jsonObjectStatus);
                }
                if (response != null && response.code() != 200)
                    retroStatus.setTitle(response.code() + " - " + response.message());
            } catch (JSONException e) {
                Utils.showLog(e);
                try {
                    retroStatus.setTitle((json.toLowerCase().contains("title") ? getStringInsideTag(json, "title") : "Error"));
                    retroStatus.setMessage(getStringInsideTag(json, "body"));
                } catch (Exception e1) {
                    retroStatus.setMessage(json);
                }
            }
            return retroStatus;
        }
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
        return RequestBody.create(description, MediaType.parse("multipart/form-data"));
    }


    public static Retrofit createRetrofit(String URL) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    okhttp3.Response response = chain.proceed(request);
                    return response;
                })
                .connectionSpecs(Collections.singletonList(ConnectionSpec.COMPATIBLE_TLS))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.MINUTES);

        return new Retrofit.Builder()
                .baseUrl(URL)
                .client(client.build())
                .build();
    }

    public static Retrofit createRetrofit(String URL, GsonConverterFactory gsonConverterFactory) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    okhttp3.Response response = chain.proceed(request);
                    return response;
                })
                .connectionSpecs(Collections.singletonList(ConnectionSpec.COMPATIBLE_TLS))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.MINUTES);

        return new Retrofit.Builder()
                .baseUrl(URL)
                .client(client.build())
                .addConverterFactory(gsonConverterFactory)
                .build();
    }

    public static void showRetroDialog(final Context context, RetroStatus retroStatus) {
        showRetroDialog(context, retroStatus, false);
    }

    public static void showRetroDialog(final Context context, RetroStatus retroStatus, final boolean finish) {
        if (retroStatus != null && context != null) {
            String title = retroStatus.getTitle();
            String message = retroStatus.getMessage();
            final SalamanderDialog salamanderDialog = new SalamanderDialog(context);
            salamanderDialog.setAlign(SalamanderDialog.ALIGN_LEFT);
            if (retroStatus.getCode() == RetroStatus.STATUS_SUCCESS) {
                salamanderDialog.setDialogType(SalamanderDialog.DIALOG_INFORMATION);
                salamanderDialog.setAlign(SalamanderDialog.ALIGN_CENTER);
            } else if (retroStatus.getCode() == RetroStatus.STATUS_WARNING)
                salamanderDialog.setDialogType(SalamanderDialog.DIALOG_WARNING);
            else salamanderDialog.setDialogType(SalamanderDialog.DIALOG_ERROR);
            if (!Utils.isEmpty(title))
                salamanderDialog.setDialogTitle(title);
            if (!Utils.isEmpty(message))
                salamanderDialog.setMessage(Utils.textToHtml(message));
            salamanderDialog.setPositiveButtonClickListener(v -> {
                if (finish)
                    ((Activity) context).finish();
            });
            salamanderDialog.show();
        }
    }

    private static String getStringInsideTag(String message, String tag) {
        String openTag = "<" + tag + ">";
        String closeTag = "</" + tag + ">";
        if (message.contains(openTag) && message.contains(closeTag))
            message = message.substring(message.indexOf(openTag) + openTag.length(), message.indexOf(closeTag));
        return message;
    }
}
