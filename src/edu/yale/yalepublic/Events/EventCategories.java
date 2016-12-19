package edu.yale.yalepublic.Events;

import android.app.ActionBar;
import android.app.Activity;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import edu.yale.yalepublic.Cache.CalendarCache;
import edu.yale.yalepublic.Cache.CalendarDatabaseTableHandler;
import edu.yale.yalepublic.R;
import edu.yale.yalepublic.Util.ActionBarUtil;

public class EventCategories extends Activity implements SearchView.OnQueryTextListener, SearchView.OnSuggestionListener{
    String[] categories;
    //gradient colors - gradient from colorFrom to colorTo
    int[] colorsTo;
    int[] colorsFrom;
    //button consists of two parts. lower is solid color, the top is gradient.
    int[] colors;

    private CalendarDatabaseTableHandler db;
    private SearchView mSearchView;
    private MenuItem mMenuItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.events_display, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mMenuItem = menu.findItem(R.id.search);
        mSearchView = (SearchView) mMenuItem.getActionView();
        mSearchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        // Autocomplete configurations for SearchView
        db = new CalendarDatabaseTableHandler(this);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnSuggestionListener(this);

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_event_categories);
        ActionBar actionbar = getActionBar();
        ActionBarUtil.setupActionBar(actionbar, getString(R.string.events));

        categories = getResources().getStringArray(R.array.events_category_names);
        colors = getResources().getIntArray(R.array.event_categories_colors);
        colorsTo = getResources().getIntArray(R.array.event_categories_colors);
        colorsFrom = getResources().getIntArray(R.array.event_categories_colors_from);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, PlaceholderFragment.newInstance(colors, colorsFrom, colorsTo, categories))
                    .commit();
        }
    }

        //adds functionality to the refersh button!
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //need to create locally inheriting class since the original one
        //is integral with splash screen (for getting the bot-right icon!)
        class Updater extends CalendarCache {
            Updater(Activity mActivity) {
                super(mActivity);
            }

            @Override
            protected void onPostExecute(String result) {
                if (super.dialog != null && super.dialog.isShowing()) {
                    super.dialog.dismiss();
                }
            }

        }

        //add functinoality to the button
        switch (item.getItemId()) {
            case R.id.refresh:
                Updater cache = new Updater(this);
                //wipe the database and preferences.
                cache.wipeDatabase();
                cache.clearPreferences();
                //download all the events and recreate the db
                cache.execute();
                return true;
        }
        //bcs you have to
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if(query.length() != 0) {
            Cursor cursor = db.getEventSuggestions(query);

            //shows a list of suggestions if available
            String[] columns = {"suggestions", "date"};
            int[] columnTextId = new int[]{R.id.suggestion_name, R.id.suggestion_date}; //where the data will be mapped to
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                    R.layout.suggestion_list_item, cursor, columns, columnTextId, 0);

            mSearchView.setSuggestionsAdapter(adapter);

            return true;
        }
        else {
            //clears the list of suggestions if search dialog is empty
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                    R.layout.empty_layout, null, null, null, 0);
            mSearchView.setSuggestionsAdapter(adapter);

            return false;
        }
    }

    @Override
    public boolean onSuggestionSelect(int i) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int i) {
        //gets details of the event clicked on
        MatrixCursor cursor = (MatrixCursor) mSearchView.getSuggestionsAdapter().getItem(i);
        cursor.moveToPosition(i);
        String name = cursor.getString(1);
        String date = cursor.getString(2);
        String[] event = db.getEventByNameAndDate(name, date);

        int color, colorTo, colorFrom;

        color = getResources().getIntArray(R.array.event_categories_colors)[Integer.parseInt(event[6])];
        colorTo = getResources().getIntArray(R.array.event_categories_colors_into)[Integer.parseInt(event[6])];
        colorFrom = getResources().getIntArray(R.array.event_categories_colors_into)[Integer.parseInt(event[6])];

        //put data into the extras
        Intent eventDetails = new Intent(this, EventsDetails.class);
        eventDetails.putExtra("title", event[0]);
        eventDetails.putExtra("start", event[4] + " " + event[1]);
        eventDetails.putExtra("end", event[4] + " " + event[2]);
        //category color in the middle of the blob/rectangle
        eventDetails.putExtra("color", color);
        //category color at the bottom of the blob/rectangle
        eventDetails.putExtra("colorTo", colorTo);
        //category color at the top of the blob/rectangle
        eventDetails.putExtra("colorFrom", colorFrom);
        eventDetails.putExtra("description", event[5]);
        eventDetails.putExtra("location", event[3]);

        mMenuItem.collapseActionView();
        startActivity(eventDetails);

        return true;
    }

    static public class PlaceholderFragment extends Fragment {

        private int[] mColors;
        private int[] mColorsFrom;
        private int[] mColorsTo;
        private String[] mCategories;
        private Activity mActivity;

        //since the fragment has to have an empty constructor
        public static PlaceholderFragment newInstance(int[] colors, int[] colorsFrom, int[] colorsTo, String[] categories) {
            PlaceholderFragment f = new PlaceholderFragment();
            Bundle bld = new Bundle();
            bld.putIntArray("colors", colors);
            bld.putIntArray("colorsTo", colorsTo);
            bld.putIntArray("colorsFrom", colorsFrom);
            bld.putStringArray("categories", categories);
            f.setArguments(bld);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Bundle mExtras = getArguments();
            mColors = mExtras.getIntArray("colors");
            mColorsTo = mExtras.getIntArray("colorsTo");
            mColorsFrom = mExtras.getIntArray("colorsFrom");
            mCategories = mExtras.getStringArray("categories");
            mActivity = getActivity();
        }

        //to keep track of the activity when we are attached and detatched
        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            mActivity = activity;
        }

        public PlaceholderFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_event_categories, container, false);
            EventsCategoriesAdapter adapter = new EventsCategoriesAdapter(mActivity, mColors, mColorsFrom, mColorsTo, mCategories);

            ListView listView = (ListView) rootView.findViewById(R.id.listview_event_categories);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    Intent showEvents;
                    showEvents = new Intent(mActivity, EventsDisplay.class);
                    //warning here, the real json categories are different than the descriptions. In eventsDisplay we will have
                    //to parse the names and do a good JSON query. This means splitting the string into separate words and adding a query
                    //for category equal to each of the elements!
                    showEvents.putExtra("category", mCategories[arg2]);
                    //to parse only for events in selected category
                    showEvents.putExtra("numberOfCategorySearchedFor", arg2);
                    //0 means no particular category
                    if (arg2 == 0) {
                        showEvents.putExtra("JsonCategories", "All");
                        showEvents.putExtra("colorsTo", mColorsTo);
                        showEvents.putExtra("colorsFrom", mColorsFrom);
                        showEvents.putExtra("colors", mColors);
                    } else {
                        showEvents.putExtra("colors", new int[]{mColors[arg2]});
                        showEvents.putExtra("colorsTo", new int[]{mColorsTo[arg2]});
                        showEvents.putExtra("colorsFrom", new int[]{mColorsFrom[arg2]});
                    }
                    Log.v("showEventsLaunch", "With given parameters:" + mCategories[arg2] + " " + Integer.toString(mColorsTo[arg2]));
                    startActivity(showEvents);
                }
            });

            return rootView;
        }
    }

    //custom adapter creating relativelayouts consisting of  imageview and textview
    private static class EventsCategoriesAdapter extends BaseAdapter {
        //for DisplayMetrics
        private Context mContext;
        //for screen dimensions (so that it looks okay on all sizes of displays)
        private DisplayMetrics display;
        //width of screen in pixels
        private int width;
        //because static....
        private int[] mColors;
        private int[] mColorsTo;
        private int[] mColorsFrom;
        private String[] mCategories;

        EventsCategoriesAdapter(Context context, int[] colors, int[] colorsFrom, int[] colorsTo, String[] categories) {
            mContext = context;
            display = mContext.getResources().getDisplayMetrics();
            width = display.widthPixels;
            mColors = colors;
            mColorsFrom = colorsFrom;
            mColorsTo = colorsTo;
            mCategories = categories;
        }

        @Override
        public int getCount() {
            return mCategories.length;
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
                convertView.setPadding(width / 20, width / 25, 0, width / 25);
                TextView tv = ((TextView) ((RelativeLayout) convertView).getChildAt(1));
                tv.setTextSize(width / 60);
                tv.setText(mCategories[i]);
                tv.setTextColor(Color.BLACK);
                convertView.setBackgroundColor(Color.parseColor("#dbdbdd"));
                return convertView;
            } else {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                RelativeLayout button = ((RelativeLayout) inflater.inflate(R.layout.events_category_button, null));
                button.setPadding(width / 20, width / 25, 0, width / 25);
                if (i != 0) {
                    ((ImageView) button.getChildAt(0)).setImageDrawable(rectangle);
                }
                TextView tv = ((TextView) button.getChildAt(1));
                tv.setTextSize(width / 60);
                tv.setText(mCategories[i]);
                tv.setTextColor(Color.BLACK);
                button.setBackgroundColor(Color.parseColor("#dbdbdd"));
                return button;
            }
        }

        //creates and returns the rounded rectangle on the left of category name.
        private LayerDrawable createRectangle(int i) {
            GradientDrawable[] layers = new GradientDrawable[2];
            layers[0] = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{mColorsFrom[i], mColorsTo[i]});
            layers[0].setShape(GradientDrawable.RECTANGLE);
            layers[0].setSize(width / 10, width / 20);
            //adding rounded corners
            layers[0].setCornerRadii(new float[]{16, 16, 16, 16, 0, 0, 0, 0});

            layers[1] = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{mColors[i], mColors[i]});
            layers[1].setShape(GradientDrawable.RECTANGLE);
            layers[1].setSize(width / 10, width / 20);
            //adding rounded corners
            layers[1].setCornerRadii(new float[]{0, 0, 0, 0, 16, 16, 16, 16});

            LayerDrawable button = new LayerDrawable(layers);
            button.setLayerInset(0, 0, 0, 0, width / 20);
            button.setLayerInset(1, 0, width / 20, 0, 0);
            return button;
        }
    }
}
