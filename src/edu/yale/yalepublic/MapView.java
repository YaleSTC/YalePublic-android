package edu.yale.yalepublic;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by Jason Liu on 10/18/14.
 */
public class MapView extends Activity {

    //String pos2;
    //double currentLatitude, currentLongitude;       // Current long. and lat. read from GPSLocs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*Bundle extras = getIntent().getExtras();
        currentLatitude = 41.3111;
        currentLongitude = -72.9267;
        pos2 = "Yale";

        if (extras != null && extras.containsKey("currentLatitude")
                && extras.containsKey("currentLongitude") ) {
            currentLatitude = getIntent().getDoubleExtra("currentLatitude", 41.3111);
            currentLongitude = getIntent().getDoubleExtra("currentLongitude", -72.9267);
            pos2 = getIntent().getStringExtra("name");
        }*/

        // setContentView(R.layout.map_simple);   // map_simple for old view
        setContentView(R.layout.map_webview);
        WebView myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        myWebView.loadUrl("http://map.yale.edu/");

        // Get a handle to the Map Fragment
        /*GoogleMap map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getMap();

        LatLng yale = new LatLng(currentLatitude, currentLongitude);   // The - signifies the Western Hemisphere

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(yale, 13));

        if (pos2 == "Yale") {
            map.addMarker(new MarkerOptions()
                    .title(pos2)
                    .snippet("The best university ever.")
                    .position(yale));
        } else {
            map.addMarker(new MarkerOptions()
                    .title(pos2)
                    .position(yale));
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.map_menu, menu);

        // Associate searchable configuration with the SearchView (NOT currently used)
        /*SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search_view).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));*/

        return super.onCreateOptionsMenu(menu);
    }

    // Called when you click the search button - loads TestSearch.class
    /*public void loadSearch(MenuItem item) {
        Intent iSearch = new Intent(this, MapSearch.class);
        startActivity(iSearch);
    }*/
}
