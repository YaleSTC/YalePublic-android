package edu.yalestc.yalepublic.Videos;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import edu.yalestc.yalepublic.DeveloperKey;
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
            JSONReader scrapeData = new JSONReader("https://www.googleapis.com/youtube/v3/playlistItems");
            scrapeData.addParams(new Pair<String, String>("part", "snippet"));
            scrapeData.addParams(new Pair<String, String>("playlistId", extras.getString("playlistId")));
            scrapeData.addParams(new Pair<String, String>("key", new DeveloperKey().DEVELOPER_KEY));
            scrapeData.addParams(new Pair<String, String>("maxResults", "50"));

            //retrieve result while checking for errors (this does stall execution of main thread!)
            try {
                rawData = scrapeData.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            //download the photos and get title and dates for videos (downloading thumbnails requires AsyncTask!)
            //note: any failure will NOT crash the app, but will result in a blank view. Only slightly better.
            try {
                Boolean success = new getInformationFromJSON().execute().get();
                if (success) {
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
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            } catch (ExecutionException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    //Async task required to download the thumbnails
    //parsing JSON is also here.
class getInformationFromJSON extends AsyncTask<Void,Void,Boolean> {
    private boolean getVideosFromJson(String rawData) {
        JSONObject videoData;
        try {
            videoData = new JSONObject(rawData);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        JSONArray playlistData;
        try {
            playlistData = videoData.getJSONArray("items");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

        titles = new String[playlistData.length()];
        bitmaps = new Bitmap[playlistData.length()];
        dates = new String[playlistData.length()];
        videoIds = new String[playlistData.length()];
        for (int i = 0; i < playlistData.length(); i++) {
            try {
                titles[i] = playlistData.getJSONObject(i)
                        .getJSONObject("snippet")
                        .getString("title");
                dates[i] = (playlistData.getJSONObject(i)
                        .getJSONObject("snippet")
                        .getString("publishedAt")).substring(0, 10);
                videoIds[i] = playlistData.getJSONObject(i)
                        .getJSONObject("snippet")
                        .getJSONObject("resourceId")
                        .getString("videoId");
                //Here we actually download the thumbnail using URL obtained from JSONObject
                try {
                    URL imageUrl = new URL(playlistData.getJSONObject(i)
                            .getJSONObject("snippet")
                            .getJSONObject("thumbnails")
                            .getJSONObject("medium")
                            .getString("url"));
                    //connect to given server
                    HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                    //safety features and avoiding errors is links redirect
                    conn.setConnectTimeout(30000);
                    conn.setReadTimeout(30000);
                    conn.setInstanceFollowRedirects(true);
                    //setting inputstream and decoding it into a bitmap
                    InputStream is = conn.getInputStream();
                    bitmaps[i] = BitmapFactory.decodeStream(is);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        }
        return true;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return getVideosFromJson(rawData);
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
