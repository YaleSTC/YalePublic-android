package edu.yalestc.yalepublic.Cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Stan Swidwinski on 1/3/15.
 */

public class CalendarDatabaseTableHandler extends SQLiteOpenHelper {

    private static final int TABLE_VERSION = 1;
    private static final String TABLE_NAME = "events";
    String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME+ " (" +
                    "DateDescription" + " TEXT, " +
                    "Title" + " TEXT, "+
                    "StartTime" + " TEXT, " +
                    "EndTime" + " TEXT, " +
                    "Location" + " TEXT, " +
                    "Description" + " TEXT, " +
                    "Category" + " TEXT, " +
                    "NumericalDate" + " INTEGER);";

    CalendarDatabaseTableHandler(Context context){
        super(context, TABLE_NAME, null, TABLE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
    }

    public void addEvent(String[] eventInfo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("DateDescription", eventInfo[4]);
        values.put("Title", eventInfo[0]);
        values.put("StartTime", eventInfo[1]);
        values.put("EndTime", eventInfo[2]);
        values.put("Location", eventInfo[3]);
        values.put("Description",eventInfo[5]);
        values.put("Category", eventInfo[6]);
            //for easier implementation of deleteEvents
        values.put("NumericalDate", Integer.parseInt(eventInfo[7]));
        Log.i("DATABASE", "Event " + eventInfo[0] + " added");
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<String[]> getEvents(String date){
       SQLiteDatabase db = this.getReadableDatabase();

        String query = "select * from " + TABLE_NAME
                + "where Date in ('Date'," + date +");";

        Cursor cursor = db.rawQuery(query,null);
        ArrayList<String[]> result = new ArrayList<String[]>();
        if(cursor.moveToFirst()){
            do {
                String[] event = new String[7];
                for (int i = 0; i < 7; i++)
                    event[i] = cursor.getString(i);
                result.add(event);
            } while (cursor.moveToNext());
        }
        return result;
    }

    public void deleteEvents(int lowerBoundDate, int upperBoundDate){
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "delete from " + TABLE_NAME
                + "where NumericalDate > " + Integer.toString(upperBoundDate) +
                " OR NumericalDate < " + Integer.toString(lowerBoundDate) + ";";

        db.rawQuery(query, null);
        db.close();
    }
}
