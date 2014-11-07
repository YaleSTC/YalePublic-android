package edu.yalestc.yalepublic.Events;

import android.app.Activity;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
    public class PlaceholderFragment extends Fragment {



        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_event_categories, container, false);
            EventsCategoriesAdapter adapter = new EventsCategoriesAdapter(getActivity());

            ListView listView = (ListView) rootView.findViewById(R.id.listview_event_categories);
            listView.setAdapter(adapter);
            
            return rootView;
        }
    }

    private class EventsCategoriesAdapter extends BaseAdapter {
        private String[] categories = getResources().getStringArray(R.array.events_category_names);
        private int[] colors = getResources().getIntArray(R.array.event_categories_colors);
        private int white = getResources().getColor(R.color.white);
        private Context mContext;
        private DisplayMetrics display;
        private int width;

        EventsCategoriesAdapter(Context context){
            mContext = context;
            display = mContext.getResources().getDisplayMetrics();
            width = display.widthPixels;
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
            rectangle.setSize(((int) width / 10), ((int) width / 10));
            rectangle.setCornerRadius(16);
            if (convertView != null && i != 0) {
                ((ImageView) ((RelativeLayout) convertView).getChildAt(0)).setImageDrawable(rectangle);
                ((RelativeLayout) convertView).setPadding(0,((int) width / 25), 0, ((int) width / 25));
                ((TextView) ((RelativeLayout) convertView).getChildAt(1)).setTextSize(width/45);
                ((TextView) ((RelativeLayout) convertView).getChildAt(1)).setText(categories[i]);
                return ((RelativeLayout) convertView);

            } else {
                //if not, create a new one from the template of a view using inflate
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                RelativeLayout button = ((RelativeLayout) inflater.inflate(R.layout.events_category_button, null));
                button.setPadding(0,((int) width / 25), 0, ((int) width / 25));
                if(i==0){

                } else {
                    ((ImageView) button.getChildAt(0)).setImageDrawable(rectangle);
                }
                ((TextView) button.getChildAt(1)).setTextSize(width/45);
                ((TextView) button.getChildAt(1)).setText(categories[i]);
                return button;
            }
        }
    }
}
