package edu.yale.yalepublic.Videos;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;


import edu.yale.yalepublic.R;

/**
 * Created by Stan Swidwinski on 1/10/15.
 */
public class PlaylistAdapter extends BaseAdapter {

    private DisplayMetrics display;
    Context mContext;
    private int height;
    private int width;
    private String[] mPlaylists;

    PlaylistAdapter (Context context, String[] playlists){
        mContext = context;
        mPlaylists = playlists;
        display = context.getResources().getDisplayMetrics();
        height = display.heightPixels;
        width = display.widthPixels;
    }

    @Override
    public int getCount() {
        return mPlaylists.length;
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
            ((TextView) ((RelativeLayout) convertView).getChildAt(0)).setText(mPlaylists[i]);
            convertView.setBackgroundColor(Color.parseColor("#dbdbdd"));
            return convertView;
        } else {
            RelativeLayout playlist = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.playlists_list_element, null);
            ((TextView) ((RelativeLayout) playlist).getChildAt(0)).setText(mPlaylists[i]);
            playlist.setBackgroundColor(Color.parseColor("#dbdbdd"));
            return playlist;
        }
    }
}