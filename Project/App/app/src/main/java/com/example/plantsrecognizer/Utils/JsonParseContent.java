package com.example.plantsrecognizer.Utils;

import android.app.Activity;
import android.text.Html;
import android.util.Log;

import com.example.plantsrecognizer.Models.JsonModel;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonParseContent {

    public JsonParseContent(@SuppressWarnings("unused") Activity activity) {
    }

    private String initialExtractsSplit(char string[]) {
        //Initial extracts parser
        StringBuilder new_string = new StringBuilder();
        for(int i = 0; i < string.length; i++){
            if(string[i] == '\n'){
                break;
            }
            else if(string[i] == '—' || string[i] == '-'){
                new_string = new StringBuilder();
                i++;
                if(string[i] == ' '){
                    i++;
                }
            }
            new_string.append(string[i]);
        }
        return new_string.toString();
    }

    private String charArrayToString(char string[], int max_index) {
        StringBuilder new_string = new StringBuilder();
        for (int i = 0; i < max_index; i++) {
            new_string.append(string[i]);
        }
        return new_string.toString();
    }

    private int getIndexOfLastDot(char[] string) {
        //Call this method to get index of last permissible dot in extracts
        int tmp = 0;
        for (int i = Constants.maxLenOfExtracts; i >= 0; i--) {
            if (string[i] == '.') {
                tmp = i;
                break;
            }
        }
        return tmp;
    }

    private int getIndexOfLastWord(char string[], int last_dot_index) {
        //Call this method to get index of last permissible word in extracts
        int counter = 0;
        int last_word_index = last_dot_index;
        for (int i = last_dot_index; i >= 0; i--) {
            if (string[i] == ' ' || string[i] == ',') {
                counter++;
                if (counter == 2) {
                    last_word_index = i;
                    break;
                }
            }
        }
        return last_word_index;
    }

    private String splitExtractsUntilLastWord(char string[], int last_dot_index) {
        int last_word_index;
        String new_string = null;
        try {
            last_word_index = getIndexOfLastWord(string, last_dot_index);
            new_string = charArrayToString(string, last_word_index);
            new_string += " ...";
        } catch (Exception unknown_exception) {
            unknown_exception.printStackTrace();
        }
        return new_string;
    }

    private String splitExtracts(char[] string) {
        //Call this method if length of extracts is greater than the maximum permissible
        int last_dot_index;
        String new_string;

        last_dot_index = getIndexOfLastDot(string);

        if (last_dot_index >= Constants.maxLenOfExtracts) {
            new_string = splitExtractsUntilLastWord(string, last_dot_index);
        } else {
            //Split extracts until last dot
            new_string = charArrayToString(string, last_dot_index);
        }
        return new_string;
    }

    private char[] setFirstCharUpperCase(String current_string) {
        char string[] = current_string.toCharArray();
        try {
            string[0] = Character.toUpperCase(string[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return string;
    }

    private char[] parse_extracts(char string[]) {

        String new_string;

        new_string = initialExtractsSplit(string);

        try {
            new_string = new_string.substring(0, new_string.length() - 2);
        } catch (StringIndexOutOfBoundsException | NullPointerException exception) {
            exception.printStackTrace();
        }

        if (new_string.length() > Constants.maxLenOfExtracts) {
            char initially_parsed_string[] = new_string.toCharArray();
            new_string = splitExtracts(initially_parsed_string);
        }

        return setFirstCharUpperCase(new_string);
    }

    private String getDescription(JSONObject all_data) throws JSONException {
        JSONObject description_data_object = all_data.getJSONObject("terms");
        String description_row = description_data_object.getString(JsonConstants.Params.DESCRIPTION);
        Log.d("Description", description_row);
        return description_row.substring(2, description_row.length() - 2);
    }

    private String getParsedDescription(char description[]) {
        char current_description[] = description;
        if (current_description.length <= Constants.minDescriptionLen) {      //Если очень маленькое описание.
            throw new SmallDescriptionException();
        } else {
            current_description[0] = Character.toUpperCase(current_description[0]);
            return charArrayToString(current_description, current_description.length);
        }
    }

    private String getExtracts(JSONObject all_data) throws JSONException {
        char extracts_array[];
        String extracts = all_data.getString(JsonConstants.Params.EXTRACTS);

        //TODO: Solve problem with deprecated methods
        //noinspection deprecation
        extracts = Html.fromHtml(extracts).toString();

        extracts_array = parse_extracts(extracts.toCharArray());
        return charArrayToString(extracts_array, extracts_array.length);
    }

    private String getSourceLink(JSONObject all_data) throws JSONException {
        return all_data.getJSONObject("thumbnail").getString(JsonConstants.Params.SOURCE);
    }

    private String getTitle(JSONObject all_data) throws JSONException {
        return all_data.getString(JsonConstants.Params.TITLE);
    }

    private JSONObject getJsonData(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        return jsonObject.getJSONObject("query")
                .getJSONArray("pages")
                .getJSONObject(0);
    }

    public JsonModel getInfo(String response) {
        JsonModel jsonModel = new JsonModel();
        String description;
        String source;
        String title;
        JSONObject all_data_object;
        try {
            all_data_object = getJsonData(response);

            try{
                description = getDescription(all_data_object);
                description = getParsedDescription(description.toCharArray());
                jsonModel.setDescription(description);
            } catch (JSONException | SmallDescriptionException e) {
                description = getExtracts(all_data_object);
                jsonModel.setDescription(description);
            }

            title = getTitle(all_data_object);
            jsonModel.setTitle(title);

            source = getSourceLink(all_data_object);
            jsonModel.setSource(source);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonModel;
    }

    private final class Constants {
        static final int minDescriptionLen = 20;
        static final int maxLenOfExtracts = 130;
    }

    private class SmallDescriptionException extends RuntimeException {
        SmallDescriptionException() {
            super();
        }
    }
}
