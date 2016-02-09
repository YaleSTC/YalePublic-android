package edu.yale.yalepublic.Athletics;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import edu.yale.yalepublic.R;
import edu.yale.yalepublic.RSS.RSSAdapter;
import edu.yale.yalepublic.RSS.RSSDownload;
import edu.yale.yalepublic.RSS.RssFeed;
import edu.yale.yalepublic.RSS.RssItem;

/**
 * This fragment shows the top stories in Yale sports. It is essentially the same and works the same
 * as the NewsReader activity but this class is a fragment instead of an activity
 */

public class Headlines extends Fragment {

    RssFeed feed;
    long time, timediff, hourdiff, daydiff;
    String downloadurl;
    ArrayList<String> rssTitles = new ArrayList<String>();
    ArrayList<String> rssLinks = new ArrayList<String>();
    ArrayList<String> rssDescription = new ArrayList<String>();
    ArrayList<String> rssTimediff = new ArrayList<String>();

    // Check for connectivity, return true if connected or connecting.
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        downloadurl = getString(R.string.top_stories_url);
        LinearLayout linearLayout = null;

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
            Log.d("Headlines", "Please connect to Internet");
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
            linearLayout = (LinearLayout)inflater.inflate(R.layout.rss_items, container, false);
            ListView listView = (ListView) linearLayout.findViewById(R.id.listRSS);
            RSSAdapter.setArrayLists(rssTitles, rssDescription, rssTimediff);
            // Usually, the parameters of setAdapter are:
            //       Activity (Context), Layout file, Id of TextView, Array that's adapted
            // However, a custom adapter is used (NewsAdapter) that overrides this functionality.
            listView.setAdapter(new RSSAdapter(getActivity(), R.layout.rss_tab, rssItems));

            // Set OnItemClickListener to open the link when it's clicked
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    Uri uriUrl = Uri.parse(rssLinks.get(arg2));
                    Log.d("HeadlinesClickLink", rssLinks.get(arg2));
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                    // For Debug purposes - show what is the link cicked
                    startActivity(launchBrowser);
                }
            });

        }
        return linearLayout;
    }
}
