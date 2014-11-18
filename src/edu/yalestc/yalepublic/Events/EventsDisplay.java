package edu.yalestc.yalepublic.Events;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import edu.yalestc.yalepublic.JSONReader;
import edu.yalestc.yalepublic.R;

import static edu.yalestc.yalepublic.R.drawable.*;

//info on making tabs:

//http://www.linux.com/learn/tutorials/761642-android-app-development-for-beginners-navigation-with-tabs
//really nice.
public class EventsDisplay extends Activity {
    ActionBar.Tab monthT, weekT, dayT;
    private String rawData;
    private Fragment dayTab;
    private Fragment monthTab;
    private Fragment weekTab;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String currentDate = dateFormat.format(new Date());
    private JSONReader dataPuller;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extras = getIntent().getExtras();
        setContentView(R.layout.activity_events_display);
        dataPuller = new JSONReader("http://calendar.yale.edu/feeds/feed/opa/json/" + currentDate + "/30days", this);
        parseCategoryAddToReader(dataPuller);

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
        monthTab = new MonthTab();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.events_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void parseCategoryAddToReader(JSONReader reader) {
        String categories = extras.getString("category");
        if (categories.split(" ").length > 1) {
            reader.addParams(new Pair<String, String>("category", categories.split(" ")[0]));
            reader.addParams(new Pair<String, String>("category", categories.split(" ")[1]));
        } else {
             if(categories.equals("All"))
                 return;
            else
                reader.addParams(new Pair<String, String>("category", categories));
        }
    }

    private class DayTab extends Fragment {
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_event_categories, container, false);
            //this is done as shown here:
            //http://stackoverflow.com/questions/6858162/custom-calendar-dayview-in-android


            return view;
        }
    }

    private class MonthTab extends Fragment {

        View rootView;
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        int day = c.get(Calendar.DAY_OF_MONTH);
        String monthName = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        int daysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        EventsCalendarGridAdapter calendarAdapter;
        EventsCalendarEventList listEvents;

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.events_calendar, container, false);
            //set the month name up there
            ((TextView) ((RelativeLayout) (((RelativeLayout) rootView).getChildAt(0))).getChildAt(1)).setText(monthName);

            //set the onclick listener of arrow changing month to the previous one
            ((Button) ((RelativeLayout) (((RelativeLayout) rootView).getChildAt(0))).getChildAt(0)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateMonthAndData(-1);
                }
            });

            //set onclick listener to arrow changing the month to the next one
            ((Button) ((RelativeLayout) (((RelativeLayout) rootView).getChildAt(0))).getChildAt(2)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateMonthAndData(1);
                }
            });

            calendarAdapter = new EventsCalendarGridAdapter(getActivity());
            calendarAdapter.update(year, month);

            listEvents = new EventsCalendarEventList(getActivity(), new EventsParseForDateWithinCategory(rawData, month, year), year, month, calendarAdapter.getCurrentlySelected(), extras.getInt("color"));
            ((ListView) ((RelativeLayout) rootView).getChildAt(3)).setAdapter(listEvents);

            ((GridView) (((RelativeLayout) rootView).getChildAt(2))).setAdapter(calendarAdapter);

            ((GridView) (((RelativeLayout) rootView).getChildAt(2))).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //you need to change the colors in a more smart way. have a functino in adapter to do it for you.
                    if (calendarAdapter.isToday(-1)) {
                        ((ImageView) ((RelativeLayout) ((GridView) (((RelativeLayout) rootView).getChildAt(2))).getChildAt(calendarAdapter.getCurrentlySelected())).getChildAt(0)).setImageDrawable(getResources().getDrawable(calendar_grid_button_current_unselected));
                        ((TextView) ((RelativeLayout) ((GridView) (((RelativeLayout) rootView).getChildAt(2))).getChildAt(calendarAdapter.getCurrentlySelected())).getChildAt(1)).setTextColor(Color.parseColor("#3d4b5a"));
                    } else {
                        ((ImageView) ((RelativeLayout) ((GridView) (((RelativeLayout) rootView).getChildAt(2))).getChildAt(calendarAdapter.getCurrentlySelected())).getChildAt(0)).setImageDrawable(getResources().getDrawable(calendar_grid_button_unselected));
                        ((TextView) ((RelativeLayout) ((GridView) (((RelativeLayout) rootView).getChildAt(2))).getChildAt(calendarAdapter.getCurrentlySelected())).getChildAt(1)).setTextColor(Color.parseColor("#3d4b5a"));
                    }
                    calendarAdapter.setCurrentlySelected(i);
                    listEvents.setmSelectedDayOfMonth(calendarAdapter.getDayNumber(i));
                    listEvents.notifyDataSetChanged();
                    if (calendarAdapter.isToday(-1)) {
                        ((ImageView) ((RelativeLayout) ((GridView) (((RelativeLayout) rootView).getChildAt(2))).getChildAt(calendarAdapter.getCurrentlySelected())).getChildAt(0)).setImageDrawable(getResources().getDrawable(calendar_grid_button_current_selected));
                        ((TextView) ((RelativeLayout) ((GridView) (((RelativeLayout) rootView).getChildAt(2))).getChildAt(calendarAdapter.getCurrentlySelected())).getChildAt(1)).setTextColor((Color.parseColor("#FFFFFF")));
                    } else {
                        ((ImageView) ((RelativeLayout) ((GridView) (((RelativeLayout) rootView).getChildAt(2))).getChildAt(calendarAdapter.getCurrentlySelected())).getChildAt(0)).setImageDrawable(getResources().getDrawable(calendar_grid_button_selected));
                        ((TextView) ((RelativeLayout) ((GridView) (((RelativeLayout) rootView).getChildAt(2))).getChildAt(calendarAdapter.getCurrentlySelected())).getChildAt(1)).setTextColor(Color.parseColor("#FFFFFF"));
                    }
                    Log.v("Item", "Clicked on calendar!");
                }

            });


            //I figured out how this is implemented. You have a calendar view above a listview. Then you override the
            //onDateChangedListener that will return the day clicked. Than you parse the JSON rawData and display all the items that
            //are on a given day. Sounds miserable.

            //refer to
            //http://stackoverflow.com/questions/11949183/calendarview-clickable-android/11951392#comment15924112_11951392
            //http://developer.android.com/reference/android/widget/CalendarView.html
            //http://developer.android.com/reference/java/util/GregorianCalendar.html
            //http://developer.android.com/reference/android/util/MonthDisplayHelper.html
            return rootView;
        }

        //changes the month and updates related fields
        void updateMonthAndData(int i) {
            c.getFirstDayOfWeek();
            month = month + i;
            if (month < 0) {
                year--;
                month = 11;
            } else if (month > 11) {
                year++;
                month = 0;
            }
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            monthName = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
            daysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
            dayOfWeek = c.get(Calendar.DAY_OF_WEEK_IN_MONTH);
            calendarAdapter.update(year, month);
            calendarAdapter.notifyDataSetChanged();
                //pull new data for a given month!!
            //Log.v("updateMonthAndData", monthNumberToString());
            Log.v("updateMonthAndData/query","http://calendar.yale.edu/feeds/feed/opa/json/" + Integer.toString(year) + "-" + monthNumberToString() + "-01"  + "/30days" );
            JSONReader newData = new JSONReader("http://calendar.yale.edu/feeds/feed/opa/json/" + Integer.toString(year) + "-" + monthNumberToString() + "-01"  + "/30days", getActivity());
            parseCategoryAddToReader(newData);
            try {
                rawData = newData.execute().get();
                //Log.d("rawData", rawData.toString());
                //rawData is null if there are problems. We get a toast for no internet!
                if (rawData == null) {
                    Toast toast = new Toast(getActivity());
                    toast = Toast.makeText(getActivity(), "You need internet connection to view the content!", Toast.LENGTH_LONG);
                    toast.show();
                    finish();
                    return;
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            //Log.v("updateMonthAndData", rawData);
            listEvents.update(rawData, month, year);
            ((TextView) ((RelativeLayout) (((RelativeLayout) rootView).getChildAt(0))).getChildAt(1)).setText(monthName);
        }

            //+1 because of the way calendar treats months (0-11)
        String monthNumberToString() {
            String stringMonth;
            if (month + 1 < 10) {
                stringMonth = "0";
            } else {
                stringMonth = new String();
            }
            stringMonth += Integer.toString(month + 1);
            return stringMonth;
        }
    }


    private class WeekTab extends Fragment {
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_event_categories, container, false);
            //this is just a list that is parsed nicely. Or so I think. If not, it can be done almost tge same as dayTab.

            return view;
        }
    }
}


