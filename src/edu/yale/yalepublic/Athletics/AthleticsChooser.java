package edu.yale.yalepublic.Athletics;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.Arrays;

import edu.yale.yalepublic.R;

/**
 * This fragment displays the men sports or women sports depending on which tab was selected in AthleticsActivity.
 * This class is very similar to NewsChooser but this class is a fragment instead of an activity.
 */
public class AthleticsChooser extends Fragment {

    private String rss_feeds[], rss_names[];

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout ll = (LinearLayout)inflater.inflate(R.layout.rss_items, container, false);

        if(getArguments() != null) { //checks if MEN tab was selected
            rss_feeds = getResources().getStringArray(R.array.men_sports_keywords); // Array of RSS Men Keywords
            rss_names = getResources().getStringArray(R.array.men_sports); // Array of Men Sports
        }
        else {
            rss_feeds = getResources().getStringArray(R.array.women_sports_keywords); // Array of RSS Women Keywords
            rss_names = getResources().getStringArray(R.array.women_sports); // Array of Women Sports
        }

        // Set up the ListView listNews with all of the titles from rss_names[].
        ListView listView = (ListView) ll.findViewById(R.id.listRSS);

        // Parameters for ArrayAdapter: Activity (Context), Layout file, TextView Id, <Array that's adapted>
        ArrayAdapter<String> mListAdapter = new ArrayAdapter<String>(getActivity(), R.layout.tab, R.id.tab);
        mListAdapter.addAll(Arrays.asList(rss_names));

        listView.setAdapter(mListAdapter);    // After constructing adapter, set it up

        // Set OnItemClickListener to open up a new activity in which we get all the RSS data
        listView.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //redirect to new activity displaying all videos
                Intent showThem = new Intent(getActivity(), AthleticsReader.class);
                showThem.putExtra("rssfeed", rss_feeds[arg2]);
                //For Debug purposes - show what is the playlistID
                Log.d("StartingActivityInVideoList", rss_names[arg2]);
                startActivity(showThem);
            }
        });
        return ll;
    }
}
