package com.example.yalepublicandroid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;


public class MenuAdapter implements ListAdapter {

    private Context mContext;
    double size;

    public MenuAdapter(Context c, double screenWidth) {
        Log.d("relative layout", "Within Constructor");
        mContext = c;
        size = screenWidth;
    }
    
    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getItemViewType(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @SuppressLint("NewApi")
    @Override
 // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("relative layout", "very beginning of get view");
        //TextView description; 
        ImageView imageView;
        Log.d("relative layout", "before loop");
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            //description = new TextView(mContext);
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams((int)size/6,(int)size/6));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            //description = (TextView) convertView;
            imageView = (ImageView) convertView;
        }
        //Log.d("relative layout", "relative layout was created");
        /*
        RelativeLayout buttonLayout = new RelativeLayout(mContext);
        Log.d("relative layout", "before rlp");
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        Log.d("relative layout", "after rlp");
        rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        buttonLayout.setLayoutParams(rlp);
        Log.d("relative layout", "after set layout params");
        */
        //RelativeLayout buttonLayout = (RelativeLayout) mContext.getResources().getLayout(R.id.button_layout);
        // description.setText(names[position]);
        imageView.setImageResource(mThumbIds[position]);
        Log.d("relative layout", "before add view");
        //buttonLayout.addView(imageView);
        //buttonLayout.addView(description);
        Log.d("relative layout", "after add view");
        
        return imageView;
    }


    @Override
    public int getViewTypeCount() {
        // TODO Auto-generated method stub
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver arg0) {
        // TODO Auto-generated method stub
        
    }

    

    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.thumb_news_default, R.drawable.thumb_directory_default,
            R.drawable.thumb_maps_default, R.drawable.thumb_videos_default,
            R.drawable.thumb_photos_default, R.drawable.thumb_events_default,
            R.drawable.thumb_transit_default, R.drawable.thumb_athletics_default,
            R.drawable.thumb_arts_events_default
    };
    
    //private String[] names = mContext.getResources().getStringArray(R.array.menu_names);
    //private String[] names = {"1","2", "3", "4", "5", "6", "7", "8", "9"};

    @Override
    public boolean areAllItemsEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isEnabled(int arg0) {
        // TODO Auto-generated method stub
        return false;
    }
    
}
