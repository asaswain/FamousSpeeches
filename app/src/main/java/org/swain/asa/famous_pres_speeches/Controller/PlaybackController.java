package org.swain.asa.famous_pres_speeches.Controller;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.swain.asa.famous_pres_speeches.Model.CurrentlyPlaying;
import org.swain.asa.famous_pres_speeches.Model.Speech;
import org.swain.asa.famous_pres_speeches.PresSpeechApplication;
import org.swain.asa.famous_pres_speeches.R;

import java.util.Timer;
import java.util.TimerTask;

import mehdi.sakout.fancybuttons.FancyButton;

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
 * This class controls the playback of the speech (I call methods in this class from ListActivity,
 * PlayerActivity, SpeechTextActivity, and WikipediaActivity)
 */
public class PlaybackController {

    public static void createControllerView(final Activity currentActivity, final String activityName, final Class c) {
        RelativeLayout statusLayout = (RelativeLayout) currentActivity.findViewById(R.id.statusWindow);

        final Speech currentSpeech = CurrentlyPlaying.getCurrentlyPlayingSpeech();
        final MediaPlayerService currentService = CurrentlyPlaying.getCurrentlyPlayingService();

        int layoutHeight;
        if (currentSpeech == null || !CurrentlyPlaying.isSpeechInitialized()) {
            layoutHeight = 0;
        } else {
            layoutHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,layoutHeight);
        statusLayout.setLayoutParams(params);

        if (currentSpeech != null && CurrentlyPlaying.isSpeechInitialized()) {
            TextView currentlyPlayingNameView = (TextView) currentActivity.findViewById(R.id.currentlyPlayingName);
            currentlyPlayingNameView.setText(currentSpeech.getTitle());
            // set listener for currently playing textView
            currentlyPlayingNameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadPlayerScreen(currentActivity, c, currentSpeech.getOrator().getFullName(), currentSpeech.getTitle());
                }
            });

            final FancyButton currentlyPlayingButton = (FancyButton) currentActivity.findViewById(R.id.currentlyPlayingButton);

            final String orator = currentSpeech.getOrator().getFullName();
            final String title = currentSpeech.getTitle();

            currentlyPlayingButton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (currentService.isSpeechPlaying()) {
                        pauseSpeech(currentService);

                        // Google Analytics code
                        PresSpeechApplication application = (PresSpeechApplication) currentActivity.getApplication();
                        application.logGoogleAnalysticsEvent(activityName, "PauseButton", orator + "/" + title);
                    } else {
                        startSpeech(CurrentlyPlaying.getCurrentlyPlayingSpeech(), currentActivity, currentService);

                        // Google Analytics code
                        PresSpeechApplication application = (PresSpeechApplication) currentActivity.getApplication();
                        application.logGoogleAnalysticsEvent(activityName, "PlayButton", orator + "/" + title);
                    }
                }
            });

            final Runnable myRunnable = new Runnable() {
                public void run() {
                    Log.d("is playing = ", "" + CurrentlyPlaying.getCurrentlyPlayingService().isSpeechPlaying());
                    if (CurrentlyPlaying.getCurrentlyPlayingService().isSpeechPlaying()) {
                        currentlyPlayingButton.setText(currentActivity.getResources().getString(R.string.pause_button));
                    } else {
                        currentlyPlayingButton.setText(currentActivity.getResources().getString(R.string.play_button));
                    }
                }
            };

            final Handler myHandler = new Handler();
            Timer myTimer = new Timer();
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    myHandler.post(myRunnable);
                }
            }, 0, 1000);
        }
    }

    /**
     * This starts playing the mediaPlayer and initialized the speech playback if necessary
     */
    public static void startSpeech(Speech currentSpeech, Activity currentActivity, MediaPlayerService mediaPlayerService) {
        if (CurrentlyPlaying.isSpeechInitialized()) {
            mediaPlayerService.startPlayback();
        } else {
            Intent intent = new Intent(currentActivity, MediaPlayerService.class);
            intent.putExtra("SpeechURL", currentSpeech.getWebRecordingURL());
            ComponentName componentName = currentActivity.startService(intent); //calls onStartCommand
            if (componentName != null) {
                CurrentlyPlaying.setIsSpeechInitialized(true);
            } else {
                Toast toast = Toast.makeText(currentActivity, "could not start Service "
                        + MediaPlayerService.class.getName(), Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    // pause current speech
    public static void pauseSpeech(MediaPlayerService mediaPlayerService) {
        mediaPlayerService.pausePlayback();
    }

    // load PlayerActivity screen for current speech
    public static void loadPlayerScreen(Activity currentActvity, Class c, String oratorName, String speechName) {
        Intent intent = new Intent(currentActvity.getBaseContext(), c);
        intent.putExtra("oratorData", oratorName);
        intent.putExtra("titleData", speechName);
        currentActvity.startActivity(intent);
    }

}
