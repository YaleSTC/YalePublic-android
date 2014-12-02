package edu.yalestc.yalepublic.Events;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
        parseCategoryAddToReader();

        try {
            rawData = dataPuller.execute().get();
            //rawData is null if there are problems. We get a toast for no internet!
            if(rawData == null){
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

    private void parseCategoryAddToReader(){
        String categories = extras.getString("category");
        if (categories.split(" ").length > 1){
            dataPuller.addParams(new Pair<String,String>("category", categories.split(" ")[0]));
            dataPuller.addParams(new Pair<String, String>("category", categories.split(" ")[1]));
        } else {
            dataPuller.addParams(new Pair<String, String>("category", categories));
        }
    }

    private class DayTab extends Fragment{
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){
            View view = inflater.inflate(R.layout.fragment_event_categories, container, false);
        //this is done as shown here:
            //http://stackoverflow.com/questions/6858162/custom-calendar-dayview-in-android

            return view;
        }
    }

    private class MonthTab extends Fragment{
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){
            View view = inflater.inflate(R.layout.fragment_event_categories, container, false);
            //I figured out how this is implemented. You have a calendar view above a listview. Then you override the
            //onDateChangedListener that will return the day clicked. Than you parse the JSON rawData and display all the items that
            //are on a given day. Sounds miserable.

            //refer to
            //http://stackoverflow.com/questions/11949183/calendarview-clickable-android/11951392#comment15924112_11951392
            //http://developer.android.com/reference/android/widget/CalendarView.html
            //http://developer.android.com/reference/java/util/GregorianCalendar.html
            //http://developer.android.com/reference/android/util/MonthDisplayHelper.html


            return view;
        }
    }

    private class WeekTab extends Fragment{
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){
            View view = inflater.inflate(R.layout.fragment_event_categories, container, false);
        //this is just a list that is parsed nicely. Or so I think. If not, it can be done almost tge same as dayTab.

            return view;
        }
    }

}
