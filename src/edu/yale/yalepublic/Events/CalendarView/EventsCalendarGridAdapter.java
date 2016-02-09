package edu.yale.yalepublic.Events.CalendarView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import edu.yale.yalepublic.R;

import static edu.yale.yalepublic.R.drawable.calendar_grid_button_current_selected;
import static edu.yale.yalepublic.R.drawable.calendar_grid_button_selected;
import static edu.yale.yalepublic.R.drawable.calendar_grid_button_unselected;

/**
 * Created by Stan Swidwinski on 11/11/14.
 * <p/>
 * Class describes the behavior of grids in custom calendar.
 */
public class EventsCalendarGridAdapter extends BaseAdapter {
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
    private int[] mColors;
    private int[] mColorsFrom;
    private int mCategory;
    private ArrayList<Integer> daysWithEvents;

    //Context for getting resources and layout inflater
    EventsCalendarGridAdapter(Context context, int[] colors, int[] colorsFrom, int category) {
        mContext = context;
        mColors = colors;
        mColorsFrom = colorsFrom;
        mCategory = category;
        //automatically set selected day to current date
        currentlySelected = c.get(Calendar.DAY_OF_MONTH);
        //so that we have no nullptr exceptions
        daysWithEvents = new ArrayList<>();
    }

    //called from listadapter to set the days with events!
    public void setDaysWithEvents(ArrayList<Integer> days) {
        daysWithEvents = days;
    }

    //month and year are set from outside, we need to updateEvents the inner values every time we change them
    //using onclick listeners in the main activity!
    public void update(int year, int month) {
        mYear = year;
        //in calendar format
        mMonth = month;
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, 1);
        firstDayInWeekOfMonth = c.get(Calendar.DAY_OF_WEEK);
        daysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        c.set(Calendar.MONTH, month - 1);
        daysInPreviousMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        //otherwise we could get a nullptr exception if the selected day in former month is out of bounds
        // for current month. For example 29 January  when February has 28 days!
        Calendar tmp = Calendar.getInstance();
        if (mMonth == tmp.get(Calendar.MONTH))
            setCurrentlySelected(getGridNumber(tmp.get(Calendar.DAY_OF_MONTH)));
        else
            setCurrentlySelected(getGridNumber(1));
    }

    //used in the onClickListeners of gridview to change the imageviews of tiles ("highligh" them,
    //"dehighlight" them etc. gives the id number of grid
    public int getCurrentlySelected() {
        return currentlySelected;
    }

    //used in the onClickListeners to updateEvents the value. sets the id of currently selected grid
    public void setCurrentlySelected(int i) {
        currentlySelected = i;
    }

    //checking if ith day of the month is indeed today. i is the ordinal number of grid element.
    // It is called from the fragment class, hence shorthand if
    //an illegal number is passed (negative) we treat it as currentlySelected (not to invoke getCurrentlySelected)
    public boolean isToday(int i) {
        if (i < 0)
            i = currentlySelected;
        //NOT to highlight the date in former month with same "calendar number"
        if (i < firstDayInWeekOfMonth) {
            i = -1;
        } else {
            i = getDayNumber(i);
        }
        boolean yearMatch = (Calendar.getInstance().get(Calendar.YEAR) == mYear);
        boolean monthMatch = (Calendar.getInstance().get(Calendar.MONTH) == mMonth);
        boolean dayMatch = (i == Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        return dayMatch && monthMatch && yearMatch;
    }

    //get day number as the "Calendar" number (ID # of the grid) - for example 1st of June
    // gives the number 1 but might be 4th as counted by grid enumeration!
    public int getDayNumber(int i) {
        if (i < firstDayInWeekOfMonth - 1) {
            return daysInPreviousMonth + i - firstDayInWeekOfMonth + 2;
        } else if (i < daysInMonth + firstDayInWeekOfMonth - 1) {
            return i - firstDayInWeekOfMonth + 2;
        } else {
            return i - firstDayInWeekOfMonth - daysInMonth + 2;
        }
    }

    public int getGridNumber(int i) {
        return firstDayInWeekOfMonth + i - 2;
    }

    //checks if given tile ( i is the ID #!) represents a day not in the current month.
    // Used for coloring the text fields. It is called from the fragment class, hence shorthand if
    //an illegal number is passed (negative) we treat it as currentlySelected
    //(not to invoke getCurrentlySelected)
    public boolean isOutsideCurrentMonth(int i) {
        if (i < 0)
            i = currentlySelected;
        if (i < firstDayInWeekOfMonth - 1) {
            return true;
        } else if (i < daysInMonth + firstDayInWeekOfMonth - 1) {
            return false;
        }
    return true;
    }

    //make the little blob next under textView indicating presence of event
    protected GradientDrawable createBlob(int color, int colorFrom) {
        int[] colors = new int[]{colorFrom, color};
        GradientDrawable blob = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        blob.setShape(GradientDrawable.OVAL);
        blob.setSize(20, 20);
        blob.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        blob.setGradientRadius(15);
        blob.setGradientCenter((float) 0.5, (float) 0.0);

        return blob;
    }

    //the count has to include the "fill in" tiles to have a nice rectangular region.
    @Override
    public int getCount() {
        int fillIn = 0;
        while ((firstDayInWeekOfMonth + daysInMonth + fillIn - 1) % 7 != 0)
            fillIn++;
        return (firstDayInWeekOfMonth + daysInMonth + fillIn - 1);
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
        if (convertView != null) {
            if (isToday(i) && !isOutsideCurrentMonth(i)) {
                //current date has a separate image for itself. Inflate a new layout for it and change imageView.
                convertView = LayoutInflater.from(mContext).inflate(R.layout.calendar_image_button_selector, null);
                //set the image
                ((ImageView) ((RelativeLayout) convertView).getChildAt(0)).setImageDrawable(mContext.getResources().getDrawable(calendar_grid_button_current_selected));
                //by default current date is selected at the beginning
                currentlySelected = i;
                //color of selected, current date is white ("current date" being "today's date"
                ((TextView) ((RelativeLayout) convertView).getChildAt(1)).setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                if (i != currentlySelected) {
                    //set the image
                    ((ImageView) ((RelativeLayout) convertView).getChildAt(0)).setImageDrawable(mContext.getResources().getDrawable(calendar_grid_button_unselected));
                    if (isOutsideCurrentMonth(i)) {
                        //color of not-selected dates outside current month is different. Light Gray.
                        ((TextView) ((RelativeLayout) convertView).getChildAt(1)).setTextColor(Color.parseColor("#888888"));
                    } else {
                        //color of non-selected dates inside current month is different. Dark Gray.
                        ((TextView) ((RelativeLayout) convertView).getChildAt(1)).setTextColor(Color.parseColor("#3d4b5a"));
                    }
                } else {
                    //set the image
                    ((ImageView) ((RelativeLayout) convertView).getChildAt(0)).setImageDrawable(mContext.getResources().getDrawable(calendar_grid_button_selected));
                    //color of selected dates inside current month is different. White
                    ((TextView) ((RelativeLayout) convertView).getChildAt(1)).setTextColor(Color.parseColor("#FFFFFF"));
                }
            }
            //set the blob under text on grid if events exist
            if (daysWithEvents.contains(getDayNumber(i)) && !isOutsideCurrentMonth(i)) {
                if (mCategory != 0) {
                    // if category is non-zero the blob is in the color of the category
                    ((ImageView) ((RelativeLayout) convertView).getChildAt(2)).setImageDrawable(createBlob(mColors[0], mColorsFrom[0]));
                } else {
                    // if category is 0 the blob will be Yale Blue
                    ((ImageView) ((RelativeLayout) convertView).getChildAt(2)).setImageDrawable(createBlob(Color.parseColor("#0F4D92"), Color.parseColor("#5ba5f8")));
                }
            } else {
                //no events or day outside of month -> blank drawable
                ((ImageView) ((RelativeLayout) convertView).getChildAt(2)).setImageDrawable(new GradientDrawable());
            }
            //set the day number. It is the cardinal calendar number!
            ((TextView) ((RelativeLayout) convertView).getChildAt(1)).setText(Integer.toString(getDayNumber(i)));
            return convertView;
        } else {
            RelativeLayout calendar_grid = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.calendar_image_button_selector, null);
            if (isToday(i) && !isOutsideCurrentMonth(i)) {
                //current date has a separate image for itself. Inflate a new layout for it and change imageView.
                ((ImageView) (calendar_grid).getChildAt(0)).setImageDrawable(mContext.getResources().getDrawable(calendar_grid_button_current_selected));
                //by default current date is selected at the beginning
                currentlySelected = i;
                //color of selected, current date is white ("current date" being "today's date"
                ((TextView) calendar_grid.getChildAt(1)).setTextColor((Color.parseColor("#FFFFFF")));
            } else {
                if (i != currentlySelected) {
                    //set the image
                    ((ImageView) (calendar_grid).getChildAt(0)).setImageDrawable(mContext.getResources().getDrawable(calendar_grid_button_unselected));
                    if (isOutsideCurrentMonth(i)) {
                        //color of not-selected dates outside current month is different. Light Gray.
                        ((TextView) (calendar_grid).getChildAt(1)).setTextColor(Color.parseColor("#888888"));
                    } else {
                        //color of non-selected dates inside current month is different. Dark Gray.
                        ((TextView) (calendar_grid).getChildAt(1)).setTextColor(Color.parseColor("#3d4b5a"));
                    }
                } else {
                    //set the image
                    ((ImageView) (calendar_grid).getChildAt(0)).setImageDrawable(mContext.getResources().getDrawable(calendar_grid_button_selected));
                    //color of non-selected dates inside current month is different. Dark Gray.
                    ((TextView) (calendar_grid).getChildAt(1)).setTextColor(Color.parseColor("#FFFFFF"));
                }
            }
            // set the blob under text on grid if events exist. Note: no need to do anything for days
            // without events or days outside of month -> the imageview is empty by default
            if (daysWithEvents.contains(getDayNumber(i)) && !isOutsideCurrentMonth(i)) {
                if (mCategory != 0) {
                    // if category is non-zero the blob is in the color of the category
                    ((ImageView) (calendar_grid).getChildAt(2)).setImageDrawable(createBlob(mColors[0], mColorsFrom[0]));
                } else {
                    // if category is 0 the blob will be Yale Blue
                    ((ImageView) (calendar_grid).getChildAt(2)).setImageDrawable(createBlob(Color.parseColor("#0F4D92"), Color.parseColor("#5ba5f8")));
                }
            }
            //set the day number. It is the cardinal calendar number!
            ((TextView) calendar_grid.getChildAt(1)).setText(Integer.toString(getDayNumber(i)));
            return calendar_grid;
        }
    }
}