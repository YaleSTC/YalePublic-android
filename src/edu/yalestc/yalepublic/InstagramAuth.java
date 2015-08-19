package edu.yalestc.yalepublic;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jinstagram.auth.InstagramAuthService;
import org.jinstagram.auth.model.Token;
import org.jinstagram.auth.model.Verifier;
import org.jinstagram.auth.oauth.InstagramService;

/**
 * This class handles Instagram authentication for user
 */


public class InstagramAuth extends Activity {

    public static final String CALLBACK_URL = "http://www.yale.edu/";
    private static String access;

    //build the base url with client_id, client_secret and redirect url
    private final InstagramService service = new InstagramAuthService()
            .apiKey(DeveloperKey.INSTAGRAM_CLIENT_ID)
            .apiSecret(DeveloperKey.INSTAGRAM_CLIENT_SECRET)
            .callback(CALLBACK_URL)
            .scope("likes")
            .build();
    private static final Token EMPTY_TOKEN = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram_auth);
        PhotoAuth gettingDetails = new PhotoAuth();
        gettingDetails.authorizeUser();
    }

    public class PhotoAuth {

        //generate authorization url
        public void authorizeUser() {
            String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
            Log.d("Auth", authorizationUrl);
            //get Instagram code from authorization url
            WebView webview = new WebView(getApplicationContext());
            webview.setWebViewClient(new WebViewClient() {
                String code = null;
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {

                    if (url.startsWith(CALLBACK_URL)) {
                        Log.d("Auth", url);
                        if (url.contains("code=")) {
                            code = url.split("=")[1];
                            Log.d("Auth", code);
                        } else if (url.contains("error=access_denied")) {
                            Log.d("Auth", "Access denied");
                        }
                        AlbumTask get = new AlbumTask();
                        get.execute(code);
                        Intent intent = new Intent(InstagramAuth.this, PhotosWithinAlbum.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                        return true;
                    }
                    //load url
                    return super.shouldOverrideUrlLoading(view, url);
                }

            });
            Log.d("Auth", "loading webview");
            setContentView(webview);
            webview.loadUrl(authorizationUrl);
        }

    }
    public class AlbumTask extends AsyncTask<String, Integer, Void> {

        //this method gets the access token for user given some code from Instagram
        @Override
        protected Void doInBackground(String... params) {
            Verifier verifier = new Verifier(params[0]);
            Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
            Log.d("Access", accessToken.getToken());
            access = accessToken.getToken();
            return null;

        }
    }

    public static String getAccess() {
        return access;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_instagram_auth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //If skip button is clicked, the photosWithinAlbum class will come up
        if (id == R.id.skip_button) {
            Intent intent = new Intent(this, PhotosWithinAlbum.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
