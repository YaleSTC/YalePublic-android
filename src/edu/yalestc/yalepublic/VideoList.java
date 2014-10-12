package edu.yalestc.yalepublic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.os.Build;

public class VideoList extends Activity {
    private ArrayAdapter<String> mVideoAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_video_list,
                    container, false);
            
            
            
            
 
            VideoTask videoList = new VideoTask();
            videoList.execute();
           
            mVideoAdapter = new ArrayAdapter<String>(
                    getActivity(), R.layout.tab, R.id.tab);
            
            ListView listView = (ListView) rootView.findViewById(R.id.listview_video);
            listView.setAdapter(mVideoAdapter);
            
            return rootView;
        }
    }
    
    public class VideoTask extends AsyncTask<Void, Void, String[]> {

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
            for (int i = 0; i < playlistData.length(); i++){
                try {
                    Log.v("JSON parsing", playlistData.getJSONObject(i).getJSONObject("snippet").getString("title"));
                    allPlaylists[i] = playlistData.getJSONObject(i).getJSONObject("snippet").getString("title");
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
            // TODO Auto-generated method stub
            try{
                
                final String BASE_URL = "https://www.googleapis.com/youtube/v3/playlists?";
                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter("part", "snippet")
                        .appendQueryParameter("channelId", "UC4EY_qnSeAP1xGsh61eOoJA")
                        .appendQueryParameter("key", new DeveloperKey().DEVELOPER_KEY)
                        .appendQueryParameter("maxResults", "50")
                        .build();
                
                
                URL url = new URL(builtUri.toString());
                
                
                Log.v("URI", "Built URI " + builtUri.toString());
                
                
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                
                Log.v("URI", "connected");
                
                int status = urlConnection.getResponseCode();
                
                InputStream inputStream = urlConnection.getInputStream();
                
                StringBuffer buffer = new StringBuffer();
                
                Log.v("URI", "input stream successful");
                
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

                Log.v("URI", "Forecast string: " + videosJsonStr);
                return getPlaylistsFromJson(videosJsonStr);
                
            }
            
            catch (IOException e){
                Log.e("URI", "uri was invalid or api request failed");
                e.printStackTrace();
                return null;
            }
        }
        
        @Override
        protected void onPostExecute(String[] result){
            List<String> videos = new ArrayList<String>(Arrays.asList(result));
            mVideoAdapter.addAll(videos); 
        }
        

    }

}
