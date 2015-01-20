package edu.yalestc.yalepublic.Events.CalendarView;

import android.app.Activity;
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

import edu.yalestc.yalepublic.Cache.CalendarCache;
import edu.yalestc.yalepublic.Cache.CalendarDatabaseTableHandler;
import edu.yalestc.yalepublic.Events.DateFormater;
import edu.yalestc.yalepublic.Events.EventsAdapterForLists;
import edu.yalestc.yalepublic.Events.EventsJSONReader;
import edu.yalestc.yalepublic.Events.EventsParseForDateWithinCategory;
import edu.yalestc.yalepublic.R;

/**
 * Created by Stan Swidwinski on 11/11/14.
 * <p/>
 * Class handles displaying the data in the list beneath the custom calendar.
 */
public class EventsCalendarEventList extends EventsAdapterForLists {

    //for displaying only relevant data. Given as the day of month as understood by EventsCalendarGridAdapter.getDayNumber()..
    private int mSelectedDayOfMonth;
    //array holding only the displayed events
    ArrayList<String[]> eventsOnCurrentDay;

    public EventsCalendarEventList(Activity activity, int year, int month, int selectedDayOfMonth, int category, int[] colors, int colorsFrom[]) {
        super(activity, year, month, category, colors, colorsFrom);
        allTheEvents = null;
        mActivity = activity;
        update(year, month);
        mSelectedDayOfMonth = selectedDayOfMonth;
        // there is no need for handling anything when the data is not cached since the underlying EventsAdapterForLists
        // already does it!
        if(CalendarCache.isCached(mActivity, mMonth, mYear)){
            CalendarDatabaseTableHandler db = new CalendarDatabaseTableHandler(mActivity);
            eventsOnCurrentDay = db.getEventsOnDateWithinCategory((DateFormater.convertDateToString(mYear, mMonth, mSelectedDayOfMonth)), mCategoryNo);
        }
    }

    //used from CalendarFragment for getting the events
    public String[] getEventInfo(int whichEvent) {
        return eventsOnCurrentDay.get(whichEvent);
    }

    //called when the selected day is changed. Updates the events for a given day and the day itself.
    public void setmSelectedDayOfMonth(int selectedDayOfMonth) {
        mSelectedDayOfMonth = selectedDayOfMonth;
        //getting the dataset to display depends on the existance of it in the cached database
        if (!CalendarCache.isCached(mActivity, mMonth, mYear)) {
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
        GradientDrawable circle = super.createBlob(color, colorFrom);

        if (convertView != null) {
            return super.recycleView((RelativeLayout) convertView, singleEvent, circle);
        } else {
            return super.createNewEventElement(singleEvent, circle);
        }
    }
}