package edu.yale.yalepublic.Events.ListView;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.Calendar;

import edu.yale.yalepublic.Events.EventsDetails;
import edu.yale.yalepublic.Events.EventsParseForDateWithinCategory;
import edu.yale.yalepublic.R;

/**
 * Created by Stan Swidwinski on 1/19/15.
 */
public class ListFragment extends Fragment {

    View rootView;
    Bundle mExtras;
    Activity mActivity;
    Calendar c = Calendar.getInstance();
    int month = c.get(Calendar.MONTH);
    int year = c.get(Calendar.YEAR);
    int day = c.get(Calendar.DAY_OF_MONTH);
    EventsListAdapter mAdapter;

    //since the fragment has to have an empty constructor
    public static ListFragment newInstance(Bundle extras) {
        ListFragment f = new ListFragment();
        f.setArguments(extras);
        return f;
    }

    public ListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mExtras = getArguments();
        mActivity = getActivity();
    }

    //to keep track of the activity when we are attached and detatched
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_events_list, container, false);
        mAdapter = new EventsListAdapter(mActivity, year, month, day, mExtras.getInt("numberOfCategorySearchedFor"), mExtras.getIntArray("colors"), mExtras.getIntArray("colorsFrom"));
        ((ListView) ((RelativeLayout) rootView).getChildAt(0)).setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String[] eventInfo = mAdapter.getInformation(i);

                if (eventInfo == null)
                    return;

                int color, colorTo, colorFrom;
                //depending on the category, we either have a single color (!=0) or many colors
                // (0)
                if (mExtras.getIntArray("colors").length == 1) {
                    color = mExtras.getIntArray("colors")[0];
                    colorTo = mExtras.getIntArray("colorsTo")[0];
                    colorFrom = mExtras.getIntArray("colorsFrom")[0];
                } else {
                    //since the category is a string with multiple categories, we need to retrieve
                    int cat = EventsParseForDateWithinCategory.retrieveCategory(eventInfo[6]);
                    color = mExtras.getIntArray("colors")[cat];
                    colorTo = mExtras.getIntArray("colorsTo")[cat];
                    colorFrom = mExtras.getIntArray("colorsFrom")[cat];
                }
                //put data into the extras
                Intent eventDetails = new Intent(getActivity(), EventsDetails.class);
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
        (((RelativeLayout) rootView).getChildAt(0)).post( new Runnable() {
            @Override
            public void run (){
                ((ListView) ((RelativeLayout) rootView).getChildAt(0)).setSelection(mAdapter.scrollListTo);
           }
        });
        return rootView;
    }
}
