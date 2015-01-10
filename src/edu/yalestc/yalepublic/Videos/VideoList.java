package edu.yalestc.yalepublic.Videos;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import edu.yalestc.yalepublic.DeveloperKey;
import edu.yalestc.yalepublic.JSONReader;
import edu.yalestc.yalepublic.R;

//**
//Created by Stan Swidwinski and Carsten Peterson
//**

public class VideoList extends Activity {

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

            // create an asynctask that fetches the playlist titles. It should speak for itself.
            //Just note that in constructor we give in the base url (WITHOUT "?" at the end).
            VideosJSONReader scrapeData = new VideosJSONReader("https://www.googleapis.com/youtube/v3/playlists", getActivity());
            scrapeData.addParams(new Pair<String, String>("part", "snippet"));
            scrapeData.addParams(new Pair<String, String>("channelId", "UC4EY_qnSeAP1xGsh61eOoJA"));
            scrapeData.addParams(new Pair<String, String>("key", new DeveloperKey().DEVELOPER_KEY));
            scrapeData.addParams(new Pair<String, String>("maxResults", "50"));

            ListView listView = (ListView) rootView.findViewById(R.id.listview_video);
            scrapeData.addListView(listView);
            scrapeData.execute();

            return rootView;

        }
    }
}
