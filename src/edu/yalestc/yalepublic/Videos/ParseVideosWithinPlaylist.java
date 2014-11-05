package edu.yalestc.yalepublic.Videos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stan Swidwinski on 10/29/14.
 */
//the result will be of the form (Bitmaps, [titles, dates, videoIds]

//.get() from activity returns null if error occurred. Almost exclusively if the user does not have Internet (Google won't blow up suddenly).
//If this happens you should counter it using, for example,
/*if(new ParseVideosWithinPlaylist("Abc").execute().get() == null){
        Toast toast = new Toast(getActivity());
        toast = Toast.makeText(getActivity(), "You need internet connection to view the content!", Toast.LENGTH_LONG);
        toast.show();
        finish();
        return null;
        }
        */
public class ParseVideosWithinPlaylist extends AsyncTask<Void, Void, Pair<Bitmap[], ArrayList<String[]>> > {

    private String rawData;
    private String[] titles;
    private String[] dates;
    private String[] videoIds;
    private Bitmap[] bitmaps;
    private Context mContext;

    ParseVideosWithinPlaylist(String raw, Context context){
        rawData = raw;
        mContext = context;
    }


    @Override
    protected Pair<Bitmap[], ArrayList<String[]>> doInBackground(Void... voids) {
        if(isOnline()) {
            JSONObject videoData;
            JSONArray playlistData;
            try {
                videoData = new JSONObject(rawData);
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
                } catch (MalformedURLException e) {
                    Log.e("URI", "URL was malformed!");
                    return null;
                } catch (IllegalArgumentException e) {
                    Log.e("URI", "the argument proxy is null or of is an invalid type.");
                    return null;
                } catch (UnsupportedOperationException e) {
                    Log.e("URI", " the protocol handler does not support opening connections through proxies.");
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            //create the second part of the returned pair
            ArrayList<String[]> resultingStrings = new ArrayList<String[]>();
            resultingStrings.add(titles);
            resultingStrings.add(dates);
            resultingStrings.add(videoIds);

            Pair<Bitmap[], ArrayList<String[]>> result = new Pair<Bitmap[], ArrayList<String[]>>(bitmaps, resultingStrings);
            return result;
        } else {
            Log.e("URI","You are not connected to internet!");
            return null;
        }
    }
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
