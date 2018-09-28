package com.salamander.salamander_network.log;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.salamander.salamander_base_module.DateUtils;
import com.salamander.salamander_base_module.object.Tanggal;

import java.util.Date;

public class NetworkLogSQLite {

    private Context context;
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public NetworkLogSQLite(Context context) {
        dbHelper = new DBHelper(context);
        this.context = context;
    }

    public boolean Insert(NetworkLog networkLog) {
        try {
            db = dbHelper.getWritableDatabase();
            long result = db.insertOrThrow(Constant.TABLE_NETWORK_LOG, null, networkLogToContentValues(networkLog));
            db.close();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean Post(NetworkLog networkLog) {
        return Insert(networkLog);
    }

    public void clear() {
        db = dbHelper.getReadableDatabase();
        String query = "DELETE FROM " + Constant.TABLE_NETWORK_LOG;
        db.execSQL(query);
        db.close();
    }

    private ContentValues networkLogToContentValues(NetworkLog networkLog) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.NETWORK_USER_ID, networkLog.getUserID());
        contentValues.put(Constant.NETWORK_WAKTU, DateUtils.dateToString(Tanggal.FORMAT_DATETIME_FULL, new Date()));
        contentValues.put(Constant.NETWORK_ACTIVITY_NAME, networkLog.getRetroData().getActivityName());
        contentValues.put(Constant.NETWORK_CLASS_NAME, networkLog.getRetroData().getClassName());
        contentValues.put(Constant.NETWORK_METHOD_NAME, networkLog.getRetroData().getMethodName());
        contentValues.put(Constant.NETWORK_PARAMETER, networkLog.getRetroData().getParameter());
        contentValues.put(Constant.NETWORK_STATUS_CODE, networkLog.getRetroData().getRetroStatus().getStatusCode());
        contentValues.put(Constant.NETWORK_URL, networkLog.getRetroData().getRetroStatus().getURL());
        contentValues.put(Constant.NETWORK_HEADER, networkLog.getRetroData().getRetroStatus().getHeader());
        contentValues.put(Constant.NETWORK_RESULT, networkLog.getRetroData().getResult());
        return contentValues;
    }
}