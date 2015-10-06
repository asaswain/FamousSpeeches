package edu.nyu.scps.SpeechPlayer.View;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import edu.nyu.scps.SpeechPlayer.Controller.MediaPlayerService;
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

    SpeechList mySpeechList;

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

        mySpeechList = new SpeechList();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            orator = extras.getString("orator");
            title = extras.getString("title");
        }

        if (!isMyServiceRunning()) {
            startPlaying();
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
                if (mediaPlayerService != null && fromUser) {
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
                    Integer prog = (int) (mediaPlayerService.getProgress() * 100);
                    progressSeekBar.setProgress(prog);

                    int millis = mediaPlayerService.getTime();
                    String time = String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(millis),
                            TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
                    );

                    progressTextView.setText("Time Elapsed: " + time);
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

        final Button pausePlayButton = (Button) findViewById(R.id.pausePlayButton);
        pausePlayButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMyServiceRunning()) {
                    if (pausePlayButton.getText() == "Pause") {
                        mediaPlayerService.pausePlayback();
                        pausePlayButton.setText(getResources().getString(R.string.play_button));
                    } else {
                        mediaPlayerService.startPlayback();
                        pausePlayButton.setText(getResources().getString(R.string.pause_button));
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
        Speech mySpeech = mySpeechList.getSpeech(orator, title);
        if (mySpeech != null){
            Intent intent = new Intent(this, MediaPlayerService.class);
            intent.putExtra("SpeechURL", mySpeech.getWebUrl());
            ComponentName componentName = startService(intent); //calls onStartCommand

            loadSpeechTitle(mySpeech);
            loadWikipediaPage(mySpeech);

            if (componentName == null) {
                Toast toast = Toast.makeText(this, "could not start Service"
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

    /**
     * Load webview with Wikipedia page URL for speech
     * @param mySpeech - speech to load URL for
     */
    private void loadWikipediaPage(Speech mySpeech) {
        WebView webview = (WebView)findViewById(R.id.wikipediaPage);
        //WebSettings settings = webview.getSettings();
        //settings.setJavaScriptEnabled(true);
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        //final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        String loadingTitle = getResources().getString(R.string.loading_title);
        String loadingMessage = getResources().getString(R.string.loading_message);
        final ProgressDialog progressBar = ProgressDialog.show(PlayerActivity.this, loadingTitle, loadingMessage);

        webview.setWebViewClient(new WebViewClient() {
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

            /* commented out method because it is depricated

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e(TAG, "Error: " + description);
                Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
                alertDialog.setTitle("Error");
                alertDialog.setMessage(description);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                alertDialog.show();
            }
            */
        });
        if (mySpeech.getWikipediaURL().equals("")) {
            webview.loadUrl("about:blank");
        } else {
            String s = mySpeech.getWikipediaURL();
            webview.loadUrl(s);
        }
    }
}
