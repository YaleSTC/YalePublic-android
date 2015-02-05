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
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

public class ImageActivity extends Activity {

//  private ImageAdapter adapter;
    private String imageUrl;
    private Bitmap mBitmap;
    private String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ImageView imageView = (ImageView) findViewById(R.id.photoImageView);
        imageUrl = getIntent().getExtras().getString(PhotosWithinAlbum.PHOTO_URL_KEY);
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
            Log.d("bitmap",Integer.toString(mBitmap.getByteCount()));
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
