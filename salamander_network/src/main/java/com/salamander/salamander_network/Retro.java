package com.salamander.salamander_network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;

import com.salamander.salamander_base_module.Utils;
import com.salamander.salamander_network.gson.GsonConverterFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Retro {

    private static final String TAG = "Retro";

    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_ERROR = "error";
    public static final String STATUS_WARNING = "warning";
    public static final int STATUS_SUCCESS_CODE = 1;

    private static String getErrorMessage(ByteArrayInputStream byteArrayInputStreamError) {
        if (byteArrayInputStreamError == null)
            return null;
        BufferedReader r = new BufferedReader(new InputStreamReader(byteArrayInputStreamError));
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

    public static String getString(Response<ResponseBody> response) {
        Response<ResponseBody> bodyResponse = response;
        try {
            if (response.isSuccessful()) {
                String respon = getResponseBody(response).string().trim();
                if (!(Utils.isEmpty(respon)))
                    return respon;
                else
                    return null;
            } else {
                //InputStream i = getErrorBody(response).byteStream();
                //ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(getBytesFromInputStream(i));
                //return getErrorMessage(byteArrayInputStreamError);
            }
        } catch (Exception e) {
            Utils.showLog(Retro.class.getSimpleName(), "getString", e.toString());
            return null;
        }
        return null;
    }
    public static byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        try {
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        } catch (OutOfMemoryError error) {
            return null;
        }
    }

    public static RetroStatus getRetroStatus(Response<ResponseBody> response, ByteArrayInputStream byteArrayInputStreamError, String json) {
        Utils.showLog(Retro.class.getSimpleName(), "getStatus", json);
        if (!Utils.isEmpty(json) && JSON.isJSONObject(json)) {
            RetroStatus retroStatus = new RetroStatus();
            JSONObject jsonObj;
            try {
                jsonObj = new JSONObject(json);
                if (jsonObj.has("retroStatus")) {
                    JSONObject jsonObject = jsonObj.getJSONObject("retroStatus");
                    if (jsonObject.has("status"))
                        retroStatus.setSuccess(jsonObject.getString("status").trim().toLowerCase().equals(STATUS_SUCCESS));
                    if (jsonObject.has("title"))
                        retroStatus.setMessage(jsonObject.getString("title").trim());
                    if (jsonObject.has("msg"))
                        retroStatus.setMessage(jsonObject.getString("msg").trim());
                    else if (jsonObject.has("message"))
                        retroStatus.setMessage(jsonObject.getString("message").trim());
                    if (jsonObject.has("sql"))
                        retroStatus.setQuery(jsonObject.getString("sql").trim());
                }
            } catch (JSONException e) {
                Utils.showLog(Retro.class.getSimpleName(), "getStatus", e.toString());
                retroStatus.setMessage(json);
            }
            return retroStatus;
        } else {
            try {
                String errorMsg = getErrorMessage(byteArrayInputStreamError);
                if (Utils.isEmpty(errorMsg))
                    errorMsg = json;
                if (Utils.isEmpty(errorMsg))
                    errorMsg = response.message();
                if (response != null && response.code() != 200)
                    return new RetroStatus(false, "Error : "+String.valueOf(response.code()), getErrorMsgFromCode(response.code(), errorMsg), null);
                return new RetroStatus(false, "Server Error", errorMsg, "");
            } catch (Exception e) {
                if (response != null && response.code() != 200)
                    return new RetroStatus(false, "Error : "+String.valueOf(response.code()), response.message(), null);
                return new RetroStatus(false, "Server Error", e.toString(), "");
            }
        }
    }

    private static String getErrorMsgFromCode(int code, String errorMsg) {
        switch (code) {
            case 400 : return "Page Not Found";
            default: return errorMsg;
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

    public static ResponseBody getResponseBody(final Response<ResponseBody> response) {
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

    public static ResponseBody getErrorBody(final Response<ResponseBody> response) {
        if (response.errorBody() == null)
            return null;
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

    @Nullable
    private static ResponseBody cloneResponseBody(@Nullable final ResponseBody body) {
        if (body == null) {
            return null;
        }
        final Buffer buffer = new Buffer();
        try {
            BufferedSource source = body.source();
            buffer.writeAll(source);
            source.close();
        } catch (IOException e) {
            Log.w(TAG, "Failed to clone ResponseBody");
            return null;
        }
        return new ResponseBody() {
            @Override
            public MediaType contentType() {
                return body.contentType();
            }

            @Override
            public long contentLength() {
                return buffer.size();
            }

            @Override
            public BufferedSource source() {
                return buffer.clone();
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
                .connectTimeout(2, TimeUnit.MINUTES)
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
                .connectTimeout(2, TimeUnit.MINUTES)
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
            Log.d(TAG, "getSSLConfig([context])  => " + e.toString());
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

            builder.connectTimeout(2, TimeUnit.MINUTES);
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
}
