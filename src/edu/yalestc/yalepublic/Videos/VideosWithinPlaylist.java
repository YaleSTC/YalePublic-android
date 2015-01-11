package edu.yalestc.yalepublic.Videos;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import android.content.Context;

import android.graphics.Color;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import edu.yalestc.yalepublic.DeveloperKey;
import edu.yalestc.yalepublic.JSONReader;
import edu.yalestc.yalepublic.R;


//**
//Created by Stan Swidwinski
//**

public class VideosWithinPlaylist extends Activity {

    Bundle extras;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //to get the passed parameters
        extras = getIntent().getExtras();
        setContentView(R.layout.activity_video_within_list);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container1, new PlaceholderFragment()).commit();
            
        }
}
    //definition of our custom fragment.
    public class PlaceholderFragment extends Fragment {
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_video_within_list,
                    container, false);

            // create an asynctask that fetches the playlist titles. It should speak for itself.
            //Just note that in constructor we give in the base url (WITHOUT "?" at the end).
            VideosWithinPlaylistJSONReader scrapeData = new VideosWithinPlaylistJSONReader("https://www.googleapis.com/youtube/v3/playlistItems", getActivity());
            scrapeData.addParams(new Pair<String, String>("part", "snippet"));
            scrapeData.addParams(new Pair<String, String>("playlistId", extras.getString("playlistId")));
            scrapeData.addParams(new Pair<String, String>("key", new DeveloperKey().DEVELOPER_KEY));
            scrapeData.addParams(new Pair<String, String>("maxResults", "50"));

            ListView listView = (ListView) rootView.findViewById(R.id.listview_video_in_playlist);
            scrapeData.addListView(listView);
            scrapeData.execute();

                return rootView;
        }

    }
    
}
