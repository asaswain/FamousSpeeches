package org.swain.asa.famous_pres_speeches.View;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.swain.asa.famous_pres_speeches.Controller.PlaybackController;
import org.swain.asa.famous_pres_speeches.Model.Speech;
import org.swain.asa.famous_pres_speeches.Model.SpeechList;
import org.swain.asa.famous_pres_speeches.PresSpeechApplication;
import org.swain.asa.famous_pres_speeches.R;

import java.io.IOException;
import java.io.InputStream;

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
 * This class displays the transcript of the speech (from the Assets folder)
 */

public class SpeechTextActivity extends AppCompatActivity {

    private String orator;
    private String title;
    private String assetTitle;

    // Google Analytics
    private Tracker mTracker;
    private final static String activityName = SpeechTextActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_text);

        Bundle extras = getIntent().getExtras();
        if (extras.getString("oratorData") != null) {
            orator = extras.getString("oratorData");
        }
        if (extras.getString("titleData") != null) {
            title = extras.getString("titleData");
            assetTitle = title;
            // purge apostrophes from speech title
            if (assetTitle != null) {
                assetTitle = assetTitle.replace("'", "");
                assetTitle = assetTitle.replace("’", "");
            }
        }

        SpeechList mySpeechList = new SpeechList(this);
        Speech mySpeech = mySpeechList.getSpeech(orator, title);

        TextView titleview = (TextView) findViewById(R.id.speechTitle);
        titleview.setText(mySpeech.getTitle());

        TextView oratorView = (TextView) findViewById(R.id.oratorNameAndYear);
        String oratorAndYear = mySpeech.getOrator().getFullName() + " (" + mySpeech.getYear() + ")";
        oratorView.setText(oratorAndYear);

        AssetManager assetManager = getAssets();
        try {
            String assetName = orator + "*" + assetTitle + ".txt";
            InputStream input = assetManager.open(assetName);

            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();

            // byte buffer into a string
            String speechText = new String(buffer);
            TextView speechTextView = (TextView) findViewById(R.id.speechText);
            speechTextView.setText(speechText);
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Cannot load transcript of this speech", Toast.LENGTH_LONG);
            toast.show();
        }

        // Google Analytics code
        // Obtain the shared Tracker instance.
        PresSpeechApplication application = (PresSpeechApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // create speech playback view if speech is playing
        PlaybackController.createControllerView(SpeechTextActivity.this, SpeechTextActivity.class);

        // Google Analytics code
        Log.i(activityName, "Setting screen name: " + activityName);
        mTracker.setScreenName(activityName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
