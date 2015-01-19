package edu.yalestc.yalepublic.Events;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Toast;

import edu.yalestc.yalepublic.Events.CalendarView.EventsCalendarEventList;
import edu.yalestc.yalepublic.Events.ListView.EventsListAdapter;
import edu.yalestc.yalepublic.JSONReader;

/**
 * Created by Stan Swidwinski on 1/10/15.
 * <p/>
 * Class downloading the data about events and displaying the progress dialog on screen.
 */
public class EventsJSONReader extends JSONReader {

    Activity mActivity;
    private ProgressDialog dialog;
    private String mRawData;
    private EventsAdapterForLists mAdapter;

    public EventsJSONReader(Activity activity) {
        super(activity);
        //for creating and getting preferences and tables!
        mActivity = activity;
        dialog = new ProgressDialog(mActivity);
        dialog.setCancelable(false);
        dialog.setTitle("Getting the most up-to-date event list!");
        dialog.setMessage("This should not take too long, please wait...");
        dialog.setIndeterminate(true);
    }

    public EventsJSONReader(String URL, Activity activity) {
        super(URL, activity);
        mActivity = activity;
        dialog = new ProgressDialog(mActivity);
        dialog.setCancelable(false);
        dialog.setTitle("Getting the most up-to-date event list!");
        dialog.setMessage("This should not take too long, please wait...");
        dialog.setIndeterminate(true);
    }

    public void setAdapter(EventsAdapterForLists adapter) {
        mAdapter = adapter;
    }

    @Override
    protected void onPreExecute() {
        dialog.show();
    }

    @Override
    protected String doInBackground(Void... voids) {
        mRawData = super.getData();
        if (mRawData == null) {
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast toast = new Toast(mActivity);
                    toast = Toast.makeText(mActivity, "You need internet connection to view the content!", Toast.LENGTH_LONG);
                    toast.show();
                }
            });
            mActivity.finish();
            return null;
        } else {
            Log.i("EventsJSONReader", "Success");
        }
        return mRawData;
    }

    @Override
    protected void onPostExecute(String result) {
        if (mAdapter != null) {
            mAdapter.updateEvents(mRawData);
            Log.i("EventsJSONReader", "Updating the events data set in calendar adapter");
        }

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}