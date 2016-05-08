package com.csc.telezhnaya.weather2.database;

import android.provider.BaseColumns;

public interface WeatherTable extends BaseColumns {
    String TABLE_NAME = "WEATHER";

    String COLUMN_CITY = "CITY";
    String COLUMN_REFRESH_TIME = "REFRESH_TIME";
    String COLUMN_TEMPERATURE = "TEMPERATURE";
    String COLUMN_PRESSURE = "PRESSURE";
    String COLUMN_WIND = "WIND";
}
