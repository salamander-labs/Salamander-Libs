package com.salamander.salamander_network.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.salamander.salamander_base_module.Utils;

import java.io.File;

public class CertUtil {

    private static final String CERT_PATH = "certPath";

    public static void setCertificate(Context context, File cerFile) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CERT_PATH, cerFile.getAbsolutePath());
        editor.apply();
    }

    public static File getCertificate(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String filePath = sharedPreferences.getString(CERT_PATH, null);
        if (Utils.isEmpty(filePath))
            return null;
        else {
            File certFile = new File(filePath);
            if (certFile.exists())
                return certFile;
            else return null;
        }
    }
}
