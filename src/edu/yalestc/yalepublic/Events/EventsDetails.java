package edu.yalestc.yalepublic.Events;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.yalestc.yalepublic.R;


/**
 * Created by Stan Swidwinski on 11/18/14.
 */
public class EventsDetails extends Activity {
    Bundle extras;
    DisplayMetrics display;
    int width;
    int height;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extras = getIntent().getExtras();
        display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        width = display.widthPixels;
        height = display.heightPixels;

        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.events_details,null);
            //set the rectangle at the top by the title
        ImageView rectangleByTitle = ((ImageView) ((LinearLayout)layout.getChildAt(0)).getChildAt(0));
            //see function at the end. Analogous to the one in EventCategories.
        rectangleByTitle.setImageDrawable(createRectangle(extras.getInt("color"), extras.getInt("colorFrom"), extras.getInt("colorTo")));
        rectangleByTitle.setPadding((int) width/50, (int) (height*0.023), (int) width*3/100, (int) (height*0.023));
            //set the title
        TextView title = ((TextView)((LinearLayout)layout.getChildAt(0)).getChildAt(1));
        title.setText(extras.getString("title"));
        title.setPadding(0, (int) width/25, (int) width*3/100, 0);
            //get the details linearlayout
        LinearLayout details = ((LinearLayout)layout.getChildAt(1));
            //get the start time details linear layout
        LinearLayout detailsStart = ((LinearLayout)details.getChildAt(0));
            //set the static part of start time
        TextView eventsDetailsStartStatic = (TextView)detailsStart.getChildAt(0);
        eventsDetailsStartStatic.setPadding((int) 0.18*width, (int)0.44*height, (int) 0.047*width, (int) 0.042*height);
            //set the nonstatic part of start time
        TextView eventsDetailsStartTime = (TextView)detailsStart.getChildAt(1);
        eventsDetailsStartTime.setText(extras.getString("start"));
            //get layout for the end time part of details linear layout
        LinearLayout detailsEnd = (LinearLayout)details.getChildAt(1);
            //set the static part of end time
        TextView eventsDetailsEndStatic = (TextView)detailsEnd.getChildAt(0);
        eventsDetailsEndStatic.setPadding((int) 0.18*width, (int)0.44*height, (int) 0.047*width, (int) 0.042*height);
            //set the nonstatic part of end time
        TextView eventDetailsEndTime = (TextView) detailsEnd.getChildAt(1);
        eventDetailsEndTime.setText(extras.getString("end"));
            //get layout for the separator!
        ImageView separator = (ImageView) ((LinearLayout)details.getChildAt(2)).getChildAt(0);
        separator.setMaxHeight((int)0.052*height);
        ShapeDrawable separatorRectangle = new ShapeDrawable(new RectShape());
        separatorRectangle.getPaint().setColor(-1);
        separator.setImageDrawable(separatorRectangle);
            //get the description layout
        LinearLayout description = (LinearLayout)details.getChildAt(3);
            //get the description layout static part
        TextView descriptionStatic = (TextView) description.getChildAt(0);
        descriptionStatic.setPadding((int)0.083*width, (int)0.026*height,(int)0.047*width,(int)0.026*height);
            //get the description layout nonstatic part
        TextView descriptionText = (TextView)description.getChildAt(1);
        descriptionText.setPadding((int) 0.01*width,(int)0.026*height,0,(int)0.026*height);
        descriptionText.setText(extras.getString("description"));
            //get the local. layout
        LinearLayout location = (LinearLayout)details.getChildAt(4);
            //get the location layout static part
        TextView locationStatic = (TextView) location.getChildAt(0);
        locationStatic.setPadding((int)0.14*width, (int)0.026*height,(int)0.047*width,(int)0.026*height);
            //get the location layout nonstatic part
        TextView locationText = (TextView)location.getChildAt(1);
        locationText.setPadding((int) 0.01*width, (int)0.026*height, 0,(int)0.026*height);
        locationText.setText(extras.getString("location"));
        setContentView(layout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.events_display, menu);
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

    private LayerDrawable createRectangle(int color, int colorFrom, int colorTo) {
        GradientDrawable[] layers = new GradientDrawable[2];
        layers[0] = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{colorFrom, colorTo});
        layers[0].setShape(GradientDrawable.RECTANGLE);
        layers[0].setSize(((int) (width / 10)), ((int) (width / 20)));
        //adding rounded corners
        layers[0].setCornerRadii(new float[]{16, 16, 16, 16, 0, 0, 0, 0});

        layers[1] = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{color, color});
        layers[1].setShape(GradientDrawable.RECTANGLE);
        layers[1].setSize(((int) (width / 10)), ((int) (width / 20)));
        //adding rounded corners
        layers[1].setCornerRadii(new float[]{0, 0, 0, 0, 16, 16, 16, 16});

        LayerDrawable button = new LayerDrawable(layers);
        button.setLayerInset(0,0,0,0,width/20);
        button.setLayerInset(1,0,width/20,0,0);
        return button;
    }
}
