package edu.yale.yalepublic;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

public class ImageActivity extends Activity {

//  private ImageAdapter adapter;
    private String photoId;
    private Bitmap mBitmap;
    private Bitmap mBitmap2;
    private String title;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ImageView imageView = (ImageView) findViewById(R.id.photoImageView);
        extras = getIntent().getExtras();
        if (extras == null)  // safety check
            return;

        photoId = extras.getString(PhotoList.PHOTO_ID_KEY);
        setTitle(extras.getString(PhotosWithinAlbum.TITLE_KEY));
        getPhotoTask task = new getPhotoTask();
        try {
            mBitmap2=task.execute().get();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(mBitmap2!=null){
            imageView.setImageBitmap(mBitmap2);
        }
    }
    public class getPhotoTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                //Send GET request to the server
                URL url = new URL(getPhotoAPIRequestUri().toString());
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
               return getPhotoFromJson(photosJsonStr);
            } catch (Exception e) {
                Log.e("URI", "uri was invalid or api request failed");
                e.printStackTrace();
            }
            return null;
        }

        private Bitmap getPhotoFromJson(String rawData) {
            JSONObject photoData;
            try {
                photoData = new JSONObject(rawData);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
            //Here we actually download the thumbnail using URL obtained from JSONObject
            try {
                JSONArray photoContentArray = photoData.getJSONObject("sizes").getJSONArray("size");
                Log.d("bitmap",photoContentArray.getJSONObject(8).getString("source"));
                URL imageUrl = new URL(photoContentArray.getJSONObject(8).getString("source"));
                //connect to given server
                HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
                //safety features and avoiding errors is links redirect
                conn.setConnectTimeout(300000000);
                conn.setReadTimeout(3000000);
                conn.setInstanceFollowRedirects(true);
                //setting inputstream and decoding it into a bitmap
                InputStream is=conn.getInputStream();
                mBitmap = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
            Log.d("bitmap",Integer.toString(mBitmap.getByteCount()));
            return mBitmap;

        }

    }
    private Uri getPhotoAPIRequestUri() {
        final String BASE_URL = "https://api.flickr.com/services/rest/?";
        //TODO: extract api key and secret
        Log.d("bitmap",photoId);
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getSizes")
                    .appendQueryParameter("api_key", new DeveloperKey().FLICKR_API_KEY)
                    .appendQueryParameter("photo_id",photoId)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .build();
        return builtUri;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image, menu);
        return true;
    }
}
