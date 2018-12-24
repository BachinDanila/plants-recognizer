package com.example.plantsrecognizer.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;

import com.example.plantsrecognizer.R;
import com.example.plantsrecognizer.Utils.PreferenceHandler;
import com.example.plantsrecognizer.Views.MyWebView;

public class WebViewActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        PreferenceHandler handler = new PreferenceHandler(this);
        handler.Handle();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.row_my_web);

        String raw_url = "https://ru.m.wikipedia.org/wiki/";
        Intent intent = getIntent();
        String Title = intent.getStringExtra("Title");

        setTitle(Title);

        MyWebView webView = findViewById(R.id.myWebView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(raw_url + Title);
    }
}
