package edu.yalestc.yalepublic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import edu.yalestc.yalepublic.MapView;


/**
 * Created by Jason Liu on 1/30/15.
 */

/**
 * Shows a list that can be filtered in-place with a SearchView in non-iconified mode.
 */
public class TestSearch extends Activity implements SearchView.OnQueryTextListener {

    private static final String TAG = "SearchViewFilterMode";

    private SearchView mSearchView;
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    String pos, pos2, newindex;
    double currentLatitude, currentLongitude;       // Current long. and lat. read from GPSLocs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        setContentView(R.layout.testsearch_filter);

        // List of strings for building and addresses
        final String[] buildings = getResources().getStringArray(R.array.listview_array);
        final int[] GPSLocs = getResources().getIntArray(R.array.GPSCoordinates);

        mSearchView = (SearchView) findViewById(R.id.search_view);
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
                //Log.d("MAP", pos);*/

                //String map1 = "http://maps.google.com/maps?q="
                //        + addrlist[index] + ",+New+Haven,+CT+06511";
                //Log.d(TAG, map1);
                currentLatitude = GPSLocs[2 * index]/(10000000.);
                currentLongitude = GPSLocs[(2 * index) + 1]/(10000000.);
                Intent i = new Intent(TestSearch.this, MapView.class);
                i.putExtra("currentLatitude", currentLatitude);
                i.putExtra("currentLongitude", currentLongitude);
                i.putExtra("name", pos2);
                startActivity(i);

                //Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map1));
                //startActivity(i);
            }
        });
    }

    private void setupSearchView() {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setQueryHint(getString(R.string.building_search));
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
        Log.d(TAG, query);
        return false;
    }
}

