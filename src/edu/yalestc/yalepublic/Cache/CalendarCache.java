package edu.yalestc.yalepublic.Cache;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import edu.yalestc.yalepublic.Events.EventsParseForDateWithinCategory;
import edu.yalestc.yalepublic.Videos.JSONReader;

/**
 * Created by Stan Swidwinski on 12/15/14.
 */
public class CalendarCache {
    Context mContext;
    Calendar myCalendar;
    //in the given order 0 - past, 1 - current, 2 - next, 3 - nextNext
    private JSONReader[]dataPullers;
    private EventsParseForDateWithinCategory dataParser;
    private String[] responses;

    CalendarCache(Context context){
        mContext = context;
            //TO DO: ONLY INITIALIZE AND CREATE THE DATA IF NEEDED == CHECK CACHE!!!!
        myCalendar = Calendar.getInstance();
        int month = myCalendar.get(Calendar.MONTH);
        int year = myCalendar.get(Calendar.YEAR);

        if(!needToCache(month,year))
            return;
        responses = new String[4];
            //TO DO: refactor and put all of them in arrays. Initialize as a loop.
        String[] queries = new String[4];
        for(int i = -1; i < 3; i++){
            queries[i+1] = "http://calendar.yale.edu/feeds/feed/opa/json/" + parseDateForJSON(year, month + i) +"-01"+ "/30days";
        }
        for(int i = 0; i < 4; i++){
            dataPullers[i] = new JSONReader(queries[i],context);
            dataPullers[i].execute();
        }

        String[] results = new String[4];
            //TO DO: if given is not null, execute it.
        //TO DO: threadpool to make it professional
        awaitForResults();
        for(int i = -1; i<3; i++){
            Pair<Integer, Integer> date = checkDate(year, month+i);
            dataParser = new EventsParseForDateWithinCategory(results[i+1], date.first, date.second,mContext,0);
            saveEventsToCache(date.first, date.second, dataParser);
        }
    }
        //return date in calendar format from yera and month in tentative form from -1 to 12.
    private Pair<Integer, Integer> checkDate (int year, int month){
        int myMonth = month;
        int myYear = year;
        if(myMonth == -1){
            myMonth = 11;
            year --;
        } else if(myMonth == 12){
            myMonth = 0;
            year++;
        }
        return new Pair<Integer, Integer>(month,year);
    }

        //return month in format MM
    private String parseMonth (int month){
            //calendar operates on months enumrated as 0 - 11!
        int realMonth = month + 1;
        String result ="";
        if(month < 10){
            result = "0";
        }
        result += Integer.toString(realMonth);
        return result;
    }
        //parse date into format "yyyy-mm"
    private String parseDateForJSON (int year, int month){
        int myMonth = 0;
        String result = "";
        if(month == 12){
            myMonth = 0;
            year++;
        } else if(month == -1){
            myMonth = 12;
            year--;
        }
        result = Integer.toString(year) + "-" + parseMonth(myMonth);
        return result;
    }

        //For querying EventsParseForDateWithinCategory for events on given day.
    private String parseDateForParse(int year, int month, int day){
        int myMonth = 0;
        String result = "";
        if(month == 12){
            myMonth = 0;
            year++;
        } else if(month == -1){
            myMonth = 12;
            year--;
        }
        result = Integer.toString(year) + parseMonth(myMonth) + parseDayForParse(day);
        return result;
    }

        //for usage in saveEventsToCache, querying EventsParseForDateWithinCategory
    private String parseDayForParse(int day){
        String stringDay;
        if (day < 10){
            stringDay = "0";
        } else {
            stringDay = new String();
        }
        stringDay += Integer.toString(day);
        return stringDay;
    }

        //workaround for not using threadpool
    void awaitForResults(){
        boolean[] finished = new boolean[4];
        Arrays.fill(finished, false);
        while(true) {
            try {
                wait(5000);
                try {
                    for (int i = 0; i < 4; i++) {
                        if (dataPullers[i].getStatus() == AsyncTask.Status.FINISHED) {
                            responses[i] = dataPullers[i].get();
                            finished[i] = true;
                        }
                    }
                } catch (ExecutionException e) {
                    Log.e("CalendarCache", "Cannot get the result from a puller");
                    return;
                }
            } catch (InterruptedException e) {
                Log.e("CalendarCache", "Error in awaitForResults");
                return;
            }
            if (allTrue(finished))
                return;
        }
    }
        //part deux of workaround... check if all values in array are true
    boolean allTrue(boolean[] arr){
        for(boolean val : arr){
            if(!val)
                return false;
        }
        return true;
    }

        //save events for all days to cache. Each day is saved in the following pattern:
    /*
    //title !@#$ start time !@#$ end time !@#$ place !@#$ date !@#$ description !@#$ categoryNumber
     */
    public void saveEventsToCache(int month, int year, EventsParseForDateWithinCategory parser){
        String date;
        for(int i = 1; i<32; i++){
            date = parseDateForParse(year, month, i);
            ArrayList<String[]> events = parser.getEventsOnGivenDate(date);
            if(events != null){
                try {
                    File cachedFile = File.createTempFile(date, null, mContext.getCacheDir());
                    FileOutputStream outputStream = new FileOutputStream(cachedFile);
                    for(String[] event : events){
                        for(String chunkOfDescription : event){
                            outputStream.write((chunkOfDescription + " !@#$") .getBytes());
                        }
                        outputStream.write("\n".getBytes());
                    }
                } catch (IOException e){
                    e.printStackTrace();
                    Log.e("saveEventsToCache","Error while writing file");
                }
            }
        }
    }
        //checkes if new data has to be pulled and deletes obsolete files.
    boolean needToCache(int currentMonth, int currentYear){
        File dir = mContext.getCacheDir();
        String maxDate="";
        if(dir.exists()){
            File[] files = dir.listFiles();
            for(File file : files){
                String nameYearMonth = file.getName().substring(0,6);
                int year = Integer.parseInt(nameYearMonth.substring(0,4));
                int month = Integer.parseInt(nameYearMonth.substring(5,6));
                if(!dateWithinBounds(currentMonth, currentYear, year, month)){
                    file.delete();
                }
                maxDate = nameYearMonth;
            }
        }
        if(maxDate.equals(parseDateForParse(currentYear,currentMonth+3,0).substring(0,6)))
            return false;
        else
            return true;
    }

        //TO DO: think about storing the bounds in preferences... similarly with needToCache being substrituted
    //by pairs within preferences and checking if a month has changed... this would be starter
    private boolean dateWithinBounds(int currentMonth, int currentYear, int year, int month){
        int minMonth;
        int minYear;
        int maxMonth;
        int maxYear;
        if(currentMonth + 3 > 11){
            maxYear = currentYear++;
        } else {
            maxYear = currentYear;
        }
        maxMonth = (currentMonth+3)%12;
        if(currentMonth -1 < 0){
            minYear = currentYear-1 ;
        } else {
            minYear = currentYear;
        }
        minMonth = (currentMonth-1)%12;

        if( (year <= maxYear && year >= minYear ) && (month <= maxMonth && month >= minMonth))
            return true;
        else
            return false;
    }
}