package edu.yalestc.yalepublic.RSS;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.yalestc.yalepublic.R;

/**
 *  This is a custom adapter that extends ArrayAdapter. It is used in all RSS Reader classes. It lets
 *  us set 3 fields at once for an RSS feed, given an ArrayList<RssItem> data and parses it accordingly.
 */
    public class RSSAdapter extends ArrayAdapter<RssItem> {
    private Context context;
    private ArrayList<RssItem> data;
    private int layoutResourceId;

    static ArrayList<String> rssTitles = new ArrayList<String>();
    static ArrayList<String> rssDescription = new ArrayList<String>();
    static ArrayList<String> rssTimediff = new ArrayList<String>();



    public RSSAdapter(Context context, int layoutResourceId, ArrayList<RssItem> data) {
            super(context, layoutResourceId, data);
            this.context = context;
            this.data = data;
            this.layoutResourceId = layoutResourceId;
        }

    //this helper method is used to get the necessary information from a model/data class to help populate the view
    public static void setArrayLists(ArrayList<String> title, ArrayList<String> desc, ArrayList<String> time) {
        rssTitles = title;
        rssDescription = desc;
        rssTimediff = time;
    }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;     // TODO: Possibly just use convertView. Is it mutable?
            ViewHolder holder = null;

            if(row == null) {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new ViewHolder();
                holder.textView1 = (TextView) row.findViewById(R.id.tvTitle);
                holder.textView2 = (TextView) row.findViewById(R.id.tvDate);
                holder.textView3 = (TextView) row.findViewById(R.id.tvDescription);

                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }

            holder.textView1.setText(rssTitles.get(position));
            holder.textView2.setText(rssTimediff.get(position));

            //This handles the case for sport scores (since we don't want a description)
            if(rssDescription != null) {
                holder.textView3.setText(rssDescription.get(position));
            } else {
                holder.textView3.setVisibility(View.GONE);
            }

            return row;
        }

        private class ViewHolder {
            TextView textView1;
            TextView textView2;
            TextView textView3;
        }
    }

