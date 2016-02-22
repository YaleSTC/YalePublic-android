package edu.yale.yalepublic;

/**
 * Created by carstenpeterson on 1/19/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import edu.yale.yalepublic.Cache.CalendarCache;

public class Splash extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash);

        CalendarCache cache = new CalendarCache(this);
        cache.execute();
    }

    public void getIcon(){

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                class RotatingLinkTask extends AsyncTask<Void, Void, String> {

                    @Override
                    protected String doInBackground(Void... params) {
                        try{
                            // first we create the URI - note that the base is different than in VideoList.java
                            final String BASE_URL = "https://yalestc.github.io/YalePublic-android/";
                            Uri builtUri = Uri.parse(BASE_URL);

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
                                buffer.append(line);
                            }

                            if (buffer.length() == 0) {
                                // Stream was empty.  No point in parsing.
                                return null;
                            }
                            String Link = buffer.toString();
                            // we pass the data to getPlaylistsFromJson
                            //but also remember to save the playlistID's for future
                            // Log.v("rotating link", Link);
                            return Link;
                        }

                        catch (IOException e){
                            Log.e("URI", "uri was invalid or api request failed");
                            e.printStackTrace();
                            return null;
                        }
                    }
                }

                ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                String link = null;
                if (mWifi.isConnected()) {
                    RotatingLinkTask linkTask = new RotatingLinkTask();
                    try {
                        link = linkTask.execute().get();
                        Log.v("rotating link", "" + link);  // blank link can cause NPE
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }

                // Default fallback if unable to retrive the correct URL from the site.
                if (link == null || link.isEmpty()) {
                    link = "http://artscalendar.yale.edu";
                }

                Log.d("Splash", "Starting Main Activity");
                Intent i = new Intent(Splash.this, MainActivity.class);
                i.putExtra("url", link);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}
