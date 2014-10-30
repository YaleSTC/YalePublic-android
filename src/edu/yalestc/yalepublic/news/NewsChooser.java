package edu.yalestc.yalepublic.news;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import edu.yalestc.yalepublic.R;

/**
 * Created by Jason Liu on 10/17/14.
 */
public class NewsChooser extends Activity {

    private String rss_feeds[], rss_names[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.news_items);
        rss_feeds = getResources().getStringArray(R.array.rss_feeds);
        rss_names = getResources().getStringArray(R.array.rss_names);

         /*String[] video_arrays = {"video1", "video2"};
        List<String> videos = new ArrayList<String>(Arrays.asList(video_arrays)); */

        // Parameters: Activity (Context), Layout file, Id of TextView, Array that's adapted
            /*final ArrayAdapter<String> mNewsAdapter;
            mNewsAdapter = new ArrayAdapter<String>(
                    this, R.layout.news_tab, R.id.tvTitle, rssTitles);*/

        //ArrayList<RssItem> rssItems = feed.getRssItems();
        //List<String> rssData = rssItems;
        // TODO: Convert ArrayList<rssItem> into an array of strings

        ListView listView = (ListView) findViewById(R.id.listNews);
        ArrayAdapter<String> mListAdapter = new ArrayAdapter<String>(this, R.layout.tab, R.id.tab);
        mListAdapter.addAll(Arrays.asList(rss_names));
        //Collections.addAll(mListAdapter, rss_names);


        listView.setAdapter(mListAdapter);

        //set OnItemClickListener to open up a new activity in which we get
        //all the videos listed
        listView.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //redirect to new activity displaying all videos
                Intent showThem = new Intent(NewsChooser.this, NewsReader.class);
                showThem.putExtra("rssfeed", rss_feeds[arg2]);
                //For Debug purposes - show what is the playlistID
                Log.d("StartingActivityInVideoList", rss_feeds[arg2]);
                startActivity(showThem);
            }
        });
    }
}
