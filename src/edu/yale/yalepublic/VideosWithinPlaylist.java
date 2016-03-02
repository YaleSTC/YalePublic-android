package edu.yale.yalepublic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

public class VideosWithinPlaylist extends Activity {
    //initialize arrays to keep all the information about videos
    //we need to initialize to avoid referring to them before AsyncTask
    //fills them in!
    private String[] titls = new String[1];
    private String[] dats = new String[1];
    private Bitmap[] bitmaps = new Bitmap[1];
    //to save the ID's that we will pass to the activity that displays the vids
    private String[] videoIds;

    //make the adapter available to all functions. Will come in handy when
    //we do AsyncTask to fill in the arrays defined above
    private thumbnailAdapter adapter;
    //to keep the playlistId passed into activity
    Bundle extras;
    //for easier handling of context within adapter. Can be changed.
    Context context;
    TextView loading;
    ProgressBar spinner;
    VideoTask gettingDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //to get the passed parameters
        context = this;
        extras = getIntent().getExtras();
        if (extras == null)  // safety check
            return;
        setContentView(R.layout.activity_photo_within_album);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.photoContainer, new PlaceholderFragment()).commit();
            
        }
        loading = (TextView) findViewById(R.id.tvPhotoLoading);  // Set up spinner and text
        spinner = (ProgressBar) findViewById(R.id.pbLoading);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Log.d("VideosWithinPlaylist", "backPressed");
        if (gettingDetails != null)
            gettingDetails.cancel(true);
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
            adapter = new thumbnailAdapter(context);
            
            //create custom AsyncTask to fetch all the details from youtube
            gettingDetails = new VideoTask();
            gettingDetails.execute();
            
            //create listView from template and set the adapter. 
            ListView listView = (ListView) rootView.findViewById(R.id.listview_video_in_playlist);
            listView.setAdapter(adapter);
            
            //create a OnItemClickListener to play the video
            listView.setOnItemClickListener(new OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                        int arg2, long arg3) {
                    Intent intent = new Intent(VideosWithinPlaylist.this, VideoYoutubePlayback.class);
                    intent.putExtra("videoId", videoIds[arg2]);
                    Log.v("StartingActivityInVideosInPlaylists","Starting a new action with the parameter videoID: " + videoIds[arg2]);
                    startActivity(intent);
                }
            });
                   
            
            return rootView;
        }
    }
        public class VideoTask extends AsyncTask<Void, Void, Void> {

            // this method parses the raw data (which is a String in JSON format)
            // and extracts the titles, thumbnails and dates of the videos in a playlist
            private String getVideosFromJson(String rawData){
                JSONObject videoData;
                JSONArray playlistData;
                try {
                    videoData = new JSONObject(rawData);
                    playlistData = videoData.getJSONArray("items");
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return null;
                }
                
                titls = new String[playlistData.length()];
                bitmaps = new Bitmap[playlistData.length()];
                dats = new String[playlistData.length()];
                videoIds = new String[playlistData.length()];
                for (int i = 0; i < playlistData.length(); i++){
                    try {
                        titls[i] = playlistData.getJSONObject(i)
                                .getJSONObject("snippet")
                                .getString("title");
                        dats[i] = (playlistData.getJSONObject(i)
                                .getJSONObject("snippet")
                                .getString("publishedAt")).substring(0,9);
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
                            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
                     //safety features and avoiding errors is links redirect     
                            conn.setConnectTimeout(30000);
                            conn.setReadTimeout(30000);
                            conn.setInstanceFollowRedirects(true);
                     //setting inputstream and decoding it into a bitmap
                            InputStream is=conn.getInputStream();
                            bitmaps[i] = BitmapFactory.decodeStream(is);
                        
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return null;
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
              }
            
                }
                return "1";
            }
            
            @Override
            protected Void doInBackground(Void... params) {
                try{
                    // first we create the URI - note that the base is different than in VideoList.java
                    final String BASE_URL = "https://www.googleapis.com/youtube/v3/playlistItems?";
                    Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                            .appendQueryParameter("part", "snippet")
                            .appendQueryParameter("playlistId", extras.getString("playlistId"))
                            .appendQueryParameter("key", new DeveloperKey().DEVELOPER_KEY)
                            .appendQueryParameter("maxResults", "50")
                            .build();
                    
                    // send a GET request to the server
                    URL url = new URL(builtUri.toString());
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // read all the data
                    InputStream inputStream = urlConnection.getInputStream();                
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    String videosJsonStr = buffer.toString();
                    // we pass the data to getPlaylistsFromJson
                    //but also remember to save the playlistID's for future
                    getVideosFromJson(videosJsonStr);
                    
                    // TODO check if there are more than 50 videos in the arrays
                }
                
                catch (IOException e){
                    Log.e("URI", "uri was invalid or api request failed");
                    e.printStackTrace();
                    return null;
                }
                return null;
            }
            
            @Override
            protected void onPostExecute(Void result){
                //notify the adapter and the view that is using it to get check for new data
                //in the arrays we defined at the beginning
                adapter.notifyDataSetChanged();
                spinner.setVisibility(View.GONE);  // Hide the progress
                loading.setVisibility(View.GONE);  // Hide the progress
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
                ((TextView)((RelativeLayout) convertView).getChildAt(1)).setText(titls[position]);
                ((TextView)((RelativeLayout) convertView).getChildAt(2)).setText(dats[position]);
                return ((RelativeLayout)convertView);
            } else {
                //if not, create a new one from the template of a view using inflate
                LayoutInflater inflater = (LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                RelativeLayout thumbnail = ((RelativeLayout)inflater.inflate(R.layout.thumbnail_elements,null));
                ((ImageView)thumbnail.getChildAt(0)).setImageBitmap(bitmaps[position]);
                ((TextView)thumbnail.getChildAt(1)).setText(titls[position]);
                ((TextView)thumbnail.getChildAt(2)).setText(dats[position]);
                return thumbnail;
            } 
            
        }
        //return the number of elements. This is the reason for initializing the arrays all the way
        //at the top. Otherwise we would get nullpointerException
        @Override
        public int getCount() {
            return titls.length;
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
