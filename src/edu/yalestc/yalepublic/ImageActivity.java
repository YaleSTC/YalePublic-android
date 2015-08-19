package edu.yalestc.yalepublic;
import java.io.InputStream;
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

/**
 * This class shows an enlarged version of a photo clicked from PhotosWithinAlbum with the picture's caption
 */

public class ImageActivity extends Activity {

    private Bitmap mBitmap;
    private String imageUrl;
    private String caption_text;
    private ImageView imageView;
    private TextView caption;
    private boolean captionSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

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
