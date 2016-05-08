package com.csc.telezhnaya.weather2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.csc.telezhnaya.weather2.database.MyContentProvider;
import com.csc.telezhnaya.weather2.database.WeatherTable;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    public static final Uri ENTRIES_URI = Uri.withAppendedPath(MyContentProvider.CONTENT_URI, "entries");
    public static final String CITY = "CITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            Cursor initCursor = getContentResolver().query(ENTRIES_URI, null, null, null, null);
            if (initCursor != null) {
                if (initCursor.getCount() == 0) {
                    //initial
                    ContentValues values = new ContentValues();
                    values.put(WeatherTable.COLUMN_CITY, "Moscow");
                    getContentResolver().insert(ENTRIES_URI, values);
                }
                initCursor.close();
            }

            updateAllWeather(getContentResolver());
        }

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_all, new MainFragment()).commit();
        if (findViewById(R.id.fragment_details) != null) {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_details, new DetailsFragment()).commit();
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, TimeNotification.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(),
                60 * 60 * 1000, pendingIntent);
    }

    public void onAddClick(View view) {
        EditText editText = (EditText) ((View) view.getParent()).findViewById(R.id.city_name);
        String text = editText.getText().toString();
        if (!text.isEmpty()) {
            Cursor cursor = getContentResolver().query(ENTRIES_URI, null,
                    WeatherTable.COLUMN_CITY + " = '" + text + "'", null, null);
            if (cursor == null || cursor.getCount() == 0) {
                ContentValues values = new ContentValues();
                values.put(WeatherTable.COLUMN_CITY, text);
                getContentResolver().insert(ENTRIES_URI, values);
                new UpdateWeatherTask(getContentResolver(), null, null).execute(text);
                editText.setText("");
            }

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static void updateAllWeather(ContentResolver resolver) {
        Cursor cursor = resolver.query(ENTRIES_URI,
                new String[]{WeatherTable.COLUMN_CITY}, null, null, null);
        if (cursor != null) {
            String[] cities = new String[cursor.getCount()];
            for (int i = 0; i < cities.length; i++) {
                cursor.moveToNext();
                cities[i] = cursor.getString(cursor.getColumnIndex(WeatherTable.COLUMN_CITY));
            }
            cursor.close();
            new UpdateWeatherTask(resolver, null, null).execute(cities);
        }
    }

    public static class TimeNotification extends BroadcastReceiver {
        public TimeNotification() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            updateAllWeather(context.getContentResolver());
        }
    }
}
