package edu.nyu.scps.SpeechPlayer.Controller;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import java.io.IOException;

import edu.nyu.scps.SpeechPlayer.R;

/**
 * This class plays speeches in a service
 */

public class MediaPlayerService extends Service implements AudioManager.OnAudioFocusChangeListener{
    private MediaPlayer mediaPlayer;
    private final float quietVolume = 0.1f;
    private final float defaultVolume = 0.5f;
    private float volume = defaultVolume; //0.0f is minimum, 1.0f is maximum
    private int duration = 0;
    private MediaPlayerBinder mediaPlayerBinder = new MediaPlayerBinder();
    boolean durationSaved = false;
    boolean isPlaying = false;

    public MediaPlayerService() {
    }

    // onCreate is called when service is first created
    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
    }

    // onStartCommand is called by the startService method of the Activity
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // read webpage address of speech mp3 file from Intent object
        String speechURL = (String) intent.getExtras().get("SpeechURL");
        //Uri speechURL = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.musette);

        if (speechURL != null) {
            try {
                mediaPlayer.setDataSource(speechURL);
                //mediaPlayer.setDataSource(this,speechURL);
            } catch (IOException iOException) {
                Toast toast = Toast.makeText(MediaPlayerService.this, iOException.toString(), Toast.LENGTH_LONG);
                toast.show();
            }

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    isPlaying = false;
                    MediaPlayerService.this.stopSelf();  //calls onDestroy, below
                }
            });

            mediaPlayer.prepareAsync(); //will eventually call the onPrepared method of the OnPreparedListener

            durationSaved = false;

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    startPlayback();
                }
            });
        }

        return super.onStartCommand(intent, flags, startId);
    }

    // get current volume of playback
    public double getVolume() {
        return volume;
    }

    // set volume of playback
    public void setVolume(float volume) {
        if (mediaPlayer.isPlaying()) {
            this.volume = volume;
            mediaPlayer.setVolume(volume, volume);  //left, right
        }
    }

    // get a percentage of how far we are through the recording (between 0.00 and 1.00)
    public double getProgress() {
        return calcProgress();
    }

    // get time (in milliseconds) of how far we are through the recording
    public int getTime() {
        return calcTime();
    }

    // set time (in milliseconds) for where we are in the recording
    public void setTime(int newTime) {
        mediaPlayer.pause();
        mediaPlayer.seekTo(newTime);
        // if currently playing speech, continue playback after setting time
        if (isSpeechPlaying()) {
            startPlayback();
        }
    }

    // get duration (in milliseconds) of the recording
    public int getDuration() {
        return duration;
    }

    // set the duration of the recording
    private void setDuration(int duration) {
        if (duration > 0) {
            this.duration = duration;
            durationSaved = true;
        }
    }

    // if the mediaplayer is not null, then return the current time we are in the recording (in milliseconds)
    private int calcTime() {
        int currentTime = 0;
        if (mediaPlayer != null) {
            if (mediaPlayer.getCurrentPosition() > 0) {
                currentTime = mediaPlayer.getCurrentPosition();
            }
        }
        return currentTime;
    }

    // if the mediaplayer is not null then set durection of recording (if not already set),
    // and then calculate percnetage if the records that has been played using the time and the duration
    private double calcProgress() {
        double progress = 0;
        if (mediaPlayer != null) {
            // initialize duration of recording only once
            if (!durationSaved && isPlaying) {
                int duration = mediaPlayer.getDuration();
                setDuration(duration);
            }
            if (duration > 0) {
                progress = (double) calcTime() / duration;
            }
        }

        return progress;
    }

    // start playing record
    public void startPlayback() {
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mediaPlayer.start();
            isPlaying = true;
        } else {
            Toast toast = Toast.makeText(MediaPlayerService.this, "app denied access to start playing speech", Toast.LENGTH_LONG);
            toast.show();
            isPlaying = false;
        }
    }

    // pause playing records
    public void pausePlayback() {
        mediaPlayer.pause();
        isPlaying = false;
    }

    // stop playing recording and reset time to zero
    public void stopPlayback() {
        pausePlayback();
        mediaPlayer.seekTo(0);
    }

    // rewind playing of recording by a certain number of seconds
    public void rewindPlayback(int seconds) {
        int newTime = calcTime() - seconds*1000;
        if (newTime < 0) {
            newTime = 0;
        }
        setTime(newTime);
    }

    // fast forward playing of recording by a certain number of seconds
    public void fastForwardPlayback(int seconds) {
        int newTime = calcTime() + seconds*1000;
        if (newTime > duration) {
            newTime = duration;
        }
        setTime(newTime);
    }

    // onDestroy is called by stopSelf
    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    // the getService method of the MediaPlayerBinder returns the service to the actiivty class
    public class MediaPlayerBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    // onBind is called by the bindService method of an Activity
    @Override
    public IBinder onBind(Intent intent) {
        //Return the communication channel to the service.
        return mediaPlayerBinder;
    }

    // onAudioFocusChanges is called when a chance has occurred to the focus of the audio playback
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                //Resume playback.
                //Called only when focus is regained, not the first time focus is acquired.
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer.create(this, R.raw.musette);
                }
                mediaPlayer.setVolume(volume, volume);
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                //Lost focus for a long time.
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                //Lost focus for short time, but playback is likely to resume.
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                //Lost focus for short time, but can keep playing quietly.
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.setVolume(quietVolume, quietVolume);
                }
                break;

            default:
                break;
        }
    }

    public boolean isSpeechPlaying() {
        return isPlaying;
    }

    // end and kill current playback
    public void kill() {
        MediaPlayerService.this.stopSelf();  //calls onDestroy method
    }
}
