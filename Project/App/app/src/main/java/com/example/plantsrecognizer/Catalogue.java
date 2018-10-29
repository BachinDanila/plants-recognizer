package com.example.plantsrecognizer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Catalogue extends AppCompatActivity {

    ListView listView;
    MyListAdapterJson JsonAdapter;
    ArrayList<JsonModel> jsonModelList;

    private JsonParseContent parseContent;
    private final int jsoncode = 1;
    JsonModel jsonModel;

    String raw_url = "https://ru.m.wikipedia.org/w/api.php?action=query&prop=pageimages|extracts|pageterms&titles=%s&piprop=original|name|thumbnail&pithumbsize=150&continue=&format=json&formatversion=2";
    String plants_list[] = {"Берёза","Тополь"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalogue);

        listView = (ListView) findViewById(R.id.listView);
        parseContent = new JsonParseContent(this);
        jsonModelList = new ArrayList<>();

        try {
            for(int i = 0; i< plants_list.length;i++){
                parseJson(String.format(raw_url,plants_list[i]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
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

    private void parseJson(final String URL) throws IOException, JSONException {

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
                Log.d("newwwss",result);
                onTaskCompleted(result,jsoncode);
            }
        }.execute();
    }
    public void onTaskCompleted(String response, int serviceCode) {
        Log.d("responsejson", response.toString());
        Log.d("service_code",Integer.toString(serviceCode));
        switch (serviceCode) {
            case jsoncode:
                JsonUtils.removeSimpleProgressDialog();  //will remove progress dialog
                jsonModel = parseContent.getInfo(response);
                jsonModelList.add(jsonModel);

                if(jsonModelList.size() == plants_list.length){
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
        }
    }
}
