package com.example.plantsrecognizer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends AppCompatActivity{

    private final int SETTINGS_ACTION = 1;
    Button settings_button;
    Button catalogue_button;
    Button all_questions_button;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);

        ThemeHandler(pref);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings_button = findViewById(R.id.settings_button);
        catalogue_button = findViewById(R.id.catalogue_button);
        all_questions_button = findViewById(R.id.all_questions_button);

        OnClickListener onClick_Handler = new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.settings_button:
                        startActivityForResult(new Intent(MainActivity.this, ThemePreferenceActivity.class), SETTINGS_ACTION);
                        break;

                    case R.id.catalogue_button:
                        startActivity(new Intent(MainActivity.this, Catalogue.class));
                        break;

                    case R.id.all_questions_button:
                        startActivity(new Intent(MainActivity.this, All_Questions.class));
                        break;
                }
            }
        };
        catalogue_button.setOnClickListener(onClick_Handler);
        settings_button.setOnClickListener(onClick_Handler);
        all_questions_button.setOnClickListener(onClick_Handler);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTINGS_ACTION) {
            if (resultCode == ThemePreferenceActivity.RESULT_CODE_THEME_UPDATED) {
                finish();
                startActivity(getIntent());
                return;
            }
            if (resultCode == ThemePreferenceActivity.RESULT_CODE_GALLERY_UPDATED){
                finish();
                startActivity(getIntent());
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void ThemeHandler(SharedPreferences pref) {
        String themeName = pref.getString("theme", "AppTheme");
        String galleryName = pref.getString("gallery", "A");

        if (themeName.equals("Theme1")) {
            setTheme(R.style.Theme1);
        } else if (themeName.equals("Theme2")) {
            //Toast.makeText(this, "Theme has been reset to " + themeName, Toast.LENGTH_SHORT).show();
            setTheme(R.style.Theme2);
        } else if (themeName.equals("AppTheme")) {
            setTheme(R.style.AppTheme);
        }

        Log.v("Gallery: ", galleryName);
        Log.v("themeName", themeName);
    }
}
