package edu.yalestc.yalepublic.Cache;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import edu.yalestc.yalepublic.Events.EventsParseForDateWithinCategory;
import edu.yalestc.yalepublic.Events.dateFormater;
import edu.yalestc.yalepublic.JSONReader;

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
    Activity mActivity;
    Calendar myCalendar;
    SharedPreferences prefs;
    final int year;
    final int month;
    private ProgressDialog dialog;

    public CalendarCache(Activity activity){
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
        prefs = mActivity.getSharedPreferences("events",0);
    }

    @Override
    protected void onPreExecute(){
        dialog.show();
    }

    @Override
    protected String doInBackground(Void... voids) {
        if(prefs.contains("dateCached")){
            updateDatabase(year, month);
        } else {
            createDatabaseForTheFirstTime(year, month);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result){
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

        //can be used both to create and update preferences.
    private void updatePreferences(int currentYear, int currentMonth){
        SharedPreferences eventPreferences = mActivity.getSharedPreferences("events",0);
        SharedPreferences.Editor createEventPreferences = eventPreferences.edit();
        int mYear = currentYear;
        int mMonth = currentMonth;
        int mDay = 1;
        createEventPreferences.putInt("dateCached", Integer.parseInt(dateFormater.formatDateForEventsParseForDate(mYear, mMonth, mDay)));
        int topMonth = mMonth + 2;
        int topYear = mYear;
        createEventPreferences.putInt("topBoundDate", Integer.parseInt(dateFormater.formatDateForEventsParseForDate(topYear,topMonth,mDay)));
        int botYear = mYear;
        int botMonth = mMonth - 1;
        createEventPreferences.putInt("botBoundDate", Integer.parseInt(dateFormater.formatDateForEventsParseForDate(botYear, botMonth, mDay)));
        createEventPreferences.apply();
    }

        //month and year needed to query for events on given day. can be refactored by changing the EventsParseForDateWithinCategory class.
    private void parseAndSaveToDb (String rawData, int year, int month){
        CalendarDatabaseTableHandler eventTable = new CalendarDatabaseTableHandler(mActivity);
            //the 0 at the end means that we disregard the categories and just get all of them.
        //-1 because we need calendar format and we provide real format
        EventsParseForDateWithinCategory parser = new EventsParseForDateWithinCategory(rawData, month - 1, year, mActivity, 0);
            //brute force all days
        for(int i = 1; i < 32; i++){
            //formatDateForEventsParseForDate takes in calendar format so need to decrement the month
            String date = dateFormater.formatDateForEventsParseForDate(year, month - 1, i);
            Log.i("Cache", "date " + date);
            ArrayList<String[]> validEvents = parser.getEventsOnGivenDate(date);
            if(validEvents.size() > 0){
                for(String[] event : validEvents){
                    eventTable.addEvent(event);
                }
            }
        }
    }

    //NOT TESTED. PLEASE TEST AFTER DEMO SINCE THIS IS NOT TOO IMPORTANT.
    //maybe instead of doing that just delete the whole database and redownload it? will be slower
    //but will have all events, even those added at the last moment
    //Best way out: have Yale Calendar return JSON with a field being "last updated: YYYYMMDD...
    private void updateDatabase(int year, int month){
        CalendarDatabaseTableHandler eventTable = new CalendarDatabaseTableHandler(mActivity);
        deleteObsolete(year, month, eventTable);
        for(int i = 0; i<4; i++){
            //-1 is effect of checking months from -1 in past to +2 in future
            int before = (dateFormater.toYearMonthCalendToReal(year, month + i))*100;
            int after = (dateFormater.toYearMonthCalendToReal(year, month + i - 1))*100;
            Log.i("CalendarCache","Checking if events between " + Integer.toString(after) + " and " + Integer.toString(before) + " are present");
            if(eventTable.getEventsBeforeAndAfter(after, before).size() == 0){
                int yearMonth = dateFormater.toYearMonthCalendToReal(year, month + i - 1);
                String query = "http://calendar.yale.edu/feeds/feed/opa/json/" + dateFormater.formatDateForJSONQuery(year, month + i - 1) + "/30days";
                super.setURL(query);
                String result = super.getData();
                Log.i("Cache", "contents of result[" + Integer.toString(i) + "]:" + result.substring(0, 100));
                if (result == null) {
                    Toast toast = new Toast(mActivity);
                    toast = Toast.makeText(mActivity, "You need internet connection to successfully finish", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                parseAndSaveToDb(result, (int) (yearMonth / 100), yearMonth % 100);
            }
        }
        //create the preferences
        updatePreferences(year, month);
    }

    private void deleteObsolete(int year, int month, CalendarDatabaseTableHandler eventTable){
        //YYYYMM00
        int lowerbound = dateFormater.toYearMonthCalendToReal(year, month + 3)*100;
        //YYYYMM33
        int upperbound = dateFormater.toYearMonthCalendToReal(year, month - 1)+33;
        eventTable.deleteEvents(lowerbound, upperbound);
    }

    private void createDatabaseForTheFirstTime(int year, int month){
        String[] queries;
        String[] results;
        //for later parsing purposes. EventsParseForDateWithinCategory has to discard nonsense events
        //and for that needs year and month (YYYYMM)in real format
        int[] yearMonth;
        //prepare the queries and JSONReaders for -1 month up to +2 months and get info
        queries = new String[4];
        yearMonth = new int[4];
        results = new String[4];
        for (int i = 0; i < 4; i++){
            yearMonth[i] = dateFormater.toYearMonthCalendToReal(year, month + i - 1);
            queries[i] = "http://calendar.yale.edu/feeds/feed/opa/json/" + dateFormater.formatDateForJSONQuery(year, month + i - 1) + "/30days";
            super.setURL(queries[i]);
            results[i] = super.getData();
            //Log.i("Cache", "contents of result[" + Integer.toString(i) + "]:" + results[i].substring(0, 100));
            Log.i("CacheUpdater","Created puller with query" + queries[i]);
             Log.i("CacheUpdater", "yearMonth " + yearMonth[i]);
            if (results[i] == null) {
                mActivity.runOnUiThread(new Runnable() {
                    public void run(){
                        Toast toast = new Toast(mActivity);
                        toast = Toast.makeText(mActivity, "Downloading newest data failed. No internet connection.", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
                return;
            }
        }

        for(int i = 0; i < 4; i++) {
            parseAndSaveToDb(results[i], (int) (yearMonth[i] / 100), yearMonth[i] % 100);
        }
        //create the preferences
        updatePreferences(year, month);
    }
}