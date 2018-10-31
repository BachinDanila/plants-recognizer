package com.example.plantsrecognizer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Catalogue extends AppCompatActivity {

    final String raw_url = "https://ru.m.wikipedia.org/w/api.php?action=query&prop=pageimages|extracts|pageterms&titles=%s&piprop=original|name|thumbnail&pithumbsize=150&continue=&format=json&formatversion=2";
    MyListAdapterJson JsonAdapter;
    XlsParser xls_parser;
    private ListView listView;
    private JsonParseContent parseContent;
    private final int jsoncode = 1;
    JsonModel jsonModel;
    private ArrayList<JsonModel> jsonModelList;
    private String[] plants_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalogue);

        listView = findViewById(R.id.listView);
        parseContent = new JsonParseContent(this);
        jsonModelList = new ArrayList<>();

        xls_parser = new XlsParser(this);
        plants_list = xls_parser.getXls_plants();

        for (int i = 0; i < plants_list.length; i++) {
            parseJson(String.format(raw_url, plants_list[i]));
        }
    }

    private void print_log(int index){
        Log.v("Begin ","_____________________________________________________________");
        Log.v("Source: ",jsonModelList.get(index).getSource());
        Log.v("Description: ",jsonModelList.get(index).getDescription());
        Log.v("Title: ",jsonModelList.get(index).getTitle());
        Log.v("Height: ",jsonModelList.get(index).getImageHeight());
        Log.v("Width: ",jsonModelList.get(index).getImageWidth());
        Log.v("End ","_____________________________________________________________");
    }

    private void parseJson(final String URL) {

        if (!JsonUtils.isNetworkAvailable(Catalogue.this)) {
            Toast.makeText(Catalogue.this, "Internet is required!", Toast.LENGTH_SHORT).show();
            return;
        }
        JsonUtils.showSimpleProgressDialog(Catalogue.this);
        new AsyncTask<Void, Void, String>(){
            protected String doInBackground(Void[] params) {
                String response="";
                HashMap<String, String> map=new HashMap<>();
                try {
                    HttpRequest req = new HttpRequest(URL);
                    response = req.prepare(HttpRequest.Method.POST).withData(map).sendAndReadString();
                } catch (Exception e) {
                    response=e.getMessage();
                }
                return response;
            }
            protected void onPostExecute(String result) {
                //do something with response
                //Log.d("newwwss",result);
                onTaskCompleted(result,jsoncode);
            }
        }.execute();
    }

    private void write_file(JsonModel jsonModel) {
        FileOutputStream outputStream;
        String filename = jsonModel.getTitle();
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(jsonModel.getSource().getBytes());
            outputStream.write("\n".getBytes());
            outputStream.write(jsonModel.getDescription().getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JsonModel read_file(String filename) throws FileNotFoundException {
        byte[] buffer = new byte[1000];
        JsonModel jsonModel_raw = new JsonModel();
        InputStream input = openFileInput(filename);
        // read fills buffer with data and returns
        // the number of bytes read (which of course
        // may be less than the buffer size, but
        // it will never be more).
        int total = 0;
        int nRead = 0;
        try {
            while ((nRead = input.read(buffer)) != -1) {
                // Convert to String so we can display it.
                // Of course you wouldn't want to do this with
                // a 'real' binary file.
                String[] data = buffer_processing(new String(buffer));
                jsonModel_raw.setDescription(data[0]);
                jsonModel_raw.setSource(data[1]);
                jsonModel_raw.setTitle(filename);
                //Log.v("BUFFER",data[0] + " " + data[1]);
                total += nRead;
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonModel_raw;
    }

    private String[] buffer_processing(String string) {
        char[] buffer = string.toCharArray();
        String data[] = {"", ""};
        for (int i = 0; i < string.length(); i++) {
            if (buffer[i] == 0) {
                break;
            }
            if (buffer[i] == '\n') {
                data[1] = data[0];
                data[0] = "";
            }
            if (buffer[i] != '\n')
                data[0] += buffer[i];
        }
        return data;
    }

    private void onTaskCompleted(String response, int serviceCode) {
        //Log.d("responsejson", response);
        //Log.d("service_code",Integer.toString(serviceCode));
        switch (serviceCode) {
            case jsoncode:
                try {
                    String filename = parseContent.getTitle(response);
                    jsonModel = read_file(filename);
                    //Log.d("DATA", jsonModel.getTitle() + " " + jsonModel.getDescription() + " " + jsonModel.getSource());
                } catch (FileNotFoundException e) {
                    jsonModel = parseContent.getInfo(response);
                    write_file(jsonModel);
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }

                jsonModelList.add(jsonModel);

                if(jsonModelList.size() == plants_list.length){
                    JsonUtils.removeSimpleProgressDialog();  //will remove progress dialog
                    JsonAdapter = new MyListAdapterJson(this,jsonModelList);
                    listView.setAdapter(JsonAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            Object o = listView.getItemAtPosition(position);
                            JsonModel thisJsonModel = (JsonModel)o;
                            String Title = thisJsonModel.getTitle();

                            //Toast.makeText(getBaseContext(),Title,Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(Catalogue.this, WebViewActivity.class);
                            intent.putExtra("Title",Title);
                            startActivity(intent);
                        }
                    });
                }
                break;
        }
    }
}
