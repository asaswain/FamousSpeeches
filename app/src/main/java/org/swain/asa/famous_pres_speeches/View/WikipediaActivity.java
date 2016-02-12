package org.swain.asa.famous_pres_speeches.View;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.swain.asa.famous_pres_speeches.AnalyticsApplication;
import org.swain.asa.famous_pres_speeches.R;

/**
 * Famous US Speeches Android Application
 * Copyright (C) 2015  Asa F. Swain
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * This class displays the Wikipedia entry of the speech
 */

public class WikipediaActivity extends AppCompatActivity {

    // Google Analytics
    private Tracker mTracker;
    private static final String activityName = ListActivity.class.getSimpleName();

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

        // Google Analytics code
        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Google Analytics code
        Log.i(activityName, "Setting screen name: " + activityName);
        mTracker.setScreenName(activityName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
