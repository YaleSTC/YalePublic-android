package edu.yalestc.yalepublic.Events;

/**
 * Created by Stan Swidwinski on 1/3/15.
 *
 * Class includes several ways to transform a date from one format to the other.
 *
 * The convention here is as follows:
 *
 *  - Calendar format is the format provided by the built-in Calendar class. It gives months
 *  within the interval 0 - 11 (0 - Januaryh, 11 - December)
 *
 *  - Standard format will be the format used by normal people. Months are given within
 *  the interval 1 - 12 (1 - January, 12 - December)
 */
public class DateFormater {

        //from calendar format to MM format from 01 (January) to 12 (December)
    public static String monthFromCalendarToStandard(int month){
        //calendar operates on months enumrated as 0 - 11!
        int realMonth = month + 1;
        String result ="";
        if(realMonth < 10){
            result = "0";
        }
        result += Integer.toString(realMonth);
        return result;
    }

        //from calendar format to year format. Result is in the YYYYMM format and is an integer.
        //the function handles changing the year if month is out of bounds, however month has to be
        //within the interval -12, 24.
    public static int yearMonthFromCalendarToStandard(int year, int month){
        if(month < 0){
            month = month + 12;
            year--;
        } else if(month > 11){
            month = month - 12;
            year++;
        }
        return year*100 + month + 1;
    }
        //from calendar format to calendar format. Reuslt is in the YYYYMM format and is an integer.
        //the function handles changing the year if month is out of bounds, however month has to be
        //within the interval -12, 24.
    public static int yearMonthFromCalendarToCalendar(int year, int month){
        if(month < 0){
            month = month + 12;
            year--;
        } else if(month > 11){
            month = month - 12;
            year++;
        }
        return year*100 + month;
    }

        //from integer to DD string. String ranges from 01 to 31
    public static String dayToString(int day){
        String result ="";
        if(day < 10){
            result = "0";
        }
        result += Integer.toString(day);
        return result;
    }

        //from standard to standard format. The output is a string YYYYMMDD.
    public static String convertDateToString(int year, int month, int day){
        String result = "";
            //input calendar format to the yearMonthFromCalendarToStandard and than change it back to real format!
        result = Integer.toString(yearMonthFromCalendarToStandard(year, month - 1));
        result += dayToString(day);
        return result;
    }

        //from calendar format to standard format. The output is of the format YYYY-MM-01
    public static String calendarDateToJSONQuery(int year, int month){
        String result = "";
        if(month > 11){
            month = month % 12;
            year++;
        } else if(month < 0){
                //modulo in java is just flawed in so many ways.
            month = month + 12;
            year--;
        }
        result = Integer.toString(year) + "-" + monthFromCalendarToStandard(month);
        result += "-01";
        return result;
    }

        //from calendar format to standard format. The output is of the format YYYYMMDD.
    public static String calendarDateToEventsParseForDate(int year, int calendarMonth, int day){
        int myMonth = calendarMonth;
        String result = "";
        if(calendarMonth > 11){
            myMonth = calendarMonth % 12;
            year++;
        } else if(calendarMonth < 0){
            myMonth = calendarMonth + 12;
            year--;
        }
        result = Integer.toString(year) + monthFromCalendarToStandard(myMonth) + dayToString(day);
        return result;
    }

        //the upper and bottom lines are the bounds for date. All are given in the YYYYMM format,
        // the month being in standard format.
    public static boolean inInterval(int bottomLine, int upperLine, int date){
        if(date <= upperLine && date >= bottomLine){
            return true;
        }
        return false;
    }
}