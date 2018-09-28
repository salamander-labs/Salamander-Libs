package com.salamander.salamander_network.log;

import android.database.Cursor;

public class Constant {

    public static final int DATABASE_VERSION = 1;

    public static final String FORMAT_DB = "yyyy-MM-dd";
    public static final String FORMAT_DB_FULL = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DB_FULL_NO_SEC = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_UI = "EEEE, dd MMMM yyyy";
    public static final String FORMAT_UI_NO_DAY = "dd MMMM yyyy";

    public static final String TABLE_NETWORK_LOG = "NetworkLog";
    public static final String NETWORK_ID = "ID";
    public static final String NETWORK_USER_ID = "UserID";
    public static final String NETWORK_WAKTU = "Waktu";
    public static final String NETWORK_ACTIVITY_NAME = "ActivityName";
    public static final String NETWORK_CLASS_NAME = "ClassName";
    public static final String NETWORK_METHOD_NAME = "MethodName";
    public static final String NETWORK_PARAMETER = "Parameter";
    public static final String NETWORK_STATUS_CODE = "Code";
    public static final String NETWORK_URL = "URL";
    public static final String NETWORK_HEADER = "Header";
    public static final String NETWORK_RESULT = "Result";

    public static final String CREATE_TABLE_NETWORK_LOG = "CREATE TABLE " + TABLE_NETWORK_LOG + "(" +
            NETWORK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            NETWORK_USER_ID + " INTEGER, " +
            NETWORK_WAKTU + " DATETIME, " +
            NETWORK_ACTIVITY_NAME + " TEXT," +
            NETWORK_CLASS_NAME + " TEXT," +
            NETWORK_METHOD_NAME + " TEXT," +
            NETWORK_PARAMETER + " TEXT," +
            NETWORK_STATUS_CODE + " TEXT," +
            NETWORK_URL + " TEXT," +
            NETWORK_HEADER + " TEXT," +
            NETWORK_RESULT + " TEXT )";

    public static int getInt(Cursor cursor, String key) {
        return cursor.getInt(cursor.getColumnIndex(key));
    }

    public static String getString(Cursor cursor, String key) {
        return cursor.getString(cursor.getColumnIndex(key));
    }

    public static double getDouble(Cursor cursor, String key) {
        return cursor.getDouble(cursor.getColumnIndex(key));
    }
}