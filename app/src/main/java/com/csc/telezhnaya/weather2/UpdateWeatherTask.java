package com.csc.telezhnaya.weather2;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;

import com.csc.telezhnaya.weather2.database.WeatherTable;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;

public class UpdateWeatherTask extends AsyncTask<Void, Void, ArrayList<UpdateWeatherTask.JsonWeatherDescription>> {
    private static final String URL = "http://api.openweathermap.org/data/2.5/weather";
    private ContentResolver resolver;

    UpdateWeatherTask(ContentResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    protected ArrayList<JsonWeatherDescription> doInBackground(Void... params) {
        ArrayList<String> cities = new ArrayList<>();
        Cursor cursor = resolver.query(MainFragment.ENTRIES_URI, new String[]{WeatherTable.COLUMN_CITY}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                cities.add(cursor.getString(cursor.getColumnIndex(WeatherTable.COLUMN_CITY)));
            }
            cursor.close();
        }

        ArrayList<JsonWeatherDescription> weathers = new ArrayList<>();
        for (String city : cities) {
            StringBuilder json = new StringBuilder();
            try {
                URL urlObject = new URL(URL + "?q=" + URLEncoder.encode(city, "UTF-8") + "&APPID=76c1fbdf0c24d3528b0ed685d88f31ea");
                HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String next = reader.readLine();
                do {
                    json.append(next);
                    next = reader.readLine();
                } while (next != null);

                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            JsonWeatherDescription description = new Gson().fromJson(json.toString(), JsonWeatherDescription.class);
            if (description != null) {
                weathers.add(description);
            }
        }
        return weathers;
    }

    @Override
    protected void onPostExecute(ArrayList<JsonWeatherDescription> cities) {
        for (JsonWeatherDescription description : cities) {
            ContentValues values = new ContentValues();
            values.put(WeatherTable.COLUMN_TEMPERATURE, description.main.temp);
            values.put(WeatherTable.COLUMN_PRESSURE, description.main.pressure);
            values.put(WeatherTable.COLUMN_WIND, description.wind.speed);
            values.put(WeatherTable.COLUMN_REFRESH_TIME, new Date().toString());
            resolver.update(MainFragment.ENTRIES_URI, values, WeatherTable.COLUMN_CITY + " = '" + description.name + "'", null);
        }
    }

    class JsonWeatherDescription {
        public String name;
        public int id;
        public ArrayList<Weather> weather;
        public Main main;
        public Wind wind;

        public class Weather {
            public int id;
        }

        public class Main {
            public double temp;
            public double pressure;
        }

        public class Wind {
            public double speed;
        }
    }
}
