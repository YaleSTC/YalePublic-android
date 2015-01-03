package edu.yalestc.yalepublic.Events;

/**
 * Created by Stan Swidwinski on 1/3/15.
 */
public class dateFormater {
    dateFormater(){}

        //from calendar format to MM from 01 to 12
    public static String formatMonthFromCalendarFormat(int month){
        //calendar operates on months enumrated as 0 - 11!
        int realMonth = month + 1;
        String result ="";
        if(month < 10){
            result = "0";
        }
        result += Integer.toString(realMonth);
        return result;
    }

        //from calendar format to DD from 01 to 31
    public static String formatDayFromCalendarFormat(int day){
        String result ="";
        if(day < 10){
            result = "0";
        }
        result += Integer.toString(day);
        return result;
    }

        //from calendar format to YYYY-MM-01
    public static String formatDateForJSONQuery(int year, int month){
        int myMonth = 0;
        String result = "";
        if(month == 12){
            myMonth = 0;
            year++;
        } else if(month == -1){
            myMonth = 12;
            year--;
        }
        result = Integer.toString(year) + "-" + formatMonthFromCalendarFormat(myMonth);
        result += "-01";
        return result;
    }

        //from calendar format to YYYYMMDD
    public static String formatDateForEventsParseForDate(int year, int month, int day){
        int myMonth = 0;
        String result = "";
        if(month == 12){
            myMonth = 0;
            year++;
        } else if(month == -1){
            myMonth = 12;
            year--;
        }
        result = Integer.toString(year) + formatMonthFromCalendarFormat(myMonth) + formatDayFromCalendarFormat(day);
        return result;
    }

}
