package com.example.plantsrecognizer;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Catalogue extends AppCompatActivity implements Serializable {

    final String raw_url = "https://ru.m.wikipedia.org/w/api.php?action=query&prop=pageimages|extracts|pageterms&titles=%s&piprop=original|name|thumbnail&pithumbsize=150&continue=&format=json&formatversion=2";
    private static final long serialVersionUID = 1L;
    transient MyListAdapterJson JsonAdapter;
    XlsParser xls_parser;
    JsonModel jsonModel;
    ThemeHandler handler;

    private transient ListView listView;
    private transient JsonParseContent parseContent;
    private final int jsoncode = 1;
    private transient ArrayList<JsonModel> jsonModelList = null;
    private String[] plants_list = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        handler = new ThemeHandler(this);
        handler.Handle();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalogue);

        handleIntent(getIntent());

        listView = findViewById(R.id.listView);
        jsonModelList = new ArrayList<>();

        xls_parser = new XlsParser(this);
        plants_list = xls_parser.getXls_plants();

        try {
            for (int i = 0; i < plants_list.length; i++) {
                jsonModelList.add(read_jsonModel(plants_list[i]));
            }
            JsonUtils.showSimpleProgressDialog(Catalogue.this);
            ListAdapterInit(jsonModelList);
        } catch (FileNotFoundException e) {
            jsonModelList.clear();
            parseContent = new JsonParseContent(this);
            for (int i = 0; i < plants_list.length; i++) {
                parseJson(String.format(raw_url, plants_list[i]));
            }
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
                String response;
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

    private void onTaskCompleted(String response, int serviceCode) {
        //Log.d("responsejson", response);
        //Log.d("service_code",Integer.toString(serviceCode));
        switch (serviceCode) {
            case jsoncode:
                jsonModel = parseContent.getInfo(response);
                write_file(jsonModel.getTitle(), jsonModel);
                jsonModelList.add(jsonModel);

                if(jsonModelList.size() == plants_list.length){
                    ListAdapterInit(jsonModelList);
                }
                break;
        }
    }

    private void write_file(String filename, JsonModel object) {
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

    private JsonModel read_jsonModel(String filename) throws FileNotFoundException {
        FileInputStream fis = openFileInput(filename);
        try {
            ObjectInputStream is = new ObjectInputStream(fis);
            JsonModel jsonmodel = (JsonModel) is.readObject();
            is.close();
            fis.close();
            return jsonmodel;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void ListAdapterInit(ArrayList<JsonModel> jsonModelList) {
        JsonAdapter = new MyListAdapterJson(this, jsonModelList);
        JsonAdapter.notifyDataSetChanged();
        listView.setAdapter(JsonAdapter);
        try {
            JsonUtils.removeSimpleProgressDialog();  //will remove progress dialog
        } catch (Exception e) {
            e.printStackTrace();
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Object o = listView.getItemAtPosition(position);
                    JsonModel thisJsonModel = (JsonModel) o;
                    String Title = thisJsonModel.getTitle();
                    //Toast.makeText(getBaseContext(),Title,Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Catalogue.this, WebViewActivity.class);
                    intent.putExtra("Title", Title);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.dashboard, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            search(query);
            Log.d("SEARCH", query);
        }
    }

    private void search(String keyword) {
        int len = JsonAdapter.getCount();
        for (int j = 0; j < len; j++) {
            for (int i = 0; i < JsonAdapter.getCount(); i++) {
                JsonModel current = (JsonModel) JsonAdapter.getItem(i);
                String text = current.getTitle().toLowerCase();
                if (!text.contains(keyword.toLowerCase())) {
                    JsonAdapter.removeItem(i);
                }
            }
        }
        if (JsonAdapter.getCount() == 0) {
            Toast toast = Toast.makeText(this, "Not Found", Toast.LENGTH_SHORT);
            toast.show();
            try {
                for (int i = 0; i < plants_list.length; i++) {
                    JsonAdapter.addItem(read_jsonModel(plants_list[i]));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //Log.d("SERIALISED", model.getTitle());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }
}
