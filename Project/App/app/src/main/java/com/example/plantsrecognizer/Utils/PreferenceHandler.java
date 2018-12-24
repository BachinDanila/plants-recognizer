package com.example.plantsrecognizer.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.plantsrecognizer.R;

import java.io.Serializable;

public class PreferenceHandler implements Serializable {

    private final Context context;
    private static final long serialVersionUID = 1L;

    public PreferenceHandler(Context current) {
        context = current;
    }

    public void Handle() {
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
        //Log.v("ThemeName", themeName);
    }
}
