package edu.yalestc.yalepublic.Events;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
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
            if(convertView != null){
                ((ImageView)((RelativeLayout) convertView).getChildAt(0)).setImageBitmap(); //ADD THE TRUE COLOR / RECTANGLE
                ((TextView)((RelativeLayout) convertView).getChildAt(1)).setText(categories[i]);
                return ((RelativeLayout)convertView);
            } else {
                //if not, create a new one from the template of a view using inflate
                LayoutInflater inflater = (LayoutInflater)mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                RelativeLayout button = ((RelativeLayout)inflater.inflate(R.layout.thumbnail_elements,null));
                ((ImageView)button.getChildAt(0)).setImageBitmap();//ADD THE TRUE COLOR / RECTANGLE
                ((TextView)button.getChildAt(1)).setText(categories[i]);
                return button;
            }
        }
    }
}
