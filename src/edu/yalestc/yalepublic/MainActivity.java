package edu.yalestc.yalepublic;

// import android.support.v7.app.ActionBarActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.stetho.Stetho;

import edu.yalestc.yalepublic.Cache.CalendarCache;
import edu.yalestc.yalepublic.Videos.PlaylistList;
import edu.yalestc.yalepublic.Events.EventCategories;
import edu.yalestc.yalepublic.News.NewsChooser;

@SuppressLint("NewApi")
public class MainActivity extends Activity {

    double screenWidth;
    double screenHeight;
    public static final String VIDEO_MODE_KEY = "Videos"; // TODO: Review choice
    public static final String PHOTO_MODE_KEY = "Photos"; // of keys
    
    private OnClickListener buttonClickListener = new OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.bNews:
                Intent iNews = new Intent(MainActivity.this, NewsChooser.class);
                startActivity(iNews);
                break;
            case R.id.bDirectory:
                break;
            case R.id.bMaps:
                Intent iMaps = new Intent(MainActivity.this, MapView.class);
                startActivity(iMaps);
                break;
            case R.id.bVideos:
                Intent videoListIntent = new Intent(MainActivity.this, PlaylistList.class);
                videoListIntent.putExtra(VIDEO_MODE_KEY, true);
                startActivity(videoListIntent);
                break;
            case R.id.bPhotos:
                Intent photoListIntent = new Intent(MainActivity.this, PhotoList.class);
                photoListIntent.putExtra(PHOTO_MODE_KEY, true);
                startActivity(photoListIntent);
                break;
            case R.id.bEvents:
                Intent iEvents = new Intent(MainActivity.this, EventCategories.class);
                startActivity(iEvents);
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
                Intent i = getIntent();
                Uri uriUrl3 = Uri.parse(i.getStringExtra("url"));
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
        ActionBar actionbar = getActionBar();
        //actionbar.setDisplayHomeAsUpEnabled(true); // Show home as a back arrow
        //actionbar.setDisplayShowHomeEnabled(true);   // Show application logo
        actionbar.setDisplayShowTitleEnabled(true);  // Show activity title/subtitle
        actionbar.setDisplayUseLogoEnabled(false);   // Use activity logo instead of activity icon
        //actionbar.setTitle("Yale");                // Set title

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());

        Intent i = getIntent();
        String rotatingLink = getIntent().getStringExtra("url");
        ImageButton rotatingImageButton = (ImageButton) findViewById(R.id.bArts);
        TextView rotatingTextView = (TextView) findViewById(R.id.tArts);

        if (rotatingLink != null) {
            switch (rotatingLink) {
                case "http://artscalendar.yale.edu":
                    rotatingTextView.setText("Arts Calendar");
                    rotatingImageButton.setImageResource(R.drawable.module_arts_events_default);
                    break;
                case "http://yalecollege.yale.edu/freshman":
                    rotatingTextView.setText("Orientation");
                    rotatingImageButton.setImageResource(R.drawable.module_orientation_default);
                    break;
                case "http://aya.yale.edu/content/current-assembly":
                    rotatingTextView.setText("AYA Assembly");
                    rotatingImageButton.setImageResource(R.drawable.module_assembly_default);
                    break;
                case "http://commencement.yale.edu/":
                    rotatingTextView.setText("Commencement");
                    rotatingImageButton.setImageResource(R.drawable.module_commencement_default);
                    break;
            }
        }

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
        getMenuInflater().inflate(R.menu.about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_about:
                Intent iAbout = new Intent(MainActivity.this, About.class);
                startActivity(iAbout);
                return true;
//            case R.id.action_settings:
//                // Open the settings menu here
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
    }
    

}
