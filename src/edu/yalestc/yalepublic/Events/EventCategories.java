package edu.yalestc.yalepublic.Events;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import edu.yalestc.yalepublic.R;

public class EventCategories extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_categories);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_event_categories, container, false);
            return rootView;
        }
    }

    private class EventsCategoriesAdapter extends BaseAdapter {
        String[] categories = getResources().getStringArray(R.array.events_category_names);
        int[] colors = getResources().getIntArray(R.array.event_categories_colors);
        int white = getResources().getColor(R.color.white);
        Context mContext;

        EventsCategoriesAdapter(Context context){
            mContext = context;
        }

        @Override
        public int getCount() {
            return categories.length;
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
            GradientDrawable rectangle = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[]{white, colors[i]});
            rectangle.setShape(GradientDrawable.RECTANGLE);
            rectangle.setCornerRadius(16);
            if (convertView != null) {
                ((ImageView) ((RelativeLayout) convertView).getChildAt(0)).setImageDrawable(rectangle);
                ((TextView) ((RelativeLayout) convertView).getChildAt(1)).setText(categories[i]);
                return ((RelativeLayout) convertView);
            } else {
                //if not, create a new one from the template of a view using inflate
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                RelativeLayout button = ((RelativeLayout) inflater.inflate(R.layout.events_category_button, null));
                ((ImageView) button.getChildAt(0)).setImageDrawable(rectangle);
                ((TextView) button.getChildAt(1)).setText(categories[i]);
                return button;
            }
        }
    }
}
