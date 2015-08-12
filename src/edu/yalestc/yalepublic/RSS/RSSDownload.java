package edu.yalestc.yalepublic.RSS;

import android.os.AsyncTask;

import android.util.Log;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by Jason Liu on 10/4/14.
 * This AsyncTask downloads RSS data from a given String URL and returns a RssFeed with all
 * of the RSS data parsed into different fields described in RssItem.
 */
public class RSSDownload extends AsyncTask<String, Integer, RssFeed> {

    public RSSDownload() {
        super();
    }

    @Override
    protected RssFeed doInBackground(String... strings) {
        URL url = null;         // Set our URL to the String passed in to the AsyncTask
        try {
            url = new URL(strings[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        RssFeed feed = null;   // Call RssReader to download from our url, and return feed
        try {
            feed = RssReader.read(url);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (feed != null) {    // If URL is bad, it will return EHOSTUNREACH: No route to host
                ArrayList<RssItem> rssItems = feed.getRssItems();
            for (RssItem rssItem : rssItems) {
                Log.d("RSS Reader", rssItem.getTitle());  // Log out all RSS Titles for debug
            }
        }
        return feed;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(RssFeed s) {
        super.onPostExecute(s);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
