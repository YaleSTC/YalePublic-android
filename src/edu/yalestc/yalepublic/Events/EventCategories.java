package edu.yalestc.yalepublic.Events;

import android.app.Activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import edu.yalestc.yalepublic.R;

public class EventCategories extends Activity {
    private String[] categories;
    //gradient colors - gradient from colorFrom to colorTo
    private int[] colorsTo;
    private int[] colorsFrom;
    //button consists of two parts. lower is solid colors. these are the colors.
    private int[] colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categories = getResources().getStringArray(R.array.events_category_names);
        colors = getResources().getIntArray(R.array.event_categories_colors);
        colorsTo = getResources().getIntArray(R.array.event_categories_colors);
        colorsFrom = getResources().getIntArray(R.array.event_categories_colors_from);
        setContentView(R.layout.events_event_categories);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

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

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    Intent showEvents = new Intent(EventCategories.this, EventsDisplay.class);
                    //warning here, the real json categories are different than the descriptions. In eventsDisplay we will have
                    //to parse the names and do a good JSON query. This means splitting the string into separate words and adding a query
                    //for category equal to each of the elements!
                    showEvents.putExtra("category", categories[arg2]);
                    //to parse only for events in selected category
                    showEvents.putExtra("numberOfCategorySearchedFor", arg2);
                    //0 means no particular category
                    if (arg2 == 0) {
                        showEvents.putExtra("JsonCategories", "All");
                        showEvents.putExtra("colorsTo", colorsTo);
                        showEvents.putExtra("colorsFrom", colorsFrom);
                        showEvents.putExtra("colors", colors);
                    } else {
                        showEvents.putExtra("colors", new int[]{colors[arg2]});
                        showEvents.putExtra("colorsTo", new int[]{colorsTo[arg2]});
                        showEvents.putExtra("colorsFrom", new int[]{colorsFrom[arg2]});
                    }
                    Log.v("showEventsLaunch", "With given parameters:" + categories[arg2] + " " + Integer.toString(colorsTo[arg2]));
                    startActivity(showEvents);
                }
            });

            return rootView;
        }
    }

    //custom adapter creating relativelayouts consisting of  imageview and textview
    private class EventsCategoriesAdapter extends BaseAdapter {
        private Context mContext;
        //for screen dimensions (so that it looks okay on all sizes of displays)
        private DisplayMetrics display;
        //width of screen in pixels
        private int width;

        EventsCategoriesAdapter(Context context) {
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
            //the color-coded rectangles used in original app are actually relatively complex. Function createRectangle takes care of it.
            LayerDrawable rectangle = createRectangle(i);
            // i != 0 because the 0th element does not have an image in the imageView, so we do not want to reuse it!
            if (convertView != null && i != 0) {
                ((ImageView) ((RelativeLayout) convertView).getChildAt(0)).setImageDrawable(rectangle);
                ((RelativeLayout) convertView).setPadding(width / 20, width / 25, 0, width / 25);
                ((TextView) ((RelativeLayout) convertView).getChildAt(1)).setTextSize(width / 45);
                ((TextView) ((RelativeLayout) convertView).getChildAt(1)).setText(categories[i]);
                convertView.setBackgroundColor(Color.parseColor("#dbdbdd"));
                return ((RelativeLayout) convertView);

            } else {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                RelativeLayout button = ((RelativeLayout) inflater.inflate(R.layout.events_category_button, null));
                button.setPadding(width / 20, width / 25, 0, width / 25);
                if (i == 0) {

                } else {
                    ((ImageView) button.getChildAt(0)).setImageDrawable(rectangle);
                }
                ((TextView) button.getChildAt(1)).setTextSize(width / 45);
                ((TextView) button.getChildAt(1)).setText(categories[i]);
                button.setBackgroundColor(Color.parseColor("#dbdbdd"));
                return button;
            }
        }

        private LayerDrawable createRectangle(int i) {
            GradientDrawable[] layers = new GradientDrawable[2];
            layers[0] = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{colorsFrom[i], colorsTo[i]});
            layers[0].setShape(GradientDrawable.RECTANGLE);
            layers[0].setSize(width / 10, width / 20);
            //adding rounded corners
            layers[0].setCornerRadii(new float[]{16, 16, 16, 16, 0, 0, 0, 0});

            layers[1] = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{colors[i], colors[i]});
            layers[1].setShape(GradientDrawable.RECTANGLE);
            layers[1].setSize(width / 10, width / 20);
            //adding rounded corners
            layers[1].setCornerRadii(new float[]{0, 0, 0, 0, 16, 16, 16, 16});

            LayerDrawable button = new LayerDrawable(layers);
            button.setLayerInset(0, 0, 0, 0, width/20);
            button.setLayerInset(1, 0, width/20, 0, 0);
            return button;
        }
    }
}
