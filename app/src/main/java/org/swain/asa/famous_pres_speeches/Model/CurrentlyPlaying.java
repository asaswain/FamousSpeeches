package org.swain.asa.famous_pres_speeches.Model;

import org.swain.asa.famous_pres_speeches.Controller.MediaPlayerService;

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
 * This class keeps track of which speech is currently playing
 */

public class CurrentlyPlaying {

    static private Speech currentlyPlayingSpeech = null;
    static private MediaPlayerService currentlyPlayingService = null;
    static private int currentVolume = 50;

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

    public static int getCurrentVolume() {
        return currentVolume;
    }

    public static void setCurrentVolume(int currentVolume) {
        CurrentlyPlaying.currentVolume = currentVolume;
    }
}
