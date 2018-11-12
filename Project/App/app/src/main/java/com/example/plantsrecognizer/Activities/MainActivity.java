package com.example.plantsrecognizer.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.example.plantsrecognizer.R;
import com.example.plantsrecognizer.Utils.PreferenceHandler;

public class MainActivity extends AppCompatActivity{

    private final int SETTINGS_ACTION = 1;
    PreferenceHandler handler;

    ImageButton settings_button;
    ImageButton catalogue_button;
    ImageButton all_questions_button;

    private Animation mEnlargeAnimation;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        handler = new PreferenceHandler(this);
        handler.Handle();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings_button = findViewById(R.id.settings_button);
        catalogue_button = findViewById(R.id.catalogue_button);
        all_questions_button = findViewById(R.id.all_questions_button);

        mEnlargeAnimation = AnimationUtils.loadAnimation(this, R.anim.button_animation_zoom);

        OnClickListener onClickHandler = new OnClickListener() {
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
                        //all_questions_button.startAnimation(mShrinkAnimation);
                        //all_questions_button.clearAnimation();
                        startActivity(new Intent(MainActivity.this, AllQuestions.class));
                        break;
                }
            }
        };

        catalogue_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        catalogue_button.startAnimation(mEnlargeAnimation);
                        break;

                    case MotionEvent.ACTION_UP:
                        v.performClick();
                        break;
                }
                return true;
            }
        });

        settings_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        settings_button.startAnimation(mEnlargeAnimation);
                        break;

                    case MotionEvent.ACTION_UP:
                        v.performClick();
                        break;
                }
                return true;
            }
        });

        all_questions_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        all_questions_button.startAnimation(mEnlargeAnimation);
                        //Log.d("DISTANCE",Float.toString(event.getDownTime()));
                        break;
                    case MotionEvent.ACTION_UP:
                        //all_questions_button.clearAnimation();
                        //onEnterAnimationComplete();
                        v.performClick();
                        break;
                }
                return true;
            }
        });

        catalogue_button.setOnClickListener(onClickHandler);
        settings_button.setOnClickListener(onClickHandler);
        all_questions_button.setOnClickListener(onClickHandler);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTINGS_ACTION) {
            if (resultCode == ThemePreferenceActivity.RESULT_CODE_THEME_UPDATED) {
                finish();
                startActivity(getIntent());
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
