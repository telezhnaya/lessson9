package com.csc.lesson9;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Random;

public class MasterFragment extends Fragment {
    public static final String EXTRA_TITLE = "extra_title";

    public static MasterFragment newInstance(String title) {
        MasterFragment masterFragment = new MasterFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_TITLE, title);
        masterFragment.setArguments(bundle);
        return masterFragment;
    }

    public MasterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        view.setBackgroundColor(makeRandomColor());
        ((TextView) view.findViewById(R.id.tv)).setText(getArguments().getString(EXTRA_TITLE));
        return view;
    }

    private static int makeRandomColor() {
        Random rnd = new Random();
        return Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }
}
