package com.csc.telezhnaya.weather2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.csc.telezhnaya.weather2.database.WeatherTable;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private CursorAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listTasks = (ListView) view.findViewById(R.id.list_items);
        adapter = new CursorAdapter(getContext(), null, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(R.layout.city_item, parent, false);
            }

            @Override
            public void bindView(final View view, Context context, Cursor cursor) {
                LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.item_list);
                TextView temperature = (TextView) view.findViewById(R.id.item_list_temp);
                temperature.setText(cursor.getString(cursor.getColumnIndex(WeatherTable.COLUMN_TEMPERATURE)));

                final String name = cursor.getString(cursor.getColumnIndex(WeatherTable.COLUMN_CITY));
                TextView city = (TextView) view.findViewById(R.id.item_list_name);
                city.setText(name + ": (F)");

                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FrameLayout details = (FrameLayout) getActivity().findViewById(R.id.fragment_details);
                        Bundle bundle = new Bundle();
                        bundle.putString(MainActivity.CITY, "'" + name + "'");
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        DetailsFragment fragment = new DetailsFragment();
                        fragment.setArguments(bundle);
                        if (details == null) {
                            ft.replace(R.id.fragment_all, fragment).commit();
                        } else {
                            ft.add(R.id.fragment_details, fragment).commit();
                        }
                    }
                });
            }
        };
        listTasks.setAdapter(adapter);
        getActivity().getSupportLoaderManager().initLoader(0, null, this);
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), MainActivity.ENTRIES_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
