package com.example.plantsrecognizer.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.plantsrecognizer.Models.JsonModel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class CatalogueDataObject {

    @SuppressWarnings("FieldCanBeLocal")
    private final String raw_url = "https://ru.m.wikipedia.org/w/api.php?action=query&" +
            "prop=pageimages|extracts|pageterms&titles=%s&piprop=original|name|thumbnail&" +
            "pithumbsize=80&continue=&format=json&formatversion=2";
    @SuppressWarnings("FieldCanBeLocal")
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private JsonParseContent parseContent;
    private ArrayList<JsonModel> jsonModelList;
    private String[] plants_list;

    public CatalogueDataObject(Context context) {
        this.context = context;
        XlsParser xls_parser = new XlsParser(this.context);
        plants_list = xls_parser.getXlsPlants();
        jsonModelList = new ArrayList<>();
    }

    public ArrayList<JsonModel> getDataFromFile() throws FileNotFoundException {

        for (String aPlants_list : plants_list) {
            jsonModelList.add(readJsonModel(aPlants_list));
        }
        JsonUtils.removeSimpleProgressDialog();
        return jsonModelList;
    }

    public ArrayList<JsonModel> getDataFromInternet() {

        JsonUtils.showSimpleProgressDialog(context);

        if (jsonModelList != null) {
            jsonModelList.clear();
        }
        //parseContent = new JsonParseContent(context);
        for (String aPlants_list : plants_list) {
            parseJson(String.format(raw_url, aPlants_list));
        }
        return jsonModelList;
    }

    private String getResponse(final String URL) {
        String response;
        HashMap<String, String> map = new HashMap<>();
        try {
            HttpRequest req = new HttpRequest(URL);
            response = req.prepare(HttpRequest.Method.POST).withData(map).sendAndReadString();
            Log.d("url", response);
        } catch (Exception e) {
            response = e.getMessage();
        }
        return response;
    }

    private void setDataFromResponse(String response) {
        JsonModel jsonModel = parseContent.getInfo(response);
        writeFile(jsonModel.getTitle(), jsonModel);
        jsonModelList.add(jsonModel);
        if (jsonModelList.size() == plants_list.length) {
            JsonUtils.removeSimpleProgressDialog();
        }
    }

    private void parseJson(final String URL) {

        String response;
        if (!JsonUtils.isNetworkAvailable(context)) {
            Toast.makeText(context, "Internet is required!", Toast.LENGTH_SHORT).show();
            return;
        }
        response = getResponse(URL);
        try {
            setDataFromResponse(response);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    private void writeFile(String filename, JsonModel object) {
        try {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(object);
            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JsonModel readJsonModel(String filename) throws FileNotFoundException {
        FileInputStream fis = context.openFileInput(filename);
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
}
