package com.salamander.logger;

import android.database.Cursor;

public class Const {

    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_LOG = "ErrorLog";

    public static final String CREATE_TABLE_LOG = "CREATE TABLE " + TABLE_LOG + "(" +
            ErrorLog.ERROR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            ErrorLog.ERROR_CLASS_NAME + " TEXT, " +
            ErrorLog.ERROR_METHOD_NAME + " TEXT, " +
            ErrorLog.ERROR_EXCEPTION + " TEXT, " +
            ErrorLog.ERROR_LOGCAT + " TEXT, " +
            ErrorLog.ERROR_MESSAGE + " TEXT, " +
            ErrorLog.ERROR_LINE_NUMBER + " INTEGER," +
            ErrorLog.ERROR_TANGGAL + " DATETIME )";

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
