package edu.yalestc.yalepublic.Events.ListView;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import edu.yalestc.yalepublic.Events.DateFormater;
import edu.yalestc.yalepublic.Events.EventsAdapterForLists;
import edu.yalestc.yalepublic.Events.EventsJSONReader;

/**
 * Created by Stan Swidwinski on 1/19/15.
 */
public class EventsListAdapter extends EventsAdapterForLists {

public EventsListAdapter(Activity activity, int year, int month, int category, int[] colors, int colorsFrom[]){
    super(activity, year, month, category, colors, colorsFrom);
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

}
