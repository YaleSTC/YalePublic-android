package edu.yalestc.yalepublic.Cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import edu.yalestc.yalepublic.Events.EventsParseForDateWithinCategory;
import edu.yalestc.yalepublic.Events.dateFormater;
import edu.yalestc.yalepublic.Videos.JSONReader;

/**
 * Created by Stan Swidwinski on 12/15/14.
 */
public class CalendarCache{
    Context mContext;
    Calendar myCalendar;
    SharedPreferences prefs;
    private EventsParseForDateWithinCategory dataParser;
    private String[] responses;

    CalendarCache(Context context){
            //for creating and getting preferences and tables!
        mContext = context;
        myCalendar = Calendar.getInstance();
        int month = myCalendar.get(Calendar.MONTH);
        int year = myCalendar.get(Calendar.YEAR);
        prefs = mContext.getSharedPreferences("events",0);
        if(prefs.contains("dateCached")){
            //check if the cached data is up-to-date and update if it is not.
        } else {
           createDatabaseForTheFirstTime(year, month);
        }
    }

        //can be used both to create and update preferences.
    private void updatePreferences(int currentYear, int currentMonth){
        SharedPreferences eventPreferences = mContext.getSharedPreferences("events",0);
        SharedPreferences.Editor createEventPreferences = eventPreferences.edit();
        int mYear = currentYear;
        int mMonth = currentMonth;
        int mDay = 1;
        createEventPreferences.putString("dateCached", dateFormater.formatDateForEventsParseForDate(mYear, mMonth, mDay));
        int topMonth = mMonth + 3;
        int topYear = mYear;
        if(topMonth > 11){
            topMonth = topMonth%12;
            topYear++;
        }
        createEventPreferences.putString("topBoundDate", dateFormater.formatDateForEventsParseForDate(topYear,topMonth,mDay));
        int botYear = mYear;
        int botMonth = mMonth - 1;
        if(topMonth < 0){
            botMonth = topMonth % 12;
            botYear --;
        }
        createEventPreferences.putString("botBoundDate", dateFormater.formatDateForEventsParseForDate(botYear, botMonth, mDay));
        createEventPreferences.apply();
    }

        //month and year needed to query for events on given day. can be refactored by changing the EventsParseForDateWithinCategory class.
    private void parseAndSaveToDb (String rawData, int month, int year){
        CalendarDatabaseTableHandler eventTable = new CalendarDatabaseTableHandler(mContext);
            //the 0 at the end means that we disregard the categories and just get all of them.
        EventsParseForDateWithinCategory parser = new EventsParseForDateWithinCategory(rawData, month, year, mContext, 0);
            //brute force all days
        for(int i = 1; i < 32; i++){
            String date = Integer.toString(year) + dateFormater.formatMonthFromCalendarFormat(month) + dateFormater.formatDayFromCalendarFormat(i);
            ArrayList<String[]> validEvents = parser.getEventsOnGivenDate(date);
            if(validEvents.size() > 0){
                for(String[] event : validEvents){
                    eventTable.addEvent(event);
                }
            }
        }
    }

    private void createDatabaseForTheFirstTime(int year, int month){
        String[] queries;
        String[] results;
        //for later parsing purposes. EventsParseForDateWithinCategory has to discard nonsense events
        //and for that needs year and month (YYYYMM)
        int[] yearMonth;
        JSONReader[] dataPullers = new JSONReader[4];
        //create the preferences
        updatePreferences(year, month);
        //prepare the queries and JSONReaders for -1 month up to +2 months
        queries = new String[4];
        yearMonth = new int[4];
        for (int i = 0; i < 4; i++){
            yearMonth[i] = dateFormater.toYearMonth(year, month + i -1);
            queries[i] = "http://calendar.yale.edu/feeds/feed/opa/json/" + dateFormater.formatDateForJSONQuery(year, month + i - 1) + "/30days";
            dataPullers[i] = new JSONReader(queries[i], mContext);
            Log.i("CalendarCache","Created puller with query" + queries[i]);
        }
        //try to execute and collect the results
        results = new String[4];

        try {
            for(int i = 0; i < 4; i++){
                results[i] = dataPullers[i].execute().get();
                if (results[i] == null) {
                    Toast toast = new Toast(mContext);
                    toast = Toast.makeText(mContext, "You need internet connection to succesfully finish", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < 4; i++)
            parseAndSaveToDb(results[i], (int) (yearMonth[i] / 100), yearMonth[i] % 100);
    }
}