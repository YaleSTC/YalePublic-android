package edu.yale.yalepublic;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by jason on 12/11/15.
 */
public class Directory extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.map_webview);   // map_simple for old view
        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        myWebView.loadUrl("http://directory.yale.edu/");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

}
