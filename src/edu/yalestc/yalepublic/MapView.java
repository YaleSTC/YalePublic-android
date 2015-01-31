package edu.yalestc.yalepublic;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Jason Liu on 10/18/14.
 */
public class MapView extends Activity implements SearchView.OnQueryTextListener {

    private Menu menu;
    SearchView mSearchView;
    ListView mListView;

    ArrayAdapter<String> mAdapter;
    String pos, pos2, newindex;
    double currentLatitude, currentLongitude;       // Current long. and lat. read from GPSLocs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();

        currentLatitude = 41.3111;
        currentLongitude = -72.9267;
        pos2 = "Yale";

        if (extras != null && extras.containsKey("currentLatitude")
                && extras.containsKey("currentLongitude") ) {
            currentLatitude = getIntent().getDoubleExtra("currentLatitude", 41.3111);
            currentLongitude = getIntent().getDoubleExtra("currentLongitude", -72.9267);
            pos2 = getIntent().getStringExtra("name");
        }

        // List of strings for building and addresses
        final String[] buildings = getResources().getStringArray(R.array.building_strs);
        final String[] addrlist = getResources().getStringArray(R.array.building_abbr);

        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);     // Show home as a back arrow
        //actionbar.setDisplayShowHomeEnabled(true);     // Show application logo
        actionbar.setDisplayShowTitleEnabled(true);    // Show activity title/subtitle
        actionbar.setDisplayUseLogoEnabled(false);     // Use activity logo instead of activity icon
        actionbar.setTitle(getString(R.string.maps));  // Set title

        setContentView(R.layout.map_simple);

        // Get a handle to the Map Fragment
        GoogleMap map = ((MapFragment) getFragmentManager()
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
        }

        /*mSearchView = (SearchView) findViewById(R.id.search_view);
        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setAdapter(mAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                buildings));
        mListView.setTextFilterEnabled(true);
        setupSearchView();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View v, int position,
                                    long id) {
                Log.d("MAP", "OnClick");

                pos2 = mAdapter.getItem(position);

                //Searches for the string text in the listview_array string
                //and returns index regardless of searching due to 're'search
                int index = -1;
                for (int i = 0; (i < 144) && (index == -1); i++) {
                    if (buildings[i] == pos2) {
                        index = i;
                        break;
                    }
                }

                newindex = String.valueOf(index);
                Log.d("MAP", newindex);


                //Finds current id of item. When searching items, resets counter to 0, 1, 2, etc
                //pos1 = (Long) adapter.getItemId(position);
                //pos = String.valueOf(pos1);
                //Log.d("MAP", pos);

                String map1 = "http://maps.google.com/maps?q="
                        + addrlist[index] + ",+New+Haven,+CT+06511";
                Log.d("MAPVIEWSEARCH", map1);
                //Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map1));
                //startActivity(i);
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        //final String[] mStrings = Cheeses.sCheeseStrings;

        //getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        //getMenuInflater().inflate(R.menu.testsearch_menu, menu);

        getMenuInflater().inflate(R.menu.map_menu, menu);
        this.menu = menu;

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //SearchView searchView =
          //      (SearchView) menu.findItem(R.id.search_view).getActionView();
        //searchView.setSearchableInfo(
          //      searchManager.getSearchableInfo(getComponentName()));
        //setContentView(R.layout.testsearch_filter);

        return super.onCreateOptionsMenu(menu);
    }

    private void setupSearchView() {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(edu.yalestc.yalepublic.MapView.this);
        mSearchView.setSubmitButtonEnabled(false);
        //mSearchView.setQueryHint(getString(R.string.cheese_hunt_hint));
        mSearchView.setQueryHint("Hint");
    }

    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            mListView.clearTextFilter();
        } else {
            mListView.setFilterText(newText.toString());
        }
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        Log.d("MapViewSubmit", query);
        return false;
    }

    public void loadSearch(MenuItem item) {
        Intent iSearch = new Intent(this, TestSearch.class);
        startActivity(iSearch);
    }

        /*
        getMenuInflater().inflate(R.menu.map_menu, menu);
        this.menu = menu;

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("Query submit", query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                //loadHistory(query);
                return true;
            }
        });

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                Log.d("Position1", Integer.toString(position));
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Log.d("Position2", Integer.toString(position));
                return false;
            }
        });*/


        //return super.onCreateOptionsMenu(menu);
    //}

    /*private void loadHistory(String query) {
        // Cursor
        String[] columns = new String[] { "_id", "text" };
        Object[] temp = new Object[] { 0, "default" };
        List<String> items = Arrays.asList(getResources().getStringArray(R.array.building_strs));

        MatrixCursor cursor = new MatrixCursor(columns);

        for(int i = 0; i < items.size(); i++) {
            temp[0] = i;
            temp[1] = items.get(i);
            cursor.addRow(temp);
        }

        // SearchView
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        //final SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setSuggestionsAdapter(new MapAdapter(this, cursor, items));
    }*/
}
