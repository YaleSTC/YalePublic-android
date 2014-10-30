package edu.yalestc.yalepublic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class VideoList extends Activity {
	
	public enum Mode {
		VIDEO,
		PHOTO,
		EMPTY
	}
	
    // this is a class parameter so that it can be modified in the asynctask
    private ArrayAdapter<String> mVideoAdapter;	//TODO: Refactor
    //this is a string in which we store the ID's of playlists to pass them
    //into VideosWithinPlaylist
    private String[] playlistIds;				//TODO: Refactor
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);	//TODO: Refactor
        if (savedInstanceState == null) {
        	Mode mode = Mode.EMPTY; //	Default 
        	if (getIntent().getExtras()!=null) {
        		if (getIntent().getExtras().containsKey
        				(MainActivity.PHOTO_MODE_KEY))
        			mode = Mode.PHOTO;
        		else if (getIntent().getExtras().containsKey
        				(MainActivity.VIDEO_MODE_KEY))
        			mode = Mode.VIDEO;
        	}
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment(mode)).commit();
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment extends Fragment {
    	Mode fragmentMode;
        public PlaceholderFragment(Mode mode) {
        	fragmentMode = mode;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_video_list,
                    container, false);				
            
            // initialize the ArrayAdapter
            mVideoAdapter = new ArrayAdapter<String>(
                    getActivity(), R.layout.tab, R.id.tab);
            switch (fragmentMode){
            	case VIDEO:
            		populateAdapterWithVideos();
            		break;
            	case PHOTO:
            		//TODO: Populate adapter with albums
            		setTitle("Albums");
            		break;
            	case EMPTY:	//TODO: Confirm error policy
            		Log.d("WARNING","list fragment has EMPTY mode");
            }
            ListView listView = (ListView) rootView.findViewById(R.id.listview_video);
            listView.setAdapter(mVideoAdapter);
            //set OnItemClickListener to open up a new activity in which we get 
            //all the videos listed
            listView.setOnItemClickListener(new OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                        int arg2, long arg3) {
                    //redirect to new activity displaying all videos
                    Intent showThem = new Intent(VideoList.this, VideosWithinPlaylist.class);
                    showThem.putExtra("playlistId", playlistIds[arg2]);
                    //For Debug purposes - show what is the playlistID
                    Log.d("StartingActivityInVideoList",playlistIds[arg2]);
                    startActivity(showThem);
                }
            });
            return rootView;
        }

		private void populateAdapterWithVideos() {
			// create an asynctask that fetches the playlist titles
            VideoTask videoList = new VideoTask();
            videoList.execute();
		}
    }
    
    public class VideoTask extends AsyncTask<Void, Void, String[]> {

        // this method parses the raw data (which is a String in JSON format)
        // and extracts the titles of the playlists
        private String[] getPlaylistsFromJson(String rawData){
            JSONObject videoData;
            try {
                videoData = new JSONObject(rawData);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
            JSONArray playlistData;
            try {
                playlistData = videoData.getJSONArray("items");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
            
            String[] allPlaylists = new String[playlistData.length()];
            //we need to remember playlistIDs for future processing!
            playlistIds = new String[playlistData.length()];
            for (int i = 0; i < playlistData.length(); i++){
                try {
                    allPlaylists[i] = playlistData.getJSONObject(i)
                            .getJSONObject("snippet")
                            .getString("title");
                    playlistIds[i] = playlistData.getJSONObject(i)
                            .getString("id");
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return null;
                }
            }
            return allPlaylists;
        }
        
        @Override
        protected String[] doInBackground(Void... params) {
            try{
                // first we create the URI
                final String BASE_URL = "https://www.googleapis.com/youtube/v3/playlists?";
                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter("part", "snippet")
                        .appendQueryParameter("channelId", "UC4EY_qnSeAP1xGsh61eOoJA")
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
                return getPlaylistsFromJson(videosJsonStr);
                
                // TODO check if there are more than 50 videos in the arrays
            }
            
            catch (IOException e){
                Log.e("URI", "uri was invalid or api request failed");
                e.printStackTrace();
                return null;
            }
        }
        
        @Override
        protected void onPostExecute(String[] result){
            // we need to use result in our ArrayAdapter
            List<String> videos = new ArrayList<String>(Arrays.asList(result));
            mVideoAdapter.addAll(videos); 
        }
    }
    
    
}
