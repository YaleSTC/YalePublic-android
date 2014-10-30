package edu.yalestc.yalepublic.Videos;

import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Stan Swidwinski on 10/29/14.
 */
//this might be a pretty bad idea!
public class JSONParser {

    private String rawData;
    //object that holds a pair of a key (String will be what we parse into) and value.
    //Value is a list of pairs string-string which will tell us what we are looking for
    // example: Pair<"Videos, [Pair <"Object","snippet">] will then parse into string Videos
    // and pull JSONObject("snippet"). It is a list so that we can build upon the former, say
    // JSONObject("snippet").getString("Id");
    public List<Pair<String, List<Pair <String, String> > > > parseDecider;


    JSONParser(String raw){
        rawData = raw;
    }

    public void addParseProcedure(Pair<String, List<Pair <String, String> > > parsingCommand){
        parseDecider.add(parsingCommand);
    }

    public void setParseProcedure(List<Pair<String, List<Pair <String, String> > > > parsingCommand){
        parseDecider = parsingCommand;
    }

    public void setRawData(String rawJSONString){
        rawData = rawJSONString;
    }

    private String[] parse(String rawData) {
        JSONObject data;

        try {
            data = new JSONObject(rawData);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        JSONArray requestedData;
        try {
            requestedData = data.getJSONArray("items");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        for (int i = 0; i < parseDecider.size(); i++) {

        }
        return null;
    }
}