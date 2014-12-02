package edu.yalestc.yalepublic.Events;

import android.app.Activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
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
    //not private to adapter since we will use it in the buttons to specific categories!
    private String[] JsonCategoryNames;
    private String[] categories;
    private int[] colors;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JsonCategoryNames = getResources().getStringArray(R.array.events_category_names_json);
        categories = getResources().getStringArray(R.array.events_category_names);
        colors = getResources().getIntArray(R.array.event_categories_colors);
        setContentView(R.layout.activity_event_categories);
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

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3){
                    Intent showEvents = new Intent(EventCategories.this, EventsDisplay.class);
                    showEvents.putExtra("category", categories[arg2]);
                    showEvents.putExtra("color", colors[arg2]);
                    //warning here, the real json categories are different than the descriptions. In eventsDisplay we will have
                    //to parse the names and do a good JSON query. This means splitting the string into separate words and adding a query
                    //for category equal to each of the elements!
                    String JsonCategory = JsonCategoryNames[arg2];
                    if(JsonCategory == "PlaceHolderForAll"){

                    } else{
                        showEvents.putExtra("JsonCategories", JsonCategory);
                    }
                    startActivity(showEvents);
                }
            });

            return rootView;
        }
    }
//custom adapter creating relativelayouts consisting of  imageview and textview
    private class EventsCategoriesAdapter extends BaseAdapter {
        private int white = getResources().getColor(R.color.white);
        private Context mContext;
    //for screen dimensions (so that it looks okay on all sizes of displays)
        private DisplayMetrics display;
    //width of screen in pixels
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
            //this is the rectangle displayed on the left of category
            GradientDrawable rectangle = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[]{white, colors[i]});
            rectangle.setShape(GradientDrawable.RECTANGLE);
            rectangle.setSize(((int) width / 10), ((int) width / 10));
            //adding rounded corners
            rectangle.setCornerRadius(16);
            // i != 0 because the 0th element does not have an image in the imageView, so we do not want to reuse it!
            if (convertView != null && i != 0) {
                ((ImageView) ((RelativeLayout) convertView).getChildAt(0)).setImageDrawable(rectangle);
                ((RelativeLayout) convertView).setPadding(0,((int) width / 25), 0, ((int) width / 25));
                ((TextView) ((RelativeLayout) convertView).getChildAt(1)).setTextSize(width/45);
                ((TextView) ((RelativeLayout) convertView).getChildAt(1)).setText(categories[i]);
                return ((RelativeLayout) convertView);

            } else {
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
