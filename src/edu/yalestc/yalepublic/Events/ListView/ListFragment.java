package edu.yalestc.yalepublic.Events.ListView;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.Calendar;

import edu.yalestc.yalepublic.Events.CalendarView.EventsCalendarEventList;
import edu.yalestc.yalepublic.R;

/**
 * Created by Stan Swidwinski on 1/19/15.
 */
public class ListFragment extends Fragment {

    View rootView;
    Bundle mExtras;
    Activity mActivity;
    Calendar c = Calendar.getInstance();
    int month = c.get(Calendar.MONTH);
    int year = c.get(Calendar.YEAR);
    int day = c.get(Calendar.DAY_OF_MONTH);
    EventsListAdapter mAdapter;

    public static ListFragment newInstance(Bundle extras){
        ListFragment f = new ListFragment();
        Bundle bdl = extras;
        f.setArguments(bdl);
        return f;
    }

    public ListFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        rootView = inflater.inflate(R.layout.activity_events_list, container, false);
        mAdapter = new EventsListAdapter(mActivity, year, month, day, mExtras.getInt("numberOfCategorySearchedFor"), mExtras.getIntArray("colors"), mExtras.getIntArray("colorsFrom"));
        ((ListView)((RelativeLayout) rootView).getChildAt(0)).setAdapter(mAdapter);
        return rootView;
    }
}
