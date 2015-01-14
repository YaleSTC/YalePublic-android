package edu.yalestc.yalepublic.Events;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import edu.yalestc.yalepublic.JSONReader;
import edu.yalestc.yalepublic.R;

//info on making tabs:

//http://www.linux.com/learn/tutorials/761642-android-app-development-for-beginners-navigation-with-tabs
//really nice.
public class EventsDisplay extends Activity {
    ActionBar.Tab monthT, weekT, dayT;
    private String rawData;
    private Calendar mCalendar;
    private Fragment dayTab;
    private Fragment monthTab;
    private Fragment weekTab;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
    String currentDate = dateFormat.format(new Date());
        //for use in onCreate only. Later data pulling when the month is changed is done within the tabs fragments
    private JSONReader dataPuller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_display);
        mCalendar = Calendar.getInstance();

        if(!isCached()){
            // if caching has failed, pull the data from internet
            pullNewDataFromInternet();
        } else {
            rawData = null;
        }

        monthTab = CalendarFragment.newInstance(getIntent().getExtras(), rawData);

        ActionBar actionBar = getActionBar();
        monthT = actionBar.newTab().setText("Month");
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

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

    }

    private void pullNewDataFromInternet(){
        dataPuller = new JSONReader("http://calendar.yale.edu/feeds/feed/opa/json/" + currentDate +"-01"+ "/30days", this);

        try {
            rawData = dataPuller.execute().get();
            //Log.d("rawData", rawData.toString());
            //rawData is null if there are problems. We get a toast for no internet!
            if (rawData == null) {
                Toast toast = new Toast(this);
                toast = Toast.makeText(this, "You need internet connection to view the content!", Toast.LENGTH_LONG);
                toast.show();
                finish();
                return;
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void getDataFromDb(){

    }

    private boolean isCached(){
        Calendar mCalendar = Calendar.getInstance();
        int month = mCalendar.get(Calendar.MONTH);
        int year = mCalendar.get(Calendar.YEAR);
            //YYYYMM01 format
        int eventsParseFormat = Integer.parseInt(dateFormater.formatDateForEventsParseForDate(year, month, 1));
            //same format as above. See CalendarCache
        SharedPreferences eventPreferences = this.getSharedPreferences("events", 0);
        int lowerBoundDate = eventPreferences.getInt("botBoundDate", 0);
        int topBoundDate = eventPreferences.getInt("topBoundDate", 0);
        return dateFormater.inInterval(lowerBoundDate, topBoundDate, eventsParseFormat);
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