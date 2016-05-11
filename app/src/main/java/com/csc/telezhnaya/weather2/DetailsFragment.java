package com.csc.telezhnaya.weather2;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.csc.telezhnaya.weather2.database.WeatherTable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DetailsFragment extends Fragment {
    private int currId;
    private String currCity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        String projection = getActivity().getIntent().getStringExtra(MainActivity.CITY);
        if (projection == null && getArguments() != null) {
            projection = getArguments().getString(MainActivity.CITY);
        }
        if (projection != null) {
            projection = WeatherTable.COLUMN_CITY + "=" + projection;
        }
        Cursor cursor = getActivity().getContentResolver().query(MainActivity.ENTRIES_URI, null, projection, null, null);
        if (cursor != null) {
            cursor.moveToNext();
            setAll(view, cursor);
            cursor.close();
        }

        Button refresh = (Button) view.findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateWeatherTask(getActivity().getContentResolver(), (View) v.getParent(), getResources()).execute(currCity);
            }
        });

        final Button next = (Button) view.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View p = (View) v.getParent();
                Cursor idCursor = getActivity().getContentResolver().query(
                        MainActivity.ENTRIES_URI, new String[]{WeatherTable._ID}, null, null, null);
                if (idCursor != null) {
                    List<Integer> allIds = new ArrayList<>(idCursor.getCount());
                    while (idCursor.moveToNext()) {
                        allIds.add(idCursor.getInt(idCursor.getColumnIndex(WeatherTable._ID)));
                    }
                    idCursor.close();

                    Cursor cursor = getActivity().getContentResolver().query(MainActivity.ENTRIES_URI, null,
                            WeatherTable._ID + "=" + allIds.get((allIds.indexOf(currId) + 1) % allIds.size()), null, null);
                    if (cursor != null) {
                        cursor.moveToNext();
                        setAll(p, cursor);
                        cursor.close();
                    }
                }
            }
        });

        Button delete = (Button) view.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getContentResolver().delete(MainActivity.ENTRIES_URI, WeatherTable.COLUMN_CITY + "='"
                        + currCity + "'", null);
                next.callOnClick();
            }
        });

        Button back = (Button) view.findViewById(R.id.back);
        if (back != null) {
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getFragmentManager().beginTransaction().replace(R.id.fragment_all, new MainFragment()).commit();
                }
            });
        }

        return view;
    }

    private void setAll(View view, Cursor cursor) {
        currId = cursor.getInt(cursor.getColumnIndex(WeatherTable._ID));
        currCity = cursor.getString(cursor.getColumnIndex(WeatherTable.COLUMN_CITY));
        ((TextView) view.findViewById(R.id.city_name)).setText(getString(R.string.city_name) + currCity);
        ((TextView) view.findViewById(R.id.temperature)).setText(getString(R.string.temperature)
                + cursor.getString(cursor.getColumnIndex(WeatherTable.COLUMN_TEMPERATURE)));
        ((TextView) view.findViewById(R.id.pressure)).setText(getString(R.string.pressure)
                + cursor.getString(cursor.getColumnIndex(WeatherTable.COLUMN_PRESSURE)));
        ((TextView) view.findViewById(R.id.wind)).setText(getString(R.string.wind)
                + cursor.getString(cursor.getColumnIndex(WeatherTable.COLUMN_WIND)));
        ((TextView) view.findViewById(R.id.last_update)).setText(getString(R.string.last_update)
                + new Date(cursor.getLong(cursor.getColumnIndex(WeatherTable.COLUMN_REFRESH_TIME))).toString());
    }
}
