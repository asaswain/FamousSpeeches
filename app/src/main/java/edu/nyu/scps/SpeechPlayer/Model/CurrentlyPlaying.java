package edu.nyu.scps.SpeechPlayer.Model;

import edu.nyu.scps.SpeechPlayer.Controller.MediaPlayerService;

/**
 * This class keeps track of which speech is currently playing
 */
public class CurrentlyPlaying {

    static private Speech currentlyPlayingSpeech = null;
    static private MediaPlayerService currentlyPlayingService = null;

    public static Speech getCurrentlyPlayingSpeech() {
        return currentlyPlayingSpeech;
    }

    public static void setCurrentlyPlayingSpeech(Speech newSpeech) {
        CurrentlyPlaying.currentlyPlayingSpeech = new Speech(newSpeech);
    }

    public static MediaPlayerService getCurrentlyPlayingService() {
        return currentlyPlayingService;
    }

    public static void setCurrentlyPlayingService(MediaPlayerService currentlyPlayingService) {
        CurrentlyPlaying.currentlyPlayingService = currentlyPlayingService;
    }
}
