package edu.yalestc.yalepublic.Events;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import edu.yalestc.yalepublic.R;

/**
 * Created by Stan Swidwinski on 11/11/14.
 * <p/>
 * Class used to parse given JSON for the EventsCalendarEventList usage. Parses the given JSON into
 * a ListArray of a class holding the place, date and title of the event. Can be queried from outside
 * for events on a given day
 */
public class EventsParseForDateWithinCategory {

    private ArrayList<event> validEvents;
    private int mMonth;
    private int mYear;
    private int mSearchedCategoryNumber;
    //for use in events class to parse the category of an event that will be used in the EventsCalendarEventList class
    //for deciding on the color of the blobs
    final private String[] availableCategories;

    //Context passed in for access to resources
    public EventsParseForDateWithinCategory(String rawData, int month, int year, Context context, int searchedCategoryNumber) {
        mSearchedCategoryNumber = searchedCategoryNumber;
        availableCategories = context.getResources().getStringArray(R.array.events_category_names_json);
        setNewEvents(rawData, month, year);
    }

    //parse the data pulled for validEvents. A validEvent is one that starts over the course of
    //given month
    public void setNewEvents(String rawData, int month, int year) {
        //because the calendar gives numbers 0-11
        mMonth = month + 1;
        mYear = year;
        JSONArray events;
        JSONObject mAllData;
        validEvents = new ArrayList<event>();
        try {
            //substring necessary - JSON option was implemented by Yale poorly and returns javascript.
            //have to get rid of variable definition
            mAllData = new JSONObject(rawData.substring(24));
            events = mAllData.getJSONObject("bwEventList")
                    .getJSONArray("events");
            for (int i = 0; i < events.length(); i++) {
                if (isValidEvent(events.getJSONObject(i))) {
                    validEvents.add(new event(events.getJSONObject(i)));
                }
            }
        } catch (JSONException e) {
            Log.e("EventsParseForCategory", "setNewEvents JSON error");
        }
    }

    public int getNumberOfEvents(){
        return validEvents.size();
    }

    //returns an ArrayList of String[] with the information about events on a given date.
    //The structure of every event is:
    //event[0] = title
    //event[1] = start time
    //event[2] = end time
    //event[3] = place
    //event[4] = dateDescription
    //event[5] = description
    //event[6] = categoryNumber
    //event[7] = date
    public ArrayList<String[]> getEventsOnGivenDate(String date) {
        ArrayList<String[]> givenEvents = new ArrayList<String[]>();
        for (int i = 0; i < validEvents.size(); i++) {
            if (validEvents.get(i).getDate().equals(date)) {
                givenEvents.add(validEvents.get(i).getInfo());
            }
        }
        return givenEvents;
    }

    public ArrayList<String[]> getAllEventsInfo(){
        ArrayList<String[]> allEvents = new ArrayList<String[]>();
        for (int i = 0; i < validEvents.size(); i++) {
           allEvents.add(validEvents.get(i).getInfo());
        }
        return allEvents;
    }

    //check if given event is valid
    //if we are looking for all events - it only has to start in current month
    //if we are looking for special category it has to start in current month and be in the given category!
    private boolean isValidEvent(JSONObject JSONevent) {
        try {
            JSONObject startTime = JSONevent.getJSONObject("start");
            String yearMonth = startTime.getString("datetime");
            yearMonth = yearMonth.substring(0, 6);
            //Log.v("isValidEvent", yearMonth);
            if (yearMonth.equals(Integer.toString(mYear) + monthToString())) {
                if (mSearchedCategoryNumber == 0) {
                    return true;
                } else {
                    if (isInConsideredCategory(JSONevent)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            } else {
                return false;
            }
        } catch (JSONException e) {
            Log.e("EventsParseForCategory/isValidEvent", "JSONerror");
            return false;
        }
    }

    private boolean isInConsideredCategory(JSONObject JSONevent) {
        try {
            JSONArray mCategories = JSONevent.getJSONArray("categories");
            String category;
            for (int i = 0; i < mCategories.length(); i++) {
                if (availableCategories[mSearchedCategoryNumber].split(" ").length != 1) {
                    category = availableCategories[mSearchedCategoryNumber].split(" ")[0];
                    if (category.equals(mCategories.getString(i))) {
                        return true;
                    }
                    category = availableCategories[mSearchedCategoryNumber].split(" ")[1];
                    if (category.equals(mCategories.getString(i))) {
                        return true;
                    }
                } else {
                    category = availableCategories[mSearchedCategoryNumber];
                    if (category.equals(mCategories.getString(i))) {
                        return true;
                    }
                }
            }
            return false;
        } catch (JSONException e) {
            Log.e("EventsParseForCategory/events/setCategoryNumber", "Json error");
            return false;
        }
    }

    //helper function for usage in isValidEvent. returns the month as MM. MM ranges from 01 to 12.
    private String monthToString() {
        String stringMonth;
        if (mMonth < 10) {
            stringMonth = "0";
        } else {
            stringMonth = new String();
        }
        stringMonth += Integer.toString(mMonth);
        return stringMonth;
    }

    private class event {
        private String title;
        private String startTime;
        private String endTime;
        private String place;
        private String date;
        private String dateDescription;
        private String description;
        private int categoryNumber;

        //return a String[] with information about the event
        public String[] getInfo() {
            String[] eventInfo = new String[]{title, startTime, endTime, place, dateDescription, description, Integer.toString(categoryNumber), date};
            return eventInfo;
        }

        public String getDate() {
            return date;
        }

        event(JSONObject event) {
            setTitle(event);
            setStartTime(event);
            setEndTime(event);
            setPlace(event);
            setDate(event);
            setDescription(event);
            setDateDescription(event);
            setCategoryNumber(event);
        }

        private void setTitle(JSONObject JSONevent) {
            try {
                String tmp = JSONevent.getString("summary");
                title = tmp.replace("&amp;","&");
            } catch (JSONException e) {
                Log.e("EventsParseForCategory/setTitle", "JSON error");
            }
        }

        private void setStartTime(JSONObject JSONevent) {
            try {
                JSONObject time = JSONevent.getJSONObject("start");
                if (time.getString("allday") == "true") {
                    startTime = "All day";
                } else {
                    startTime = time.getString("time");
                }
            } catch (JSONException e) {
                Log.e("EventsParseForCategory/setTime", "JSON error");
            }
        }

        private void setEndTime(JSONObject JSONevent) {
            try {
                JSONObject time = JSONevent.getJSONObject("end");
                if (time.getString("allday") == "true") {
                    endTime = "All day";
                } else {
                    endTime = time.getString("time");
                }
            } catch (JSONException e) {
                Log.e("EventsParseForCategory/setTime", "JSON error");
            }
        }

        private void setPlace(JSONObject JSONevent) {
            try {
                JSONObject location = JSONevent.getJSONObject("location");
                place = location.getString("name");
                place += " - ";
                place += location.getString("address");
                place += ", ";
                place += location.getString("city");
                place += ", ";
                place += location.getString("zip");
            } catch (JSONException e) {
                Log.e("EventsParseForCategory/setPlace", "JSON error");
            }
        }

        private void setDate(JSONObject JSONevent) {
            try {
                date = JSONevent.getJSONObject("start").getString("datetime").substring(0, 8);
            } catch (JSONException e) {
                Log.e("EventsParseForCategory/events/setDate", "Json error");
            }
        }

        private void setDateDescription(JSONObject JSONevent) {
            try {
                JSONObject time = JSONevent.getJSONObject("start");
                dateDescription = time.getString("longdate");
            } catch (JSONException e) {
                Log.e("EventsParseForCategory/setTime", "JSON error");
            }
        }

        private void setDescription(JSONObject JSONevent) {
            try {
                String tmp = JSONevent.getString("description");
                description = tmp.replace("&amp;","&");
            } catch (JSONException e) {
                Log.e("EventsParseForCategory/setTime", "JSON error");
            }
        }

        //this is necessary only for mSearchedCategoryNumber == 0 !! Otherwise we know!
        private void setCategoryNumber(JSONObject JSONevent) {
            if (mSearchedCategoryNumber == 0) {
                JSONArray mCategories;
                String category;
                //pull the categories assigned to an event (there are many)
                try {
                    mCategories = JSONevent.getJSONArray("categories");
                    //for every assigned category check against the possible categories and return the number
                    //indicating the place of category in availableCategories array, since the color of the blob
                    //is the same! We always chose the first that matches. Although complexity here is big, there
                    //are rarely more than 5 elements in the first list and the second one is fixed and short.
                    for (int i = 0; i < mCategories.length(); i++) {
                        for (int j = 1; j < availableCategories.length; j++) {
                            if (availableCategories[j].split(" ").length != 1) {
                                category = availableCategories[j].split(" ")[0];
                                if (category.equals(mCategories.getString(i))) {
                                    categoryNumber = j;
                                    return;
                                }
                                category = availableCategories[j].split(" ")[1];
                                if (category.equals(mCategories.getString(i))) {
                                    categoryNumber = j;
                                    return;
                                }
                            } else {
                                category = availableCategories[j];
                                if (category.equals(mCategories.getString(i))) {
                                    categoryNumber = j;
                                    return;
                                }
                            }
                        }
                    }
                    //for the events that cannot be categorized!
                    categoryNumber = 13;
                } catch (JSONException e) {
                    Log.e("EventsParseForCategory/events/setCategoryNumber", "Json error");
                }
            } else {
                categoryNumber = mSearchedCategoryNumber;
            }
        }
    }
}