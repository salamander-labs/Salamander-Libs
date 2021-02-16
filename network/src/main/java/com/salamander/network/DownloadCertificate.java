package com.salamander.network;

import android.content.Context;
import android.os.AsyncTask;

import com.salamander.core.Utils;
import com.salamander.core.utils.DateUtils;
import com.salamander.network.utils.CertUtil;
import com.salamander.network.utils.ConnectionUtils;

import org.threeten.bp.temporal.ChronoUnit;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Calendar;

public class DownloadCertificate extends AsyncTask<String, String, String> {

    private static final String TAG = "CERT_DOWNLOAD";
    //private static final String CERT_URL = "http://www.datascrip.co.id/ssl/datascrip_co_id.crt";
    private static final String CERT_URL = "http://dtswebapi.datascrip.co.id/ssl/datascrip_co_id.crt";

    private WeakReference<Context> mWeakContext;
    private CertUtil.PostDownload callback;
    private String aURL = CERT_URL, errorMessage, certText;

    public DownloadCertificate(Context context, CertUtil.PostDownload callback) {
        this.mWeakContext = new WeakReference<>(context);
        this.callback = callback;
        if (isCertificateValid())
            onPostExecute(null);
        else execute();
    }

    public DownloadCertificate(Context context, String aURL, CertUtil.PostDownload callback) {
        this.mWeakContext = new WeakReference<>(context);
        this.callback = callback;
        this.aURL = aURL;
        if (!isCertificateValid())
            onPostExecute(null);
        else execute();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... aurl) {
        try {
            URL url;
            if (aURL != null)
                url = new URL(aURL);
            else url = new URL(CERT_URL);
            URLConnection connection =  url.openConnection();
            //connection.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
            //connection.setRequestProperty("Accept","*/*");
            connection.connect();

            int lengthOfFile = connection.getContentLength();
            Utils.showLog("DownloadCertificate => Length of the file: " + lengthOfFile);

            InputStream inputStream = url.openStream();
            InputStream input = new BufferedInputStream(inputStream);
            certText = CertUtil.streamToString(input);
        } catch (Exception e) {
            errorMessage = e.getClass() + " => " + e.getMessage();
        }
        return certText;
    }

    @Override
    protected void onPostExecute(String certText) {
        if (callback != null && mWeakContext != null) {
            if (certText != null) {
                if (!ConnectionUtils.isConnected(mWeakContext.get()))
                    errorMessage = "Not connected to internet.\nCheck your connection and try again";
                else CertUtil.setCertificate(mWeakContext.get(), certText);
            }
            callback.downloadDone(errorMessage);
        }
    }

    private boolean isCertificateValid() {
        if (Utils.isEmpty(CertUtil.getCertificateText(mWeakContext.get())))
            return false;
        try {
            Certificate certificate = CertUtil.getCertificate(mWeakContext.get());
            if (certificate != null) {
                long currentDate = System.currentTimeMillis();
                long startDate = ((X509Certificate) certificate).getNotBefore().getTime();
                long endDate = ((X509Certificate) certificate).getNotAfter().getTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(endDate);
                calendar.add(Calendar.MONTH, -1);
                return currentDate > startDate &&
                        currentDate < endDate &&
                        DateUtils.interval(ChronoUnit.DAYS, currentDate, endDate) > 30 &&
                        DateUtils.interval(ChronoUnit.DAYS, startDate, currentDate) > 30;
            }
        } catch (Exception e) {
            Utils.showLog(e);
        }
        return false;
    }
}