package com.example.plantsrecognizer.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import com.example.plantsrecognizer.Models.JsonModel;
import com.example.plantsrecognizer.R;
import com.example.plantsrecognizer.Utils.HttpRequest;
import com.example.plantsrecognizer.Utils.JsonParseContent;
import com.example.plantsrecognizer.Utils.JsonUtils;
import com.example.plantsrecognizer.Utils.XlsParser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class CustomPreferenceActivity extends PreferenceActivity {

    public static final int RESULT_CODE_THEME_UPDATED = 1;
    public static final int RESULT_CODE_SWIPE_THEME_UPDATED = 2;

    @SuppressWarnings("FieldCanBeLocal")
    private final String raw_url = "https://ru.m.wikipedia.org/w/api.php?action=query&" +
            "prop=pageimages|extracts|pageterms&titles=%s&piprop=original|name|thumbnail&" +
            "pithumbsize=80&continue=&format=json&formatversion=2";
    private final int json_code = 1;
    private String[] plants_list;
    private JsonParseContent parseContent;
    private int numOfCompletedTasks = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        XlsParser xlsParser = new XlsParser(this);
        plants_list = xlsParser.getXlsPlants();

        parseContent = new JsonParseContent(this);

        //TODO: Solve problem with deprecated methods
        //noinspection deprecation
        addPreferencesFromResource(R.xml.preferences);
        //noinspection deprecation
        findPreference("theme").
                setOnPreferenceChangeListener(new RefreshActivityOnPreferenceChangeListener(RESULT_CODE_THEME_UPDATED));
        //noinspection deprecation
        findPreference("swipeTheme").
                setOnPreferenceChangeListener(new RefreshActivityOnPreferenceChangeListener(RESULT_CODE_SWIPE_THEME_UPDATED));

        //noinspection deprecation
        Preference button = findPreference("update_button");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                updateAppData();
                return true;
            }
        });
    }

    private void updateAppData() {
        if (!JsonUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "Internet is required!", Toast.LENGTH_SHORT).show();
            return;
        }
        for (String aPlants_list : plants_list) {
            parseJson(String.format(raw_url, aPlants_list));
        }
    }

    private void parseJson(final String URL) {
        JsonUtils.showSimpleProgressDialog(this);
        responseJson(URL);
    }

    @SuppressLint("StaticFieldLeak")
    private void responseJson(final String URL) {
        new AsyncTask<Void, Void, String>() {
            protected String doInBackground(Void[] params) {
                String response;
                HashMap<String, String> map = new HashMap<>();
                try {
                    HttpRequest req = new HttpRequest(URL);
                    response = req.prepare(HttpRequest.Method.POST).withData(map).sendAndReadString();
                } catch (Exception e) {
                    response = e.getMessage();
                }
                return response;
            }

            protected void onPostExecute(String result) {
                //do something with response
                onTaskCompleted(result, json_code);
            }
        }.execute();
    }

    private void onTaskCompleted(String response,
                                 @SuppressWarnings("SameParameterValue") int serviceCode) {
        //Log.d("service_code",Integer.toString(serviceCode));
        switch (serviceCode) {
            case json_code:
                numOfCompletedTasks++;
                JsonModel jsonModel = parseContent.getInfo(response);
                writeFile(jsonModel.getTitle(), jsonModel);

                if (numOfCompletedTasks == plants_list.length) {
                    numOfCompletedTasks = 0;
                    JsonUtils.removeSimpleProgressDialog();
                }
                break;
        }
    }

    private void writeFile(String filename, JsonModel object) {
        try {
            FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(object);
            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class RefreshActivityOnPreferenceChangeListener implements OnPreferenceChangeListener {
        private final int resultCode;

        RefreshActivityOnPreferenceChangeListener(int resultCode) {
            this.resultCode = resultCode;
        }

        @Override
        public boolean onPreferenceChange(Preference p, Object newValue) {
            setResult(resultCode);
            return true;
        }
    }
}