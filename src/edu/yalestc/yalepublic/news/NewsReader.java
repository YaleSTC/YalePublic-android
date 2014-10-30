package edu.yalestc.yalepublic.news;

import android.app.Activity;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import edu.yalestc.yalepublic.R;
import edu.yalestc.yalepublic.news.RssReader;
import edu.yalestc.yalepublic.news.RssFeed;

/**
 * Created by Jason Liu on 10/4/14.
 * This Activity gets an RssFeed using NewsDownload, and then sets it up into news_tab.xml
 * using a custom adapter which overrides setAdapter()/getView. As a result, we can set three
 * text fields at the same time with three sets of data rather than one field with one string.
 */

public class NewsReader extends Activity {

    TextView tRSSTitle;
    RssFeed feed;
    long time, timediff, hourdiff, daydiff;
    String downloadurl;
    ArrayList<String> rssTitles = new ArrayList<String>();
    ArrayList<String> rssLinks = new ArrayList<String>();
    ArrayList<String> rssDescription = new ArrayList<String>();
    ArrayList<String> rssContent = new ArrayList<String>();
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
        setContentView(R.layout.news_items);
        tRSSTitle = (TextView) findViewById(R.id.tvRSSTitle);

        tRSSTitle.setVisibility(View.GONE);     // Hide the top textview


        downloadurl = this.getIntent().getStringExtra("rssfeed");
        Log.d("NewsReader passed", downloadurl);

        // If we're online, downloads the RSS Feed and returns it as `feed`
        if (isOnline()) {
            NewsDownload start = new NewsDownload();
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

        time = System.currentTimeMillis();

        if (feed != null) {       // EHOSTUNREACH: No route to host
            ArrayList<RssItem> rssItems = feed.getRssItems();

            /*private String title;
            private String link;
            private Date pubDate;
            private String description;
            private String content;*/

            for (RssItem rssItem : rssItems) {
                //Log.d("RSS Reader", rssItem.getPubDate().toString());
                timediff = time - rssItem.getPubDate().getTime();      // difference in milliseconds
                //Log.d("Timediff", String.valueOf(timediff));
                daydiff = TimeUnit.MILLISECONDS.toDays(timediff);      // difference in days
                hourdiff = TimeUnit.MILLISECONDS.toHours(timediff);    // difference in hours
                if (0L == daydiff) {  // 0 days ago
                    rssTimediff.add(String.valueOf(hourdiff) + " hours ago");
                } else if (1L == daydiff) {
                    rssTimediff.add("Yesterday");     // In the same style as the original app
                } else {
                    rssTimediff.add(String.valueOf(daydiff) + " days ago");
                }

                rssTitles.add(rssItem.getTitle());
                rssLinks.add(rssItem.getLink());
                rssDescription.add(rssItem.getDescription());
                //rssContent.add(rssItem.getContent());
            }

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
            listView.setAdapter(new NewsAdapter(this, R.layout.news_tab, rssItems));
            //listView.setAdapter(mNewsAdapter);

            //set OnItemClickListener to open up a new activity in which we get
            //all the videos listed
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    //redirect to new activity displaying all videos
                    Uri uriUrl = Uri.parse(rssLinks.get(arg2));
                    Log.d("NewsReaderClickLink", rssLinks.get(arg2));
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                    //For Debug purposes - show what is the playlistID
                    startActivity(launchBrowser);
                }
            });
        }
    }

    public class NewsAdapter extends ArrayAdapter<RssItem> {
        private final Context context;
        private final ArrayList<RssItem> data;
        private final int layoutResourceId;

        // fix here
        public NewsAdapter(Context context, int layoutResourceId, ArrayList<RssItem> data) {
            super(context, layoutResourceId, data);
            this.context = context;
            this.data = data;
            this.layoutResourceId = layoutResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder = null;

            if(row == null) {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new ViewHolder();
                holder.textView1 = (TextView) row.findViewById(R.id.tvTitle);
                holder.textView2 = (TextView) row.findViewById(R.id.tvDate);
                holder.textView3 = (TextView) row.findViewById(R.id.tvDescription);

                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }

            //RssItem rItem = data.get(position);

            holder.textView1.setText(rssTitles.get(position));
            holder.textView2.setText(rssTimediff.get(position));
            holder.textView3.setText(rssDescription.get(position));

            return row;
        }

        private class ViewHolder {
            TextView textView1;
            TextView textView2;
            TextView textView3;
        }
    }

}
