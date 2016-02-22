package edu.yale.yalepublic.News;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import edu.yale.yalepublic.R;
import edu.yale.yalepublic.RSS.RSSAdapter;
import edu.yale.yalepublic.RSS.RSSDownload;
import edu.yale.yalepublic.RSS.RssFeed;
import edu.yale.yalepublic.RSS.RssItem;
import edu.yale.yalepublic.Util.ActionBarUtil;

/**
 * Created by Jason Liu on 10/4/14.
 * This Activity gets an RssFeed using RSSDownload, and then sets it up into rss_tab.xml
 * using a custom adapter (RSSAdapter) which overrides setAdapter()/getView. As a result, we can set three
 * text fields at the same time with three sets of data rather than one field with one string.
 */

public class NewsReader extends Activity {

    RssFeed feed;
    long time, timediff, hourdiff, daydiff;
    String downloadurl, downloadname;
    ArrayList<String> rssTitles = new ArrayList<String>();
    ArrayList<String> rssLinks = new ArrayList<String>();
    ArrayList<String> rssDescription = new ArrayList<String>();
    ArrayList<String> rssTimediff = new ArrayList<String>();

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
        setContentView(R.layout.rss_items);


        downloadurl = this.getIntent().getStringExtra("rssfeed");
        downloadname = this.getIntent().getStringExtra("rssname");
        Log.d("NewsReader passed", downloadurl);

        ActionBar actionbar = getActionBar();
        ActionBarUtil.setupActionBar(actionbar, downloadname);

        // If we're online, downloads the RSS Feed and returns it as `feed`
        if (isOnline() && downloadurl != null) {
            RSSDownload start = new RSSDownload();
            try {
                feed = start.execute(downloadurl).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("NewsReader", "Please connect to Internet");
        }

        time = System.currentTimeMillis();  // Current system time for `5 hours ago`, etc

        if (feed != null) {   // If URL is bad, it will return EHOSTUNREACH: No route to host
            ArrayList<RssItem> rssItems = feed.getRssItems();

            for (RssItem rssItem : rssItems) {
                timediff = time - rssItem.getPubDate().getTime();      // difference in milliseconds
                daydiff = TimeUnit.MILLISECONDS.toDays(timediff);      // difference in days
                hourdiff = TimeUnit.MILLISECONDS.toHours(timediff);    // difference in hours

                if (0L == daydiff) {                  // 0 days ago: output `5 hours ago`, etc
                    rssTimediff.add(String.valueOf(hourdiff) + " hours ago");
                } else if (1L == daydiff) {           // In the same style as the original app
                    rssTimediff.add("Yesterday");
                } else {                              // Else, output `3 days ago`, etc.
                    rssTimediff.add(String.valueOf(daydiff) + " days ago");
                }

                rssTitles.add(rssItem.getTitle());
                rssLinks.add(rssItem.getLink());
                rssDescription.add(rssItem.getDescription());
            }

            ListView listView = (ListView) findViewById(R.id.listRSS);
            RSSAdapter.setArrayLists(rssTitles, rssDescription, rssTimediff);
            // Usually, the parameters of setAdapter are:
            //       Activity (Context), Layout file, Id of TextView, Array that's adapted
            // However, a custom adapter is used (NewsAdapter) that overrides this functionality.
            listView.setAdapter(new RSSAdapter(this, R.layout.rss_tab, rssItems));

            // Set OnItemClickListener to open the link when it's clicked
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    Uri uriUrl = Uri.parse(rssLinks.get(arg2));
                    Log.d("NewsReaderClickLink", rssLinks.get(arg2));
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                    // For Debug purposes - show what is the link cicked
                    startActivity(launchBrowser);
                }
            });
        }
    }
}
