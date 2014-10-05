package edu.yalestc.yalepublic.news;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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

public class NewsReader extends Activity {

    TextView tRSSTitle, tRSSContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_items);
        tRSSTitle = (TextView) findViewById(R.id.tvRSSTitle);
        tRSSContent = (TextView) findViewById(R.id.tvRSSContent);

        URL url = null;
        try {
            url = new URL("http://news.yale.edu/news-rss");
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

        ArrayList<RssItem> rssItems = feed.getRssItems();
        for(RssItem rssItem : rssItems) {
            Log.d("RSS Reader", rssItem.getTitle());
        }
    }
}
