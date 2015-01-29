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
 * <p/>
 * * IMPORTANT NOTE: When the data set is not available and has to be downloaded, the underlying
 * class (EventsAdapterForLists) does the work for this class. However, the data set in this class
 * is empty until the underlying class finishes. Hence, we allow the underlying class to notify this
 * adapter when it is done using .notifyDataSetChanged(). This downloads additional data if necessary
 * and after it is done, displays the data. Hence, the calls are as follows:
 * <p/>
 * 1) constructor creates and empty data set so that getCount() does not throw nullptr
 * 2) underlying class downloads and parses data for current month
 * 3) underlying class calls notifyDataSetChanged
 * 4) data set is added to this adapter and displayed
 */
public class EventsCalendarEventList extends EventsAdapterForLists {

    //for displaying only relevant data. Given as the day of month as understood by EventsCalendarGridAdapter.getDayNumber()..
    private int mSelectedDayOfMonth;
    //array holding only the displayed events
    ArrayList<String[]> eventsOnCurrentDay;
    //for sending the days with events to grid adapter
    private EventsCalendarGridAdapter mAdapter;
    //we only want to send the days with events when new dataset is fetched (new month)
    private boolean _newDataSet;

    public EventsCalendarEventList(Activity activity, int year, int month, int selectedDayOfMonth, int category, int[] colors, int colorsFrom[], EventsCalendarGridAdapter adapter) {
        super(activity, year, month, category, colors, colorsFrom);
        mAdapter = adapter;
        _newDataSet = true;
        // for displaying the data after it is downloaded. Please see note at the top
        super.setCallbackAdapter(this);
        eventsOnCurrentDay = new ArrayList<>();
        setmSelectedDayOfMonth(selectedDayOfMonth);
        mActivity = activity;
    }

    //used from CalendarFragment for getting the events
    public String[] getEventInfo(int whichEvent) {
        return eventsOnCurrentDay.get(whichEvent);
    }

    // IMPORTANT NOTE: the underlying class pulls data from internet using an asyncTask. Hence,
    // when the adapter is first instantiated the data set is empty and only becomes available once
    // the asynctask completes and the underlying class finishes parsing the dataset. Then, it calls
    // the notifyDataSetChanged() on this class! See also .setCallbackAdapter(BaseAdapter) in
    // Events Adapter for Lists
    @Override
    public void notifyDataSetChanged() {
        setmSelectedDayOfMonth(mSelectedDayOfMonth);
        super.notifyDataSetChanged();
    }

    public void update(int year, int month) {
        super.update(year, month);
        _newDataSet = true;
    }

    //called when the selected day is changed. Updates the events for a given day and the day itself.
    public void setmSelectedDayOfMonth(int selectedDayOfMonth) {
        mSelectedDayOfMonth = selectedDayOfMonth;
        //getting the dataset to display depends on the existance of it in the cached database
        if (!CalendarCache.isCached(mActivity, mMonth, mYear)) {
            // allTheEvents is null when we first pull all the data (asyncTask is not done yet)
            // please see the note at the top of the class
            if (allTheEvents != null) {
                eventsOnCurrentDay = allTheEvents.getEventsOnGivenDate((DateFormater.convertDateToString(mYear, mMonth, mSelectedDayOfMonth)));
                if (_newDataSet) {
                    _newDataSet = false;
                    mAdapter.setDaysWithEvents(allTheEvents.daysWithEvents(mCategoryNo));
                    mAdapter.notifyDataSetChanged();
                }
            }
        } else {
            CalendarDatabaseTableHandler db = new CalendarDatabaseTableHandler(mActivity);
            eventsOnCurrentDay = db.getEventsOnDateWithinCategory((DateFormater.convertDateToString(mYear, mMonth, mSelectedDayOfMonth)), mCategoryNo);
            if (_newDataSet) {
                _newDataSet = false;
                mAdapter.setDaysWithEvents(db.getDaysWithEventsInCategory(mCategoryNo, mYear * 100 + mMonth));
                mAdapter.notifyDataSetChanged();
            }
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
            //since the category is a string with multiple categories, we need to retrieve
            int category = EventsParseForDateWithinCategory.retrieveCategory(singleEvent[6]);
            color = mColors[category];
            colorFrom = mColorsFrom[category];
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