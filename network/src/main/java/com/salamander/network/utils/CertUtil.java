package com.salamander.network.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.salamander.core.Utils;
import com.salamander.network.DownloadCertificate;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

public class CertUtil {

    private static final String CERT_PATH = "certPath";
    private static final String CERT_TEXT = "certText";

    public static void setCertificate(Context context, String cerText) {
        if (cerText != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(CERT_TEXT, cerText.trim());
            editor.apply();
        }
    }

    public static Certificate getCertificate(Context context) {
        Certificate certificate = null;
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            try (InputStream inputStream = CertUtil.stringToStream(CertUtil.getCertificateText(context))) {
                certificate = certificateFactory.generateCertificate(inputStream);
            }
        } catch (Exception e) {
            Utils.showLog(e);
        }
        return certificate;
    }

    public static String getCertificateText(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getString(CERT_TEXT, null);
    }

    public static InputStream stringToStream(String input) {
        //use ByteArrayInputStream to get the bytes of the String and convert them to InputStream.
        return new ByteArrayInputStream(input.getBytes(Charset.forName("UTF-8")));
    }

    public static String streamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the
         * BufferedReader.readLine() method. We iterate until the BufferedReader
         * return null which means there's no more data to read. Each line will
         * appended to a StringBuilder and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static void downloadCertificate(Context context, PostDownload postDownload) {
        new DownloadCertificate(context, postDownload);
    }

    public interface PostDownload {
        void downloadDone(String errorMessage);
    }
}
