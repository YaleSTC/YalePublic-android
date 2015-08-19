package edu.yalestc.yalepublic;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONObject;
import javax.net.ssl.HttpsURLConnection;

/**
 * This class shows an enlarged version of a photo clicked from PhotosWithinAlbum with the picture's caption
 */

public class ImageActivity extends Activity {

    private String access_token; //this is needed to like and unlike the photo
    private Bitmap mBitmap;
    private String imageUrl;
    private String caption_text;
    private String media_ID;
    private ImageView imageView;
    private TextView caption;
    private boolean captionSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        access_token = InstagramAuth.getAccess();

        media_ID = getIntent().getExtras().getString(getString(R.string.media_id));
        caption_text = getIntent().getExtras().getString(getString(R.string.caption));
        imageUrl = getIntent().getExtras().getString(PhotosWithinAlbum.PHOTO_URL_KEY);

        imageView = (ImageView) findViewById(R.id.photoImageView);
        caption= (TextView) findViewById(R.id.image_caption);
        caption.setText(caption_text);
        caption.setMovementMethod(new ScrollingMovementMethod());

        getPhotoTask task = new getPhotoTask();
        try {
            mBitmap =task.execute().get();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(mBitmap !=null){
            imageView.setImageBitmap(mBitmap);
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likePhotoTask like = new likePhotoTask();
                like.execute((String[]) null);
            }
        });
    }

    //We set the caption's location here
    public void onWindowFocusChanged(boolean hasFocus) {
       if(mBitmap != null & !captionSet ) { //captionSet is used so the caption is only set once
           int bitmap_width = mBitmap.getWidth();
           int bitmap_height = mBitmap.getHeight();
           int scaled_height = imageView.getWidth() * bitmap_height / bitmap_width;
           int blank_space_buffer = (imageView.getHeight() - scaled_height)/2;

           caption.setTranslationY(blank_space_buffer + scaled_height + 10); //the 5 is for padding
           caption.setVisibility(View.VISIBLE); //it was previously invisible in case there was a delay with getPhotoTask
           captionSet = true;
       }
    }

    //This AyncTask is used to like a photo, this requires the user to be signed in
    public class likePhotoTask extends AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String builtUri = "https://api.instagram.com/v1/media/"+ media_ID +
                    "/likes?access_token=" + access_token;
            try {
                URL url = new URL(builtUri);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.setDoInput(true);
                httpsURLConnection.setDoOutput(false);
                InputStream is = httpsURLConnection.getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(is));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line);
                }
                JSONObject response = new JSONObject(total.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    public class getPhotoTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap mBitmap;
            try {
                URL url = new URL(imageUrl);
                Log.d("bitmap",imageUrl);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
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
            Log.d("bitmap", Integer.toString(mBitmap.getByteCount()));
            return mBitmap;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image, menu);
        return true;
    }
}
