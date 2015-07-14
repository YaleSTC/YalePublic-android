package edu.yalestc.yalepublic.Events;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

import edu.yalestc.yalepublic.Cache.CalendarCache;
import edu.yalestc.yalepublic.Cache.CalendarDatabaseTableHandler;
import edu.yalestc.yalepublic.Events.CalendarView.CalendarFragment;
import edu.yalestc.yalepublic.Events.ListView.ListFragment;
import edu.yalestc.yalepublic.R;


public class EventsDisplay extends Activity implements SearchView.OnQueryTextListener, SearchView.OnSuggestionListener{
    ActionBar.Tab monthT, listT;
    //    private Fragment dayTab;
    private Fragment monthTab;
    private Fragment listTab;
    Bundle extras;

    private CalendarDatabaseTableHandler db;
    private SearchView mSearchView;
    private MenuItem mMenuItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.events_display, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mMenuItem = menu.findItem(R.id.search);
        mSearchView = (SearchView) mMenuItem.getActionView();
        mSearchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        // Autocomplete configurations for SearchView
        db = new CalendarDatabaseTableHandler(this);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnSuggestionListener(this);

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);     // Show home as a back arrow
        //actionbar.setDisplayShowHomeEnabled(true);     // Show application logo
        actionbar.setDisplayShowTitleEnabled(true);    // Show activity title/subtitle
        actionbar.setDisplayUseLogoEnabled(false);     // Use activity logo instead of activity icon

        actionbar.setTitle(getString(R.string.events));  // Set title

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_display);

        extras = getIntent().getExtras();
        if (extras == null)  // safety check
            return;

        monthTab = CalendarFragment.newInstance(extras);
        listTab = ListFragment.newInstance(extras);

        //lets use the deprecated versions for now... this is deprecated only in android L
        ActionBar actionBar = getActionBar();
        monthT = actionBar.newTab().setText("Month");
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        //TODO recycle the tabs. at the moment we just recreate them
        monthT.setTabListener(new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                fragmentTransaction.replace(R.id.container, monthTab);
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                fragmentTransaction.remove(monthTab);
            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            }
        });

        actionBar.addTab(monthT);

        listT = actionBar.newTab().setText("List");
        listT.setTabListener(new ActionBar.TabListener() {

            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                fragmentTransaction.replace(R.id.container, listTab);
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                fragmentTransaction.remove(listTab);
            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            }
        });
        actionBar.addTab(listT);

    }

    //add functionality to the refresh button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //locally inheriting class bcs the original one is an integral part
        //of splash screen (at least getting the icon in bot-right)
        class Updater extends CalendarCache {
            //intent to relaunch the activity after getting new data. Easiest for us.
            Intent mIntent;
            Updater(Activity mActivity, Intent intent) {
                super(mActivity);
                mIntent = intent;
            }

            @Override
            protected void onPostExecute(String result) {
                if (super.dialog != null && super.dialog.isShowing()) {
                    super.dialog.dismiss();
                }
                //restart the activity
                super.mActivity.finish();
                startActivity(mIntent);
            }

        }
        //add functionality
        switch (item.getItemId()) {
            case R.id.refresh:
                Updater cache = new Updater(this, getIntent());
                //get rid of old data
                cache.wipeDatabase();
                cache.clearPreferences();
                //get new data
                cache.execute();
                return true;
        }
        //bcs you have to
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if(query.length() != 0) {
            Cursor cursor = db.getEventSuggestions(query);

            //shows a list of suggestions if available
            if (cursor.getCount() != 0 || query != null) {
                String[] columns = {"suggestions", "date"};
                int[] columnTextId = new int[]{R.id.suggestion_name, R.id.suggestion_date}; //where the data will be mapped to
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                        R.layout.suggestion_list_item, cursor, columns, columnTextId, 0);

                mSearchView.setSuggestionsAdapter(adapter);

                return true;
            } else {
                return false;
            }
        }
        else {
            //clears the list of suggestions if search dialog is empty
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                    R.layout.empty_layout, null, null, null, 0);
            mSearchView.setSuggestionsAdapter(adapter);

            return false;
        }
    }

    @Override
    public boolean onSuggestionSelect(int i) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int i) {
        //gets details of the event clicked on
        MatrixCursor cursor = (MatrixCursor) mSearchView.getSuggestionsAdapter().getItem(i);
        cursor.moveToPosition(i);
        String name = cursor.getString(1);
        String date = cursor.getString(2);
        String[] event = db.getEventByNameAndDate(name, date);

        int color, colorTo, colorFrom;

        color = getResources().getIntArray(R.array.event_categories_colors)[Integer.parseInt(event[6])];
        colorTo = getResources().getIntArray(R.array.event_categories_colors_into)[Integer.parseInt(event[6])];
        colorFrom = getResources().getIntArray(R.array.event_categories_colors_into)[Integer.parseInt(event[6])];

        //put data into the extras
        Intent eventDetails = new Intent(this, EventsDetails.class);
        eventDetails.putExtra("title", event[0]);
        eventDetails.putExtra("start", event[4] + " " + event[1]);
        eventDetails.putExtra("end", event[4] + " " + event[2]);
        //category color in the middle of the blob/rectangle
        eventDetails.putExtra("color", color);
        //category color at the bottom of the blob/rectangle
        eventDetails.putExtra("colorTo", colorTo);
        //category color at the top of the blob/rectangle
        eventDetails.putExtra("colorFrom", colorFrom);
        eventDetails.putExtra("description", event[5]);
        eventDetails.putExtra("location", event[3]);

        mMenuItem.collapseActionView();
        startActivity(eventDetails);

        return true;
    }

   /* private class DayTab extends Fragment {
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_event_categories, container, false);
            //this is done as shown here:
            //http://stackoverflow.com/questions/6858162/custom-calendar-dayview-in-android

            return view;
        }
    }

    private class WeekTab extends Fragment {
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_event_categories, container, false);
            //this is just a list that is parsed nicely. Or so I think. If not, it can be done almost tge same as dayTab.

            return view;
        }
    }*/
}