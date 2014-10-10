package edu.yalestc.yalepublic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.os.Build;

public class VideoList extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
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
            View rootView = inflater.inflate(R.layout.fragment_video_list,
                    container, false);
            
            
            
            //List<String> videos = new ArrayList<String>(Arrays.asList(countries));
            
            String[] video_arrays = {"video1", "video2"};
            List<String> videos = new ArrayList<String>(Arrays.asList(video_arrays));
            
            final ArrayAdapter<String> mVideoAdapter = new ArrayAdapter<String>(
                    getActivity(), R.layout.tab, R.id.tab, videos);
            
            ListView listView = (ListView) rootView.findViewById(R.id.listview_video);
            listView.setAdapter(mVideoAdapter);

            return rootView;
        }
    }
}
