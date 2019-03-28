package com.salamander.salamander_base_module;

import android.app.Activity;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class Utils {

    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (activity.getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            Utils.showLog(e);
        }
    }

    public static void hideKeyboard(Activity activity, View view) {
        try {
            view.requestFocus();
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            Utils.showLog(e);
        }
    }

    public static boolean isEqual(String str1, String str2) {
        return str1.equals(str2);
    }

    public static boolean isEmpty(String teks) {
        return teks == null || teks.trim().isEmpty();
    }

    public static boolean isEmpty(EditText editText) {
        return editText.getText() == null || editText.getText().toString().trim().isEmpty();
    }

    public static boolean isEmpty(TextView textView) {
        return textView.getText() == null || textView.getText().toString().trim().isEmpty();
    }

    @SuppressWarnings("deprecation")
    public static Spanned textToHtml(String text) {
        if (Utils.isEmpty(text))
            text = "";

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
            return Html.fromHtml(text);
        else
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
    }

    public static void showLog(String logText) {
        StackTraceElement[] stackTraceElementList = Thread.currentThread().getStackTrace();
        String packageName = "com.salamander";
        for (StackTraceElement stackTraceElement : stackTraceElementList) {
            if (stackTraceElement.toString().contains(packageName) &&
                    !stackTraceElement.toString().contains("<init>") &&
                    !stackTraceElement.getClassName().contains(BuildConfig.APPLICATION_ID) &&
                    !stackTraceElement.isNativeMethod()) {
                Log.e(Salamander.getInstance().getTAG(),
                        "=>\n==============================================================================================================================================" +
                                "\nClassName \t: " + stackTraceElement.getClassName() +
                                "\nMethodName \t: " + stackTraceElement.getMethodName() +
                                "\nLineNumber \t: " + stackTraceElement.getLineNumber() +
                                "\nLog \t\t: " + logText +
                                "\n==============================================================================================================================================\n.");
                break;
            }
        }
    }

    public static void showLog(Exception exception) {
        showLog(exception.getClass().getName() + " => " + exception.getMessage());
    }

    public static void showLog(Throwable throwable) {
        StackTraceElement[] stackTraceElementList;
        if (throwable.getCause() != null)
            stackTraceElementList = throwable.getCause().getStackTrace();
        else stackTraceElementList = throwable.getStackTrace();
        String packageName = "com.salamander";
        for (StackTraceElement stackTraceElement : stackTraceElementList) {
            if (stackTraceElement.toString().contains(packageName) &&
                    !stackTraceElement.toString().contains("<init>") &&
                    !stackTraceElement.getClassName().contains(BuildConfig.APPLICATION_ID) &&
                    !stackTraceElement.isNativeMethod()) {
                Log.e(Salamander.getInstance().getTAG(),
                        "=>\n==============================================================================================================================================" +
                                "\nClassName \t: " + stackTraceElement.getClassName() +
                                "\nMethodName \t: " + stackTraceElement.getMethodName() +
                                "\nLineNumber \t: " + stackTraceElement.getLineNumber() +
                                "\nLog \t\t: " + (throwable.getCause() != null ? throwable.getCause().getLocalizedMessage() : throwable.getLocalizedMessage()) +
                                "\n==============================================================================================================================================\n.");
                break;
            }
        }
    }

    public static void showLog(String identifier, Exception exception) {
        showLog(identifier + " => " + exception.getMessage());
    }
}