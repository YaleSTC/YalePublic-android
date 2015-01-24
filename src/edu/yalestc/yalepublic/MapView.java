package edu.yalestc.yalepublic;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
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
public class MapView extends Activity {

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        LatLng yale = new LatLng(41.3111, -72.9267);   // The - signifies the Western Hemisphere

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(yale, 13));

        map.addMarker(new MarkerOptions()
                .title("Yale")
                .snippet("The best university ever.")
                .position(yale));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
                loadHistory(query);
                return true;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    private void loadHistory(String query) {
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
    }
}
