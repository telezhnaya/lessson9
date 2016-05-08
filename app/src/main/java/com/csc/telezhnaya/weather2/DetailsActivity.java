package com.csc.telezhnaya.weather2;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.csc.telezhnaya.weather2.database.WeatherTable;

public class DetailsActivity extends AppCompatActivity {
    public static final String CITY = "CITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Cursor cursor = getContentResolver().query(MainFragment.ENTRIES_URI, null,
                WeatherTable.COLUMN_CITY + "=" + getIntent().getStringExtra(CITY), null, null);
        try {
            cursor.moveToNext();
            ((TextView) findViewById(R.id.city_name)).setText(getString(R.string.city_name)
                    + cursor.getString(cursor.getColumnIndex(WeatherTable.COLUMN_CITY)));
            ((TextView) findViewById(R.id.temperature)).setText(getString(R.string.temperature)
                    + cursor.getString(cursor.getColumnIndex(WeatherTable.COLUMN_TEMPERATURE)));
            ((TextView) findViewById(R.id.pressure)).setText(getString(R.string.pressure)
                    + cursor.getString(cursor.getColumnIndex(WeatherTable.COLUMN_PRESSURE)));
            ((TextView) findViewById(R.id.wind)).setText(getString(R.string.wind)
                    + cursor.getString(cursor.getColumnIndex(WeatherTable.COLUMN_WIND)));
            ((TextView) findViewById(R.id.last_update)).setText(getString(R.string.last_update)
                    + cursor.getString(cursor.getColumnIndex(WeatherTable.COLUMN_REFRESH_TIME)));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public void onRefreshClick(View view) {
        new UpdateWeatherTask(getContentResolver()).execute();
    }

    public void onDeleteClick(View view) {
        getContentResolver().delete(MainFragment.ENTRIES_URI, WeatherTable.COLUMN_CITY + "="
                + getIntent().getStringExtra(CITY), null);
        finish();
    }
}
