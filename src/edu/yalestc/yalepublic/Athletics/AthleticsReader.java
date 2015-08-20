package edu.yalestc.yalepublic.Athletics;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import edu.yalestc.yalepublic.RSS.RSSAdapter;
import edu.yalestc.yalepublic.RSS.RSSDownload;
import edu.yalestc.yalepublic.RSS.RssFeed;
import edu.yalestc.yalepublic.RSS.RssItem;
import edu.yalestc.yalepublic.R;

/**
 * This Activity gets an RssFeed using AthleticsDownload, and then sets it up into rss_tabxml
 * using a custom adapter which overrides setAdapter()/getView. This activity shows sports news
 * or scores depending on what the user chooses
 */

public class AthleticsReader extends ActionBarActivity {

    RssFeed feed;
    android.support.v7.app.ActionBar actionbar;
    long time, timediff, hourdiff, daydiff;
    String sportKeyword;
    //these URL fragments are used in conjunction with sportKeyword to get RSS feeds
    String baseURL;
    String headlinesExtension;
    String eventsExtension;

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
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);     // Show home as a back arrow
        //actionbar.setDisplayShowHomeEnabled(true);     // Show application logo
        actionbar.setDisplayShowTitleEnabled(true);    // Show activity title/subtitle
        actionbar.setDisplayUseLogoEnabled(false);     // Use activity logo instead of activity icon
        actionbar.setTitle("News");  // Set title
        setContentView(R.layout.rss_items);

        //gets the URL fragments from the string resource file
        baseURL = getString(R.string.baseURL);
        headlinesExtension = getString(R.string.headlinesExt);
        eventsExtension = getString(R.string.eventsExt);

        sportKeyword = this.getIntent().getStringExtra("rssfeed");
        String downloadurl = baseURL + sportKeyword + headlinesExtension; //creates downloadurl using sportKeyword
        Log.d("AthleticsReader passed", downloadurl);
        setListView(downloadurl);
    }

    //this method gets the RSS data using the downloadURL and populates the listView
    private void setListView(String downloadurl) {
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
            Log.d("AthleticsReader", "Please connect to Internet");
        }

        time = System.currentTimeMillis();  // Current system time for `5 hours ago`, etc
        ListView listView = (ListView) findViewById(R.id.listRSS);

        if (feed != null && feed.getRssItems().size() != 0) {   // If URL is bad, it will return EHOSTUNREACH: No route to host
            //this is for formatting the date for Scores data
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd, yyyy 'at' hh:mm a");

            //resets the arraylists
            rssTitles.clear();
            rssLinks.clear();
            rssDescription.clear();
            rssTimediff.clear();

            ArrayList<RssItem> rssItems = feed.getRssItems();
            boolean isNews = actionbar.getTitle().equals("News");
            for (RssItem rssItem : rssItems) {
                if(isNews) {
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
                } else {
                    calendar.setTimeInMillis(rssItem.getPubDate().getTime());
                    rssTimediff.add(formatter.format(calendar.getTime()));
                }
                rssTitles.add(rssItem.getTitle());
                rssLinks.add(rssItem.getLink());
                rssDescription.add(rssItem.getDescription());
            }

            if(isNews) {
                RSSAdapter.setArrayLists(rssTitles, rssDescription, rssTimediff); //provides adapter with data
            } else {
                //We want to display scores from the newest to oldest
                Collections.reverse(rssTitles);
                Collections.reverse(rssLinks);
                Collections.reverse(rssDescription);
                Collections.reverse(rssTimediff);
                //We don't want to display any desc for scores so we pass null for the desc parameter
                RSSAdapter.setArrayLists(rssTitles, null, rssTimediff);
            }
            // Usually, the parameters of setAdapter are:
            // Activity (Context), Layout file, Id of TextView, Array that's adapted
            // However, a custom adapter is used (NewsAdapter) that overrides this functionality.
            listView.setAdapter(new RSSAdapter(this, R.layout.rss_tab, rssItems));


            // Set OnItemClickListener to open the link when it's clicked
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    Uri uriUrl = Uri.parse(rssLinks.get(arg2));
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                    // For Debug purposes - show what is the link cicked
                    Log.d("ARClickLink", rssLinks.get(arg2));
                    startActivity(launchBrowser);
                }
            });
        } else {
            //if feed is empty, displays a message to the user
            listView.setAdapter(null);
            TextView emptyTextView = (TextView) findViewById(android.R.id.empty);

            if (actionbar.getTitle().equals("News")) {
                emptyTextView.setText("Sorry, no News right now!");
            } else {
                emptyTextView.setText("Sorry, no Scores right now!");
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflates the menu; this adds the toggle button to the action bar.
        getMenuInflater().inflate(R.menu.athletics_toggle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //this adds functionality to the toggle button allowing it to toggle between RSS feeds
        switch (item.getItemId()) {
            case R.id.toggle:
                if(actionbar.getTitle().equals("News")) {
                    actionbar.setTitle("Scores");
                    item.setTitle("News");
                    String downloadurl = baseURL + sportKeyword + eventsExtension;
                    setListView(downloadurl);
                }
                else {
                    actionbar.setTitle("News");
                    item.setTitle("Scores");
                    String downloadurl = baseURL + sportKeyword + headlinesExtension;
                    setListView(downloadurl);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
