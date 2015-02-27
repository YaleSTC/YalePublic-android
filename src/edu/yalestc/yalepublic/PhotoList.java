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

import org.jinstagram.Instagram;
import org.jinstagram.auth.InstagramAuthService;
import org.jinstagram.auth.model.Token;
import org.jinstagram.auth.model.Verifier;
import org.jinstagram.auth.oauth.InstagramService;
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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PhotoList extends Activity {

    public static final String PHOTO_ID_KEY = "playlistId";
    public static final String CALLBACK_URL = "http://www.yale.edu/";
    public enum Mode {
        VIDEO,
        PHOTO,
        EMPTY
    }
    // this is a class parameter so that it can be modified in the asynctask
    private ArrayAdapter<String> mVideoAdapter;    //TODO: Refactor
    //this is a string in which we store the ID's of playlists to pass them
    //into VideosWithinPlaylist
    private String[] playlistIds;                //TODO: Refactor
    private Mode mode;
    TextView loading;
    ProgressBar spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mode = Mode.EMPTY; //    Default 
        setContentView(R.layout.activity_photo_within_album);
        if (savedInstanceState == null) {
            if (getIntent().getExtras() != null) {
                if (getIntent().getExtras().containsKey
                        (MainActivity.PHOTO_MODE_KEY)) {
                    mode = Mode.PHOTO;
                    setTitle("Albums");
                } else if (getIntent().getExtras().containsKey
                        (MainActivity.VIDEO_MODE_KEY)) {
                    mode = Mode.VIDEO;
                    setTitle("Videos");
                }
            }
            getFragmentManager().beginTransaction()
                    .add(R.id.photoContainer, new PlaceholderFragment()).commit();
        }
        loading = (TextView) findViewById(R.id.tvPhotoLoading);  // Set up spinner and text
        spinner = (ProgressBar) findViewById(R.id.pbLoading);


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
            View rootView = inflater.inflate(R.layout.fragment_photo_list,
                    container, false);                
            
            // initialize the ArrayAdapter
            mVideoAdapter = new ArrayAdapter<String>(
                    getActivity(), R.layout.tab, R.id.tab);
            Log.d("Auth","started");
            PhotoAuth photoauth = new PhotoAuth();
            photoauth.AuthorizeUser();

            ListView listView = (ListView) rootView.findViewById(R.id.listview_photo);
            listView.setAdapter(mVideoAdapter);
            //set OnItemClickListener to open up a new activity in which we get 
            //all the videos listed
            listView.setOnItemClickListener(new OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                        int arg2, long arg3) {
                    //redirect to new activity displaying all videos
                    if (mode == Mode.VIDEO) {
                        Intent showThem = new Intent(PhotoList.this, VideosWithinPlaylist.class);
                        showThem.putExtra(PHOTO_ID_KEY, playlistIds[arg2]);
                        //For Debug purposes - show what is the playlistID
                        Log.d("StartingActivityInVideoList",playlistIds[arg2]);
                        startActivity(showThem);
                    }
                    else if (mode == Mode.PHOTO) {
                        Intent showThem = new Intent(PhotoList.this, PhotosWithinAlbum.class);
                        showThem.putExtra("playlistId", playlistIds[arg2]);
                        //For Debug purposes - show what is the playlistID
                        Log.d("StartingActivityInVideoList",playlistIds[arg2]);
                        startActivity(showThem);
                    }
                }
            });
            return rootView;
        }
    }
    public class PhotoAuth{
        private static final String ACCESS_TOKEN_KEY ="Instagram Access Token";
        //build the base url with client_id, client_secret and redirect url
        private final InstagramService service = new InstagramAuthService()
                .apiKey(DeveloperKey.INSTAGRAM_CLIENT_ID)
                .apiSecret(DeveloperKey.INSTAGRAM_CLIENT_SECRET)
                .callback(CALLBACK_URL)
                .scope("likes")
                .build();
        //generate authorization url
        private final Token EMPTY_TOKEN = null;

        public void AuthorizeUser() {
            String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
            Log.d("Auth",authorizationUrl);
            //get Instagram code from authorization url
            WebView webview = new WebView(getApplicationContext());
            webview.setWebViewClient(new WebViewClient() {
                String code = null;
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if(url.startsWith(CALLBACK_URL)) {
                        Log.d("Auth",url);
                        if (url.contains("code=")) {
                            code = url.split("=")[1];
                            Log.d("AuthCode", code);
                        }
                        else if(url.contains("error=access_denied")) {
                            Log.d("Auth", "Access denied");
                        }
                        //execute photoTask
                        PhotoTask photoTask = new PhotoTask();
                        photoTask.execute(code);
                        //set content View back to our album
                        setContentView(R.layout.activity_photo_within_album);
                        //Do not load redirect url
                        return true;
                    }
                    //load url
                    return super.shouldOverrideUrlLoading(view,url);
                }

            });
            Log.d("Auth","loading webview");
            setContentView(webview);
            webview.loadUrl(authorizationUrl);
        }
        public class PhotoTask extends AsyncTask<String, Void, String[]> {
            private Token accessToken = EMPTY_TOKEN;
            @Override
            protected String[] doInBackground(String... params) {
                try {
                    Verifier verifier = new Verifier(params[0]);
                    accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
                    Log.d("Auth", "accessToken");
                } catch (Exception e) {
                    Log.d("Auth", "Access Token not received");
                }
                //TODO: Handle no accessToken : use client id instead.
                Instagram instagram = new Instagram(accessToken);
                try {
                    Uri builtUri = getPhotoAPIRequestUri();
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
                    String photosJSonStr = buffer.toString();
                    // we pass the data to getPlaylistsFromJson
                    //but also remember to save the playlistID's for future
                    return getPlaylistsFromJson(photosJSonStr);


                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return null;
                }
            }
            private Uri getPhotoAPIRequestUri() {
                final String USER_ID = "1701574";    //Yale instagram user id
                final String BASE_URL = "https://api.instagram.com/v1/users/"
                                         + USER_ID + "/media/recent?";
                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter("client_id", DeveloperKey.INSTAGRAM_CLIENT_ID)
                        .build();
                return builtUri;
            }
            private String[] getPlaylistsFromJson(String rawData){

                JSONObject photoData;
                JSONArray playlistData = null;
                try {
                    photoData = new JSONObject(rawData);
                    playlistData = photoData.getJSONArray("data");
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
                                        .getString("link");
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
                return allPlaylists;
            }
            @Override
            protected void onPostExecute(String[] result){
                //if null, do nothing
                if (result==null)
                    return;
                // we need to use result in our ArrayAdapter; adds all of the resulting values.
                spinner.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);  // Hide the progress
                List<String> videos = new ArrayList<String>(Arrays.asList(result));
                mVideoAdapter.addAll(videos);
            }

        }
    }

}
