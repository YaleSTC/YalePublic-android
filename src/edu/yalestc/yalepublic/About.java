package edu.yalestc.yalepublic;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

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
                    share.setType("message/rfc822");
                    share.putExtra(Intent.EXTRA_TEXT, "Here's a cool Yale app! Hope you like it!");
                    share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  // TODO: Return to app after email
                    startActivity(share);
                    break;
                case R.id.ibMoreInfo:
                    Uri uriUrl1 = Uri.parse("https://yalestc.github.io/");
                    Intent launchBrowser1 = new Intent(Intent.ACTION_VIEW, uriUrl1);
                    startActivity(launchBrowser1);
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_screen);

        findViewById(R.id.ibFeedback).setOnClickListener(ibListener);
        findViewById(R.id.ibShare).setOnClickListener(ibListener);
        findViewById(R.id.ibMoreInfo).setOnClickListener(ibListener);
    }
}

