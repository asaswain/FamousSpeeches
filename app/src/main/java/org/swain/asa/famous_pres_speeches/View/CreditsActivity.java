package org.swain.asa.famous_pres_speeches.View;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.swain.asa.famous_pres_speeches.PresSpeechApplication;
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
 * This class displays credits for the application
 */

public class CreditsActivity extends AppCompatActivity {

    // Google Analytics
    private Tracker mTracker;
    private final static String activityName = CreditsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        // Google Analytics code
        // Obtain the shared Tracker instance.
        PresSpeechApplication application = (PresSpeechApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Google Analytics code
        Log.i(getClass().getSimpleName(), "Setting screen name: " + activityName);
        mTracker.setScreenName(activityName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
