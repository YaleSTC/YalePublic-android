package edu.yalestc.yalepublic.Events;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import edu.yalestc.yalepublic.JSONReader;

/**
 * Created by Stan Swidwinski on 1/10/15.
 */
public class EventsJSONReader extends JSONReader {

    Context mContext;
    private ProgressDialog dialog;
    private String mRawData;
    private int mMonth;
    private int mYear;
    private EventsCalendarEventList mAdapter;

    public EventsJSONReader(Context context){
        super(context);
        //for creating and getting preferences and tables!
        mContext = context;
        dialog = new ProgressDialog(mContext);
        dialog.setCancelable(false);
        dialog.setTitle("Getting the most up-to-date event list!");
        dialog.setMessage("This should not take too long, please wait...");
        dialog.setIndeterminate(true);
        mYear = 0;
        mMonth = 0;
    }

    public EventsJSONReader(String URL, Context context){
        super(URL, context);
        mContext = context;
        dialog = new ProgressDialog(mContext);
        dialog.setCancelable(false);
        dialog.setTitle("Getting the most up-to-date event list!");
        dialog.setMessage("This should not take too long, please wait...");
        dialog.setIndeterminate(true);
        mYear = 0;
        mMonth = 0;
    }

    public void setYearAndMonth(int year, int month){
        mYear = year;
        mMonth = month;
    }

    public void setEventsListAdapter(EventsCalendarEventList adapter){
        mAdapter = adapter;
    }

    @Override
    protected void onPreExecute(){
        dialog.show();
    }

    @Override
    protected String doInBackground(Void... voids) {
        mRawData = super.getData();
        if (mRawData == null) {
            Toast toast = new Toast(mContext);
            toast = Toast.makeText(mContext, "You need internet connection to view the content!", Toast.LENGTH_LONG);
            toast.show();
            Log.i("CalendarFragment", "Failure");
            return null;
        } else {
            Log.i("CalendarFragment", "Success");
        }
        return mRawData;
    }

    @Override
    protected void onPostExecute(String result){
        if(mAdapter != null && mYear != 0 && mMonth != 0){
            mAdapter.update(mRawData, mYear, mMonth);
        }
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }
}
