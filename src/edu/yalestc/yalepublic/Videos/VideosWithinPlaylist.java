package edu.yalestc.yalepublic.Videos;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import android.content.Context;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import edu.yalestc.yalepublic.DeveloperKey;
import edu.yalestc.yalepublic.JSONReader;
import edu.yalestc.yalepublic.R;


//**
//Created by Stan Swidwinski
//**

public class VideosWithinPlaylist extends Activity {
    //initialize arrays to keep all the information about videos
    //we need to initialize to avoid referring to them before AsyncTask
    //fills them in!
    private String[] titles;
    private String[] dates;
    private Bitmap[] bitmaps;
    private String rawData;
    //to save the ID's that we will pass to the activity that displays the vids
    private String[] videoIds;
    //make the adapter available to all functions. Will come in handy when 
    //we do AsyncTask to fill in the arrays defined above
    private thumbnailAdapter adapter;
    //to keep the playlistId passed into activity
    Bundle extras;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //to get the passed parameters
        extras = getIntent().getExtras();
        setContentView(R.layout.activity_video_within_list);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container1, new PlaceholderFragment()).commit();
            
        }
}
    //definition of our custom fragment.
    public class PlaceholderFragment extends Fragment {
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_video_within_list,
                    container, false);
            //initialize the adapter
            adapter = new thumbnailAdapter(getActivity());

            // create an asynctask that fetches the playlist titles. It should speak for itself.
            //Just note that in constructor we give in the base url (WITHOUT "?" at the end).
            JSONReader scrapeData = new JSONReader("https://www.googleapis.com/youtube/v3/playlistItems", getActivity());
            scrapeData.addParams(new Pair<String, String>("part", "snippet"));
            scrapeData.addParams(new Pair<String, String>("playlistId", extras.getString("playlistId")));
            scrapeData.addParams(new Pair<String, String>("key", new DeveloperKey().DEVELOPER_KEY));
            scrapeData.addParams(new Pair<String, String>("maxResults", "50"));

            //retrieve result while checking for errors (this does stall execution of main thread!)
            try {
                rawData = scrapeData.execute().get();
                //if we fail to receive data, not to fail we just toast the user and stay in the current menu
                if(rawData == null){
                    Toast toast = new Toast(getActivity());
                    toast = Toast.makeText(getActivity(), "You need internet connection to view the content!", Toast.LENGTH_LONG);
                    toast.show();
                    finish();
                    return null;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            //download the photos and get title and dates for videos (downloading thumbnails requires AsyncTask!)
            //note: any failure will NOT crash the app, but will result in a blank view. Only slightly better.

            Pair<Bitmap[], ArrayList<String[]>> videosInfo;
            try {
                videosInfo = new ParseVideosWithinPlaylist(rawData, getActivity()).execute().get();
                if(videosInfo == null){
                    Toast toast = new Toast(getActivity());
                    toast = Toast.makeText(getActivity(), "You need internet connection to view the content!", Toast.LENGTH_LONG);
                    toast.show();
                    finish();
                    return null;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            } catch (ExecutionException e) {
                e.printStackTrace();
                return null;
            }
            if (videosInfo != null) {
                bitmaps = videosInfo.first;
                titles = videosInfo.second.get(0);
                dates = videosInfo.second.get(1);
                videoIds = videosInfo.second.get(2);
                //create listView from template and set the adapter.
                ListView listView = (ListView) rootView.findViewById(R.id.listview_video_in_playlist);
                listView.setAdapter(adapter);

                //create a OnItemClickListener to play the video
                listView.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {
                        Intent intent = new Intent(VideosWithinPlaylist.this, VideoYoutubePlayback.class);
                        intent.putExtra("videoId", videoIds[arg2]);
                        Log.v("StartingActivityInVideosInPlaylists", "Starting a new action with the parameter videoID: " + videoIds[arg2]);
                        startActivity(intent);
                    }
                });


                return rootView;
            } else {
                return null;
            }
        }

    }


    //Our custom Adapter
    public class thumbnailAdapter extends BaseAdapter{
        Context mContext;
            thumbnailAdapter(Context context) {
                mContext = context;
            }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //if we can reuse a view, do so.
            if(convertView != null){
                ((ImageView)((RelativeLayout) convertView).getChildAt(0)).setImageBitmap(bitmaps[position]);
                ((TextView)((RelativeLayout) convertView).getChildAt(1)).setText(titles[position]);
                ((TextView)((RelativeLayout) convertView).getChildAt(2)).setText(dates[position]);
                return ((RelativeLayout)convertView);
            } else {
                //if not, create a new one from the template of a view using inflate
                LayoutInflater inflater = (LayoutInflater)mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                RelativeLayout thumbnail = ((RelativeLayout)inflater.inflate(R.layout.thumbnail_elements,null));
                ((ImageView)thumbnail.getChildAt(0)).setImageBitmap(bitmaps[position]);
                ((TextView)thumbnail.getChildAt(1)).setText(titles[position]);
                ((TextView)thumbnail.getChildAt(2)).setText(dates[position]);
                return thumbnail;
            } 
            
        }
        //return the number of elements. This is the reason for initializing the arrays all the way
        //at the top. Otherwise we would get nullpointerException
        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }
    }
    
    
}
