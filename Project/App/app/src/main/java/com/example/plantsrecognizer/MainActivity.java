package com.example.plantsrecognizer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;

public class MainActivity extends AppCompatActivity{

    private int SETTINGS_ACTION = 1;
    private Button settings_button;
    private Button catalogue_button;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);

        String themeName = pref.getString("theme", "AppTheme");
        String galleryName = pref.getString("gallery", "A");

        if (themeName.equals("Theme1")) {
            setTheme(R.style.Theme1);
        }

        else if (themeName.equals("Theme2")) {
            //Toast.makeText(this, "Theme has been reset to " + themeName, Toast.LENGTH_SHORT).show();
            setTheme(R.style.Theme2);
        }

        else if (themeName.equals("AppTheme")){
            setTheme(R.style.AppTheme);
        }

        Log.v("Gallery: ",galleryName);
        Log.v("themeName",themeName);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings_button = findViewById(R.id.settings_button);
        catalogue_button = findViewById(R.id.catalogue_button);

        OnClickListener onClick_Handler = new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.settings_button:
                        startActivityForResult(new Intent(MainActivity.this, ThemePreferenceActivity.class), SETTINGS_ACTION);
                        break;

                    case R.id.catalogue_button:
                        Intent intent = new Intent(MainActivity.this, Catalogue.class);
                        startActivity(intent);
//
//                    case R.id.how_to_use_button:
//                        Intent how_to_use_intent = new Intent(MainActivity.this, HowToUseActivity.class);
//                        startActivity(how_to_use_intent);
//                        break;
//
//                    case R.id.all_question_screen_button:
//                        Intent all_question_screen_intent = new Intent(MainActivity.this, All_questions.class);
//                        startActivity(all_question_screen_intent);
//                        break;
//
//                    case R.id.recommended_question_screen_button:
//                        Intent recommended_question_screen_intent = new Intent(MainActivity.this, Recommended_questions.class);
//                        startActivity(recommended_question_screen_intent);
//                        break;
                }
            }
        };
        catalogue_button.setOnClickListener(onClick_Handler);
        settings_button.setOnClickListener(onClick_Handler);
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
}
