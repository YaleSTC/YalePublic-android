package edu.yalestc.yalepublic.Events.ListView;

import android.app.Activity;

import edu.yalestc.yalepublic.Events.CalendarView.EventsCalendarEventList;

/**
 * Created by root on 1/19/15.
 */
public class EventsListAdapter extends EventsCalendarEventList {

    public EventsListAdapter(Activity activity, int year, int month, int selectedDayOfMonth, int category, int[] colors, int[] colorsFrom) {
        super(activity, year, month, selectedDayOfMonth, category, colors, colorsFrom);
    }
}
