package edu.yalestc.yalepublic;

// import android.support.v7.app.ActionBarActivity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.os.Build;

@SuppressLint("NewApi")
public class MainActivity extends ActionBarActivity {

    double screenWidth;
    double screenHeight;
    
    private OnClickListener buttonClickListener = new OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.bNews:
                break;
            case R.id.bDirectory:
                break;
            case R.id.bMaps:
                break;
            case R.id.bVideos:
                Intent intent = new Intent(MainActivity.this, VideoList.class);
                startActivity(intent);
                break;
            case R.id.bPhotos:
                break;
            case R.id.bEvents:
                break;
            case R.id.bTransit:
                Uri uriUrl1 = Uri.parse("http://yale.transloc.com");
                Intent launchBrowser1 = new Intent(Intent.ACTION_VIEW, uriUrl1);
                startActivity(launchBrowser1);
                break;
            case R.id.bAthletics:
                Uri uriUrl2 = Uri.parse("http://www.yalebulldogs.com/landing/index");
                Intent launchBrowser2 = new Intent(Intent.ACTION_VIEW, uriUrl2);
                startActivity(launchBrowser2);
                break;
            case R.id.bArts:
                Uri uriUrl3 = Uri.parse("http://artscalendar.yale.edu");
                Intent launchBrowser3 = new Intent(Intent.ACTION_VIEW, uriUrl3);
                startActivity(launchBrowser3);
                break;
            }
        };
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*Configuration config = getResources().getConfiguration();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = (double)config.screenWidthDp * dm.density;
        screenHeight = (double)config.screenHeightDp * dm.density;
        final GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setPadding((int)(screenWidth/12.0), (int) (screenHeight*(1.5/20)), (int)(screenWidth/12.0), 0);
        gridview.setVerticalSpacing((int)(screenHeight*(1.5/20)));
        gridview.setHorizontalSpacing((int)(screenWidth/6.0));
        Log.d("relative layout", "before adapter was set");
        gridview.setAdapter(new MenuAdapter(this, screenWidth));
        Log.d("Menu", String.valueOf(gridview.getPaddingTop()));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("ITEM_CLICKED", "" + String.valueOf(gridview.getItemAtPosition(position)));
                Toast toast = new Toast(getApplicationContext());
                toast.setText(String.valueOf(gridview.getItemAtPosition(position)));
                toast.show();
            }
        });*/
        registerButtons();
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container,
                    false);
            return rootView;
        }
    }
    
    
    private void register(int buttonResourceId){
        findViewById(buttonResourceId).setOnClickListener(buttonClickListener);
    }
    
    private void registerButtons(){
        register(R.id.bNews);
        register(R.id.bDirectory);
        register(R.id.bMaps);
        register(R.id.bVideos);
        register(R.id.bPhotos);
        register(R.id.bEvents);
        register(R.id.bTransit);
        register(R.id.bAthletics);
        register(R.id.bArts);
        ;
    }
    

}
