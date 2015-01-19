package edu.yalestc.yalepublic.Events;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.Date;

import edu.yalestc.yalepublic.Events.CalendarView.CalendarFragment;
import edu.yalestc.yalepublic.Events.ListView.ListFragment;
import edu.yalestc.yalepublic.JSONReader;
import edu.yalestc.yalepublic.R;


public class EventsDisplay extends Activity {
    ActionBar.Tab monthT, weekT, dayT;
    //    private Fragment dayTab;
    private Fragment monthTab;
    private Fragment listTab;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
    //for use in onCreate only. Later data pulling when the month is changed is done within the tabs fragments
    private JSONReader dataPuller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);     // Show home as a back arrow
        //actionbar.setDisplayShowHomeEnabled(true);     // Show application logo
        actionbar.setDisplayShowTitleEnabled(true);    // Show activity title/subtitle
        actionbar.setDisplayUseLogoEnabled(false);     // Use activity logo instead of activity icon
        actionbar.setTitle(getString(R.string.events));  // Set title

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_display);

        monthTab = CalendarFragment.newInstance(getIntent().getExtras());
        listTab = ListFragment.newInstance(getIntent().getExtras());

        ActionBar actionBar = getActionBar();
        monthT = actionBar.newTab().setText("Month");
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        //TODO recycle the tabs. at the moment we just recreate them
        monthT.setTabListener(new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                fragmentTransaction.replace(R.id.container, monthTab);
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                fragmentTransaction.remove(monthTab);
            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            }
        });

        actionBar.addTab(monthT);

        dayT = actionBar.newTab().setText("List");
        dayT.setTabListener(new ActionBar.TabListener(){

            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                fragmentTransaction.replace(R.id.container, listTab);
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                fragmentTransaction.remove(listTab);
            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            }
        });

    }

   /* private class DayTab extends Fragment {
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_event_categories, container, false);
            //this is done as shown here:
            //http://stackoverflow.com/questions/6858162/custom-calendar-dayview-in-android

            return view;
        }
    }

    private class WeekTab extends Fragment {
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_event_categories, container, false);
            //this is just a list that is parsed nicely. Or so I think. If not, it can be done almost tge same as dayTab.

            return view;
        }
    }*/
}