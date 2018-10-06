package com.example.plantsrecognizer;

import android.app.Activity;
import android.text.Html;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class JsonParseContent {

    private Activity activity;

    ArrayList<HashMap<String, String>> arraylist;

    public JsonParseContent(Activity activity) {
        this.activity = activity;
    }

    public String parse_extracts(char string[]){
        String new_string = "";
        for(int i = 0; i < string.length; i++){
            if(string[i] == '\n'){
                break;
            }
            else if(string[i] == '—' || string[i] == '-'){
                new_string = "";
                i++;
                if(string[i] == ' '){
                    i++;
                }
            }
            new_string += string[i];
        }
        return new_string;
    }
    public JsonModel getInfo(String response) {
        JsonModel jsonModel = new JsonModel();
        try {
            JSONObject jsonObject = new JSONObject(response);
            arraylist = new ArrayList<HashMap<String, String>>();

            JSONObject all_dataobj = jsonObject.getJSONObject("query").getJSONArray("pages").getJSONObject(0);

            try{
                JSONObject description_dataobj = all_dataobj.getJSONObject("terms");
                String description_row = description_dataobj.getString(JsonConstants.Params.DESCRIPTION);
                String description = description_row.substring(2,description_row.length() - 2);
                jsonModel.setDescription(description);
            }catch (JSONException e){
                String extracts = all_dataobj.getString(JsonConstants.Params.EXTRACTS);
                extracts = Html.fromHtml(extracts).toString();
                String description = parse_extracts(extracts.toCharArray());
                //Log.v("Extracts",description);
                jsonModel.setDescription(description);
            }

            jsonModel.setTitle(all_dataobj.getString(JsonConstants.Params.TITLE));

            String source = all_dataobj.getJSONObject("thumbnail").getString(JsonConstants.Params.SOURCE);
            jsonModel.setSource(source);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonModel;
    }
}
