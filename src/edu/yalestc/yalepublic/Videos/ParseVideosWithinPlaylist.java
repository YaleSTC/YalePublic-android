package edu.yalestc.yalepublic.Videos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stan Swidwinski on 10/29/14.
 */
//this might be a pretty bad idea!
public class ParseVideosWithinPlaylist extends AsyncTask<Void, Void, Pair<Bitmap[], ArrayList<String[]>> > {

    private String rawData;
    private String[] titles;
    private String[] dates;
    private String[] videoIds;
    private Bitmap[] bitmaps;
    //object that holds a pair of a key (String will be what we parse into) and value.
    //Value is a list of pairs string-string which will tell us what we are looking for
    // example: Pair<"Videos, [Pair <"Object","snippet">] will then parse into string Videos
    // and pull JSONObject("snippet"). It is a list so that we can build upon the former, say
    // JSONObject("snippet").getString("Id");

    ParseVideosWithinPlaylist(String raw){
        rawData = raw;
    }


    @Override
    protected Pair<Bitmap[], ArrayList<String[]>> doInBackground(Void... voids) {
        JSONObject videoData;
        try {
            videoData = new JSONObject(rawData);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        JSONArray playlistData;
        try {
            playlistData = videoData.getJSONArray("items");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        titles = new String[playlistData.length()];
        bitmaps = new Bitmap[playlistData.length()];
        dates = new String[playlistData.length()];
        videoIds = new String[playlistData.length()];
        for (int i = 0; i < playlistData.length(); i++) {
            try {
                titles[i] = playlistData.getJSONObject(i)
                        .getJSONObject("snippet")
                        .getString("title");
                dates[i] = (playlistData.getJSONObject(i)
                        .getJSONObject("snippet")
                        .getString("publishedAt")).substring(0, 10);
                videoIds[i] = playlistData.getJSONObject(i)
                        .getJSONObject("snippet")
                        .getJSONObject("resourceId")
                        .getString("videoId");
                //Here we actually download the thumbnail using URL obtained from JSONObject
                try {
                    URL imageUrl = new URL(playlistData.getJSONObject(i)
                            .getJSONObject("snippet")
                            .getJSONObject("thumbnails")
                            .getJSONObject("medium")
                            .getString("url"));
                    //connect to given server
                    HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                    //safety features and avoiding errors is links redirect
                    conn.setConnectTimeout(30000);
                    conn.setReadTimeout(30000);
                    conn.setInstanceFollowRedirects(true);
                    //setting inputstream and decoding it into a bitmap
                    InputStream is = conn.getInputStream();
                    bitmaps[i] = BitmapFactory.decodeStream(is);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        ArrayList<String[]> resultingStrings = new ArrayList<String[]>();
        resultingStrings.add(titles);
        resultingStrings.add(dates);
        resultingStrings.add(videoIds);
        Pair<Bitmap[], ArrayList<String[]> > result = new Pair<Bitmap[], ArrayList<String[]> > (bitmaps, resultingStrings);
        return result;
    }
}
