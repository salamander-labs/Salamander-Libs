package com.salamander.salamander_base_module;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.text.DecimalFormat;
import java.util.Locale;

public class Utils {

    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (activity.getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            Log.e("hideKeyboard", e.toString());
        }
    }

    public static void hideKeyboard(Activity activity, View view) {
        try {
            view.requestFocus();
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            Log.e("hideKeyboard", e.toString());
        }
    }

    public static boolean isEqual(String str1, String str2) {
        return str1.equals(str2);
    }

    public static boolean isEmpty(String teks) {
        return teks == null || teks.trim().isEmpty();
    }

    public static void showLog(String className, String methodName, String logText) {
        Log.e(Salamander.SHORT_PACKAGE_NAME, className + " => " + methodName + " => " + logText);
    }

    public static String formatNumber(double d) {
        DecimalFormat numberFormat = new DecimalFormat("###,###,##0.00");
        Locale.setDefault(Locale.US);
        return numberFormat.format(d);
    }

    public static String formatNumber(String format, double d) {
        DecimalFormat numberFormat = new DecimalFormat(format);
        Locale.setDefault(Locale.US);
        return numberFormat.format(d);
    }

}
