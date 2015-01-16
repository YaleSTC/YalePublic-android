package edu.yalestc.yalepublic.Events;

/**
 * Created by Stan Swidwinski on 1/3/15.
 */
public class DateFormater {
    DateFormater(){}

        //from calendar format to MM from 01 to 12
    public static String formatMonthFromCalendarFormat(int month){
        //calendar operates on months enumrated as 0 - 11!
        int realMonth = month + 1;
        String result ="";
        if(realMonth < 10){
            result = "0";
        }
        result += Integer.toString(realMonth);
        return result;
    }

        //the input is in calendar format the output in real format
    public static int toYearMonthCalendToReal(int year, int month){
        if(month < 0){
            month = month + 12;
            year--;
        } else if(month > 11){
            month = month - 12;
            year++;
        }
        return year*100 + month + 1;
    }

    public static int toYearMonthCalendToCalend(int year, int month){
        if(month < 0){
            month = month + 12;
            year--;
        } else if(month > 11){
            month = month - 12;
            year++;
        }
        return year*100 + month;
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

        //from real human-usable format to real human-usable format (String as YYYYMMDD, MM from 01 to 12)
    public static String convertDateToString(int year, int month, int day){
        String result = "";
            //input calendar format to the toYearMonthCalendToReal and than change it back to real format!
        result = Integer.toString(toYearMonthCalendToReal(year, month - 1));
        result += formatDayFromCalendarFormat(day);
        return result;
    }

        //from calendar format to YYYY-MM-01
    public static String formatDateForJSONQuery(int year, int month){
        String result = "";
        if(month > 11){
            month = month % 12;
            year++;
        } else if(month < 0){
                //modulo in java is just flawed in so many ways.
            month = month + 12;
            year--;
        }
        result = Integer.toString(year) + "-" + formatMonthFromCalendarFormat(month);
        result += "-01";
        return result;
    }

        //from calendar format to YYYYMMDD (with month incremented)
    public static String formatDateForEventsParseForDate(int year, int calendarMonth, int day){
        int myMonth = calendarMonth;
        String result = "";
        if(calendarMonth > 11){
            myMonth = calendarMonth % 12;
            year++;
        } else if(calendarMonth < 0){
            myMonth = calendarMonth + 12;
            year--;
        }
        result = Integer.toString(year) + formatMonthFromCalendarFormat(myMonth) + formatDayFromCalendarFormat(day);
        return result;
    }
        //the lines and date are given in the format YYYYMM.
    public static boolean inInterval(int bottomLine, int upperLine, int date){
        if(date <= upperLine && date >= bottomLine){
            return true;
        }
        return false;
    }
}