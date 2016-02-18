package org.swain.asa.famous_pres_speeches.View;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.swain.asa.famous_pres_speeches.PresSpeechApplication;
import org.swain.asa.famous_pres_speeches.Controller.DownloadImageTask;
import org.swain.asa.famous_pres_speeches.Controller.MediaPlayerService;
import org.swain.asa.famous_pres_speeches.Model.CurrentlyPlaying;
import org.swain.asa.famous_pres_speeches.Model.Speech;
import org.swain.asa.famous_pres_speeches.Model.SpeechList;
import org.swain.asa.famous_pres_speeches.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Famous US Speeches Android Application
 * Copyright (C) 2015  Asa F. Swain
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * This class plays the recording of a speech and displays controls for the playback,
 * and buttons to go to the speech transcript and wikipeida entry
 */

public class PlayerActivity extends AppCompatActivity {

    private MediaPlayerService mediaPlayerService;
    private boolean isBound = false; //Is this Activity currently bound to the Service?
    private SeekBar volumeSeekBar;
    private SeekBar progressSeekBar;
    private TextView progressTextView;
    private String orator;
    private String title;
    // speech record currently loaded
    private Speech mySpeech;
    private int volumeUpdateCounter;

    // Google Analytics
    private Tracker mTracker;
    private final static String activityName = PlayerActivity.class.getSimpleName();

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            //This Activity is are bound to the MediaPlayerService.
            MediaPlayerService.MediaPlayerBinder binder = (MediaPlayerService.MediaPlayerBinder) service;
            mediaPlayerService = binder.getService();
            isBound = true;
            volumeSeekBar.setProgress(CurrentlyPlaying.getCurrentVolume());
            volumeUpdateCounter = 3;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
            mediaPlayerService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Bundle extras = getIntent().getExtras();
        if (extras.getString("oratorData") != null) {
            orator = extras.getString("oratorData");
        }
        if (extras.getString("titleData") != null) {
            title = extras.getString("titleData");
        }

        SpeechList mySpeechList = new SpeechList(this);
        mySpeech = mySpeechList.getSpeech(orator, title);

        if (mySpeech != null) {
            // load title and portrait for speech
            loadSpeechTitle(mySpeech);
            loadPortrait(mySpeech);

            // if another speech is playing, stop that speech
            Speech currentSpeech = CurrentlyPlaying.getCurrentlyPlayingSpeech();
            if (currentSpeech != null && !currentSpeech.equals(mySpeech)) {
                // check if we need to kill old speech
                if (CurrentlyPlaying.getCurrentlyPlayingService() != null && isMyServiceRunning()) {
                    CurrentlyPlaying.getCurrentlyPlayingService().kill();
                }
            }
        }

        volumeSeekBar = (SeekBar) findViewById(R.id.volumeSeekBar);
        progressSeekBar = (SeekBar) findViewById(R.id.progressSeekBar);
        progressTextView = (TextView) findViewById(R.id.timeElapsed);

        // set background colors and slider images for progress and volume seekbars
        volumeSeekBar.setProgressDrawable(ContextCompat.getDrawable(this, R.drawable.seek_bar));
        progressSeekBar.setProgressDrawable(ContextCompat.getDrawable(this, R.drawable.seek_bar));
        volumeSeekBar.setThumb(ContextCompat.getDrawable(this, R.drawable.seek_bar_thumb));
        progressSeekBar.setThumb(ContextCompat.getDrawable(this, R.drawable.seek_bar_thumb));

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                CurrentlyPlaying.setCurrentVolume(progress);
                volumeUpdateCounter = 3;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        progressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayerService != null && isMyServiceRunning()) {
                    int musicDuration = mediaPlayerService.getDuration();
                    int musicTime = Math.round((progress / (float) seekBar.getMax()) * musicDuration);
                    mediaPlayerService.setTime(musicTime);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        final Runnable myRunnable = new Runnable() {
            public void run() {
                if (mediaPlayerService != null) {
                    //update progress bar
                    Integer prog = (int) (mediaPlayerService.getProgress() * 100);
                    progressSeekBar.setProgress(prog);

                    // update time elapsed view
                    int elapsedMillis = mediaPlayerService.getTime();
                    String elpasedTime = String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(elapsedMillis),
                            TimeUnit.MILLISECONDS.toSeconds(elapsedMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(elapsedMillis))
                    );

                    // this code sets the volume 3 times, because for some reason it doesn't always take
                    // the first time I change the MediaPlayer volume when starting a speech
                    if (volumeUpdateCounter > 0) {
                        int currentVolume = Double.valueOf(mediaPlayerService.getVolume() * volumeSeekBar.getMax()).intValue();
                        if (currentVolume != CurrentlyPlaying.getCurrentVolume()) {
                            setMediaPlayerVolume(CurrentlyPlaying.getCurrentVolume());
                        }
                        if (volumeUpdateCounter == 1) {
                            // finally, update the saved volume in CurrentlyPlaying object with the current volume level
                            currentVolume = Double.valueOf(mediaPlayerService.getVolume() * volumeSeekBar.getMax()).intValue();
                            CurrentlyPlaying.setCurrentVolume(currentVolume);
                        }
                        volumeUpdateCounter -= 1;
                    }

                    int totalMillis = mediaPlayerService.getDuration();
                    String totalTime = String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(totalMillis),
                            TimeUnit.MILLISECONDS.toSeconds(totalMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalMillis))
                    );

                    String timeElapsed;
                    if (totalMillis != 0) {
                        timeElapsed = getResources().getString(R.string.time_elapsed) + " " + elpasedTime + " / " + totalTime;
                    } else {
                        Speech currentSpeech = CurrentlyPlaying.getCurrentlyPlayingSpeech();
                        if (mySpeech != null && currentSpeech != null && currentSpeech.equals(mySpeech)) {
                            // display "loading" message until we have loaded the speech
                            timeElapsed = getResources().getString(R.string.loading);
                        } else {
                            timeElapsed = "";
                        }
                    }
                    progressTextView.setText(timeElapsed);

                    // change pause/play button text based on if music is playing
                    final FancyButton pausePlayButton = (FancyButton) findViewById(R.id.pausePlayButton);
                    if (mediaPlayerService.isSpeechPlaying()) {
                        pausePlayButton.setText(getResources().getString(R.string.pause_button));
                    } else {
                        pausePlayButton.setText(getResources().getString(R.string.play_button));
                    }

                    CurrentlyPlaying.setCurrentlyPlayingService(mediaPlayerService);
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

        // button click listeners

        // recording playback control buttons
        final FancyButton pausePlayButton = (FancyButton) findViewById(R.id.pausePlayButton);
        pausePlayButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMyServiceRunning() && mediaPlayerService.isSpeechPlaying()) {
                    mediaPlayerService.pausePlayback();
                    // Google Analytics code
                    PresSpeechApplication application = (PresSpeechApplication) getApplication();
                    application.logGoogleAnalysticsEvent(activityName, "PauseButton", orator + "/" + title);
                } else {
                    // if we weren't playing this speech before, initialize speech playback, otherwise restart mediaplayer
                    Speech currentSpeech = CurrentlyPlaying.getCurrentlyPlayingSpeech();
                    if (currentSpeech == null || !currentSpeech.equals(mySpeech)) {
                        CurrentlyPlaying.setCurrentlyPlayingSpeech(mySpeech);
                        // configure and start playing new speech
                        initializePlayback();
                    } else {
                        mediaPlayerService.startPlayback();
                    }
                    // update volume in case user changes volume while mediaplayer was paused
                    volumeUpdateCounter = 3;

                    // Google Analytics code
                    PresSpeechApplication application = (PresSpeechApplication) getApplication();
                    application.logGoogleAnalysticsEvent(activityName, "StartButton", orator + "/" + title);
                }
            }
        });

        final FancyButton stopButton = (FancyButton) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick (View v){
                if (isMyServiceRunning()) {
                    pausePlayButton.setText(getResources().getString(R.string.play_button));
                    mediaPlayerService.stopPlayback();
                    // Google Analytics code
                    PresSpeechApplication application = (PresSpeechApplication) getApplication();
                    application.logGoogleAnalysticsEvent(activityName, "StopButton", orator + "/" + title);
                }
            }
        });

        final FancyButton rewindButton = (FancyButton) findViewById(R.id.rewindButton);
        rewindButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick (View v){
                if (isMyServiceRunning()) {
                    int timeInSeconds = 15;
                    mediaPlayerService.rewindPlayback(timeInSeconds);
                    // Google Analytics code
                    PresSpeechApplication application = (PresSpeechApplication) getApplication();
                    application.logGoogleAnalysticsEvent(activityName, "RewindButton", orator + "/" + title);
                }
            }
        });

        final FancyButton fastforwardButton = (FancyButton) findViewById(R.id.fastForwardButton);
        fastforwardButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick (View v){
                if (isMyServiceRunning()) {
                    int timeInSeconds = 15;
                    mediaPlayerService.fastForwardPlayback(timeInSeconds);
                    // Google Analytics code
                    PresSpeechApplication application = (PresSpeechApplication) getApplication();
                    application.logGoogleAnalysticsEvent(activityName, "FastForwardButton", orator + "/" + title);
                }
            }
        });

        // listener for speech transcript button
        final FancyButton speechTextButton = (FancyButton) findViewById(R.id.speechTextButton);
        speechTextButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick (View v){
                Intent intent = new Intent(getBaseContext(), SpeechTextActivity.class);
                intent.putExtra("oratorData", mySpeech.getOrator().getFullName());
                intent.putExtra("titleData", mySpeech.getTitle());
                startActivity(intent);
                // Google Analytics code
                PresSpeechApplication application = (PresSpeechApplication) getApplication();
                application.logGoogleAnalysticsEvent(activityName, "WikipediaButton", orator + "/" + title);
            }
        });

        // listener for Wikipedia button
        final FancyButton wikipediaButton = (FancyButton) findViewById(R.id.wikipediaButton);
        wikipediaButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick (View v){
                Intent intent = new Intent(getBaseContext(), WikipediaActivity.class);
                intent.putExtra("wikipediaURL", mySpeech.getWikipediaURL());
                startActivity(intent);
                // Google Analytics code
                PresSpeechApplication application = (PresSpeechApplication) getApplication();
                application.logGoogleAnalysticsEvent(activityName, "TranscriptButton", orator + "/" + title);
            }
        });

        // Google Analytics code
        // Obtain the shared Tracker instance.
        PresSpeechApplication application = (PresSpeechApplication) getApplication();
        mTracker=application.getDefaultTracker();
        application.logGoogleAnalysticsEvent(activityName,"StartButton",orator+"/"+title);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Google Analytics code
        Log.i(activityName, "Setting screen name: " + activityName);
        mTracker.setScreenName(activityName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    //Is the MediaPlayerService already running?

    private boolean isMyServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(Integer.MAX_VALUE);
        String name = MediaPlayerService.class.getName();

        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            if (runningServiceInfo.service.getClassName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Try to bind this Activity to the MediaPlayerService.
        //If successful, this will trigger a call to the onBind method of the Service, and then
        //a call to the onServiceConnected method of the ServiceConnection.
        //Create the MediaPlayerService if it does not already exist.
        Intent intent = new Intent(this, MediaPlayerService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Unbind this Activity from the MediaPlayerService.
        if (isBound) {
            isBound = false;
            mediaPlayerService = null;
            unbindService(serviceConnection);
        }
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

    private void initializePlayback() {
        if (mySpeech != null) {
            Intent intent = new Intent(this, MediaPlayerService.class);
            intent.putExtra("SpeechURL", mySpeech.getWebRecordingURL());
            ComponentName componentName = startService(intent); //calls onStartCommand

            if (componentName == null) {
                Toast toast = Toast.makeText(this, "could not start Service "
                        + MediaPlayerService.class.getName(), Toast.LENGTH_LONG);
                toast.show();
            }
        } else {
            Toast toast = Toast.makeText(this, "Speech is missing"
                    + MediaPlayerService.class.getName(), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    /*
    private void stopPlaying() {
        Intent intent = new Intent(this, MediaPlayerService.class);
        boolean stoppedService = stopService(intent); //calls onStartCommand
        if (stoppedService == false) {
            Toast toast = Toast.makeText(this, "could not find Service to stop "
                    + MediaPlayerService.class.getName(), Toast.LENGTH_LONG);
            toast.show();
        }
    }
    */

    private void loadSpeechTitle(Speech mySpeech) {
        TextView titleview = (TextView) findViewById(R.id.speechTitle);
        titleview.setText(mySpeech.getTitle());

        TextView oratorView = (TextView) findViewById(R.id.oratorNameAndYear);
        String oratorAndYear = mySpeech.getOrator().getFullName() + " (" + mySpeech.getYear() + ")";
        oratorView.setText(oratorAndYear);
    }

    /**
     * Load webview with portrait of orator of speech
     *
     * @param mySpeech - speech to load portrait URL for
     */
    private void loadPortrait(Speech mySpeech) {
        String url = mySpeech.getPortraitURL();
        new DownloadImageTask((ImageView) findViewById(R.id.portrait)).execute(url);
    }

    /**
     * Set volume of mediaPlayerService
     *
     * @param volume - integer of current volume
     */
    private void setMediaPlayerVolume(int volume) {
        volumeSeekBar = (SeekBar) findViewById(R.id.volumeSeekBar);
        if (mediaPlayerService != null) {
            //float maxVolume = volumeSeekBar.getMax();
            //float volumePercentage=(float)(1-(Math.log(maxVolume-volume)/Math.log(maxVolume)));
            float volumePercentage = volume / (float) volumeSeekBar.getMax();
            mediaPlayerService.setVolume(volumePercentage);
            Log.d("Volume", "" + volume);
        } else {
            Log.d("Volume", "fail " + volume);
        }
    }
}
