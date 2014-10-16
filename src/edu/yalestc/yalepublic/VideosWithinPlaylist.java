package edu.yalestc.yalepublic;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import android.util.Log;
import android.app.Activity;
import android.os.Bundle;

public class VideosWithinPlaylist extends Activity {
    YouTubeThumbnailView thumbnails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        thumbnails = new YouTubeThumbnailView(this);
        super.onCreate(savedInstanceState);
        thumbnails.initialize(new DeveloperKey().DEVELOPER_KEY, new YouTubeThumbnailView.OnInitializedListener(){

            @Override
            public void onInitializationFailure(YouTubeThumbnailView arg0,
                    YouTubeInitializationResult arg1) {
                Log.e("YTThumbnailView", "I have failed to initialize the thumbnailLoader!"); 
            }
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView arg0,
                    YouTubeThumbnailLoader arg1) {
                Log.v("YTThumbnailView", "I have successfully initialized the thumbnailLoader!");
                String playlistId = getIntent().getStringExtra("playlistId");
                arg1.setPlaylist(playlistId);
                
            }
            
        });
        setContentView(thumbnails);
}
    
}
