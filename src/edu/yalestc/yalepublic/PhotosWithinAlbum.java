package edu.yalestc.yalepublic;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.yalestc.yalepublic.VideoList.Mode;
import edu.yalestc.yalepublic.VideosWithinPlaylist.thumbnailAdapter;
import android.R.integer;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//PhotosWithinAlbum tries to mirror the structure of
//VideosWithinPlaylist as far as possible for consistency
//TODO: It may be worth while to create a single parent
//Class for both of these.

public class PhotosWithinAlbum extends Activity {

	//TODO: Pass as parameters to AlbumTask asynctask
	private thumbnailAdapter adapter;
	private String albumId;
	private String[] titls = new String[1];
	private Bitmap[] bitmaps = new Bitmap[1];
    private String[] photoIds;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getIntent().getExtras()!=null && 
			getIntent().getExtras().containsKey(VideoList.PHOTO_ID_KEY)) { //TODO:Pull album title
    		albumId = getIntent().getStringExtra(VideoList.PHOTO_ID_KEY);
    		}
		setContentView(R.layout.activity_photo_within_album);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.photoContainer, new PlaceholderFragment()).commit();
            
        }
	}
	 public class PlaceholderFragment extends Fragment {
	        public PlaceholderFragment() {
	        }

	        @Override
	        public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                Bundle savedInstanceState) {
	            View rootView = inflater.inflate(R.layout.fragment_photo_within_album,
	                    container, false);            
//	            //initialize the adapter
//	            adapter = new thumbnailAdapter(context);
//	            
//	            //create custom AsyncTask to fetch all the details from youtube
//	            VideoTask gettingDetails = new VideoTask();
//	            gettingDetails.execute();
//	            
//	            //create listView from template and set the adapter. 
//	            ListView listView = (ListView) rootView.findViewById(R.id.listview_video_in_playlist);
//	            listView.setAdapter(adapter);
//	            
//	            //create a OnItemClickListener to play the video
//	            listView.setOnItemClickListener(new OnItemClickListener(){
//
//	                @Override
//	                public void onItemClick(AdapterView<?> arg0, View arg1,
//	                        int arg2, long arg3) {
//	                    Intent intent = new Intent(VideosWithinPlaylist.this, VideoYoutubePlayback.class);
//	                    intent.putExtra("videoId", videoIds[arg2]);
//	                    Log.v("StartingActivityInVideosInPlaylists","Starting a new action with the parameter videoID: " + videoIds[arg2]);
//	                    startActivity(intent);
//	                }
//	            });
//	                   
	            
	            return rootView;
	        }
//	        public static final int GRIDVIEW_SPACING = 3;
//	        public static final int GRIDVIEW_COLUMN_WIDTH = 75;
//	        private void initializeComponents() {
//	        	Display display = getWindowManager().getDefaultDisplay();
//	            Point outSizePoint; 
//	            display.getSize(outSizePoint);
//	        	float spacing = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
//	                    GRIDVIEW_SPACING, getResources().getDisplayMetrics());
//	            gridView.setNumColumns(outSizePoint.x / GRIDVIEW_COLUMN_WIDTH);
//	            gridView.setPadding((int) spacing, (int) spacing, (int) spacing, (int) spacing);
//	            gridView.setVerticalSpacing((int) spacing);
//	            gridView.setHorizontalSpacing((int) spacing);
//	        }
	    }
	 	public class AlbumTask extends AsyncTask<Void, Void, Void> {

			private String getPhotosFromJson(String rawData) {
				JSONObject albumData;
                try {
                    albumData = new JSONObject(rawData);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return null;
                }
                JSONArray photolistData;
                try {
                    photolistData = albumData.getJSONObject("photoset")
    						.getJSONArray("photo");
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return null;
                }
                
                titls = new String[photolistData.length()];
                bitmaps = new Bitmap[photolistData.length()];
   
                photoIds = new String[photolistData.length()];
                for (int i = 0; i < photolistData.length(); i++){
                    try {
                        titls[i] = photolistData.getJSONObject(i)
                                .getString("title");
                        photoIds[i] = photolistData.getJSONObject(i)
                                .getString("id");
                    //Here we actually download the thumbnail using URL obtained from JSONObject
                        try {
                            URL imageUrl = new URL(photolistData.getJSONObject(i)
                                    .getString("url_t"));
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
                return "1"; //TODO: Why?
			}
			@Override
			protected Void doInBackground(Void... params) {
				try {
					//Send GET request to the server
					URL url = new URL(getPhotosAPIRequestUri().toString());
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
	                urlConnection.setRequestMethod("GET");
	                urlConnection.connect();
	                
	                //read all the data
	                InputStream inputStream = urlConnection.getInputStream();                
                    StringBuffer buffer = new StringBuffer();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n"); //Debugging ease
                    }

                    if (buffer.length() == 0) {
                        return null;
                    }
                    String photosJsonStr = buffer.toString();
                    getPhotosFromJson(photosJsonStr);
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
				} catch (Exception e) {
					Log.e("URI", "uri was invalid or api request failed");
					e.printStackTrace();
				}
				return null;
			}
            @Override
            protected void onPostExecute(Void result){
                //notify the adapter and the view that is using it to get check for new data
                //in the arrays we defined at the beginning
                adapter.notifyDataSetChanged();
            }
			private Uri getPhotosAPIRequestUri() {
				final String USER_ID = "12208415@N08";	//Yale flickr user id
				final String BASE_URL = "https://api.flickr.com/services/rest/?";
				//TODO: extract api key and secret
				Uri builtUri = Uri.parse(BASE_URL).buildUpon()
				            .appendQueryParameter("method", "flickr.photosets.getPhotos")
				            .appendQueryParameter("api_key", new DeveloperKey().FLICKR_API_KEY)
				            .appendQueryParameter("photoset_id", albumId) 
				            .appendQueryParameter("format", "json")
				            .appendQueryParameter("nojsoncallback", "1")
				            .build();
				return builtUri;
			}
	 		
	 	}
}
