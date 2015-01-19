package edu.yalestc.yalepublic.Events.ListView;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.yalestc.yalepublic.Events.CalendarView.EventsCalendarEventList;
import edu.yalestc.yalepublic.R;

/**
 * Created by root on 1/19/15.
 */
public class ListFragment extends Fragment {

    View rootView;
    Bundle mExtras;
    Activity mActivity;
    //TO DO: Get rid of one. each fragment, calendar and list has its own. suboptimal.
    EventsCalendarEventList mAdapter;

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

        return rootView;
    }
}
