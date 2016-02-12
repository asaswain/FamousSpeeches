package org.swain.asa.famous_pres_speeches.View;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.swain.asa.famous_pres_speeches.AnalyticsApplication;
import org.swain.asa.famous_pres_speeches.Controller.DownloadImageTask;
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
 * This class displays the title screen for the application
 */

public class MainActivity extends AppCompatActivity {

    private Handler mHandler = new Handler();
    private boolean isListActivityNotLoaded;

    // Google Analytics
    private Tracker mTracker;
    private static final String activityName = ListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // load capitol image
        loadTitleImage();

        // set listener for capitol image, to go to list of speeches
        ImageView capitol = (ImageView) findViewById(R.id.capitol);
        capitol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadListActivity();
            }
        });

        // Google Analytics code
        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isListActivityNotLoaded = true;

        // load list page automatically after a short delay
        mHandler.postDelayed(new Runnable() {
            public void run() {
                loadListActivity();
            }
        }, 5000);

        // Google Analytics code
        Log.i(activityName, "Setting screen name: " + activityName);
        mTracker.setScreenName(activityName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    /**
     * Load ListActivity screen
     */
    private void loadListActivity() {
        // this is to keep the ListActivity from loading twice if user clicks on screen
        if (isListActivityNotLoaded) {
            Intent intent = new Intent(getBaseContext(), ListActivity.class);
            startActivity(intent);
            isListActivityNotLoaded = false;
        }
    }

    /**
     * Load webview with title image
     */
    private void loadTitleImage() {
        new DownloadImageTask((ImageView) findViewById(R.id.capitol)).execute(getResources().getString(R.string.capitol_image));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // don't build menu
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
