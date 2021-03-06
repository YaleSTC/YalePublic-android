package edu.yale.yalepublic;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import edu.yale.yalepublic.Util.ActionBarUtil;

/**
 * Created by Jason Liu on 11/8/14.
 */
public class About extends Activity {

    private View.OnClickListener ibListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ibFeedback:
                    Intent sendFeedback = new Intent(Intent.ACTION_SEND);
                    sendFeedback.setType("message/rfc822");
                    sendFeedback.putExtra(Intent.EXTRA_EMAIL, new String[]{"mobile.apps@yale.edu"});
                    sendFeedback.putExtra(Intent.EXTRA_SUBJECT, "Yale Feedback");
                    sendFeedback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  // TODO: Return to app after email
                    startActivity(sendFeedback);
                    break;
                case R.id.ibShare:
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("message/text");
                    share.putExtra(Intent.EXTRA_SUBJECT, "Yale Public Application.");
                    share.putExtra(Intent.EXTRA_TEXT,
                            "Hey!\n\n I've been using this cool Yale app, which you can download "
                            + "at https://play.google.com/store/apps/details?id=edu.yale.yalepublic! "
                            + "Hope you like it too!");
                    share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  // TODO: Return to app after email
                    startActivity(share);
                    break;
                case R.id.ibMoreInfo:
                    Uri uriUrl1 = Uri.parse("https://yalestc.github.io/");
                    Intent launchBrowser1 = new Intent(Intent.ACTION_VIEW, uriUrl1);
                    startActivity(launchBrowser1);
                    break;
                case R.id.bLicenses:
                    Intent ourIntent7 = new Intent(getApplicationContext(), Licenses.class);
                    startActivity(ourIntent7);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_screen);
        ActionBar actionbar = getActionBar();
        ActionBarUtil.setupActionBar(actionbar, getString(R.string.action_about));

        findViewById(R.id.ibFeedback).setOnClickListener(ibListener);
        findViewById(R.id.ibShare).setOnClickListener(ibListener);
        findViewById(R.id.ibMoreInfo).setOnClickListener(ibListener);
        findViewById(R.id.bLicenses).setOnClickListener(ibListener);
    }
}

