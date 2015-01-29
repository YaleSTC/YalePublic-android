package edu.yalestc.yalepublic.Cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import edu.yalestc.yalepublic.Events.EventsParseForDateWithinCategory;

/**
 * Created by Stan Swidwinski on 1/3/15.
 *
 * Used to handle the database. Includes all the queries etc.
 */

public class CalendarDatabaseTableHandler extends SQLiteOpenHelper {

    private static final int TABLE_VERSION = 1;
    private static final String TABLE_NAME_EVENTS = "events";
    String TABLE_CREATE_EVENTS =
            "CREATE TABLE " + TABLE_NAME_EVENTS + " (" +
                    "Title" + " TEXT, " +
                    "StartTime" + " TEXT, " +
                    "EndTime" + " TEXT, " +
                    "Location" + " TEXT, " +
                    "DateDescription" + " TEXT, " +
                    "Description" + " TEXT, " +
                    "Category" + " TEXT, " +
                    "NumericalDate" + " INTEGER);";

    public CalendarDatabaseTableHandler(Context context) {
        super(context, TABLE_NAME_EVENTS, null, TABLE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_EVENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
    }

    //IMPORTANT NOTE: Category is a string representing the numbers of categories as enumerated
    //in resources. The numbers are separated by ",". For example: ",1,10,2,4,". The category will be
    //queried using " like '%,N,%' " where N is a number. To retrieve such encoded category when
    // searching using 0, use the EventsParseForDateWithinCategory.retrieveCategory(String cat);
    public void addEvent(String[] eventInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Title", eventInfo[0]);
        values.put("StartTime", eventInfo[1]);
        values.put("EndTime", eventInfo[2]);
        values.put("Location", eventInfo[3]);
        values.put("DateDescription", eventInfo[4]);
        values.put("Description", eventInfo[5]);
        values.put("Category", eventInfo[6]);
        //for easier implementation of deleteEvents
        int date = Integer.parseInt((eventInfo[7]));
        values.put("NumericalDate", date);
        Log.i("DATABASE", "Event " + eventInfo[0] + " added on " + eventInfo[7]);
        db.insert(TABLE_NAME_EVENTS, null, values);
    }
//
//    public ArrayList<String[]> getEventsOn(String date) {
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        String query = "select * from " + TABLE_NAME_EVENTS
//                + "where Date in ('Date'," + date + ");";
//
//        Cursor cursor = db.rawQuery(query, null);
//        ArrayList<String[]> result = new ArrayList<String[]>();
//        if (cursor.moveToFirst()) {
//            do {
//                String[] event = new String[8];
//                for (int i = 0; i < 8; i++)
//                    event[i] = cursor.getString(i);
//                result.add(event);
//            } while (cursor.moveToNext());
//        }
//        Log.i("Database", "Queried for : " + query);
//        Log.i("Database", "Returned " + Integer.toString(result.size()) + " elements");
//        db.close();
//        return result;
//    }

    public ArrayList<String[]> getEventsBeforeAndAfter(int dateAfter, int dateBefore) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "select * from " + TABLE_NAME_EVENTS
                + " where NumericalDate > " + Integer.toString(dateAfter)
                + " AND NumericalDate < " + Integer.toString(dateBefore) + ";";

        Cursor cursor = db.rawQuery(query, null);

        ArrayList<String[]> result = new ArrayList<String[]>();
        if (cursor.moveToFirst()) {
            do {
                String[] event = new String[8];
                //fill in six first fields
                for (int i = 0; i < 6; i++)
                    event[i] = cursor.getString(i);
                // extract the category from string (seventh field)
                event[6] = String.valueOf(EventsParseForDateWithinCategory.retrieveCategory(cursor.getString(6)));
                // add the last field (numerical date)
                event[7] = cursor.getString(7);
                result.add(event);
            } while (cursor.moveToNext());
        }
        Log.i("Database", "Queried for : " + query);
        Log.i("Database", "Returned " + Integer.toString(result.size()) + " elements");
        db.close();
        return result;
    }

    public ArrayList<String[]> getEventsOnDateWithinCategory(String date, int category) {
        String query;
        //to look within a given category, we will use wildcards. every number begins with
        //, and is finished with ,
        if (category != 0) {
            query = "select * from " + TABLE_NAME_EVENTS
                    + " where Category like \'%," + Integer.toString(category) + ",%\'"
                    + " AND NumericalDate='" + date + "';";
        } else {
            query = "select * from " + TABLE_NAME_EVENTS
                    + " where NumericalDate= '" + date + "';";
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<String[]> result = new ArrayList<String[]>();
        if (cursor.moveToFirst()) {
            do {
                String[] event = new String[8];
                //fill in six first fields
                for (int i = 0; i < 6; i++)
                    event[i] = cursor.getString(i);
                // since the category has to be extracted from a string, we do that for category == 0
                // which means that we want all the categories. IF we are querying for specific value,
                // we can just put that value as category, since we only get events from that category
                // from query
                if (category == 0) {
                    event[6] = String.valueOf(EventsParseForDateWithinCategory.retrieveCategory(cursor.getString(6)));
                } else {
                    event[6] = Integer.toString(category);
                }
                // add the numerical date
                event[7] = cursor.getString(7);
                result.add(event);
            } while (cursor.moveToNext());
        }
        Log.i("Database", "Queried for : " + query);
        Log.i("Database", "Returned " + Integer.toString(result.size()) + " elements");
        db.close();
        return result;
    }

    public ArrayList<String[]> getEventsInMonthWithinCategory(int date, int category){
        String query;
        if (category != 0) {
            query = "select * from " + TABLE_NAME_EVENTS
                    + " where Category like '%," + Integer.toString(category) + ",%\'"
                    + " AND ( NumericalDate > " + Integer.toString(date)
                    + " AND NumericalDate < " + Integer.toString(date + 99) + ") ;";
        } else {
            query = "select * from " + TABLE_NAME_EVENTS
                    + " where NumericalDate > " + Integer.toString(date)
                    + " AND NumericalDate < " + Integer.toString(date + 99) + ";";
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<String[]> result = new ArrayList<String[]>();
        if (cursor.moveToFirst()) {
            do {
                String[] event = new String[8];
                // fill in six first fields
                for (int i = 0; i < 6; i++)
                    event[i] = cursor.getString(i);
                // since the category has to be extracted from a string, we do that for category == 0
                // which means that we want all the categories. IF we are querying for specific value,
                // we can just put that value as category, since we only get events from that category
                // from query
                String[] categories = cursor.getString(6).split(",");
                if (category == 0) {
                    event[6] = String.valueOf(EventsParseForDateWithinCategory.retrieveCategory(cursor.getString(6)));
                } else {
                    event[6] = Integer.toString(category);
                }
                // add the numerical date
                event[7] = cursor.getString(7);
                result.add(event);
            } while (cursor.moveToNext());
        }
        Log.i("Database", "Queried for : " + query);
        Log.i("Database", "Returned " + Integer.toString(result.size()) + " elements");
        db.close();
        return result;
    }

    public ArrayList<String[]> searchEventsByName(String name){
        String query = "select * from " + TABLE_NAME_EVENTS
                + " where Title like \'%" + name + "%\';";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<String[]> result = new ArrayList<String[]>();
        if (cursor.moveToFirst()) {
            do {
                String[] event = new String[8];
                // fill in six first fields
                for (int i = 0; i < 6; i++)
                    event[i] = cursor.getString(i);
                // since the category has to be extracted from a string
                event[6] = String.valueOf(EventsParseForDateWithinCategory.retrieveCategory(cursor.getString(6)));
                // add the numerical date
                event[7] = cursor.getString(7);
                result.add(event);
            } while (cursor.moveToNext());
        }
        Log.i("Database", "Queried for : " + query);
        Log.i("Database", "Returned " + Integer.toString(result.size()) + " elements");
        db.close();
        return result;

    }

    public ArrayList<Integer> getDaysWithEventsInCategory(int category, int yearMonth){
        SQLiteDatabase db = this.getWritableDatabase();

        String query;
        if(category != 0){
            query = "select NumericalDate from " + TABLE_NAME_EVENTS
                    + " where NumericalDate > " + Integer.toString(yearMonth*100)
                    + " AND NumericalDate < " + Integer.toString(yearMonth*100 + 35)
                    + " AND Category like " + "\'%," + Integer.toString(category) + ",%\'" + ";";
        } else {
            query = "select NumericalDate from " + TABLE_NAME_EVENTS
                    + " where NumericalDate > " + Integer.toString(yearMonth*100)
                    + " AND NumericalDate < " + Integer.toString(yearMonth*100 + 35) + ";";
        }

        ArrayList<Integer> result = new ArrayList<>();
        Cursor cursor = db.rawQuery(query, null);

        int processedDay = 1;
        if(cursor.moveToFirst()) {
            do {
                int day = cursor.getInt(0)%100;
                if(day == processedDay){
                    processedDay ++;
                    result.add(day);
                } else if (day > processedDay){
                    processedDay = day;
                    result.add(day);
                }
            } while (cursor.moveToNext());
        }
        return result;
    }

    public void deleteEvents(int lowerBoundDate, int upperBoundDate) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "delete from " + TABLE_NAME_EVENTS
                + " where NumericalDate > " + Integer.toString(upperBoundDate) +
                " OR NumericalDate < " + Integer.toString(lowerBoundDate) + ";";

        db.rawQuery(query, null);
        db.close();
    }
}
