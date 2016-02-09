package edu.yale.yalepublic;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by Jason Liu on 8/19/15.
 */
public class Licenses extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.licenses);
        TextView tvLicenses = (TextView) findViewById(R.id.tvLicenses);
        tvLicenses.setText(GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(this));

        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);     // Show home as a back arrow
        //actionbar.setDisplayShowHomeEnabled(true);     // Show application logo
        actionbar.setDisplayShowTitleEnabled(true);    // Show activity title/subtitle
        actionbar.setDisplayUseLogoEnabled(false);     // Use activity logo instead of activity icon
        actionbar.setTitle(getString(R.string.maps));  // Set title
        super.onCreate(savedInstanceState);
    }
}
