package edu.yalestc.yalepublic.Events.SearchEvents;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import edu.yalestc.yalepublic.Cache.CalendarDatabaseTableHandler;
import edu.yalestc.yalepublic.Events.EventsDetails;
import edu.yalestc.yalepublic.Events.ListView.EventsListAdapter;
import edu.yalestc.yalepublic.R;

/**
 * Created by Stan Swidwinski on 1/23/15.
 */
public class SearchByName extends ActionBarActivity {

    View rootView;
    EventsListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true); // Show home as a back arrow
        actionbar.setDisplayShowTitleEnabled(true);  // Show activity title/subtitle
        actionbar.setDisplayUseLogoEnabled(false);   // Use activity logo instead of activity icon
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (query != null)
                doMySearch(query);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (query != null)
                doMySearch(query);
        }
    }

    private void doMySearch(String query) {
        CalendarDatabaseTableHandler db = new CalendarDatabaseTableHandler(this);
        ArrayList<String[]> eventsFound = db.searchEventsByName(query);
        if (eventsFound.size() != 0) {
            mAdapter = new EventsListAdapter(this);
            mAdapter.setAllEventsInfo(eventsFound);

            rootView = getLayoutInflater().inflate(R.layout.activity_events_list, null);
            ((ListView) ((RelativeLayout) rootView).getChildAt(0)).setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String[] eventInfo = mAdapter.getInformation(i);

                    if (eventInfo == null)
                        return;

                    int color, colorTo, colorFrom;

                    color = getResources().getIntArray(R.array.event_categories_colors)[Integer.parseInt(eventInfo[6])];
                    colorTo = getResources().getIntArray(R.array.event_categories_colors_into)[Integer.parseInt(eventInfo[6])];
                    colorFrom = getResources().getIntArray(R.array.event_categories_colors_into)[Integer.parseInt(eventInfo[6])];

                    //put data into the extras
                    Intent eventDetails = new Intent(SearchByName.this, EventsDetails.class);
                    eventDetails.putExtra("title", eventInfo[0]);
                    eventDetails.putExtra("start", eventInfo[4] + " " + eventInfo[1]);
                    eventDetails.putExtra("end", eventInfo[4] + " " + eventInfo[2]);
                    //category color in the middle of the blob/rectangle
                    eventDetails.putExtra("color", color);
                    //category color at the bottom of the blob/rectangle
                    eventDetails.putExtra("colorTo", colorTo);
                    //category color at the top of the blob/rectangle
                    eventDetails.putExtra("colorFrom", colorFrom);
                    eventDetails.putExtra("description", eventInfo[5]);
                    eventDetails.putExtra("location", eventInfo[3]);
                    startActivity(eventDetails);
                }
            });
            ((ListView) ((RelativeLayout) rootView).getChildAt(0)).setAdapter(mAdapter);

            setContentView(rootView);
        } else {
            setContentView(R.layout.events_search_not_found);
        }
    }
}
