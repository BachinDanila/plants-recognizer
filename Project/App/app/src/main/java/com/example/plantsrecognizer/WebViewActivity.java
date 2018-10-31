package com.example.plantsrecognizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

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
