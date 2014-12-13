package edu.yalestc.yalepublic.Events;

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
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import edu.yalestc.yalepublic.R;
import edu.yalestc.yalepublic.Videos.JSONReader;

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
    Calendar c = Calendar.getInstance();
    int month = c.get(Calendar.MONTH);
    int year = c.get(Calendar.YEAR);
    String monthName = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
    int daysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
    int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
    EventsCalendarGridAdapter calendarAdapter;
    EventsCalendarEventList listEvents;
        //passed in constructor!
    Bundle mExtras;
        //For initial rawData
    String mRawData;

    public CalendarFragment(Bundle extras, String rawData){
        mExtras = extras;
        mRawData = rawData;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.events_calendar, container, false);
            //set the month name up there
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
        listEvents = new EventsCalendarEventList(getActivity(), new EventsParseForDateWithinCategory(mRawData, month, year, getActivity(), mExtras.getInt("numberOfCategorySearchedFor")), year, month, calendarAdapter.getCurrentlySelected(), mExtras.getIntArray("colors"), mExtras.getIntArray("colorsFrom"));
        ((ListView) ((RelativeLayout) rootView).getChildAt(4)).setAdapter(listEvents);
            //set the listener for elemnts on the list, create intent and add all the information required
        ((ListView) ((RelativeLayout) rootView).getChildAt(4)).setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String[] eventInfo = listEvents.getEventInfo(i);
                int color, colorTo, colorFrom;
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
                eventDetails.putExtra("color",color);
                eventDetails.putExtra("colorTo", colorTo);
                eventDetails.putExtra("colorFrom", colorFrom);
                eventDetails.putExtra("description",eventInfo[5]);
                eventDetails.putExtra("location",eventInfo[3]);
                startActivity(eventDetails);
            }
        });
        ((GridView) (((RelativeLayout) rootView).getChildAt(2))).setAdapter(calendarAdapter);
        ((GridView) (((RelativeLayout) rootView).getChildAt(2))).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!calendarAdapter.isOutsideCurrentMonth(i)) {
                    //Change the drawables of tile that become "unselected"
                    if (calendarAdapter.isToday(-1)) {
                        //If the selected day was today's day, load a special bitmap ...
                        ((ImageView) ((RelativeLayout) ((GridView) (((RelativeLayout) rootView).getChildAt(2))).getChildAt(calendarAdapter.getCurrentlySelected())).getChildAt(0)).setImageDrawable(getResources().getDrawable(calendar_grid_button_current_unselected));
                    } else {
                        //If the selected day was not today's day, load the usual bitmap
                        ((ImageView) ((RelativeLayout) ((GridView) (((RelativeLayout) rootView).getChildAt(2))).getChildAt(calendarAdapter.getCurrentlySelected())).getChildAt(0)).setImageDrawable(getResources().getDrawable(calendar_grid_button_unselected));
                    }
                    //change text color to dark gray
                    ((TextView) ((RelativeLayout) ((GridView) (((RelativeLayout) rootView).getChildAt(2))).getChildAt(calendarAdapter.getCurrentlySelected())).getChildAt(1)).setTextColor(Color.parseColor("#3d4b5a"));
                    //notify calendar Adapter of change in selected item
                    calendarAdapter.setCurrentlySelected(i);
                    //get new set of items to be displayed in the list of events beneath the calendar
                    listEvents.setmSelectedDayOfMonth(calendarAdapter.getDayNumber(i));
                    //update the list of events
                    listEvents.notifyDataSetChanged();
                    //change the drawables of tiles that become "selected"
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
                } else {

                }
            }

        });
        return rootView;
    }

        //change the month by i and update related fields. That includes: year, calendarAdapter, dayOfWeek
    //daysInMonth, monthName, mRawData, listEvents
    void updateMonthAndData(int i) {
        c.getFirstDayOfWeek();
        month = month + i;
        if (month < 0) {
            year--;
            month = 11;
        } else if (month > 11) {
            year++;
            month = 0;
        }
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        monthName = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        daysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        dayOfWeek = c.get(Calendar.DAY_OF_WEEK_IN_MONTH);
        calendarAdapter.update(year, month);
        calendarAdapter.notifyDataSetChanged();
             //pull new data for a given month!!
        JSONReader newData = new JSONReader("http://calendar.yale.edu/feeds/feed/opa/json/" + Integer.toString(year) + "-" + monthNumberToString() + "-01"  + "/30days", getActivity());
        try {
            mRawData = newData.execute().get();
            //rawData is null if there are problems. We get a toast for no internet!
            if (mRawData == null) {
                Toast toast = new Toast(getActivity());
                toast = Toast.makeText(getActivity(), "You need internet connection to view the content!", Toast.LENGTH_LONG);
                toast.show();
                getParentFragment().getActivity().finish();
                return;
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
            //parse mRawData into array of events. Checkout EventsParseForDateWithinCategory and EventsCalendarEventList for more information.
        listEvents.update(mRawData, month, year);
            //set the proper name of month at the header of the calendar
        ((TextView) ((RelativeLayout) (((RelativeLayout) rootView).getChildAt(0))).getChildAt(1)).setText(monthName);
    }

        //helper used in creating new URL for querying. Returns the month in the format MM and in the range 01-12
    String monthNumberToString() {
        String stringMonth;
        if (month + 1 < 10) {
            stringMonth = "0";
        } else {
            stringMonth = new String();
        }
            //+1 because of the way calendar treats months (0-11)
        stringMonth += Integer.toString(month + 1);
        return stringMonth;
    }
}