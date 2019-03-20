package com.salamander.salamander_logger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LogDBHelper extends SQLiteOpenHelper {

    private Context context;

    public LogDBHelper(Context context) {
        super(context, getDBName(context), null, Const.DATABASE_VERSION);
        this.context = context;
    }

    public static String getDBName(Context context) {
        return context.getApplicationContext().getPackageName()+"_error_log.sqlite";
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Const.CREATE_TABLE_LOG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
