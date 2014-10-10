package edu.yalestc.yalepublic.news;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
        
        // TODO: Check for Internet Connectivity first
        if (isOnline()) {
            NewsDownload start = new NewsDownload();
            start.execute("http://news.yale.edu/news-rss");
        } else {
            Log.d("NewsReader", "Please connect to Internet");
        }
    }

}
