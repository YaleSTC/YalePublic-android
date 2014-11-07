package edu.yalestc.yalepublic;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Stan Swidwinski on 10/29/14.
 */

//.get() from activity returns null if error occurred. Almost exclusively if the user does not have Internet (Google won't blow up suddenly).
//If this happens you should counter it using, for example,
/*if(new JSONReader("Abc", getActivity()).execute().get() == null){
        Toast toast = new Toast(getActivity());
        toast = Toast.makeText(getActivity(), "You need internet connection to view the content!", Toast.LENGTH_LONG);
        toast.show();
        finish();
        return null;
        }
        */
public class JSONReader extends AsyncTask<Void, String, String>{
    //The URL that we will be adding parameters to. For example https://www.googleapis.com/youtube/v3/playlists
    private String BASE_URL = "";
    //These are the parameters we will be adding to the URL specified above. For example, if parameters = [("part","snippet")]
    //will later result in adding ?part=snippet to the URL making https://www.googleapis.com/youtube/v3/playlists?part=snippet
    //If there is more than one element in parameters, the code will automatically adapt to create, for example
    //https://www.googleapis.com/youtube/v3/playlists?part=snippet&channelId=UC4EY_qnSeAP1xGsh61eOoJA from
    //parameters = [("part","snippet"),("channelId","UC4EY_qnSeAP1xGsh61eOoJA ")]
    private ArrayList<Pair<String, String> > parameters = new ArrayList<Pair<String, String>>();
    //for checking if we are online since this class is not an Activity class
    private Context mContext;

    JSONReader(Context context){
        mContext = context;
    }

    JSONReader(String URL, Context context){
        BASE_URL = URL;
        mContext = context;
    }

    public String getURL(){
        return BASE_URL;
    }

    public void setURL(String URL){
        BASE_URL = URL;
    }

    public List<Pair <String, String> > getParams(){
        return parameters;
    }

    public void addParams(Pair<String, String> param){
        parameters.add(param);
    }

    public void setParams(ArrayList <Pair< String, String> > params){
        parameters = params;
    }

    // this method parses the raw data (which is a String in JSON format)
    // and extracts the titles of the playlists

        //abstracted builder of URI
    private Uri buildMyUri(){
        Uri.Builder buildingUri = Uri.parse(BASE_URL).buildUpon();
        for(int i = 0; i < parameters.size() ; i++){
            buildingUri.appendQueryParameter(parameters.get(i).first, parameters.get(i).second);
        }
        return buildingUri.build();
    }
    @Override
    protected String doInBackground(Void... params){
       if(isOnline()) {
           try {
               // first we create the URI
               Uri builtUri = buildMyUri();

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
               String JSONresponse = buffer.toString();
               // we pass the data to getPlaylistsFromJson
               //but also remember to save the playlistID's for future
               return JSONresponse;

               // TODO check if there are more than 50 videos in the arrays
           } catch (MalformedURLException e){
               Log.e("URI", "URL was malformed!");
               return null;
           } catch (IllegalArgumentException e) {
               Log.e("URI", "the argument proxy is null or of is an invalid type.");
               return null;
           } catch(UnsupportedOperationException e) {
               Log.e("URI"," the protocol handler does not support opening connections through proxies.");
               return null;
           }catch (IOException e) {
               Log.e("URI", "uri was invalid or api request failed");
               e.printStackTrace();
               return null;
           }
           //if isOnline returns false, we toast the user
       } else {
           Log.e("URI","You are not connected to internet!");
           return null;
       }
    }

    @Override
    protected void onPostExecute(String result){
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
