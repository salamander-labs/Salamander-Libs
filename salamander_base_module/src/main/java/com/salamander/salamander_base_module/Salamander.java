package com.salamander.salamander_base_module;

import android.app.Application;

public class Salamander extends Application {

    public static String PACKAGE_NAME = null;
    public static String SHORT_PACKAGE_NAME = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static void init(String packageName) {
        PACKAGE_NAME = packageName;
        String[] splitString = packageName.split("\\.");
        SHORT_PACKAGE_NAME = splitString[splitString.length - 1].toUpperCase();
    }
}
