package edu.yale.yalepublic;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesUtil;

import edu.yale.yalepublic.Util.ActionBarUtil;

/**
 * Created by Jason Liu on 8/19/15.
 */
public class Licenses extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.licenses);
        TextView tvLicenses = (TextView) findViewById(R.id.tvLicenses);
        tvLicenses.setText(GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(this));

        ActionBar actionbar = getActionBar();
        ActionBarUtil.setupActionBar(actionbar, getString(R.string.maps));
    }
}
