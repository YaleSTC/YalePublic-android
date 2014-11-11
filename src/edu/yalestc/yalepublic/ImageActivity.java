package edu.yalestc.yalepublic;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
import android.view.MenuItem;
import android.widget.ImageView;

public class ImageActivity extends Activity {

	
	private String photoId;
	private Bitmap mBitmap;
	private String title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		ImageView imageView = (ImageView) findViewById(R.id.photoImageView);
		photoId = getIntent().getExtras().getString(VideoList.PHOTO_ID_KEY);
		setTitle(getIntent().getExtras().getString(PhotosWithinAlbum.TITLE_KEY));
		getPhotoTask task = new getPhotoTask();
		task.execute();
	}
	public class getPhotoTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
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
                getPhotoFromJson(photosJsonStr);
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

		private String getPhotoFromJson(String rawData) {
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
            	JSONArray photoContentArray = photoData.getJSONObject("urls").getJSONArray("url");
                URL imageUrl = new URL(photoContentArray.getJSONObject(0).getString("_content"));
                //connect to given server 
                HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
                //safety features and avoiding errors is links redirect     
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(30000);
                conn.setInstanceFollowRedirects(true);
                //setting inputstream and decoding it into a bitmap
                InputStream is=conn.getInputStream();
                mBitmap = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
            return "1"; //TODO: Why?
			
		}
		
	}
	private Uri getPhotoAPIRequestUri() {
		final String BASE_URL = "https://api.flickr.com/services/rest/?";
		//TODO: extract api key and secret
		Uri builtUri = Uri.parse(BASE_URL).buildUpon()
		            .appendQueryParameter("method", "flickr.photos.getInfo")
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
