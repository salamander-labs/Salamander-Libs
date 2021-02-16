package com.salamander.logger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.salamander.core.utils.DateUtils;

import java.util.ArrayList;

public class LogSQLite {

    private Context context;
    private LogDBHelper dbHelper;
    private SQLiteDatabase db;

    public LogSQLite(Context context) {
        dbHelper = new LogDBHelper(context);
        this.context = context;
    }

    public boolean Insert(ErrorLog errorLog) {
        try {
            db = dbHelper.getWritableDatabase();
            long result = db.insertOrThrow(Const.TABLE_LOG, null, errorLogToContentValues(errorLog));
            db.close();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean Post(ErrorLog errorLog) {
        return Insert(errorLog);
    }

    public ArrayList<ErrorLog> getAll() {
        db = dbHelper.getReadableDatabase();
        ArrayList<ErrorLog> list_error = new ArrayList<>();
        String query = "SELECT * FROM " + Const.TABLE_LOG;
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do
                list_error.add(cursorToErrorLog(c));
            while (c.moveToNext());
        }
        db.close();
        c.close();
        return list_error;
    }

    public void clear() {
        db = dbHelper.getWritableDatabase();
        String query = "DELETE FROM " + Const.TABLE_LOG;
        db.execSQL(query);
        db.close();
    }

    private ContentValues errorLogToContentValues(ErrorLog errorLog) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ErrorLog.ERROR_CLASS_NAME, errorLog.getClassName());
        contentValues.put(ErrorLog.ERROR_METHOD_NAME, errorLog.getMethodName());
        contentValues.put(ErrorLog.ERROR_LINE_NUMBER, errorLog.getLineNumber());
        contentValues.put(ErrorLog.ERROR_EXCEPTION, errorLog.getException());
        contentValues.put(ErrorLog.ERROR_LOGCAT, errorLog.getLogCat());
        contentValues.put(ErrorLog.ERROR_MESSAGE, errorLog.getMessage());
        contentValues.put(ErrorLog.ERROR_TANGGAL, DateUtils.format(DateUtils.FORMAT_DATETIME_FULL, errorLog.getErrorDate()));
        return contentValues;
    }

    private ErrorLog cursorToErrorLog(Cursor cursor) {
        ErrorLog errorLog = new ErrorLog();
        errorLog.setClassName(cursor.getString(cursor.getColumnIndex(ErrorLog.ERROR_CLASS_NAME)));
        errorLog.setMethodName(cursor.getString(cursor.getColumnIndex(ErrorLog.ERROR_METHOD_NAME)));
        errorLog.setLineNumber(cursor.getInt(cursor.getColumnIndex(ErrorLog.ERROR_LINE_NUMBER)));
        errorLog.setException(cursor.getString(cursor.getColumnIndex(ErrorLog.ERROR_EXCEPTION)));
        errorLog.setLogCat(cursor.getString(cursor.getColumnIndex(ErrorLog.ERROR_LOGCAT)));
        errorLog.setMessage(cursor.getString(cursor.getColumnIndex(ErrorLog.ERROR_MESSAGE)));
        errorLog.setErrorDate(DateUtils.toLong(DateUtils.FORMAT_DATETIME_FULL, cursor.getString(cursor.getColumnIndex(ErrorLog.ERROR_TANGGAL))));
        return errorLog;
    }
}
