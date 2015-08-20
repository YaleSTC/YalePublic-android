package edu.yalestc.yalepublic.Videos;

import android.app.ActionBar;

import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import edu.yalestc.yalepublic.DeveloperKey;
import edu.yalestc.yalepublic.R;


//**
//Created by Stan Swidwinski
//**

public class VideosWithinPlaylist extends ActionBarActivity {

    Bundle extras;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //to get the passed parameters
        extras = getIntent().getExtras();
        super.onCreate(savedInstanceState);

        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);     // Show home as a back arrow
        //actionbar.setDisplayShowHomeEnabled(true);     // Show application logo
        actionbar.setDisplayShowTitleEnabled(true);    // Show activity title/subtitle
        actionbar.setDisplayUseLogoEnabled(false);     // Use activity logo instead of activity icon
        actionbar.setTitle(extras.getString("playlistName"));  // Set title

        setContentView(R.layout.activity_video_within_list);
        if (savedInstanceState == null && extras != null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container1, PlaceholderFragment.newInstance(extras)).commit();
        }
}
    //definition of our custom fragment.
    public static class PlaceholderFragment extends Fragment {

        private Bundle mExtras;

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(Bundle extras){
            PlaceholderFragment f = new PlaceholderFragment();
            f.setArguments(extras);
            return f;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_video_within_list,
                    container, false);

            mExtras = getArguments();

            // create an asynctask that fetches the playlist titles. It should speak for itself.
            //Just note that in constructor we give in the base url (WITHOUT "?" at the end).
            VideosWithinPlaylistJSONReader scrapeData = new VideosWithinPlaylistJSONReader("https://www.googleapis.com/youtube/v3/playlistItems", getActivity());
            scrapeData.addParams(new Pair<>("part", "snippet"));
            scrapeData.addParams(new Pair<>("playlistId", mExtras.getString("playlistId")));
            scrapeData.addParams(new Pair<>("key", new DeveloperKey().DEVELOPER_KEY));
            scrapeData.addParams(new Pair<>("maxResults", "50"));

            ListView listView = (ListView) rootView.findViewById(R.id.listview_video_in_playlist);
            scrapeData.addListView(listView);
            scrapeData.execute();

                return rootView;
        }

    }
    
}
