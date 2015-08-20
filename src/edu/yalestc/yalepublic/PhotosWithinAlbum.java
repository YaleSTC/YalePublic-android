package edu.yalestc.yalepublic;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * This class displays recent photos from Yale's Instagram account
 */
public class PhotosWithinAlbum extends Activity {

    private ImageThumbnailAdapter adapter;
    //TODO: Refactor out imageUrls
    private List<String> imageUrls = new ArrayList<String>();
    private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
    private List<String> photoIds = new ArrayList<String>();
    private List<String> captions = new ArrayList<String>();
    private static String imageUrl;
    private static String caption_text;
    private String paginationUrl = null;
    private ProgressDialog spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);        // Show home as a back arrow
        //actionbar.setDisplayShowHomeEnabled(true);     // Show application logo
        actionbar.setDisplayShowTitleEnabled(true);     // Show activity title/subtitle
        actionbar.setDisplayUseLogoEnabled(false);     // Use activity logo instead of activity icon
        actionbar.setTitle(getString(R.string.photos_in_album));        // Set title
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_within_album);
        if (savedInstanceState == null) {
            //load fragment
            getFragmentManager().beginTransaction()
                    .add(R.id.photoContainer, new PlaceholderFragment()).commit();

        }

        //sets up spinner for loading information
        spinner = new ProgressDialog(this);
        spinner.setCancelable(true);
        spinner.setMessage("Loading");
        spinner.setTitle("Fetching Photos...");
        spinner.setIndeterminate(true);

    }

    @SuppressLint("ValidFragment")
    public class PlaceholderFragment extends Fragment implements AbsListView.OnScrollListener {
        GridView gridview;
        Button loadbtn;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_photo_within_album,
                    container, false);
            //initialize the adapter
            adapter = new ImageThumbnailAdapter(PhotosWithinAlbum.this);

            //create custom AsyncTask to fetch recent Media
            AlbumTask photos = new AlbumTask();
            photos.execute((String[]) null);

            //create gridView and set the adapter.
            gridview = (GridView) rootView.findViewById(R.id.imageGridView);
            gridview.setAdapter(adapter);

            //create a OnItemClickListener to load photo
            gridview.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    //this is done so ImageDialog knows what photo to display
                    imageUrl = imageUrls.get(position);
                    caption_text = captions.get(position);

                    //sets up a dialog which displays the photo clicked on
                    final ImageDialog dialog = new ImageDialog(PhotosWithinAlbum.this);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    dialog.show();
                    Window window = dialog.getWindow();
                    //this is done to make the dialog bigger
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                }
            });
            loadbtn = (Button) rootView.findViewById(R.id.btnLoad);
            loadbtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (paginationUrl == null) {
                        Toast.makeText(PhotosWithinAlbum.this, "No more photos to load", Toast.LENGTH_SHORT).show();
                    } else {
                        //create custom AsyncTask to fetch recent Media
                        AlbumTask photos = new AlbumTask();
                        photos.execute((String[]) null);
//
                    }

                }
            });
            gridview.setOnScrollListener(this);

            return rootView;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (gridview.getLastVisiblePosition() + 1 == gridview.getAdapter().getCount()) {
                loadbtn.setVisibility(View.VISIBLE);
            } else {
                loadbtn.setVisibility(View.GONE);
            }
        }
    }

    public class AlbumTask extends AsyncTask<String, Integer, Void> {

        private Void getPhotosFromJson(String rawData) {
            JSONObject albumData;
            JSONArray photolistData;
            try {
                albumData = new JSONObject(rawData);
                photolistData = albumData.getJSONArray("data");
                //if paginated, then update pagination_url
                if (albumData.has("pagination")) {
                    paginationUrl = albumData.getJSONObject("pagination").getString("next_url");
                    Log.d("PAGINATION", paginationUrl);
                } else {
                    paginationUrl = null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            int count = photolistData.length();
            Log.d("JSON", rawData);

            for (int i = 0; i < count; i++) {
                try {
                    publishProgress(i + 1, count);
                    photoIds.add(photolistData.getJSONObject(i).getString("id"));
                    captions.add(photolistData.getJSONObject(i).getJSONObject("caption").getString("text"));
                    imageUrls.add(photolistData.getJSONObject(i).getJSONObject("images")
                            .getJSONObject("standard_resolution")
                            .getString("url"));
                    //Here we actually download the thumbnail using URL obtained from JSONObject
                    try {
                        URL imageUrl = new URL(photolistData.getJSONObject(i).getJSONObject("images")
                                .getJSONObject("thumbnail")
                                .getString("url"));
                        //connect to given server
                        HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                        //safety features and avoiding errors is links redirect
                        conn.setConnectTimeout(30000);
                        conn.setReadTimeout(30000);
                        conn.setInstanceFollowRedirects(true);
                        //setting inputstream and decoding it into a bitmap
                        InputStream is = conn.getInputStream();
                        bitmaps.add(BitmapFactory.decodeStream(is));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            }
            return null;

        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                //Send GET request to the server to get the list of photos
                URL url;
                //Check if Asynctask is provided with a URL.
                if (paginationUrl != null) {
                    url = new URL(paginationUrl);
                } else {
                    url = new URL(getRequestUri().toString());
                }
                Log.d("Photos", url.toString());

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

                // Note that onProgressUpdate can be accessed by any function called by the
                // doInBackground() function, such as this one.
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
        protected void onPreExecute() {
            super.onPreExecute();
            spinner.setMessage("Loading");
            spinner.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            spinner.setMessage("Loading: " + String.valueOf(values[0]) + " of " + String.valueOf(values[1]));
        }

        @Override
        protected void onPostExecute(Void result) {
            //notify the adapter and the view that is using it to get check for new data
            //in the arrays we defined at the beginning
            Log.d("DONE", "Reached on Post Exec");
            Log.d("DONE", "bitmap size:" + bitmaps.size());

            if (spinner != null && spinner.isShowing()) {
                spinner.dismiss();
            }
            adapter.notifyDataSetChanged();

        }

        private Uri getRequestUri() {
            final String USER_ID = getString(R.string.yale_instagram_id);    //Yale instagram user id
            final String BASE_URL = "https://api.instagram.com/v1/users/"
                    + USER_ID + "/media/recent?";
            Uri builtUri;
            builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter("client_id", DeveloperKey.INSTAGRAM_CLIENT_ID)
                    .build();
            return builtUri;
        }

    }

    // Set up a new ImageView with specified parameters
    public class ImageThumbnailAdapter extends BaseAdapter {
        private DisplayMetrics display;
        private Context mContext;
        int width;

        public ImageThumbnailAdapter(Context c) {
            mContext = c;
            display = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(display);
            width = display.widthPixels;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(2 * width / 9, 2 * width / 9));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(0, 0, 0, 0);
            } else {
                imageView = (ImageView) convertView;
            }
            try { //this is needed in case the tasks are out of sync
                imageView.setImageBitmap(bitmaps.get(position));
            } catch (Exception e) {
            }
            return imageView;
        }

        @Override
        //Can also use if statement to set count to 0
        //if imageUrls is uninitialized. Currently mirroring
        //VideoWithinPlaylist
        public int getCount() {
            return imageUrls.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

    //These two getters are used by ImageDialog to get the photo clicked on
    public static String getImageUrl() {
        return imageUrl;
    }

    public static String getCaption() {
        return caption_text;
    }
}
