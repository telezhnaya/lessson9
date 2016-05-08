package com.csc.telezhnaya.weather2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MyOpenHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "com.csc.telezhnaya.weather2";

    private static final String SQL_CREATE_ENTRIES_TABLE = "CREATE TABLE " + WeatherTable.TABLE_NAME + "("
            + WeatherTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + WeatherTable.COLUMN_CITY + " TEXT, "
            + WeatherTable.COLUMN_REFRESH_TIME + " INTEGER, "
            + WeatherTable.COLUMN_TEMPERATURE + " DOUBLE, "
            + WeatherTable.COLUMN_PRESSURE + " INTEGER, "
            + WeatherTable.COLUMN_WIND + " DOUBLE)";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + WeatherTable.TABLE_NAME;

    public MyOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
