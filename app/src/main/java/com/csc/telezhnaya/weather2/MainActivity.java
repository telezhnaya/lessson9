package com.csc.telezhnaya.weather2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.csc.telezhnaya.weather2.database.WeatherTable;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            Cursor cursor = getContentResolver().query(MainFragment.ENTRIES_URI, null, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() == 0) {
                    //initial
                    ContentValues values = new ContentValues();
                    values.put(WeatherTable.COLUMN_CITY, "Moscow");
                    getContentResolver().insert(MainFragment.ENTRIES_URI, values);
                }
                cursor.close();
            }

            new UpdateWeatherTask(getContentResolver()).execute();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_fragment, new MainFragment()).commit();
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
            Cursor cursor = getContentResolver().query(MainFragment.ENTRIES_URI, null,
                    WeatherTable.COLUMN_CITY + " = '" + text + "'", null, null);
            if (cursor == null || cursor.getCount() == 0) {
                ContentValues values = new ContentValues();
                values.put(WeatherTable.COLUMN_CITY, text);
                getContentResolver().insert(MainFragment.ENTRIES_URI, values);
                new UpdateWeatherTask(getContentResolver()).execute();
                editText.setText("");
            }

            if (cursor != null) {
                cursor.close();
            }
        }

    }

    public class TimeNotification extends BroadcastReceiver {
        public TimeNotification() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
                new UpdateWeatherTask(context.getContentResolver()).execute();
        }
    }
}
