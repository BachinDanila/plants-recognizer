package com.example.plantsrecognizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.Serializable;

public class ThemeHandler implements Serializable {

    private Context context;
    private static final long serialVersionUID = 1L;

    public ThemeHandler(Context current) {
        context = current;
    }

    public void Handle() {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);

        String themeName = pref.getString("theme", "Default");

        if (themeName.equals("Light")) {
            context.setTheme(R.style.Light);
        } else if (themeName.equals("Dark")) {
            context.setTheme(R.style.Dark);
        } else if (themeName.equals("Default")) {
            context.setTheme(R.style.Default);
        }
        //Log.v("ThemeName", themeName);
    }
}
