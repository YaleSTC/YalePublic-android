package edu.yale.yalepublic;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PlaceholderFragment extends Fragment {

    public static final String PHOTO_ID_KEY = "playlistId";
    public enum Mode {
        VIDEO,
        PHOTO,
        EMPTY
    }
    // this is a class parameter so that it can be modified in the asynctask
    private ArrayAdapter<String> mVideoAdapter;    //TODO: Refactor
    //this is a string in which we store the ID's of playlists to pass them
    //into VideosWithinPlaylist
    //private String[] playlistIds;                //TODO: Refactor
    private Mode mode;
    TextView loading;
    ProgressBar spinner;
    VideoTask videoList;

    /**
     * A placeholder fragment containing a simple view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo_list,
                container, false);

        Bundle extras = getArguments();

        mode = Mode.EMPTY; //    Default
        if (extras != null) {
            Log.d("plfrag", "extras");
            if (extras.containsKey
                    (MainActivity.PHOTO_MODE_KEY)) {
                mode = Mode.PHOTO;
                getActivity().setTitle("Albums");
            }
            else if (extras.containsKey
                    (MainActivity.VIDEO_MODE_KEY)) {
                mode = Mode.VIDEO;
                getActivity().setTitle("Videos");
            }
        } else {
            Log.d("plfrag", "no extras");
        }

        loading = (TextView) rootView.findViewById(R.id.tvPhotoLoading);  // Set up spinner and text
        spinner = (ProgressBar) rootView.findViewById(R.id.pbLoading);

        // initialize the ArrayAdapter
        mVideoAdapter = new ArrayAdapter<String>(
                getActivity(), R.layout.tab, R.id.tab);
        // create an asynctask that fetches the playlist titles
        videoList = new VideoTask();
        String result = null;
        try {
            Log.d("placeholderfrag", "execute http");
            result = videoList.execute().get();

            String[] newres = getPlaylistsFromJson(result, false);

            // we need to use result in our ArrayAdapter; adds all of the resulting values.
            //spinner.setVisibility(View.GONE);
            //loading.setVisibility(View.GONE);  // Hide the progress
            if (newres != null) {
                List<String> videos = new ArrayList<String>(Arrays.asList(newres));
                mVideoAdapter.addAll(videos);
            } else {
                Toast.makeText(getActivity(), "You need internet connection to view the content!", Toast.LENGTH_LONG).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        ListView listView = (ListView) rootView.findViewById(R.id.listview_photo);
        listView.setAdapter(mVideoAdapter);
        //set OnItemClickListener to open up a new activity in which we get
        //all the videos listed
        final String finalResult = result;
        listView.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                //redirect to new activity displaying all videos
                String[] playlistIds = getPlaylistsFromJson(finalResult, true);

                if (mode == Mode.VIDEO) {
                    Intent showThem = new Intent(getActivity(), VideosWithinPlaylist.class);
                    showThem.putExtra(PHOTO_ID_KEY, playlistIds[arg2]);
                    //For Debug purposes - show what is the playlistID
                    Log.d("StartingVideoList", playlistIds[arg2]);
                    startActivity(showThem);
                }
                else if (mode == Mode.PHOTO) {
                    Intent showThem = new Intent(getActivity(), PhotosWithinAlbum.class);
                    showThem.putExtra("playlistId", playlistIds[arg2]);
                    //For Debug purposes - show what is the playlistID
                    Log.d("StartingVideoList", playlistIds[arg2]);
                    startActivity(showThem);
                }
            }
        });

        return rootView;

    }

    private void populateAdapterWithVideos() {  // TODO: Unused, called in onCreateView
        // create an asynctask that fetches the playlist titles
        VideoTask videoList = new VideoTask();
        videoList.execute();
    }


    private String[] getPlaylistsFromJson(String rawData, boolean plIDs){
        if (rawData == null)
            return null;
        String[] playlistIds;                //TODO: Refactor

        JSONObject videoData;
        JSONArray playlistData = null;
        try {
            videoData = new JSONObject(rawData);
            switch(mode){
                case VIDEO:
                    playlistData = videoData.getJSONArray("items");
                    break;
                case PHOTO:
                    playlistData = videoData.getJSONObject("photosets")
                            .getJSONArray("photoset");
                    break;
                default:
                    break;

            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        String[] allPlaylists = new String[playlistData.length()];
        //we need to remember playlistIDs for future processing!
        int playlistDataLength = playlistData.length();
        playlistIds = new String[playlistDataLength];
        for (int i = 0; i < playlistDataLength; i++){
            try {
                switch(mode){
                    case VIDEO:
                        allPlaylists[i] = playlistData.getJSONObject(i)
                                .getJSONObject("snippet")
                                .getString("title");
                        break;
                    case PHOTO:
                        allPlaylists[i] = playlistData.getJSONObject(i)
                                .getJSONObject("title")
                                .getString("_content");
                        break;
                    default:
                        break;
                }
                playlistIds[i] = playlistData.getJSONObject(i)
                        .getString("id");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }
        if (plIDs)
            return playlistIds;
        else
            return allPlaylists;
    }

    // Parses the raw data (which is a String in JSON format) and extracts the titles of
    // the photo albums to display in a ListView.
    public class VideoTask extends AsyncTask<Void, Void, String> {
        
        @Override
        protected String doInBackground(Void... params) {
            Uri builtUri = null;
            try{
                // first we create the URI
                switch(mode){
                case VIDEO:
                    builtUri = getVideoAPIRequestUri();
                    break;
                case PHOTO:
                    builtUri = getPhotoAPIRequestUri();
                    break;
                default:
                    return null;  // #135, this should never happen. Extras not passed correctly
                }
                
                // send a GET request to the server
                if (builtUri == null)
                    return null;
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
                return videosJsonStr; //getPlaylistsFromJson(videosJsonStr);
                
                // TODO check if there are more than 50 videos in the arrays (not for photos)
            }
            
            catch (IOException e){
                Log.e("URI", "uri was invalid or api request failed");
                e.printStackTrace();
                return null;
            }
        }

        // Returns a URI in the form (https://api.flickr.com/...) for use in polling the API
        // to get the list of albums from the Yale Flickr account.
        private Uri getPhotoAPIRequestUri() {
            final String USER_ID = "12208415@N08";    //Yale flickr user id
            final String BASE_URL = "https://api.flickr.com/services/rest/?";
            //TODO: extract api key and secret
            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter("method", "flickr.photosets.getList")
                        .appendQueryParameter("api_key", new DeveloperKey().FLICKR_API_KEY)
                        .appendQueryParameter("user_id", USER_ID) 
                        .appendQueryParameter("format", "json")
                        .appendQueryParameter("nojsoncallback", "1")
                        .build();
            return builtUri;
        }

        // Returns a URI in the form (https://googleapis.com/youtube/...) for use in polling the API
        // to get the list of playlists from the Yale Youtube account.
        private Uri getVideoAPIRequestUri() {
            final String BASE_URL = "https://www.googleapis.com/youtube/v3/playlists?";
            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter("part", "snippet")
                    .appendQueryParameter("channelId", "UC4EY_qnSeAP1xGsh61eOoJA")
                    .appendQueryParameter("key", new DeveloperKey().DEVELOPER_KEY)
                    .appendQueryParameter("maxResults", "50")
                    .build();
            return builtUri;
        }

    }
}
