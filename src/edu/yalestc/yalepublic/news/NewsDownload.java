package edu.yalestc.yalepublic.news;

import android.os.AsyncTask;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import edu.yalestc.yalepublic.R;
import edu.yalestc.yalepublic.news.RssReader;
import edu.yalestc.yalepublic.news.RssFeed;

/**
 * Created by Jason Liu on 10/4/14.
 */
public class NewsDownload extends AsyncTask<String, Integer, RssFeed> {

    public NewsDownload() {
        super();
    }

    @Override
    protected RssFeed doInBackground(String... strings) {
        URL url = null;
        try {
            url = new URL(strings[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        RssFeed feed = null;
        try {
            feed = RssReader.read(url);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (feed != null) {       // EHOSTUNREACH: No route to hoast
                ArrayList<RssItem> rssItems = feed.getRssItems();
            for (RssItem rssItem : rssItems) {
                Log.d("RSS Reader", rssItem.getTitle());
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
