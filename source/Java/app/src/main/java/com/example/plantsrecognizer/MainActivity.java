package com.example.plantsrecognizer;

import android.app.Activity;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;

public class MainActivity extends AppCompatActivity {

    Button settings_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings_button = findViewById(R.id.settings_button);

        OnClickListener onClick_Handler = new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.settings_button:
                        Intent settings_intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(settings_intent);
                        break;

//                    case R.id.about_button:
//                        Intent about_intent = new Intent(MainActivity.this, AboutActivity.class);
//                        startActivity(about_intent);
//                        break;
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
        settings_button.setOnClickListener(onClick_Handler);
    }
}
