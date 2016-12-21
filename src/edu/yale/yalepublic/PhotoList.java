package edu.yale.yalepublic;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PhotoList extends Activity {

    ProgressBar spinner;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("PhotoList", "backPressed");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_photo_within_album);
        spinner = (ProgressBar) findViewById(R.id.pbLoading);
        spinner.setVisibility(View.GONE);

        Bundle extras = getIntent().getExtras();
        PlaceholderFragment placeholderFragment = new PlaceholderFragment();
        placeholderFragment.setArguments(extras);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.photoContainer, placeholderFragment).commit();
    }

}
