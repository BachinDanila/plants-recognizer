package com.example.plantsrecognizer;

import android.support.v7.app.AppCompatActivity;
import android.text.Html;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class JsonParseContent {

    final AppCompatActivity activity;

    private ArrayList<HashMap<String, String>> arraylist;

    public JsonParseContent(AppCompatActivity activity) {
        this.activity = activity;
    }

    private String charArrayToString(char string[], int max_index) {
        String new_string = "";
        for (int i = 0; i < max_index; i++) {
            new_string += string[i];
        }
        return new_string;
    }

    private String parse_extracts(char string[]) {
        String new_string = "";
        int max_limit = 159;
        int tmp = max_limit;
        int counter = 0;

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
        if (new_string.length() > max_limit) {
            string = new_string.toCharArray();
            for (int i = max_limit; i >= 0; i--) {
                if (string[i] == '.') {
                    tmp = i;
                    break;
                }
            }
            if (tmp == max_limit) {
                for (int i = tmp; i >= 0; i--)
                    if (string[i] == ' ' || string[i] == ',') {
                        counter++;
                        if (counter == 2) {
                            tmp = i;
                            break;
                        }
                    }
                string[tmp] = '.';
                tmp++;
                string[tmp] = '.';
                tmp++;
                string[tmp] = '.';
                tmp++;
            }
            new_string = charArrayToString(string, tmp);
        }
        try {
            string = new_string.toCharArray();
            string[0] = Character.toUpperCase(string[0]);
            new_string = charArrayToString(string, string.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new_string;
    }

    public String getTitle(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject all_dataobj = jsonObject.getJSONObject("query").getJSONArray("pages").getJSONObject(0);
            String title = all_dataobj.getString(JsonConstants.Params.TITLE);
            return title;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JsonModel getInfo(String response) {
        JsonModel jsonModel = new JsonModel();
        int limit = 20;
        try {
            JSONObject jsonObject = new JSONObject(response);
            arraylist = new ArrayList<HashMap<String, String>>();

            JSONObject all_dataobj = jsonObject.getJSONObject("query").getJSONArray("pages").getJSONObject(0);

            try{
                JSONObject description_dataobj = all_dataobj.getJSONObject("terms");
                String description_row = description_dataobj.getString(JsonConstants.Params.DESCRIPTION);
                char[] description = description_row.substring(2, description_row.length() - 2).toCharArray();

                if (description.length <= limit) {      //Если очень маленькое описание.
                    throw new JSONException("Small Description");
                } else {
                    description[0] = Character.toUpperCase(description[0]);
                    jsonModel.setDescription(charArrayToString(description, description.length));
                }
            }catch (JSONException e){
                String extracts = all_dataobj.getString(JsonConstants.Params.EXTRACTS);
                extracts = Html.fromHtml(extracts).toString();
                String description = parse_extracts(extracts.toCharArray());
                //Log.v("WIKI_DATA_EXTRACTS",description);
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
