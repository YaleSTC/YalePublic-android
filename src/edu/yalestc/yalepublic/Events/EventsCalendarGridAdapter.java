package edu.yalestc.yalepublic.Events;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

import edu.yalestc.yalepublic.R;

import static edu.yalestc.yalepublic.R.drawable.calendar_grid_button_current_selected;
import static edu.yalestc.yalepublic.R.drawable.calendar_grid_button_unselected;

/**
 * Created by Stan Swidwinski on 11/11/14.
 *
 * Class describes the behavior of grids in custom calendar.
 *
 */
public class EventsCalendarGridAdapter extends BaseAdapter{
             //for days in month, day of week etc.
        private Calendar c = Calendar.getInstance();
            //for layoutInflater
        private Context mContext;
        private int mYear;
        private int mMonth;
        private int currentlySelected;
        private int daysInMonth;
        private int daysInPreviousMonth;
        private int firstDayInWeekOfMonth;

            //Context for getting resources and layout inflater
        EventsCalendarGridAdapter(Context context) {
            mContext = context;
                //automatically set selected day to current date
            currentlySelected = c.get(Calendar.DAY_OF_MONTH);
        }

            //month and year are set from outside, we need to update the inner values every time we change them
        //using onclick listeners in the main activity!
        public void update(int year, int month) {
            mYear = year;
            mMonth = month;
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, 1);
            firstDayInWeekOfMonth = c.get(Calendar.DAY_OF_WEEK);
            daysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
            c.set(Calendar.MONTH, month-1);
            daysInPreviousMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        }

            //used in the onClickListeners of gridview to change the imageviews of tiles ("highligh" them,
        //"dehighlight" them etc.
        public int getCurrentlySelected(){
            return currentlySelected;
        }

            //used in the onClickListeners to update the value.
        public void setCurrentlySelected(int i){
                currentlySelected = i;
            }

            //checking if ith day of the month is indeed today. i is the ordinal number of grid element.
        // It is called from the fragment class, hence shorthand if
        //an illegal number is passed (negative) we treat it as currentlySelected (not to invoke getCurrentlySelected)
        public boolean isToday(int i){
            if(i < 0)
                i = currentlySelected;
                //NOT to highlight the date in former month with same "calendar number"
            if(i < firstDayInWeekOfMonth){
                i = -1;
            } else {
                i = getDayNumber(i);
            }
            boolean yearMatch = (Calendar.getInstance().get(Calendar.YEAR) == mYear);
            boolean monthMatch = (Calendar.getInstance().get(Calendar.MONTH) == mMonth);
            boolean dayMatch = (i == Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            return dayMatch && monthMatch && yearMatch;
        }

            //get day number as the "Calendar" number - for example 1st of June gives the number 1 but might be 4th as counted
        //by grid enumeration!
        public int getDayNumber (int i){
            if (i < firstDayInWeekOfMonth - 1){
                return daysInPreviousMonth + i - firstDayInWeekOfMonth + 2;
            } else if( i < daysInMonth + firstDayInWeekOfMonth - 1) {
                return i - firstDayInWeekOfMonth + 2;
            } else {
                return i - firstDayInWeekOfMonth - daysInMonth + 2;
            }
        }

            //checks if given tile represents a day not in the current month. Used for coloring the text fields.
        // It is called from the fragment class, hence shorthand if
        //an illegal number is passed (negative) we treat it as currentlySelected (not to invoke getCurrentlySelected)
        public boolean isOutsideCurrentMonth(int i){
            if (i < 0)
                i = currentlySelected;
            if (i < firstDayInWeekOfMonth - 1){
                return true;
            } else if( i < daysInMonth + firstDayInWeekOfMonth - 1) {
                return false;
            } else {
                return true;
            }
        }


            //the count has to include the "fill in" tiles to have a nice rectangular region.
        @Override
        public int getCount() {
            int fillIn = 0;
            while ((firstDayInWeekOfMonth + daysInMonth + fillIn -1) % 7 != 0)
                fillIn++;
            return (firstDayInWeekOfMonth + daysInMonth + fillIn -1);
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            if(convertView != null) {
                if (isToday(i)) {
                        //current date has a separate image for itself. Inflate a new layout for it and change imageView.
                    convertView =  (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.calendar_image_button_selector, null);
                        //set the image
                    ((ImageView)((RelativeLayout)convertView).getChildAt(0)).setImageDrawable(mContext.getResources().getDrawable(calendar_grid_button_current_selected));
                        //by default current date is selected at the beginning
                    currentlySelected = i;
                        //color of selected, current date is white ("current date" being "today's date"
                    ((TextView)((RelativeLayout)convertView).getChildAt(1)).setTextColor(Color.parseColor("#FFFFFF"));
                } else {
                        //set the image
                    ((ImageView)((RelativeLayout)convertView).getChildAt(0)).setImageDrawable(mContext.getResources().getDrawable(calendar_grid_button_unselected));
                    if (isOutsideCurrentMonth(i)) {
                            //color of not-selected dates outside current month is different. Light Gray.
                        ((TextView) ((RelativeLayout) convertView).getChildAt(1)).setTextColor(Color.parseColor("#888888"));
                    } else {
                            //color of non-selected dates inside current month is different. Dark Gray.
                        ((TextView) ((RelativeLayout) convertView).getChildAt(1)).setTextColor(Color.parseColor("#3d4b5a"));
                    }
                }
                /*set the size of the drawable
                ((ImageView)((RelativeLayout)convertView).getChildAt(0)).getLayoutParams().height=((int)(height*136/1920));
                ((ImageView)((RelativeLayout)convertView).getChildAt(0)).getLayoutParams().width=((int)(width/7));*/
                    //set the day number. It is the cardinal calendar number!
                ((TextView)((RelativeLayout)convertView).getChildAt(1)).setText(Integer.toString(getDayNumber(i)));
                return convertView;
            } else {
                RelativeLayout calendar_grid = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.calendar_image_button_selector, null);
                if (isToday(i)) {
                        //current date has a separate image for itself. Inflate a new layout for it and change imageView.
                    ((ImageView)((RelativeLayout)calendar_grid).getChildAt(0)).setImageDrawable(mContext.getResources().getDrawable(calendar_grid_button_current_selected));
                        //by default current date is selected at the beginning
                    currentlySelected = i;
                        //color of selected, current date is white ("current date" being "today's date"
                    ((TextView)calendar_grid.getChildAt(1)).setTextColor((Color.parseColor("#FFFFFF")));
                } else {
                    if (isOutsideCurrentMonth(i)) {
                            //color of not-selected dates outside current month is different. Light Gray.
                        ((TextView) ((RelativeLayout) calendar_grid).getChildAt(1)).setTextColor(Color.parseColor("#888888"));
                    } else {
                            //color of non-selected dates inside current month is different. Dark Gray.
                        ((TextView) ((RelativeLayout) calendar_grid).getChildAt(1)).setTextColor(Color.parseColor("#3d4b5a"));
                    }
                }
                /*   //set the image size
                ((ImageView)((RelativeLayout)calendar_grid).getChildAt(0)).getLayoutParams().height=((int)(height*136/1920));
                ((ImageView)((RelativeLayout)calendar_grid).getChildAt(0)).getLayoutParams().width=((int)(width/6.5));*/
                //set the day number. It is the cardinal calendar number!
                ((TextView)calendar_grid.getChildAt(1)).setText(Integer.toString(getDayNumber(i)));
                return calendar_grid;
            }
        }
    }