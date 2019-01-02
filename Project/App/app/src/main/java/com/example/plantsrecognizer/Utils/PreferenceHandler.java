package com.example.plantsrecognizer.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;

import com.example.plantsrecognizer.R;

import java.io.Serializable;

public class PreferenceHandler implements Serializable {

    private final Context context;
    private static final long serialVersionUID = 1L;

    public PreferenceHandler(Context current) {
        context = current;
    }

    public void setTheme() {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);

        String themeName = pref.getString("theme", "Default");

        assert themeName != null;
        switch (themeName) {
            case "Light":
                context.setTheme(R.style.Light);
                break;
            case "Dark":
                context.setTheme(R.style.Dark);
                break;
            case "Default":
                context.setTheme(R.style.Default);
                break;
        }

    }

    public String getSwipeRefreshLayoutTheme() {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        String swipeThemeName = pref.getString("swipeTheme", "Default");

        assert swipeThemeName != null;
        return swipeThemeName;
    }

    public void setSwipeRefreshLayoutTheme(String swipeThemeName, SwipeRefreshLayout mSwipeRefreshLayout) {
        //Log.d("SwipeThemeName", swipeThemeName);
        switch (swipeThemeName) {
            case "Dark":
                //Dark theme enabled by default
                break;
            case "Blue":
                mSwipeRefreshLayout.setColorSchemeResources(R.color.light_blue,
                        R.color.middle_blue,
                        R.color.deep_blue);
                break;
            case "Default":
                mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                        android.R.color.holo_green_light,
                        android.R.color.holo_orange_light,
                        android.R.color.holo_red_light);
                break;
        }
    }

}
