package edu.yalestc.yalepublic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

//**
//Created by Stan Swidwinski and Carsten Peterson
//**
public class VideoList extends Activity {
    // this is a class parameter so that it can be modified in the asynctask
    private ArrayAdapter<String> mVideoAdapter;
    //this is a string in which we store the ID's of playlists to pass them
    //into VideosWithinPlaylist
    private String[] playlistIds;
    private String[] allPlaylists;
    private String rawData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_video_list,
                    container, false);

            // create an asynctask that fetches the playlist titles
            JSONReader scrapeData = new JSONReader("https://www.googleapis.com/youtube/v3/playlists");
            scrapeData.addParams(new Pair<String, String>("part","snippet"));
            scrapeData.addParams(new Pair<String, String>("channelId","UC4EY_qnSeAP1xGsh61eOoJA"));
            scrapeData.addParams(new Pair<String, String>("key",new DeveloperKey().DEVELOPER_KEY));
            scrapeData.addParams(new Pair<String, String>("maxResults","50"));

            try {
                 rawData = scrapeData.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


            if(getPlaylistsFromJson(rawData)) {
                // initialize the ArrayAdapter
                mVideoAdapter = new ArrayAdapter<String>(
                        getActivity(), R.layout.tab, R.id.tab);
                mVideoAdapter.addAll(allPlaylists);
                ListView listView = (ListView) rootView.findViewById(R.id.listview_video);
                listView.setAdapter(mVideoAdapter);
                //set OnItemClickListener to open up a new activity in which we get
                //all the videos listed
                listView.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {
                        //redirect to new activity displaying all videos
                        Intent showThem = new Intent(VideoList.this, VideosWithinPlaylist.class);
                        showThem.putExtra("playlistId", playlistIds[arg2]);
                        //For Debug purposes - show what is the playlistID
                        Log.d("StartingActivityInVideoList", playlistIds[arg2]);
                        startActivity(showThem);
                    }
                });
                return rootView;
            } else {
                return null;
            }
        }
    }

    private boolean getPlaylistsFromJson(String rawData){
        JSONObject videoData;
        try {
            videoData = new JSONObject(rawData);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        JSONArray playlistData;
        try {
            playlistData = videoData.getJSONArray("items");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
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
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
    
}
