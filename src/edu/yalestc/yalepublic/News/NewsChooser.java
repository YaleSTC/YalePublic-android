package edu.yalestc.yalepublic.News;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Arrays;

import edu.yalestc.yalepublic.R;

/**
 * Created by Jason Liu on 10/17/14.
 * This activity launches a screen where you can choose which RSS feed you want to read.
 */
public class NewsChooser extends ActionBarActivity {

    private String rss_feeds[], rss_names[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);     // Show home as a back arrow
        //actionbar.setDisplayShowHomeEnabled(true);     // Show application logo
        actionbar.setDisplayShowTitleEnabled(true);    // Show activity title/subtitle
        actionbar.setDisplayUseLogoEnabled(false);     // Use activity logo instead of activity icon
        actionbar.setTitle(getString(R.string.news));  // Set title

        setContentView(R.layout.rss_items);
        rss_feeds = getResources().getStringArray(R.array.news_feeds); // Array of News RSS URLs
        rss_names = getResources().getStringArray(R.array.news_names); // Array of News RSS Titles

        // Set up the ListView listNews with all of the titles from rss_names[].
        ListView listView = (ListView) findViewById(R.id.listRSS);

        // Parameters for ArrayAdapter: Activity (Context), Layout file, TextView Id, <Array that's adapted>
        ArrayAdapter<String> mListAdapter = new ArrayAdapter<String>(this, R.layout.tab, R.id.tab);
        mListAdapter.addAll(Arrays.asList(rss_names));

        listView.setAdapter(mListAdapter);    // After constructing adapter, set it up

        // Set OnItemClickListener to open up a new activity in which we get all the RSS data
        listView.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //redirect to new activity displaying all videos
                Intent showThem = new Intent(NewsChooser.this, NewsReader.class);
                showThem.putExtra("rssfeed", rss_feeds[arg2]);
                //For Debug purposes - show what is the playlistID
                Log.d("StartingActivityInVideoList", rss_names[arg2]);
                startActivity(showThem);
            }
        });
    }
}
