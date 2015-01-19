package edu.yalestc.yalepublic.Events.CalendarView;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import edu.yalestc.yalepublic.Cache.CalendarDatabaseTableHandler;
import edu.yalestc.yalepublic.Events.DateFormater;
import edu.yalestc.yalepublic.Events.EventsJSONReader;
import edu.yalestc.yalepublic.Events.EventsParseForDateWithinCategory;
import edu.yalestc.yalepublic.R;

/**
 * Created by Stan Swidwinski on 11/11/14.
 * <p/>
 * Class handles displaying the data in the list beneath the custom calendar.
 */
public class EventsCalendarEventList extends BaseAdapter {
    //for inflating layouts and DisplayMetrics
    private Activity mActivity;
    private DisplayMetrics display;
    private int height;
    private int mYear;
    //month here will be in the standard format
    private int mMonth;
    //for quicker parsing of events. Is passed in from MonthFragment. See EventsParseForDateWithinCategory for more information
    //if allTheEvents is null, it means that we are using cached information!
    private EventsParseForDateWithinCategory allTheEvents;
    //workaround for now. There is a discrepancy between how EventsParseForDateWithinCategory and db work...
    //IDEA: after any data-pulling always add it to db. It's text and is cleared every month. ----> seems like a good idea
    private int mCategoryNo;
    //for ovals next to time
    private int[] mColors;
    private int[] mColorsFrom;
    //for displaying only relevant data. Given as the day of month as understood by EventsCalendarGridAdapter.getDayNumber()..
    private int mSelectedDayOfMonth;
    //array holding only the displayed events
    ArrayList<String[]> eventsOnCurrentDay;

    public EventsCalendarEventList(Activity activity, int year, int month, int selectedDayOfMonth, int category, int[] colors, int colorsFrom[]) {
        allTheEvents = null;
        mActivity = activity;
        update(year, month);
        mColors = colors;
        mColorsFrom = colorsFrom;
        mSelectedDayOfMonth = selectedDayOfMonth;
        CalendarDatabaseTableHandler db = new CalendarDatabaseTableHandler(mActivity);
        eventsOnCurrentDay = db.getEventsOnDateWithinCategory((DateFormater.convertDateToString(mYear, mMonth, mSelectedDayOfMonth)), mCategoryNo);
        display = mActivity.getResources().getDisplayMetrics();
        height = display.heightPixels;
        mCategoryNo = category;
    }

    //used from CalendarFragment for getting the events
    public String[] getEventInfo(int whichEvent) {
        return eventsOnCurrentDay.get(whichEvent);
    }

    //sets new values to mMonth and mYear
    public void updateMonthYear(int year, int month) {
        mYear = year;
        //because calendar operates on 0 - 11
        mMonth = month + 1;
    }

    //called from EventsJSONReader after new data has been downloaded and is ready to be parsed.
    public void updateEvents(String rawData) {
        //allTheEvents is null as of the constructor, so need to check if it is the first time that
        //we need to parse any data.
        if (allTheEvents != null) {
            //the parser accepts the calendar-formatted date!
            allTheEvents.setNewEvents(rawData, mMonth - 1, mYear);
        } else {
            //the constructor by default takes in data to parse, so no need to call setNewEvents
            allTheEvents = new EventsParseForDateWithinCategory(rawData, mMonth - 1, mYear, mActivity, mCategoryNo);
        }
    }

    //set the new month and year, then pull the data from internet if needed. The AsyncTask
    //then calls updateEvents once it is done, so there is no need to call it at all.
    public void update(int year, int month) {
        updateMonthYear(year, month);
        if (!isCached()) {
            pullDataFromInternet();
        }
    }

    //called when the selected day is changed. Updates the events for a given day and the day itself.
    public void setmSelectedDayOfMonth(int selectedDayOfMonth) {
        mSelectedDayOfMonth = selectedDayOfMonth;
        //getting the dataset to display depends on the existance of it in the cached database
        if (!isCached()) {
            eventsOnCurrentDay = allTheEvents.getEventsOnGivenDate((DateFormater.convertDateToString(mYear, mMonth, mSelectedDayOfMonth)));
        } else {
            CalendarDatabaseTableHandler db = new CalendarDatabaseTableHandler(mActivity);
            eventsOnCurrentDay = db.getEventsOnDateWithinCategory((DateFormater.convertDateToString(mYear, mMonth, mSelectedDayOfMonth)), mCategoryNo);
        }
    }

    @Override
    public int getCount() {
        return eventsOnCurrentDay.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        int color;
        int colorFrom;
        String[] singleEvent = eventsOnCurrentDay.get(i);
        //set the color using category number
        if (mColors.length != 1) {
            color = mColors[Integer.parseInt(singleEvent[6])];
            colorFrom = mColorsFrom[Integer.parseInt(singleEvent[6])];
        } else {
            color = mColors[0];
            colorFrom = mColorsFrom[0];
        }
        //Log.v("EventsCalendarEventList", "Created a view for the " + Integer.toString(i) + " view");
        GradientDrawable circle = createBlob(color, colorFrom);

        if (convertView != null) {
            ((ImageView) ((RelativeLayout) convertView).getChildAt(0)).setImageDrawable(circle);
            //set the time of the event
            ((TextView) ((RelativeLayout) convertView).getChildAt(1)).setText(singleEvent[1]);
            //set the title of the event
            ((TextView) ((RelativeLayout) convertView).getChildAt(2)).setText(singleEvent[0]);
            //set the place of the event
            ((TextView) ((RelativeLayout) convertView).getChildAt(3)).setText(singleEvent[3]);
            return convertView;
        } else {
            RelativeLayout eventListElement = (RelativeLayout) LayoutInflater.from(mActivity).inflate(R.layout.calendar_list_element, null);
            eventListElement.setMinimumHeight((int) (height * 0.104));
            ((ImageView) eventListElement.getChildAt(0)).setImageDrawable(circle);
            //set the time of the event
            ((TextView) eventListElement.getChildAt(1)).setText(singleEvent[1]);
            //set the title of the event
            ((TextView) eventListElement.getChildAt(2)).setText(singleEvent[0]);
            //set the palce of the event
            ((TextView) eventListElement.getChildAt(3)).setText(singleEvent[3]);
            eventListElement.setBackgroundColor(Color.parseColor("#dbdbdd"));
            return eventListElement;
        }
    }

    //make the little blob next to events name etc.
    private GradientDrawable createBlob(int color, int colorFrom) {
        int[] colors = new int[]{colorFrom, color};
        GradientDrawable blob = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        blob.setShape(GradientDrawable.OVAL);
        blob.setSize(40, 40);
        blob.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        blob.setGradientRadius(30);
        blob.setGradientCenter((float) 0.5, (float) 0.0);

        return blob;
    }

    private boolean isCached() {
        //YYYYMM01 format
        int eventsParseFormat = Integer.parseInt(DateFormater.calendarDateToEventsParseForDate(mYear, mMonth - 1, 1));
        Log.i("EventsCalendarEventList", "Checking if date " + Integer.toString(eventsParseFormat) + " is cached");
        //same format as above. See CalendarCache
        SharedPreferences eventPreferences = mActivity.getSharedPreferences("events", 0);
        int lowerBoundDate = eventPreferences.getInt("botBoundDate", 0);
        int topBoundDate = eventPreferences.getInt("topBoundDate", 0);
        boolean result = DateFormater.inInterval(lowerBoundDate, topBoundDate, eventsParseFormat);
        Log.i("EventsCalendarEventList", Boolean.toString(result));
        return result;
    }

    private void pullDataFromInternet() {
        String dateSearched = DateFormater.calendarDateToJSONQuery(mYear, mMonth - 1);
        EventsJSONReader newData = new EventsJSONReader("http://calendar.yale.edu/feeds/feed/opa/json/" + dateSearched + "/30days", mActivity);
        newData.setEventsListAdapter(this);
        Log.i("EventsCalendarEventList", "Pulling uncached data using query http://calendar.yale.edu/feeds/feed/opa/json/" + dateSearched + "/30days");
        newData.execute();
    }
}