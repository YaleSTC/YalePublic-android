package edu.yalestc.yalepublic;

/**
 * Created by carstenpeterson on 1/19/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class Splash extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash);




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
                                buffer.append(line + "\n");
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

                RotatingLinkTask linkTask = new RotatingLinkTask();
                try {
                    String link = linkTask.execute().get();
                    Log.v("rotating link", link);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }


                Log.d("Splash", "Starting Main Activity");
                Intent i = new Intent(Splash.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}
