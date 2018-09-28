package com.salamander.salamander_network.log;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private Context context;

    public DBHelper(Context context) {
        super(context, getDBName(context), null, Constant.DATABASE_VERSION);
        this.context = context;
    }

    public static String getDBName(Context context) {
        return context.getApplicationContext().getPackageName()+"_network_log.sqlite";
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Constant.CREATE_TABLE_NETWORK_LOG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
