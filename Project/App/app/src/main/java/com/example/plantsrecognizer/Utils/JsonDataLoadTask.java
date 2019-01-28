package com.example.plantsrecognizer.Utils;

import android.os.AsyncTask;

import java.util.HashMap;

public class JsonDataLoadTask extends AsyncTask<Void, Void, String> {

    @SuppressWarnings("FieldCanBeLocal")
    private final int json_code = 1;
    private String URL;
    private MyCustomCallBack callback;

    JsonDataLoadTask(final MyCustomCallBack callback, String url) {
        this.callback = callback;
        URL = url;
    }

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
        if (callback != null) {
            callback.onPostExecuteCall();
            callback.onTaskCompleted(result, json_code);
        }
    }

    public interface MyCustomCallBack {
        void onPostExecuteCall();

        void onTaskCompleted(String response, int service_code);
    }

}
