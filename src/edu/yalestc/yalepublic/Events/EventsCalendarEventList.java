package edu.yalestc.yalepublic.Events;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import edu.yalestc.yalepublic.R;

/**
 * Created by Stan Swidwinski on 11/11/14.
 *
 * Class handles displaying the data in the list beneath the custom calendar.
 *
 */
public class EventsCalendarEventList extends BaseAdapter {
        //for inflating layouts
    private Context mContext;
    private DisplayMetrics display;
    private int height;
    private int mYear;
    private int mMonth;
        //for quicker parsing of events. Is passed in from MonthFragment. See EventsParseForDateWithinCategory for more information
    private EventsParseForDateWithinCategory allTheEvents;
        //for ovals next to time
    private int[] mColors;
    private int[] mColorsFrom;
        //for displaying only relevant data. Given as the day of month as understood by EventsCalendarGridAdapter.getDayNumber()..
    private int mSelectedDayOfMonth;
        //array holding only the displayed events
    ArrayList<String[]> eventsOnCurrentDay;

    EventsCalendarEventList (Context context, EventsParseForDateWithinCategory eventsParser, int year, int month, int selectedDayOfMonth, int[] colors, int[] colorsFrom){
        mContext = context;
        allTheEvents = eventsParser;
        mYear = year;
        //since calendar returns number 0 - 11 as a month
        mMonth = month+1;
        mColors = colors;
        mColorsFrom = colorsFrom;
        mSelectedDayOfMonth = selectedDayOfMonth;
        eventsOnCurrentDay = allTheEvents.getEventsOnGivenDate(getStringDateYearMonthDay());
        display = context.getResources().getDisplayMetrics();
        height = display.heightPixels;
    }

        //used from CalendarFragment for getting the events
    public String[] getEventInfo(int whichEvent){
        return eventsOnCurrentDay.get(whichEvent);
    }

        //called after the month is changed, parses the newly retrieved JSON object and updates
    //current Year and Month
    public void update(String rawData, int month, int year){
        allTheEvents.setNewEvents(rawData, month, year);
        mYear = year;
        //because calendar operates on months labelled 0 through 11
        mMonth = month + 1;
    }

        //called when the selected day is changed. Updates the events for a given day and the day itself.
    public void setmSelectedDayOfMonth(int selectedDayOfMonth) {
        mSelectedDayOfMonth = selectedDayOfMonth;
        eventsOnCurrentDay = allTheEvents.getEventsOnGivenDate(getStringDateYearMonthDay());
    }

        //helper for calling the EventsParseForDateWithinCategory in proper format. returns the date
    //as a string in the YYYYMMDD format.
    private String getStringDateYearMonthDay() {
        String date = Integer.toString(mYear);
        if(mMonth < 10){
            date += "0";
        }
        date += Integer.toString(mMonth);
        if(mSelectedDayOfMonth < 10) {
            date+="0";
        }
        date += Integer.toString(mSelectedDayOfMonth);
        return date;
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
        if(mColors.length != 1) {
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
            RelativeLayout eventListElement = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.calendar_list_element, null);
            eventListElement.setMinimumHeight((int)(height*0.104));
            ((ImageView) ((RelativeLayout) eventListElement).getChildAt(0)).setImageDrawable(circle);
                //set the time of the event
            ((TextView) ((RelativeLayout) eventListElement).getChildAt(1)).setText(singleEvent[1]);
                //set the title of the event
            ((TextView) ((RelativeLayout) eventListElement).getChildAt(2)).setText(singleEvent[0]);
                //set the palce of the event
            ((TextView) ((RelativeLayout) eventListElement).getChildAt(3)).setText(singleEvent[3]);
            eventListElement.setBackgroundColor(Color.parseColor("#dbdbdd"));
            return eventListElement;
        }
    }
        //make the little blob next to events name etc.
    private GradientDrawable createBlob(int color, int colorFrom) {
        int[] colors = new int[]{colorFrom, color};
        GradientDrawable blob = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        blob.setShape(GradientDrawable.OVAL);
        blob.setSize(40,40);
        blob.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        blob.setGradientRadius(30);
        blob.setGradientCenter((float)0.5,(float)0.0);

        return blob;
    }
}