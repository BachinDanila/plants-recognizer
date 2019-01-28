package com.example.plantsrecognizer.Activities;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.example.plantsrecognizer.Adapters.MyListAdapterJson;
import com.example.plantsrecognizer.Models.JsonModel;
import com.example.plantsrecognizer.R;
import com.example.plantsrecognizer.Utils.HttpRequest;
import com.example.plantsrecognizer.Utils.JsonParseContent;
import com.example.plantsrecognizer.Utils.JsonUtils;
import com.example.plantsrecognizer.Utils.PreferenceHandler;
import com.example.plantsrecognizer.Utils.XlsParser;

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

    @SuppressWarnings("FieldCanBeLocal")
    private final String raw_url = "https://ru.m.wikipedia.org/w/api.php?action=query&" +
            "prop=pageimages|extracts|pageterms&titles=%s&piprop=original|name|thumbnail&" +
            "pithumbsize=80&continue=&format=json&formatversion=2";
    private static final long serialVersionUID = 1L;
    private final int json_code = 1;

    private transient ListView listView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private transient JsonParseContent parseContent;
    private transient MyListAdapterJson JsonAdapter;
    private transient ArrayList<JsonModel> jsonModelList = null;
    private String[] plants_list = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        PreferenceHandler preferences = new PreferenceHandler(this);
        String swipeThemeName = preferences.getSwipeRefreshLayoutTheme();
        preferences.setTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalogue);

        handleIntent(getIntent());

        listView = findViewById(R.id.listView);
        jsonModelList = new ArrayList<>();

        XlsParser xls_parser = new XlsParser(this);
        plants_list = xls_parser.getXlsPlants();

        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        preferences.setSwipeRefreshLayoutTheme(swipeThemeName, mSwipeRefreshLayout);

        setSwipeRefreshLayoutListener();

        showCatalogueData();
    }

    private void setSwipeRefreshLayoutListener() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                updateCurrentData();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void updateCurrentData() {
        JsonAdapter.clear();
        for (String name : plants_list) {
            try {
                JsonAdapter.addItem(readJsonModel(name));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void showCatalogueData() {
        try {
            for (String aPlants_list : plants_list) {
                jsonModelList.add(readJsonModel(aPlants_list));
            }
            JsonUtils.showSimpleProgressDialog(Catalogue.this);
            ListAdapterInit(jsonModelList);
        } catch (FileNotFoundException e) {
            jsonModelList.clear();
            if (!JsonUtils.isNetworkAvailable(Catalogue.this)) {
                Toast.makeText(Catalogue.this, "Internet is required!", Toast.LENGTH_SHORT).show();
                return;
            }
            parseContent = new JsonParseContent(this);
            for (String aPlants_list : plants_list) {
                parseJson(String.format(raw_url, aPlants_list));
            }
        }
    }

    private void parseJson(final String URL) {
        JsonUtils.showSimpleProgressDialog(Catalogue.this);
        responseJson(URL);
    }

    @SuppressLint("StaticFieldLeak")
    private void responseJson(final String URL) {
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
                onTaskCompleted(result, json_code);
            }
        }.execute();
    }

    private void onTaskCompleted(String response,
                                 @SuppressWarnings("SameParameterValue") int serviceCode) {
        //Log.d("service_code",Integer.toString(serviceCode));
        switch (serviceCode) {
            case json_code:
                JsonModel jsonModel = parseContent.getInfo(response);
                writeFile(jsonModel.getTitle(), jsonModel);
                jsonModelList.add(jsonModel);

                if(jsonModelList.size() == plants_list.length){
                    ListAdapterInit(jsonModelList);
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

    private JsonModel readJsonModel(String filename) throws FileNotFoundException {
        FileInputStream fis = openFileInput(filename);
        try {
            ObjectInputStream is = new ObjectInputStream(fis);
            JsonModel jsonmodel = (JsonModel) is.readObject();
            is.close();
            fis.close();
            return jsonmodel;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
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
        setListViewOnItemClickLisener();
    }

    private void setListViewOnItemClickLisener() {
        //Click on Image with pict.
        //Open Wikipedia page
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Object o = listView.getItemAtPosition(position);
                    JsonModel thisJsonModel = (JsonModel) o;
                    String Title = thisJsonModel.getTitle();

                    if (!JsonUtils.isNetworkAvailable(Catalogue.this)) {
                        Toast.makeText(Catalogue.this, "Internet is required!", Toast.LENGTH_SHORT).show();
                        return;
                    }
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
            assert searchManager != null;
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

    private void checkIfNotFound() {
        if (JsonAdapter.getCount() == 0) {
            Toast toast = Toast.makeText(this, "Not Found", Toast.LENGTH_SHORT);
            toast.show();
            try {
                for (String aPlants_list : plants_list) {
                    JsonAdapter.addItem(readJsonModel(aPlants_list));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //Log.d("SERIALISED", model.getTitle());
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
        checkIfNotFound();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }
}
