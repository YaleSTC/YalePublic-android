package edu.yalestc.yalepublic.Events;

import android.app.Activity;
import android.app.usage.UsageEvents;
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

import java.util.concurrent.ExecutionException;

import edu.yalestc.yalepublic.Cache.CalendarCache;
import edu.yalestc.yalepublic.R;

/**
 * Created by Stan Swidwinski on 1/19/15.
 *
 * The high-level class that implements elements common to the list adapters in both the
 * listView and CalendarView.
 */
public class EventsAdapterForLists extends BaseAdapter {

    protected int mYear;
    //month here is in the standard format
    protected int mMonth;
    //for inflating layouts and displaymetrics and checking for cached
    DisplayMetrics display;
    protected Activity mActivity;
    protected int height;
    protected int width;
    //for quicker parsing of events. Is passed in from MonthFragment. See EventsParseForDateWithinCategory for more information
    //if allTheEvents is null, it means that we are using cached information!
    protected EventsParseForDateWithinCategory allTheEvents;
    //workaround for now. There is a discrepancy between how EventsParseForDateWithinCategory and db work...
    //IDEA: after any data-pulling always add it to db. It's text and is cleared every month. ----> seems like a good idea
    protected int mCategoryNo;
    //for ovals next to time
    protected int[] mColors;
    protected int[] mColorsFrom;
    private BaseAdapter mAdapter;

    //if we want to have a class that inherits, does not manage everything for us, but still
    //gives access to all those functions
    protected EventsAdapterForLists(Activity activity){
        mActivity = activity;
        display = mActivity.getResources().getDisplayMetrics();
        height = display.heightPixels;
        width = display.widthPixels;
    };

    protected EventsAdapterForLists(Activity activity, int year, int month, int category, int[] colors, int colorsFrom[]){
        mActivity = activity;
        allTheEvents = null;
        mCategoryNo = category;
        update(year, month);
        mColors = colors;
        mColorsFrom = colorsFrom;
        display = mActivity.getResources().getDisplayMetrics();
        height = display.heightPixels;
        width = display.widthPixels;
    }

    protected void setCallbackAdapter(BaseAdapter adapter){
        mAdapter = adapter;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
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
        mAdapter.notifyDataSetChanged();
    }

    //set the new month and year, then pull the data from internet if needed. The AsyncTask
    //then calls updateEvents once it is done, so there is no need to call it at all.
    public void update(int year, int month) {
        updateMonthYear(year, month);
        if (!CalendarCache.isCached(mActivity, mMonth, mYear)) {
            pullDataFromInternet();
        }
    }

    //sets new values to mMonth and mYear
    public void updateMonthYear(int year, int month) {
        mYear = year;
        //because calendar operates on 0 - 11
        mMonth = month + 1;
    }

    protected void pullDataFromInternet() {
        String dateSearched = DateFormater.calendarDateToJSONQuery(mYear, mMonth - 1);
        EventsJSONReader newData = new EventsJSONReader("http://calendar.yale.edu/feeds/feed/opa/json/" + dateSearched + "/30days", mActivity);
        newData.setAdapter(this);
        Log.i("EventsCalendarEventList", "Pulling uncached data using query http://calendar.yale.edu/feeds/feed/opa/json/" + dateSearched + "/30days");
        newData.execute();
    }

    //make the little blob next to events name etc.
    protected GradientDrawable createBlob(int color, int colorFrom) {
        int[] colors = new int[]{colorFrom, color};
        GradientDrawable blob = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        blob.setShape(GradientDrawable.OVAL);
        blob.setSize(40, 40);
        blob.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        blob.setGradientRadius(30);
        blob.setGradientCenter((float) 0.5, (float) 0.0);

        return blob;
    }

    protected View createNewEventElement(String[] information, GradientDrawable circle){
        RelativeLayout eventListElement = (RelativeLayout) LayoutInflater.from(mActivity).inflate(R.layout.calendar_list_element, null);
        eventListElement.setMinimumHeight((int) (height * 0.104));
        ((ImageView) eventListElement.getChildAt(0)).setImageDrawable(circle);
        //set the size of the time element
        ((TextView) eventListElement.getChildAt((1))).setMinWidth(width/5);
        //set the time of the event
        ((TextView) eventListElement.getChildAt(1)).setText(information[1]);
        //set the title of the event
        ((TextView) eventListElement.getChildAt(2)).setText(information[0]);
        //set the palce of the event
        ((TextView) eventListElement.getChildAt(3)).setText(information[3]);
        eventListElement.setBackgroundColor(Color.parseColor("#dbdbdd"));
        return eventListElement;
    }

    protected View recycleView(RelativeLayout convertView, String[] information, GradientDrawable circle){
        ((ImageView) ((RelativeLayout) convertView).getChildAt(0)).setImageDrawable(circle);
        //set the time of the event
        ((TextView) ((RelativeLayout) convertView).getChildAt(1)).setText(information[1]);
        //set the title of the event
        ((TextView) ((RelativeLayout) convertView).getChildAt(2)).setText(information[0]);
        //set the place of the event
        ((TextView) ((RelativeLayout) convertView).getChildAt(3)).setText(information[3]);
        return convertView;
    }
}
