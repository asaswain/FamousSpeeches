package edu.nyu.scps.SpeechPlayer.View;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import edu.nyu.scps.SpeechPlayer.R;

public class WikipediaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wikipedia);

        String wikipediaURL = "";
        Bundle extras = getIntent().getExtras();
        if (extras.getString("wikipediaURL") != null) {
            wikipediaURL = extras.getString("wikipediaURL");
        }

        // Load webview with Wikipedia page URL
        WebView webView = (WebView) findViewById(R.id.wikipedia);
        //WebSettings settings = webview.getSettings();
        //settings.setJavaScriptEnabled(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        //final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        String loadingTitle = getResources().getString(R.string.wikipedia_loading_title);
        String loadingMessage = getResources().getString(R.string.loading_message);
        final ProgressDialog progressBar = ProgressDialog.show(this, loadingTitle, loadingMessage);

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //Log.i("Webview", "Loading Wikipedia Page For Speech");
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                //Log.i("Webview", "Finished Loading Page");
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }
        });

        if (wikipediaURL == null || wikipediaURL.equals("")) {
            webView.loadUrl("about:blank");
        } else {
            webView.loadUrl(wikipediaURL);
        }
    }
}
