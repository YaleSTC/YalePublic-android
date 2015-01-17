package edu.yalestc.yalepublic.Events;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import edu.yalestc.yalepublic.R;

import static edu.yalestc.yalepublic.R.drawable.calendar_grid_button_current_selected;
import static edu.yalestc.yalepublic.R.drawable.calendar_grid_button_current_unselected;
import static edu.yalestc.yalepublic.R.drawable.calendar_grid_button_selected;
import static edu.yalestc.yalepublic.R.drawable.calendar_grid_button_unselected;

/**
 * Created by Stan Swidwinski on 11/17/14.
 *
 * Fragment being the core of the calendar screen.
 *
 */

public class CalendarFragment extends Fragment {

    View rootView;
        //for keeping track of parent activity, specifically when onAttach after detaching
    Activity mActivity;
    Calendar c = Calendar.getInstance();
    int month = c.get(Calendar.MONTH);
    int year = c.get(Calendar.YEAR);
    String monthName = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
    int daysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
    int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        //grids
    EventsCalendarGridAdapter calendarAdapter;
        //list under the calendar grids
    EventsCalendarEventList listEvents;
        //passed using static newInstance, assigned in onCreate()
    Bundle mExtras;
        //For rawData pulled from internet (raw JSON)
    String mRawData;

        //since fragments need empty constructor, a static function for creatino of new fragments
    //is necessary
    public static final CalendarFragment newInstance(Bundle extras){
        CalendarFragment f = new CalendarFragment();
        Bundle bld = extras;
        //Arguments can and are accessed within a fragment. See onCreate();
        f.setArguments(bld);
        return f;
    }

    public CalendarFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mRawData = null;
        mExtras = getArguments();
        mActivity = getActivity();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.events_calendar, container, false);
            //set the month name above the calendar grids
        ((TextView) ((RelativeLayout) (((RelativeLayout) rootView).getChildAt(0))).getChildAt(1)).setText(monthName);

            //set the onclick listener of arrow changing month to the previous one
        ((Button) ((RelativeLayout) (((RelativeLayout) rootView).getChildAt(0))).getChildAt(0)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMonthAndData(-1);
            }
        });

             //set onclick listener to arrow changing the month to the next one
        ((Button) ((RelativeLayout) (((RelativeLayout) rootView).getChildAt(0))).getChildAt(2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMonthAndData(1);
            }
        });

        calendarAdapter = new EventsCalendarGridAdapter(getActivity());
        calendarAdapter.update(year, month);

        listEvents = new EventsCalendarEventList(getActivity(), year, month, calendarAdapter.getCurrentlySelected(), mExtras.getInt("category"), mExtras.getIntArray("colors"), mExtras.getIntArray("colorsFrom"));
        ((ListView) ((RelativeLayout) rootView).getChildAt(4)).setAdapter(listEvents);

            //set the listener for elements on the list, create intent and add all the information required
        ((ListView) ((RelativeLayout) rootView).getChildAt(4)).setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String[] eventInfo = listEvents.getEventInfo(i);
                int color, colorTo, colorFrom;
                    //depending on the category, we either have a single color (!=0) or many colors
                    // (0)
                if(mExtras.getIntArray("colors").length == 1){
                    color = mExtras.getIntArray("colors")[0];
                    colorTo = mExtras.getIntArray("colorsTo")[0];
                    colorFrom = mExtras.getIntArray("colorsFrom")[0];
                } else {
                    color = mExtras.getIntArray("colors")[Integer.parseInt(eventInfo[6])];
                    colorTo = mExtras.getIntArray("colorsTo")[Integer.parseInt(eventInfo[6])];
                    colorFrom = mExtras.getIntArray("colorsFrom")[Integer.parseInt(eventInfo[6])];
                }
                Intent eventDetails = new Intent(getActivity(), EventsDetails.class);
                eventDetails.putExtra("title",eventInfo[0]);
                eventDetails.putExtra("start",eventInfo[4] + eventInfo[1]);
                eventDetails.putExtra("end",eventInfo[4] + eventInfo[2]);
                    //category color in the middle of the blob/rectangle
                eventDetails.putExtra("color",color);
                    //category color at the bottom of the blob/rectangle
                eventDetails.putExtra("colorTo", colorTo);
                    //category color at the top of the blob/rectangle
                eventDetails.putExtra("colorFrom", colorFrom);
                eventDetails.putExtra("description",eventInfo[5]);
                eventDetails.putExtra("location",eventInfo[3]);
                startActivity(eventDetails);
            }
        });
        ((GridView) (((RelativeLayout) rootView).getChildAt(2))).setAdapter(calendarAdapter);
            //onClickListener for the grids within the calendar
        ((GridView) (((RelativeLayout) rootView).getChildAt(2))).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //we only want to be able to select dates within current month
                if (!calendarAdapter.isOutsideCurrentMonth(i)) {
                    //Change the drawables of tile that become "unselected"
                    //NOTE: -1 in below tells calendarAdapter to look at currentlySelected (stored within the adapter)
                    if (calendarAdapter.isToday(-1)) {
                        //If the selected day was today's day, load a special bitmap ...
                        ((ImageView) ((RelativeLayout) ((GridView) (((RelativeLayout) rootView).getChildAt(2))).getChildAt(calendarAdapter.getCurrentlySelected())).getChildAt(0)).setImageDrawable(getResources().getDrawable(calendar_grid_button_current_unselected));
                    } else {
                        //If the selected day was not today's day, load the usual bitmap
                        ((ImageView) ((RelativeLayout) ((GridView) (((RelativeLayout) rootView).getChildAt(2))).getChildAt(calendarAdapter.getCurrentlySelected())).getChildAt(0)).setImageDrawable(getResources().getDrawable(calendar_grid_button_unselected));
                    }
                    //change text color of unselected date to dark gray
                    ((TextView) ((RelativeLayout) ((GridView) (((RelativeLayout) rootView).getChildAt(2))).getChildAt(calendarAdapter.getCurrentlySelected())).getChildAt(1)).setTextColor(Color.parseColor("#3d4b5a"));
                    //notify calendar Adapter of change in selected item
                    calendarAdapter.setCurrentlySelected(i);
                    //get new set of items to be displayed in the list of events beneath the calendar
                    listEvents.setmSelectedDayOfMonth(calendarAdapter.getDayNumber(i));
                    //updateEvents the list of events
                    listEvents.notifyDataSetChanged();
                    //change the drawables of tiles that become "selected"
                    //NOTE: -1 in below tells calendarAdapter to look at currentlySelected (stored within the adapter)
                    if (calendarAdapter.isToday(-1)) {
                        //If the selected day is today, load special bitmap
                        ((ImageView) ((RelativeLayout) ((GridView) (((RelativeLayout) rootView).getChildAt(2))).getChildAt(calendarAdapter.getCurrentlySelected())).getChildAt(0)).setImageDrawable(getResources().getDrawable(calendar_grid_button_current_selected));
                    } else {
                        //If the selected day is not today, load the usual bitmap
                        ((ImageView) ((RelativeLayout) ((GridView) (((RelativeLayout) rootView).getChildAt(2))).getChildAt(calendarAdapter.getCurrentlySelected())).getChildAt(0)).setImageDrawable(getResources().getDrawable(calendar_grid_button_selected));
                    }
                    //change the text color to "Selected color: - White.
                    ((TextView) ((RelativeLayout) ((GridView) (((RelativeLayout) rootView).getChildAt(2))).getChildAt(calendarAdapter.getCurrentlySelected())).getChildAt(1)).setTextColor((Color.parseColor("#FFFFFF")));
                    //Log.v("Item", "Clicked on calendar!");
                }
            }

        });
        return rootView;
    }

        //change the month by i and updateEvents related fields. That includes: year, calendarAdapter, dayOfWeek
    //daysInMonth, monthName, mRawData, listEvents
    void updateMonthAndData(int i) {
        c.getFirstDayOfWeek();
        //compute change in month and year. Note we are operating on the calendar range of 0-11
        month = month + i;
        if (month < 0) {
            year--;
            month = 11;
        } else if (month > 11) {
            year++;
            month = 0;
        }
        //set the new date in calendar
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        monthName = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        daysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        dayOfWeek = c.get(Calendar.DAY_OF_WEEK_IN_MONTH);
        //notify the adapter of new date
        calendarAdapter.update(year, month);
        //ask adapter to updateEvents the UI
        calendarAdapter.notifyDataSetChanged();
        listEvents.update(year, month);
        //set the proper name of month at the header of the calendar
        ((TextView) ((RelativeLayout) (((RelativeLayout) rootView).getChildAt(0))).getChildAt(1)).setText(monthName);
    }
}