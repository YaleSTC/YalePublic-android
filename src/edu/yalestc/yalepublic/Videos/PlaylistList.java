package edu.yalestc.yalepublic.Videos;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import edu.yalestc.yalepublic.DeveloperKey;
import edu.yalestc.yalepublic.R;

//**
//Created by Stan Swidwinski and Carsten Peterson
//**

public class PlaylistList extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);     // Show home as a back arrow
        //actionbar.setDisplayShowHomeEnabled(true);     // Show application logo
        actionbar.setDisplayShowTitleEnabled(true);    // Show activity title/subtitle
        actionbar.setDisplayUseLogoEnabled(false);     // Use activity logo instead of activity icon
        actionbar.setTitle(getString(R.string.videos_playlists));  // Set title

        setContentView(R.layout.activity_playlist_list);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_playlists_list,
                    container, false);

            // create an asynctask that fetches the playlist titles. It should speak for itself.
            //Just note that in constructor we give in the base url (WITHOUT "?" at the end).
            PlaylistJSONReader scrapeData = new PlaylistJSONReader("https://www.googleapis.com/youtube/v3/playlists", getActivity());
            scrapeData.addParams(new Pair<>("part", "snippet"));
            scrapeData.addParams(new Pair<>("channelId", "UC4EY_qnSeAP1xGsh61eOoJA"));
            scrapeData.addParams(new Pair<>("key", new DeveloperKey().DEVELOPER_KEY));
            scrapeData.addParams(new Pair<>("maxResults", "50"));

            ListView listView = (ListView) rootView.findViewById(R.id.listview_video);
            scrapeData.addListView(listView);
            scrapeData.execute();

            return rootView;

        }
    }
}
