package edu.nyu.scps.SpeechPlayer.Controller;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import edu.nyu.scps.SpeechPlayer.R;

/**
 * This class play speeches in a service
 */

public class MediaPlayerService extends Service implements AudioManager.OnAudioFocusChangeListener{
    private MediaPlayer mediaPlayer;
    private final float quietVolume = 0.1f;
    private final float defaultVolume = 0.5f;
    private float volume = defaultVolume; //0.0f is minimum, 1.0f is maximum
    private int duration = 0;
    private MediaPlayerBinder mediaPlayerBinder = new MediaPlayerBinder();
    boolean durationSaved = false;

    public MediaPlayerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        //todo: solve "E/MediaPlayer﹕ Should have subtitle controller already set" error
        //mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    //called by the startService method of the Activity

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // read webpage address of speech mp3 file from Intent object
        String speechURL = (String) intent.getExtras().get("SpeechURL");
        if (speechURL != null) {
            try {
                mediaPlayer.setDataSource(speechURL);
            } catch (IOException iOException) {
                Toast toast = Toast.makeText(MediaPlayerService.this, iOException.toString(), Toast.LENGTH_LONG);
                toast.show();
            }

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    MediaPlayerService.this.stopSelf();  //calls onDestroy, below
                }
            });

            mediaPlayer.prepareAsync(); //will eventually call the onPrepared method of the OnPreparedListener

            durationSaved = false;

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    play();
                }
            });
        }

        return super.onStartCommand(intent, flags, startId);
    }

    //getters and setters

    public double getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        if (mediaPlayer.isPlaying()) {
            this.volume = volume;
            mediaPlayer.setVolume(volume, volume);  //left, right
        }
    }

    public double getProgress() {
        return calcProgress();
    }

    public int getTime() {
        return calcTime();
    }

    public void setTime(int newTime) {
        mediaPlayer.pause();
        mediaPlayer.seekTo(newTime);
        play();
    }

    public int getDuration() {
        return duration;
    }

    private int calcTime() {
        int currentTime = 0;
        if (mediaPlayer != null) {
            if (mediaPlayer.getCurrentPosition() > 0) {
                currentTime = mediaPlayer.getCurrentPosition();
            }
        }
        return currentTime;
    }

    private double calcProgress() {
        double progress = 0;
        if (mediaPlayer != null) {
            // initialize duration of speec only once
            if (!durationSaved) {
                if (mediaPlayer.getDuration() > 0) {
                    duration = mediaPlayer.getDuration();
                    Log.d("duration", "" + duration);
                    durationSaved = true;
                }
            }
            if (duration > 0) {
                progress = (double) calcTime() / duration;
            }
        }

        return progress;
    }

    public void play() {
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mediaPlayer.start();
        } else {
            Toast toast = Toast.makeText(MediaPlayerService.this, "app denied access to start playing speech", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void stop() {
        mediaPlayer.pause();
        mediaPlayer.seekTo(0);
    }

    public void rewind(int seconds) {
        int newTime = calcTime() - seconds*1000;
        if (newTime < 0) {
            newTime = 0;
        }
        setTime(newTime);
    }

    public void fastForward(int seconds) {
        int newTime = calcTime() + seconds*1000;
        if (newTime > duration) {
            newTime = duration;
        }
        setTime(newTime);
    }

    //called by stopSelf

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    public class MediaPlayerBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    //called by the bindService method of an Activity.

    @Override
    public IBinder onBind(Intent intent) {
        //Return the communication channel to the service.
        return mediaPlayerBinder;
    }

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
}