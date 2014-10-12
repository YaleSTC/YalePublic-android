package edu.yalestc.yalepublic.news;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import edu.yalestc.yalepublic.R;
import edu.yalestc.yalepublic.news.RssReader;
import edu.yalestc.yalepublic.news.RssFeed;

/**
 * Created by Jason Liu on 10/4/14.
 */

public class NewsReader extends Activity {

    TextView tRSSTitle, tRSSContent;
    RssFeed feed;

    // Check for connectivity, return true if connected or connecting.
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_items);
        tRSSTitle = (TextView) findViewById(R.id.tvRSSTitle);
        tRSSContent = (TextView) findViewById(R.id.tvRSSContent);
        
        // If we're online, downloads the RSS Feed and returns it as `feed`
        if (isOnline()) {
            NewsDownload start = new NewsDownload();
            try {
                feed = start.execute("http://news.yale.edu/news-rss").get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("NewsReader", "Please connect to Internet");
        }

        if (feed != null) {       // EHOSTUNREACH: No route to hoast
            ArrayList<RssItem> rssItems = feed.getRssItems();
            ArrayList<String> rssTitles = new ArrayList<String>();
            ArrayList<String> rssLinks = new ArrayList<String>();
            ArrayList<String> rssDescription = new ArrayList<String>();
            ArrayList<String> rssContent = new ArrayList<String>();

            /*private String title;
            private String link;
            private Date pubDate;
            private String description;
            private String content;*/

            for (RssItem rssItem : rssItems) {
                Log.d("RSS Reader", rssItem.getTitle());
                rssTitles.add(rssItem.getTitle());
                rssLinks.add(rssItem.getLink());
                rssDescription.add(rssItem.getDescription());
                rssContent.add(rssItem.getContent());
            }

            /*String[] video_arrays = {"video1", "video2"};
            List<String> videos = new ArrayList<String>(Arrays.asList(video_arrays)); */

            // Parameters: Activity (Context), Layout file, Id of TextView, Array that's adapted
            final ArrayAdapter<String> mNewsAdapter;
            mNewsAdapter = new ArrayAdapter<String>(
                    this, R.layout.news_tab, R.id.tvTitle, rssTitles);

            ListView listView = (ListView) findViewById(R.id.listNews);
            listView.setAdapter(mNewsAdapter);
        }
    }

}
