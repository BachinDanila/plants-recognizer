package com.example.plantsrecognizer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class All_Questions extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_question);
        XlsParser xls = new XlsParser(this);

    }
}
