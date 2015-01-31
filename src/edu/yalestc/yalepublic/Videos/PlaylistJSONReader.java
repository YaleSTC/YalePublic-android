package edu.yalestc.yalepublic.Videos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.yalestc.yalepublic.JSONReader;

/**
 * Created by Stan Swidwinski on 1/10/15.
 */

public class PlaylistJSONReader extends JSONReader {

    Activity mActivity;
    private ProgressDialog dialog;
    private String mRawData;
    private PlaylistAdapter mAdapter;
    private ListView mListView;
    private String[] playlistIds;
    String[] allPlaylists;


    public PlaylistJSONReader(Activity activity){
        super(activity);
        //for creating and getting preferences and tables!
        mActivity = activity;
        dialog = new ProgressDialog(mActivity);
        dialog.setCancelable(false);
        dialog.setTitle("Fetching the videos!");
        dialog.setMessage("This should not take too long, please wait...");
        dialog.setIndeterminate(true);
        mListView = null;
    }

    public PlaylistJSONReader(String URL, Activity activity){
        super(URL, activity);
        mActivity = activity;
        dialog = new ProgressDialog(mActivity);
        dialog.setCancelable(false);
        dialog.setTitle("Fetching the videos!");
        dialog.setMessage("This should not take too long, please wait...");
        dialog.setIndeterminate(true);
        mListView = null;
    }

    @Override
    protected void onPreExecute(){
        dialog.show();
    }

    @Override
    protected String doInBackground(Void... voids) {
        mRawData = super.getData();
        if (mRawData == null) {
            mActivity.runOnUiThread(new Runnable() {
                public void run(){
                    Toast toast = new Toast(mActivity);
                    toast = Toast.makeText(mActivity, "You need internet connection to view the content!", Toast.LENGTH_LONG);
                    toast.show();
                }
            });
            mActivity.finish();
            return null;
        } else {
            Log.i("CalendarFragment", "Success");
        }
        return mRawData;
    }

    @Override
    protected void onPostExecute(String result){
        if(mListView != null && getPlaylistsFromJson(mRawData)){
                mAdapter = new PlaylistAdapter(mActivity, allPlaylists);
                display();
        } else {
            mActivity.runOnUiThread(new Runnable() {
                public void run(){
                    Toast toast = new Toast(mActivity);
                    toast = Toast.makeText(mActivity, "You need internet connection to view the content!", Toast.LENGTH_LONG);
                    toast.show();
                }
            });
            mActivity.finish();
        }
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    public void addListView(ListView listView){
        mListView = listView;
    }

    private void display(){
        mListView.setAdapter(mAdapter);
        //set OnItemClickListener to open up a new activity in which we get
        //all the videos listed
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                //redirect to new activity displaying all videos
                Intent showThem = new Intent(mActivity, VideosWithinPlaylist.class);
                showThem.putExtra("playlistId", playlistIds[arg2]);
                //For Debug purposes - show what is the playlistID
                Log.d("StartingActivityInVideoList", playlistIds[arg2]);
                mActivity.startActivity(showThem);
            }
        });

    }

    private boolean getPlaylistsFromJson(String rawData){
        if(rawData == null)
            return false;
        JSONObject videoData;
        try {
            videoData = new JSONObject(rawData);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        JSONArray playlistData;
        try {
            playlistData = videoData.getJSONArray("items");
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        allPlaylists = new String[playlistData.length()];
        //we need to remember playlistIDs for future processing!
        playlistIds = new String[playlistData.length()];
        for (int i = 0; i < playlistData.length(); i++){
            try {
                allPlaylists[i] = playlistData.getJSONObject(i)
                        .getJSONObject("snippet")
                        .getString("title");
                playlistIds[i] = playlistData.getJSONObject(i)
                        .getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}