package edu.yalestc.yalepublic.Events.ListView;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import edu.yalestc.yalepublic.Cache.CalendarCache;
import edu.yalestc.yalepublic.Cache.CalendarDatabaseTableHandler;
import edu.yalestc.yalepublic.Events.DateFormater;
import edu.yalestc.yalepublic.Events.EventsAdapterForLists;
import edu.yalestc.yalepublic.R;

/**
 * Created by Stan Swidwinski on 1/19/15.
 *
 * Manages the creation of all elements in the list in ListView (List Fragment) including the date
 * elements.
 */
public class EventsListAdapter extends EventsAdapterForLists {

    Calendar calendar;
    int mDay;
    public int scrollListTo;
    int lastCheckedDate;
    int today;
    public elementIdToEventId converter;
    ArrayList<String[]> allEventsInfo;

    //for usage with the search capacity. we do not want anything done here, as we will
    //set the allEventsInfo by hand.
    public EventsListAdapter(Activity activity){
        super(activity);
        mActivity = activity;
        super.mColors = mActivity.getResources().getIntArray(R.array.event_categories_colors_from);
        super.mColorsFrom = mActivity.getResources().getIntArray(R.array.event_categories_colors);
    }

    public EventsListAdapter(Activity activity, int year, int month, int day, int category, int[] colors, int colorsFrom[]) {
        super(activity, year, month, category, colors, colorsFrom);
        mDay = day;
        scrollListTo = 0;
        // since the super class does not get all the events information, need to do it this way.
        if (CalendarCache.isCached(mActivity, mMonth, mYear)) {
            //retrieve events from db
            CalendarDatabaseTableHandler db = new CalendarDatabaseTableHandler(mActivity);
            allEventsInfo = db.getEventsInMonthWithinCategory(mYear * 10000 + mMonth * 100, mCategoryNo);
            allEventsInfo.addAll(db.getEventsInMonthWithinCategory((DateFormater.yearMonthFromStandardToStandard(mYear, mMonth + 1) * 100), mCategoryNo));
        } else {
            // data is retrieved from internet by underlying EventsAdapterForLists. We only have to set
            // this variable!
            allEventsInfo = allTheEvents.getAllEventsInfo();
            super.update(mYear, mMonth + 1);
            allEventsInfo.addAll(allTheEvents.getAllEventsInfo());
        }
        lastCheckedDate = 0;
        today = DateFormater.yearMonthFromCalendarToStandard(year, month) * 100 + day;
        converter = new elementIdToEventId(allEventsInfo);
    }

    @Override
    public int getCount() {
        //to get space for the date-elements (date-flags in the list)
        return converter.getDaysWithEventsCount() + allEventsInfo.size();
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
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        int color;
        int colorFrom;
        String[] singleEvent = allEventsInfo.get(converter.convertElementIdToEventId(i));
        int date = Integer.parseInt(singleEvent[7]);

        //To know when to scroll it to
        if (today > date) {
            scrollListTo = i;
        }

        //set the color using category number
        if (mColors.length != 1) {
            color = mColors[Integer.parseInt(singleEvent[6])];
            colorFrom = mColorsFrom[Integer.parseInt(singleEvent[6])];
        } else {
            color = mColors[0];
            colorFrom = mColorsFrom[0];
        }

        if (converter.isSeparator(i)) {
            //create the blue part in between events!
            return createSeparator(date);

        } else {
            GradientDrawable circle = super.createBlob(color, colorFrom);
            //trying to recycle the view can be dangerous since the view of the "separator" is different
            //then the one used here. Better create a new one. If the app is slow, a better way has
            //to be thought of.
            return super.createNewEventElement(singleEvent, circle);
        }
    }

    //create the blue view that separates the events on consecutive days
    private View createSeparator(int date) {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, ((date/100)-1) % 100);
        calendar.set(Calendar.DAY_OF_MONTH, date % 100);
        String nameOfMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
        String nameOfDay = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US);

        RelativeLayout view = (RelativeLayout) mActivity.getLayoutInflater().inflate(R.layout.events_list_separator, null);
        view.setBackgroundColor(Color.parseColor("#0F4D92"));
        TextView description = (TextView) view.getChildAt(1);
        description.setText(nameOfDay + " " + nameOfMonth + " " + DateFormater.dayToString(date % 100) + " " + Integer.toString(date / 10000));
        description.setTextColor(Color.parseColor("#FFFFFF"));
        description.setTypeface(null, Typeface.BOLD);
        description.setPadding((int) (height * 0.05), (int) (height * 0.01), 0, (int) (height * 0.01));
        view.setClickable(false);
        return view;
    }

    //get information about the event of listID i
    public String[] getInformation(int i) {
        //to know that separators are not clickable, return null and handle in onClickListener
        if (converter.isSeparator(i)) {
            return null;
        } else {
            String[] data = allEventsInfo.get(converter.convertElementIdToEventId(i));
            return data;
        }
    }

    public void setAllEventsInfo(ArrayList<String[]> events){
        allEventsInfo = events;
        converter = new elementIdToEventId(events);
    }

    //class used for convertion of listID to the ID of event in the ArrayList of all event on given month
    public class elementIdToEventId {
        private int daysWithEvents;
        // map id in the list of separators to number of separator. Separators are enumerated from 1st to nth
        public TreeMap<Integer, Integer> dayToId;

        elementIdToEventId(ArrayList<String[]> events) {
            daysWithEvents = 0;
            dayToId = new TreeMap<Integer, Integer>();
            int lastDate = 0;
            for (int i = 0; i < events.size(); i++) {
                int currentDate = Integer.parseInt(events.get(i)[7]);
                if (currentDate != lastDate) {
                    lastDate = currentDate;
                    dayToId.put(i + daysWithEvents, daysWithEvents + 1);
                    daysWithEvents++;
                }
            }
        }

        public int getDaysWithEventsCount() {
            return daysWithEvents;
        }

        public int convertElementIdToEventId(int id) {
            if (isSeparator(id)) {
                return id - dayToId.get(id) + 1;
            }
            List<Integer> intervals = new ArrayList<Integer>(dayToId.keySet());
            Collections.sort(intervals);
            for (int i = 0; i < intervals.size(); i++) {
                int bound = intervals.get(i);
                if (bound > id)
                    return id - dayToId.get(intervals.get(i - 1));
            }

            //to handle the last set
            return id - dayToId.get(intervals.get(intervals.size() - 1));
        }

        //check if the element id in list is a separator
        public boolean isSeparator(int id) {
            List<Integer> separators = new ArrayList<Integer>(dayToId.keySet());
            for (int separator : separators) {
                if (separator == id)
                    return true;
            }
            return false;
        }
    }
}
