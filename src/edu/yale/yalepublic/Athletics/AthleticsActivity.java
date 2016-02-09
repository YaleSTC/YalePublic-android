package edu.yale.yalepublic.Athletics;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.os.Bundle;

import edu.yale.yalepublic.R;

/**
 * Created by Mahir Rana on 8/11/15.
 * This class hosts the tabs for the athletics' RSS feed
 */
public class AthleticsActivity extends FragmentActivity {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionbar = getActionBar();
        actionbar.setElevation(0);                      //Gets rid of drop shadow; targets 5.0 only
        actionbar.setDisplayHomeAsUpEnabled(true);     // Show home as a back arrow
        //actionbar.setDisplayShowHomeEnabled(true);     // Show application logo
        actionbar.setDisplayShowTitleEnabled(true);    // Show activity title/subtitle
        actionbar.setDisplayUseLogoEnabled(false);     // Use activity logo instead of activity icon

        actionbar.setTitle(getString(R.string.athletics));  // Set title
        setContentView(R.layout.activity_athletics);

        Bundle men = new Bundle(); //tells AthleticsChooser to show Men Sports

        //Sets up Tabs
        FragmentTabHost mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.addTab(mTabHost.newTabSpec("topstories").setIndicator("TOP STORIES"), Headlines.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("men").setIndicator("MEN"), AthleticsChooser.class, men);
        mTabHost.addTab(mTabHost.newTabSpec("women").setIndicator("WOMEN"), AthleticsChooser.class, null);

    }

}
