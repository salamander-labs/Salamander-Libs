package com.salamander.network.retro;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;

import com.salamander.core.Utils;
import com.salamander.core.widget.SalamanderDialog;
import com.salamander.network.gson.GsonConverterFactory;
import com.salamander.network.utils.CertUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import androidx.annotation.NonNull;
import okhttp3.Interceptor;
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
        Pattern pattern = Pattern.compile("((?s)<div(.*)div>)((?s).*)", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(response);
        String result = response;
        if (matcher.find())
            result = matcher.group(1);
        return result;
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

//
//    public static RetroStatus getRetroStatus(Response<ResponseBody> response, String json) {
//        Utils.showLog(json);
//        json = getJSON(json);
//        if (Utils.isEmpty(json)) {
//            try {
//                ResponseBody errorBody = getErrorBody(response);
//                String errorMsg = errorBody.string();
//                /*
//                if (response != null && response.code() != 200)
//                    return new RetroStatus(false, response.code() + " " + response.message(), errorMsg.substring(errorMsg.indexOf("<h1>") + "<h1>".length(), errorMsg.indexOf("</h1>")) + "<br/>" + errorMsg.substring(errorMsg.indexOf("<p>") + "<p>".length(), errorMsg.indexOf("</p>")));
//                return new RetroStatus(false, "Network Error.", errorMsg.substring(errorMsg.indexOf("<h1>") + "<h1>".length(), errorMsg.indexOf("</h1>")) + "<br/>" + errorMsg.substring(errorMsg.indexOf("<p>") + "<p>".length(), errorMsg.indexOf("</p>")));
//                */
//                if (response != null && response.code() != 200)
//                    return new RetroStatus(false, response.code() + " " + response.message(), errorMsg);
//                return new RetroStatus(false, "Error.", errorMsg);
//            } catch (Exception e) {
//                if (response != null && response.code() != 200)
//                    return new RetroStatus(false, response.code() + " " + response.message(), null);
//                return new RetroStatus(false, e.toString(), null);
//            }
//        } else {
//            RetroStatus retroStatus = new RetroStatus();
//            JSONObject jsonObject;
//            try {
//                jsonObject = new JSONObject(json);
//                if (jsonObject.has(RetroConst.RETRO_RESPONSE_STATUS)) {
//
//                    JSONObject jsonObjectStatus = jsonObject.getJSONObject(RetroConst.RETRO_RESPONSE_STATUS);
//                    retroStatus = new RetroStatus(jsonObjectStatus);
//                    /*
//                    if (jsonObjectStatus.has(RetroConst.RETRO_STATUS))
//                        retroStatus.setSuccess(jsonObjectStatus.getString(RetroConst.RETRO_STATUS).trim().toLowerCase().equals(RetroConst.STATUS_SUCCESS));
//                    else if (jsonObjectStatus.has(RetroConst.RETRO_RESPONSE_STATUS))
//                        retroStatus.setSuccess(jsonObjectStatus.getInt(RetroConst.RETRO_RESPONSE_STATUS) == RetroConst.STATUS_SUCCESS_CODE);
//                    if (jsonObjectStatus.has(RetroConst.RETRO_TITLE))
//                        retroStatus.setMessage(jsonObjectStatus.getString(RetroConst.RETRO_TITLE).trim());
//                    if (jsonObjectStatus.has(RetroConst.RETRO_MSG))
//                        retroStatus.setMessage(jsonObjectStatus.getString(RetroConst.RETRO_MSG).trim());
//                    else if (jsonObjectStatus.has(RetroConst.RETRO_MESSAGE))
//                        retroStatus.setMessage(jsonObjectStatus.getString(RetroConst.RETRO_MESSAGE).trim());
//                    if (jsonObjectStatus.has(RetroConst.RETRO_SQL))
//                        retroStatus.setQuery(jsonObjectStatus.getString(RetroConst.RETRO_SQL).trim());
//                        */
//                } else if (jsonObject.has(RetroConst.RETRO_STATUS)) {
//
//                    JSONObject jsonObjectStatus = jsonObject.getJSONObject(RetroConst.RETRO_STATUS);
//                    retroStatus = new RetroStatus(jsonObjectStatus);
//                    /*
//                    if (jsonObjectStatus.has(RetroConst.RETRO_STATUS))
//                        retroStatus.setSuccess(jsonObjectStatus.getString(RetroConst.RETRO_STATUS).trim().toLowerCase().equals(RetroConst.STATUS_SUCCESS));
//                    else if (jsonObjectStatus.has(RetroConst.RETRO_STATUS))
//                        retroStatus.setSuccess(jsonObjectStatus.getInt(RetroConst.RETRO_STATUS) == RetroConst.STATUS_SUCCESS_CODE);
//                    if (jsonObjectStatus.has(RetroConst.RETRO_TITLE))
//                        retroStatus.setMessage(jsonObjectStatus.getString(RetroConst.RETRO_TITLE).trim());
//                    if (jsonObjectStatus.has(RetroConst.RETRO_MSG))
//                        retroStatus.setMessage(jsonObjectStatus.getString(RetroConst.RETRO_MSG).trim());
//                    else if (jsonObjectStatus.has(RetroConst.RETRO_MESSAGE))
//                        retroStatus.setMessage(jsonObjectStatus.getString(RetroConst.RETRO_MESSAGE).trim());
//                    if (jsonObjectStatus.has(RetroConst.RETRO_SQL))
//                        retroStatus.setQuery(jsonObjectStatus.getString(RetroConst.RETRO_SQL).trim());
//                        */
//                }
//            } catch (JSONException e) {
//                Utils.showLog(e);
//                try {
//                    retroStatus.setTitle((json.toLowerCase().contains("title") ? getStringInsideTag(json, "title") : "Error"));
//                    retroStatus.setMessage(getStringInsideTag(json, "body"));
//                } catch (Exception e1) {
//                    retroStatus.setMessage(json);
//                }
//            }
//            return retroStatus;
//        }
//    }

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


    public static Retrofit createRetrofit(Context context, String URL) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.MINUTES);
        SSLContext sslContext = getSSLConfig(context);
        if (sslContext != null)
            client.sslSocketFactory(sslContext.getSocketFactory());
        return new Retrofit.Builder()
                .baseUrl(URL)
                .client(client.build())
                .build();
    }

    public static Retrofit createRetrofit(Context context, String URL, GsonConverterFactory gsonConverterFactory) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        okhttp3.Response response = chain.proceed(request);
                        return response;
                    }
                })
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.MINUTES);
        SSLContext sslContext = getSSLConfig(context);
        if (sslContext != null)
            client.sslSocketFactory(sslContext.getSocketFactory());
        return new Retrofit.Builder()
                .baseUrl(URL)
                .client(client.build())
                .addConverterFactory(gsonConverterFactory)
                .build();
    }

    public static SSLContext getSSLConfig(Context context) { // throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = null;
        try {
            // Loading CAs from an InputStream
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate ca;
            InputStream cert = new FileInputStream(CertUtil.getCertificate(context));
            try {
                ca = cf.generateCertificate(cert);
            } finally {
                if (cert != null)
                    cert.close();
            }

            // Creating a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Creating a TrustManager that trusts the CAs in our KeyStore.
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Creating an SSLSocketFactory that uses our TrustManager
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
        } catch (Exception e) {
            Log.d("getSSLConfig", "getSSLConfig([context])  => " + e.toString());
        }
        return sslContext;
    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };
            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);//, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            builder.connectTimeout(30, TimeUnit.SECONDS);
            builder.readTimeout(2, TimeUnit.MINUTES);
            builder.addInterceptor(interceptor);

            return builder.build();
        } catch (Exception e) {
            //FileUtil.writeExceptionLog(context, App.class.getSimpleName() + " => getUnsafeOkHttpClient  => ", e);
            throw new RuntimeException(e);
        }
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void showRetroDialog(final Context context, RetroStatus retroStatus) {
        showRetroDialog(context, retroStatus, false);
    }

    public static void showRetroDialog(final Context context, RetroStatus retroStatus, final boolean finish) {
        if (retroStatus != null) {
            String title = retroStatus.getTitle();
            String message = retroStatus.getMessage();
            final SalamanderDialog salamanderDialog = new SalamanderDialog(context);
            if (retroStatus.getCode() == RetroStatus.STATUS_SUCCESS)
                salamanderDialog.setDialogType(SalamanderDialog.DIALOG_INFORMATION);
            if (retroStatus.getCode() == RetroStatus.STATUS_WARNING)
                salamanderDialog.setDialogType(SalamanderDialog.DIALOG_WARNING);
            else salamanderDialog.setDialogType(SalamanderDialog.DIALOG_ERROR);
            salamanderDialog.setAlign(SalamanderDialog.ALIGN_LEFT);
            if (!Utils.isEmpty(title))
                salamanderDialog.setDialogTitle(title);
            if (!Utils.isEmpty(message))
                salamanderDialog.setMessage(Utils.textToHtml(message));
            salamanderDialog.setPositiveButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finish)
                        ((Activity) context).finish();
                }
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
