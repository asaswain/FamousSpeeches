package org.swain.asa.SpeechPlayer.Model;

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
 * This class is used as a data structure to store the link and information about an audio recording of a speech
 */
public class Speech {

    // title of speech
    private String title;
    // name of person who gave speech
    private Orator orator;
    // URL of mp3 recording of speech
    private String webRecordingURL;
    // URL of wikipedia page about speech
    private String wikipediaURL;
    // year speech was recorded
    int year;
    // length of speech in seconds
    int lengthInSeconds;

    public Speech() {
        this.title = "";
        this.orator = null;
        this.webRecordingURL = "";
        this.wikipediaURL = "";
        this.year = 0;
        this.lengthInSeconds = 0;
    }

    // copy constructor
    public Speech(Speech other) {
        if (other != null) {
            this.title = other.title;
            this.orator = other.orator;
            this.webRecordingURL = other.webRecordingURL;
            this.wikipediaURL = other.wikipediaURL;
            this.year = other.year;
            this.lengthInSeconds = other.lengthInSeconds;
        }
    }

    public Speech(String title, Orator orator) {
        this();
        this.title = title;
        this.orator = orator;
    }

    public Speech(String title, Orator orator, String webUrl, String wikipediaURL, int year, int lengthInSeconds) {
        this.title = title;
        this.orator = orator;
        this.webRecordingURL = webUrl;
        this.wikipediaURL = wikipediaURL;
        this.year = year;
        this.lengthInSeconds = lengthInSeconds;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Orator getOrator() {
        return new Orator(orator);
    }

    public void setOrator(Orator orator) {
        this.orator = new Orator(orator);
    }

    public String getPortraitURL() {
        return orator.getPortraitURL();
    }

    public void setPortraitURL(String portraitURL) {
        this.orator.setPortraitURL(portraitURL);
    }

    public String getWebRecordingURL() {
        return webRecordingURL;
    }

    public void setWebRecordingURL(String webRecordingURL) {
        this.webRecordingURL = webRecordingURL;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getWikipediaURL() {
        return wikipediaURL;
    }

    public void setWikipediaURL(String wikipediaURL) {
        this.wikipediaURL = wikipediaURL;
    }

    public int getLengthInSeconds() {
        return lengthInSeconds;
    }

    public void setLengthInSeconds(int lengthInSeconds) {
        this.lengthInSeconds = lengthInSeconds;
    }

    public String toString() {
        return orator + " " + title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Speech speech = (Speech) o;
        return (orator.equals(speech.orator) && title.equals(speech.title));
    }

    @Override
    public int hashCode() {
        int hash = 0;
        if (orator != null && title != null) {
            hash = (31 * title.hashCode()) + orator.hashCode();
        }
        return hash;
    }

    // this method is designed to allow a user to generate a hashcode with just a title and an orator,
    // without having to create a new Speech object
    public static int getHashCode(Orator orator, String title) {
        Speech dummySpeech = new Speech(title, orator);
        return dummySpeech.hashCode();
    }
}
