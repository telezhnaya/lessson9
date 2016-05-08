package com.csc.telezhnaya.weather2;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import com.csc.telezhnaya.weather2.database.WeatherTable;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class UpdateWeatherTask extends AsyncTask<String, Void, ArrayList<UpdateWeatherTask.JsonWeatherDescription>> {
    private static final String URL = "http://api.openweathermap.org/data/2.5/weather";
    private ContentResolver resolver;
    private View view;
    private Resources resources;

    UpdateWeatherTask(ContentResolver resolver, View view, Resources resources) {
        this.resolver = resolver;
        this.view = view;
        this.resources = resources;
    }

    @Override
    protected ArrayList<JsonWeatherDescription> doInBackground(String... params) {
        ArrayList<JsonWeatherDescription> weathers = new ArrayList<>();
        for (String city : params) {
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
        long time = Calendar.getInstance().getTimeInMillis();
        for (JsonWeatherDescription description : cities) {
            ContentValues values = new ContentValues();
            if (description.id == 0) continue;
            values.put(WeatherTable.COLUMN_TEMPERATURE, description.main.temp);
            values.put(WeatherTable.COLUMN_PRESSURE, description.main.pressure);
            values.put(WeatherTable.COLUMN_WIND, description.wind.speed);
            values.put(WeatherTable.COLUMN_REFRESH_TIME, time);
            resolver.update(MainActivity.ENTRIES_URI, values, WeatherTable.COLUMN_CITY + " = '" + description.name + "'", null);
        }
        if (view != null && cities.size() == 1) {
            JsonWeatherDescription d = cities.get(0);
            ((TextView) view.findViewById(R.id.city_name)).setText(resources.getString(R.string.city_name) + d.name);
            ((TextView) view.findViewById(R.id.temperature)).setText(resources.getString(R.string.temperature) + d.main.temp);
            ((TextView) view.findViewById(R.id.pressure)).setText(resources.getString(R.string.pressure) + d.main.pressure);
            ((TextView) view.findViewById(R.id.wind)).setText(resources.getString(R.string.wind) + d.wind.speed);
            ((TextView) view.findViewById(R.id.last_update)).setText(resources.getString(R.string.last_update)
                    + new Date(time).toString());
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
