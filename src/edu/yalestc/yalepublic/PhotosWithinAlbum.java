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

import android.R.integer;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//PhotosWithinAlbum tries to mirror the structure of
//VideosWithinPlaylist as far as possible for consistency
//TODO: It may be worth while to create a single parent
//Class for both of these.

public class PhotosWithinAlbum extends Activity {

    //TODO: Pass as parameters to AlbumTask asynctask
    public static final String TITLE_KEY ="title";
    private ImageThumbnailAdapter adapter;
    private String albumId;
    private String[] titls = new String[1];
    private Bitmap[] bitmaps = new Bitmap[1];
    private String[] photoIds;
    TextView loading;
    ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getExtras() != null &&
            getIntent().getExtras().containsKey(VideoList.PHOTO_ID_KEY)) { //TODO:Pull album title
            albumId = getIntent().getStringExtra(VideoList.PHOTO_ID_KEY);
            }
        setContentView(R.layout.activity_photo_within_album);
        if (savedInstanceState == null) {
            //load fragment
            getFragmentManager().beginTransaction()
                    .add(R.id.photoContainer, new PlaceholderFragment()).commit();
            
        }
        loading = (TextView) findViewById(R.id.tvPhotoLoading);  // Set up spinner and text
        spinner = (ProgressBar) findViewById(R.id.pbLoading);
    }

     public class PlaceholderFragment extends Fragment {
            public PlaceholderFragment() {
            }

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                    Bundle savedInstanceState) {
                View rootView = inflater.inflate(R.layout.fragment_photo_within_album,
                        container, false);            
                //initialize the adapter
                adapter = new ImageThumbnailAdapter(PhotosWithinAlbum.this);
                
                //create custom AsyncTask to fetch all the details from youtube
                AlbumTask gettingDetails = new AlbumTask();
                gettingDetails.execute();
                
                //create listView from template and set the adapter. 
                GridView gridview = (GridView) rootView.findViewById(R.id.imageGridView);
                gridview.setAdapter(adapter);
                
                //create a OnItemClickListener to load photo
                gridview.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
                        Intent intent = new Intent(PhotosWithinAlbum.this, ImageActivity.class);
                        intent.putExtra(VideoList.PHOTO_ID_KEY,photoIds[position]);
                        intent.putExtra(TITLE_KEY,titls[position]);
                        startActivity(intent);
                    }
                });
                
                return rootView;
            }
        }
     
         public class AlbumTask extends AsyncTask<Void, Integer, Void> {

            private String getPhotosFromJson(String rawData) {
                JSONObject albumData;
                try {
                    albumData = new JSONObject(rawData);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return null;
                }
                Log.d("json", albumData.toString());
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
                int bytecount=0;
                for (int i = 0; i < photolistData.length(); i++){
                    try {
                        titls[i] = photolistData.getJSONObject(i)
                                .getString("title");
                        photoIds[i] = photolistData.getJSONObject(i)
                                .getString("id");
                        //Here we actually download the thumbnail using URL obtained from JSONObject
                        try {
                            URL imageUrl = new URL(photolistData.getJSONObject(i)
                                    .getString("url_sq"));
                            Log.d("json",photolistData.getJSONObject(i)
                                    .getString("url_sq"));
                            //connect to given server
                            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
                            //safety features and avoiding errors is links redirect
                            conn.setConnectTimeout(30000);
                            conn.setReadTimeout(30000);
                            conn.setInstanceFollowRedirects(true);
                            //setting inputstream and decoding it into a bitmap
                            InputStream is = conn.getInputStream();
                            bitmaps[i] = BitmapFactory.decodeStream(is);
                            bytecount = bytecount + bitmaps[i].getByteCount();
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
                Log.d("json",Integer.toString(bytecount)); //TODO: OMG PER PIXEL BYTECOUNT IS TOOO DAMN HIGH
                return "1"; //TODO: Why?
            }
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    //Send GET request to the server to get the list of photos
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
                    //getPhotosFromJson(photosJsonStr);


                    // TODO: Copied the functionality of getPlaylistsFromJson(photosJsonStr) here so
                    // that I could access the onProgressUpdate in this function. I'm not sure that
                    // it's possible to do outside of the doinBackground() function.
                    JSONObject albumData;
                    try {
                        albumData = new JSONObject(photosJsonStr);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return null;
                    }
                    Log.d("json", albumData.toString());
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
                    //Integer[] currentitem = new Integer[2];
                    //currentitem[0] = 0;
                    //currentitem[1] = photolistData.length();
                    photoIds = new String[photolistData.length()];
                    int bytecount=0;
                    for (int i = 0; i < photolistData.length(); i++){
                        try {
                            //currentitem[0] = i;
                            //publishProgress(currentitem);
                            publishProgress(i, photolistData.length());
                            titls[i] = photolistData.getJSONObject(i)
                                    .getString("title");
                            photoIds[i] = photolistData.getJSONObject(i)
                                    .getString("id");
                            //Here we actually download the thumbnail using URL obtained from JSONObject
                            try {
                                URL imageUrl = new URL(photolistData.getJSONObject(i)
                                        .getString("url_sq"));
                                Log.d("json",photolistData.getJSONObject(i)
                                        .getString("url_sq"));
                                //connect to given server
                                HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
                                //safety features and avoiding errors is links redirect
                                conn.setConnectTimeout(30000);
                                conn.setReadTimeout(30000);
                                conn.setInstanceFollowRedirects(true);
                                //setting inputstream and decoding it into a bitmap
                                InputStream is = conn.getInputStream();
                                bitmaps[i] = BitmapFactory.decodeStream(is);
                                bytecount = bytecount + bitmaps[i].getByteCount();
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
                    Log.d("json",Integer.toString(bytecount)); //TODO: OMG PER PIXEL BYTECOUNT IS TOOO DAMN HIGH



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
             protected void onProgressUpdate(Integer... values) {
                 super.onProgressUpdate(values);
                 loading.setText("Loading: " + String.valueOf(values[0]) + " of " + String.valueOf(values[1]));
             }

             @Override
            protected void onPostExecute(Void result){
                //notify the adapter and the view that is using it to get check for new data
                //in the arrays we defined at the beginning
                adapter.notifyDataSetChanged();
                spinner.setVisibility(View.GONE);  // Hide the progress
                loading.setVisibility(View.GONE);  // Hide the progress
            }

            // See https://secure.flickr.com/services/api/flickr.photosets.getPhotos.html.
            // Uri to download the list of the user's photos, in json format
            private Uri getPhotosAPIRequestUri() {
                final String USER_ID = "12208415@N08";    //Yale flickr user id
                final String BASE_URL = "https://api.flickr.com/services/rest/?";
                //TODO: extract api key and secret
                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                            .appendQueryParameter("method", "flickr.photosets.getPhotos")
                            .appendQueryParameter("api_key", new DeveloperKey().FLICKR_API_KEY)
                            .appendQueryParameter("photoset_id", albumId)
                            .appendQueryParameter("extras", "url_sq")
                            .appendQueryParameter("format", "json")
                            .appendQueryParameter("nojsoncallback", "1")
                            .build();
                return builtUri;
            }

         }

    // Set up a new ImageView with specified parameters
    public class ImageThumbnailAdapter extends BaseAdapter {

        private Context mContext;

        public ImageThumbnailAdapter(Context c) {
             mContext = c;
         }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(0, 0, 0, 0);
            } else {
                imageView =(ImageView) convertView;
            }
            imageView.setImageBitmap(bitmaps[position]);
            return imageView;
        }
        @Override
        //Can also use if statement to set count to 0
        //if titls is uninitialized. Currently mirroring
        //VideoWithinPlaylist
        public int getCount() {
            return titls.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }
    }
}
