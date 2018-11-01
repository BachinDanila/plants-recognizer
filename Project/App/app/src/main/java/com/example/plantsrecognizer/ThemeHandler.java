package com.example.plantsrecognizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class ThemeHandler {

    private Context context;

    public ThemeHandler(Context current) {
        context = current;
    }

    public void Handle() {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);

        String themeName = pref.getString("theme", "AppTheme");

        if (themeName.equals("Theme1")) {
            context.setTheme(R.style.Theme1);
        } else if (themeName.equals("Theme2")) {
            //Toast.makeText(this, "Theme has been reset to " + themeName, Toast.LENGTH_SHORT).show();
            context.setTheme(R.style.Theme2);
        } else if (themeName.equals("AppTheme")) {
            context.setTheme(R.style.AppTheme);
        }
        Log.v("ThemeName", themeName);
    }

    public void Handle(Object themeName) {
        if (themeName.equals("Theme1")) {
            context.setTheme(R.style.Theme1);
        } else if (themeName.equals("Theme2")) {
            //Toast.makeText(this, "Theme has been reset to " + themeName, Toast.LENGTH_SHORT).show();
            context.setTheme(R.style.Theme2);
        } else if (themeName.equals("AppTheme")) {
            context.setTheme(R.style.AppTheme);
        }
    }
}
