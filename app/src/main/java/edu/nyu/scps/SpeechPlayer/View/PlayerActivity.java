package edu.nyu.scps.SpeechPlayer.View;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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

import java.io.InputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import edu.nyu.scps.SpeechPlayer.Controller.MediaPlayerService;
import edu.nyu.scps.SpeechPlayer.Model.CurrentlyPlaying;
import edu.nyu.scps.SpeechPlayer.Model.Speech;
import edu.nyu.scps.SpeechPlayer.Model.SpeechList;
import edu.nyu.scps.SpeechPlayer.R;

/**
 * This class plays the recording of a speech and displays the Wikipedia entry for that speech
 */

public class PlayerActivity extends AppCompatActivity {
    private MediaPlayerService mediaPlayerService;
    private boolean isBound = false; //Is this Activity currently bound to the Service?
    private SeekBar volumeSeekBar;
    private SeekBar progressSeekBar;
    private TextView progressTextView;
    private String orator;
    private String title;

    // speech record current loaded
    private Speech mySpeech;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            //This Activity is are bound to the MediaPlayerService.
            MediaPlayerService.MediaPlayerBinder binder = (MediaPlayerService.MediaPlayerBinder) service;
            mediaPlayerService = binder.getService();
            isBound = true;
            volumeSeekBar.setProgress((int) (mediaPlayerService.getVolume() * volumeSeekBar.getMax()));
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
            Speech currentSpeech = CurrentlyPlaying.getCurrentlyPlayingSpeech();
            if (currentSpeech == null || !currentSpeech.equals(mySpeech)) {
                // check if we need to kill old speech
                if (CurrentlyPlaying.getCurrentlyPlayingService() != null && isMyServiceRunning()) {
                    CurrentlyPlaying.getCurrentlyPlayingService().kill();
                }
                // start playing new speech
                startPlaying();
                CurrentlyPlaying.setCurrentlyPlayingSpeech(mySpeech);
            }

            // load title and portrait for speech
            loadSpeechTitle(mySpeech);
            loadPortrait(mySpeech);
        }

        volumeSeekBar = (SeekBar)findViewById(R.id.volumeSeekBar);
        progressSeekBar = (SeekBar)findViewById(R.id.progressSeekBar);
        progressTextView = (TextView) findViewById(R.id.timeElapsed);

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayerService != null) {
                    float volume = progress / (float) seekBar.getMax();
                    mediaPlayerService.setVolume(volume);
                }
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
                    int millis = mediaPlayerService.getTime();
                    String time = String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(millis),
                            TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
                    );

                    progressTextView.setText("Time Elapsed: " + time);

                    // change pause/play button text based on if music is playing
                    final Button pausePlayButton = (Button) findViewById(R.id.pausePlayButton);
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

        // display wikipedia screen button
        final Button wikipediaButton = (Button) findViewById(R.id.wikipediaButton);
        wikipediaButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button wikipediaButton = (Button) v;

                Intent intent = new Intent(getBaseContext(), WikipediaActivity.class);
                intent.putExtra("wikipediaURL", mySpeech.getWikipediaURL());
                startActivity(intent);
            }
        });

        // recording playback control buttons
        final Button pausePlayButton = (Button) findViewById(R.id.pausePlayButton);
        pausePlayButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMyServiceRunning()) {
                    if (mediaPlayerService.isSpeechPlaying()) {
                        mediaPlayerService.pausePlayback();
                    } else {
                        mediaPlayerService.startPlayback();
                    }
                }
            }

        });

        final Button stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMyServiceRunning()) {
                    pausePlayButton.setText(getResources().getString(R.string.play_button));
                    mediaPlayerService.stopPlayback();
                }
            }
        });

        final Button rewindButton = (Button) findViewById(R.id.rewindButton);
        rewindButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMyServiceRunning()) {
                    int timeInSeconds = 15;
                    mediaPlayerService.rewindPlayback(timeInSeconds);
                }
            }
        });

        final Button fastforwardButton = (Button) findViewById(R.id.fastForwardButton);
        fastforwardButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMyServiceRunning()) {
                    int timeInSeconds = 15;
                    mediaPlayerService.fastForwardPlayback(timeInSeconds);
                }
            }
        });
    }

    //Is the MediaPlayerService already running?

    private boolean isMyServiceRunning() {
        ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void startPlaying() {
        SpeechList mySpeechList = new SpeechList(this);
        mySpeech = mySpeechList.getSpeech(orator, title);
        if (mySpeech != null){
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
        TextView textview = (TextView)findViewById(R.id.speechTitle);
        textview.setText(mySpeech.getTitle());
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    /**
     * Load webview with portrait of orator of speech
     * @param mySpeech - speech to load portrait URL for
     */
    private void loadPortrait(Speech mySpeech) {
        if (mySpeech.getPortraitURL().equals("")) {
            new DownloadImageTask((ImageView) findViewById(R.id.portrait)).execute("");
        } else {
            String s = mySpeech.getPortraitURL();
            new DownloadImageTask((ImageView) findViewById(R.id.portrait)).execute(s);
        }
    }

    private void hideWikipediaPage() {
        //WebView webView = (WebView)findViewById(R.id.wikipediaPage);
        //webView.loadUrl("about:blank");
    }
}
