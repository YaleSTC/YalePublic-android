package edu.yale.yalepublic.Util;

import android.app.ActionBar;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import edu.yale.yalepublic.R;

/**
 * Created by jason on 2/12/16.
 */
public class ActionBarUtil {

    public static void setupActionBar(ActionBar actionbar, String title) {
        if (actionbar == null)
            return;

        if (android.os.Build.VERSION.SDK_INT > 21)
            actionbar.setElevation(0);                 // Gets rid of drop shadow; targets 5.0 only
        actionbar.setDisplayHomeAsUpEnabled(true);     // Show home as a back arrow
        //actionbar.setDisplayShowHomeEnabled(true);   // Show application logo
        actionbar.setDisplayShowTitleEnabled(true);    // Show activity title/subtitle
        actionbar.setDisplayUseLogoEnabled(false);     // Use activity logo instead of activity icon
        if (title != null)
            actionbar.setTitle(title);                 // Set title
    }
}
