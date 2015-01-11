package edu.yalestc.yalepublic.Videos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import edu.yalestc.yalepublic.JSONReader;
import edu.yalestc.yalepublic.R;

/**
 * Created by Stan Swidwinski on 1/10/15.
 */

public class VideosWithinPlaylistJSONReader extends JSONReader {

    Activity mActivity;
    private ProgressDialog dialog;
    private String mRawData;
    private ListView mListView;
    private thumbnailAdapter mAdapter;
    //arrays for data about videos
    private Bitmap bitmaps[];
    private String titles[];
    private String dates[];
    //to save the ID's that we will pass to the activity that displays the vids
    private String[] videoIds;
    // a flag to show if the list of videos has been successfully parsed
    private boolean parsingAndDownloadingSuccess;
    String[] allPlaylists;


    public VideosWithinPlaylistJSONReader(Activity activity) {
        super(activity);
        //for creating and getting preferences and tables!
        mActivity = activity;
        dialog = new ProgressDialog(mActivity);
        dialog.setCancelable(false);
        dialog.setTitle("Fetching videos' details!");
        dialog.setMessage("This should not take too long, please wait...");
        dialog.setIndeterminate(true);
        mListView = null;
        parsingAndDownloadingSuccess = false;
    }

    public VideosWithinPlaylistJSONReader(String URL, Activity activity) {
        super(URL, activity);
        mActivity = activity;
        dialog = new ProgressDialog(mActivity);
        dialog.setCancelable(false);
        dialog.setTitle("Fetching videos' details!");
        dialog.setMessage("This should not take too long, please wait...");
        dialog.setIndeterminate(true);
        mListView = null;
        parsingAndDownloadingSuccess = false;
    }

    @Override
    protected void onPreExecute() {
        dialog.show();
    }

    @Override
    protected String doInBackground(Void... voids) {
        mRawData = super.getData();
        if (mRawData == null) {
            Toast toast = new Toast(mActivity);
            toast = Toast.makeText(mActivity, "You need internet connection to view the content!", Toast.LENGTH_LONG);
            toast.show();
            Log.i("CalendarFragment", "Failure");
            mActivity.finish();
            return null;
        } else {
            Log.i("CalendarFragment", "Success");
        }
        parseDataAndDownloadThumbs();
        return mRawData;
    }

    @Override
    protected void onPostExecute(String result) {
        if (mListView != null && parsingAndDownloadingSuccess) {
            mAdapter = new thumbnailAdapter(mActivity);
            mListView.setAdapter(mAdapter);
            //create a OnItemClickListener to play the video
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    Intent intent = new Intent(mActivity, VideoYoutubePlayback.class);
                    intent.putExtra("videoId", videoIds[arg2]);
                    Log.v("StartingActivityInVideosInPlaylists", "Starting a new action with the parameter videoID: " + videoIds[arg2]);
                    mActivity.startActivity(intent);
                }
            });
        } else {
            Toast toast = new Toast(mActivity);
            toast = Toast.makeText(mActivity, "You need internet connection to view the content!", Toast.LENGTH_LONG);
            toast.show();
            Log.i("CalendarFragment", "Failure");
            mActivity.finish();
            return;
        }
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void parseDataAndDownloadThumbs(){
        JSONObject videoData;
        JSONArray playlistData;
        try {
            videoData = new JSONObject(mRawData);
            playlistData = videoData.getJSONArray("items");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }

        titles = new String[playlistData.length()];
        bitmaps = new Bitmap[playlistData.length()];
        dates = new String[playlistData.length()];
        videoIds = new String[playlistData.length()];
        for (int i = 0; i < playlistData.length(); i++) {
            try {
                titles[i] = playlistData.getJSONObject(i)
                        .getJSONObject("snippet")
                        .getString("title");
                dates[i] = (playlistData.getJSONObject(i)
                        .getJSONObject("snippet")
                        .getString("publishedAt")).substring(0, 10);
                videoIds[i] = playlistData.getJSONObject(i)
                        .getJSONObject("snippet")
                        .getJSONObject("resourceId")
                        .getString("videoId");
                //Here we actually download the thumbnail using URL obtained from JSONObject
                try {
                    URL imageUrl = new URL(playlistData.getJSONObject(i)
                            .getJSONObject("snippet")
                            .getJSONObject("thumbnails")
                            .getJSONObject("medium")
                            .getString("url"));
                    //connect to given server
                    HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                    //safety features and avoiding errors is links redirect
                    conn.setConnectTimeout(30000);
                    conn.setReadTimeout(30000);
                    conn.setInstanceFollowRedirects(true);
                    //setting inputstream and decoding it into a bitmap
                    InputStream is = conn.getInputStream();
                    bitmaps[i] = BitmapFactory.decodeStream(is);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return;
                }
                parsingAndDownloadingSuccess = true;
            } catch (MalformedURLException e) {
                Log.e("URI", "URL was malformed!");
                return;
            } catch (IllegalArgumentException e) {
                Log.e("URI", "the argument proxy is null or of is an invalid type.");
                return;
            } catch (UnsupportedOperationException e) {
                Log.e("URI", " the protocol handler does not support opening connections through proxies.");
                return;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    //Our custom Adapter
    public class thumbnailAdapter extends BaseAdapter {
        Context mContext;

        thumbnailAdapter(Context context) {
            mContext = context;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //if we can reuse a view, do so.
            if (convertView != null) {
                ((ImageView) ((RelativeLayout) convertView).getChildAt(0)).setImageBitmap(bitmaps[position]);
                ((TextView) ((RelativeLayout) convertView).getChildAt(1)).setText(titles[position]);
                ((TextView) ((RelativeLayout) convertView).getChildAt(2)).setText(dates[position]);
                convertView.setBackgroundColor(Color.parseColor("#dbdbdd"));
                return ((RelativeLayout) convertView);
            } else {
                //if not, create a new one from the template of a view using inflate
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                RelativeLayout thumbnail = ((RelativeLayout) inflater.inflate(R.layout.thumbnail_elements, null));
                ((ImageView) thumbnail.getChildAt(0)).setImageBitmap(bitmaps[position]);
                ((TextView) thumbnail.getChildAt(1)).setText(titles[position]);
                ((TextView) thumbnail.getChildAt(2)).setText(dates[position]);
                thumbnail.setBackgroundColor(Color.parseColor("#dbdbdd"));
                return thumbnail;
            }

        }

        //return the number of elements. This is the reason for initializing the arrays all the way
        //at the top. Otherwise we would get nullpointerException
        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }
    }

    public void addListView(ListView listView){
        mListView = listView;
    }

}