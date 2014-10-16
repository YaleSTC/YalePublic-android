package edu.yalestc.yalepublic;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class VideoYoutubePlayback extends Activity {
    private YouTubePlayerView playingWindow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialize the YTPlayerView - here we will get a handle to the actual player
        //see onInitializationSuccess
        playingWindow.initialize(new DeveloperKey().DEVELOPER_KEY, new OnInitializedListener(){

            @Override
            //if we fail, just log it and return
            public void onInitializationFailure(Provider arg0,
                    YouTubeInitializationResult arg1) {
               Log.d("YouTubePlayer", "Failed to initialize an instance of YouTubePlayer.");
               Log.d("YouTubePlayer", "Guilty provider:" + arg0.toString() + " Error: " + arg1.toString());
               return;
                
            }
            //if success consider if we are resuming or creating a new instance
            //player is the actual instance of the player!
            @Override
            public void onInitializationSuccess(Provider arg0,
                    YouTubePlayer player, boolean wasRestored) {
                Log.d("YouTubePlayer", "Initialization of an instance of YouTubePlayer done.");
                if(wasRestored){
                    player.play();
                } else {
                    player.loadVideo(getIntent().getExtras().getString("videoId"));
                    player.play();
                }
                
            }
            
        });
        setContentView(playingWindow);
    }

}
