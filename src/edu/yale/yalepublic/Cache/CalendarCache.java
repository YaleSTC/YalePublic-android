package edu.yale.yalepublic.Cache;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.Calendar;

import edu.yale.yalepublic.Events.DateFormater;
import edu.yale.yalepublic.Events.EventsParseForDateWithinCategory;
import edu.yale.yalepublic.JSONReader;
import edu.yale.yalepublic.Splash;

/**
 * Created by Stan Swidwinski on 12/15/14.
 */


/*
    Few words on how this class works. First of all it extends JSONReader, so it has access to all its
protected and public classes (see the constructor -> you need to "super" the inherited class!). Hence,
We can pull data from anywhere using this thread and we do not have to use additional threads (note that
in Android you can only execute an AsyncTask (spawn a thread) from the UI Thread (here the MainActivity),
so this is almost the only possibility we have.

    This class can be read like a class that does not extend anything but for the part where I use super.setURL()
and super.getData(). Note That the JSONReader class has been changed to allow for it (the functions are abstracted
away from doInBackground())!
 */
public class CalendarCache extends JSONReader {
    protected Activity mActivity;
    Calendar myCalendar;
    SharedPreferences prefs;
    final int year;
    // calendar format
    final int month;
    final int day;
    protected ProgressDialog dialog;

    //number of months "in the future" to cache. current month is calculated within
    private static final int MONTHS_CACHED_FORWARD = 6;
    private static final int MONTHS_CACHED_BACK = 0;

    public CalendarCache(Activity activity) {
        super(activity);
        //for creating and getting preferences and tables!
        mActivity = activity;
        dialog = new ProgressDialog(mActivity);
        dialog.setCancelable(false);
        dialog.setMessage("Obsolete information detected. Please wait while the newest information is downloaded. This may take a while...");
        dialog.setTitle("Updating...");
        dialog.setIndeterminate(true);
        myCalendar = Calendar.getInstance();
        month = myCalendar.get(Calendar.MONTH);
        year = myCalendar.get(Calendar.YEAR);
        day = myCalendar.get(Calendar.DAY_OF_MONTH);
        prefs = mActivity.getSharedPreferences("events", 0);
    }

    @Override
    protected void onPreExecute() {
        dialog.show();
    }

    @Override
    protected String doInBackground(Void... voids) {
        if (prefs.contains("dateCached")) {
            updateDatabase(year, month, day);
        } else {
            createDatabaseForTheFirstTime(year, month, day);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        ((Splash)mActivity).getIcon();
    }

    //can be used both to create and updateEvents preferences.
    private void updatePreferences(int currentYear, int currentMonth, int currentDay) {
        SharedPreferences eventPreferences = mActivity.getSharedPreferences("events", 0);
        SharedPreferences.Editor createEventPreferences = eventPreferences.edit();
        int mYear = currentYear;
        int mMonth = currentMonth;
        int mDay = currentDay;
        createEventPreferences.putInt("dateCached", Integer.parseInt(DateFormater.calendarDateToEventsParseForDate(mYear, mMonth, mDay)));
        //topYeartopMonth create the upper bound of dates cached. -1 since the cached_forward adds current month to it.
        int topMonth = mMonth + MONTHS_CACHED_FORWARD - 1;
        int topYear = mYear;
        createEventPreferences.putInt("topBoundDate", Integer.parseInt(DateFormater.calendarDateToEventsParseForDate(topYear, topMonth, 1)));
        //botYearbotMonth create the lower bound of dates cached
        int botYear = mYear;
        int botMonth = mMonth + MONTHS_CACHED_BACK;
        createEventPreferences.putInt("botBoundDate", Integer.parseInt(DateFormater.calendarDateToEventsParseForDate(botYear, botMonth, 1)));
        createEventPreferences.apply();
    }

    //month and year needed to query for events on given day. can be refactored by changing the EventsParseForDateWithinCategory class.
    private void parseAndSaveToDb(String rawData, int year, int month) {
        CalendarDatabaseTableHandler eventTable = new CalendarDatabaseTableHandler(mActivity);
        //the 0 at the end means that we disregard the categories and just get all of them.
        //-1 because we need calendar format and we provide real format
        EventsParseForDateWithinCategory parser = new EventsParseForDateWithinCategory(rawData, month - 1, year, mActivity, 0);
        //brute force all days
        for (int i = 1; i < 32; i++) {
            //calendarDateToEventsParseForDate takes in calendar format so need to decrement the month
            String date = DateFormater.calendarDateToEventsParseForDate(year, month - 1, i);
            Log.i("Cache", "date " + date);
            ArrayList<String[]> validEvents = parser.getEventsOnGivenDate(date);
            if (validEvents.size() > 0) {
                for (String[] event : validEvents) {
                    eventTable.addEvent(event);
                }
            }
        }
    }

    //just as name says. Used when updating db
    public void wipeDatabase(){
        CalendarDatabaseTableHandler eventTable = new CalendarDatabaseTableHandler(mActivity);
        eventTable.wipeDatabase();
    }

    //just as name says. Used when updating db
    public void clearPreferences(){
        SharedPreferences prefs = mActivity.getSharedPreferences("events",0);
        SharedPreferences.Editor wiper = prefs.edit();
        wiper.clear();
        wiper.commit();
    }

    //very simple method of updating cache that we will be using instead of the former, more complex one
    private void updateDatabase(int year, int month, int day){
        int dateCached = prefs.getInt("dateCached", 0);
        int dateToday = year * 10000 + month * 100 + day;
        if(dateToday - dateCached > 8) {
            wipeDatabase();
            clearPreferences();
            createDatabaseForTheFirstTime(year, month, day);
        }
    }

        //We have decided to just wipe the whole database. Hence, this function will not be used! I will
    //keep it around for reference if we need to do something smarter in the future!
//    private void updateDatabase(int year, int month) {
//        CalendarDatabaseTableHandler eventTable = new CalendarDatabaseTableHandler(mActivity);
//        deleteObsolete(year, month, eventTable);
//        for (int i = 0; i < MONTHS_CACHED_BACK + MONTHS_CACHED_FORWARD; i++) {
//            int before = (DateFormater.yearMonthFromCalendarToStandard(year, month + i + 1 - MONTHS_CACHED_BACK)) * 100;
//            int after = (DateFormater.yearMonthFromCalendarToStandard(year, month + i - (MONTHS_CACHED_BACK))) * 100;
//            Log.i("CalendarCache", "Checking if events between " + Integer.toString(after) + " and " + Integer.toString(before) + " are present");
//            if (eventTable.getEventsBeforeAndAfter(after, before).size() == 0) {
//                int yearMonth = DateFormater.yearMonthFromCalendarToStandard(year, month + i - 1);
//                String query = "http://calendar.yale.edu/feeds/feed/opa/json/" + DateFormater.calendarDateToJSONQuery(year, month + i - (1 + MONTHS_CACHED_BACK)) + "/30days";
//                super.setURL(query);
//                String result = super.getData();
//                Log.i("Cache", "contents of result[" + Integer.toString(i) + "]:" + result.substring(0, 100));
//                if (result == null) {
//                    Toast toast = new Toast(mActivity);
//                    toast = Toast.makeText(mActivity, "You need internet connection to successfully finish", Toast.LENGTH_LONG);
//                    toast.show();
//                    return;
//                }
//                parseAndSaveToDb(result, (int) (yearMonth / 100), yearMonth % 100);
//            }
//        }
//        //create the preferences
//        updatePreferences(year, month);
//    }
//
//    private void deleteObsolete(int year, int month, CalendarDatabaseTableHandler eventTable) {
//        //YYYYMM00
//        int lowerbound = DateFormater.yearMonthFromCalendarToStandard(year, month - MONTHS_CACHED_BACK) * 100;
//        //YYYYMM33
//        int upperbound = DateFormater.yearMonthFromCalendarToStandard(year, month + MONTHS_CACHED_FORWARD) + 33;
//        eventTable.deleteEvents(lowerbound, upperbound);
//    }

    private void createDatabaseForTheFirstTime(int year, int month, int day) {
        String[] queries;
        String[] results;
        //for later parsing purposes. EventsParseForDateWithinCategory has to discard nonsense events
        //and for that needs year and month (YYYYMM)in real format
        int[] yearMonth;
        //prepare the queries and JSONReaders for -1 month up to +2 months and get info
        queries = new String[MONTHS_CACHED_BACK + MONTHS_CACHED_FORWARD];
        yearMonth = new int[MONTHS_CACHED_BACK + MONTHS_CACHED_FORWARD];
        results = new String[MONTHS_CACHED_BACK + MONTHS_CACHED_FORWARD];
        for (int i = 0; i < MONTHS_CACHED_BACK + MONTHS_CACHED_FORWARD; i++) {
            yearMonth[i] = DateFormater.yearMonthFromCalendarToStandard(year, month + i - MONTHS_CACHED_BACK);
            queries[i] = "http://calendar.yale.edu/feeds/feed/opa/json/" + DateFormater.calendarDateToJSONQuery(year, month + i - MONTHS_CACHED_BACK) + "/30days";
            super.setURL(queries[i]);
            results[i] = super.getData();

            //Log.i("Cache", "contents of result[" + Integer.toString(i) + "]:" + results[i].substring(0, 100));
            Log.i("CacheUpdater", "Created puller with query" + queries[i]);
            Log.i("CacheUpdater", "yearMonth " + yearMonth[i]);
            if (results[i] == null) {
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast =
                                Toast.makeText(mActivity, "Downloading newest data failed. No internet connection.", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
                return;
            }
        }

        for (int i = 0; i < MONTHS_CACHED_BACK + MONTHS_CACHED_FORWARD; i++) {
            parseAndSaveToDb(results[i], (int) (yearMonth[i] / 100), yearMonth[i] % 100);
        }
        //create the preferences
        updatePreferences(year, month, day);
    }

    public static boolean isCached(Activity mActivity, int month, int year){
            //YYYYMM01 format
            int eventsParseFormat = Integer.parseInt(DateFormater.calendarDateToEventsParseForDate(year, month - 1, 1));
            Log.i("CalendarCache", "Checking if date " + Integer.toString(eventsParseFormat) + " is cached");
            //same format as above. See CalendarCache
            SharedPreferences eventPreferences = mActivity.getSharedPreferences("events", 0);
            int lowerBoundDate = eventPreferences.getInt("botBoundDate", 0);
            int topBoundDate = eventPreferences.getInt("topBoundDate", 0);
            boolean result = DateFormater.inInterval(lowerBoundDate, topBoundDate, eventsParseFormat);
            Log.i("CalendarCache", Boolean.toString(result));
            return result;
    }
}