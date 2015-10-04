package edu.nyu.scps.SpeechPlayer.Model;

/**
 * This class contains the link and information about an audio recording of a single speech
 */
public class Speech {

    private String title;
    private String orator;
    private String webUrl;
    private String wikipediaURL;
    int year;

    public Speech() {
        this.title = "";
        this.orator = "";
        this.webUrl = "";
        this.year = 0;
        this.wikipediaURL = "";
    }

    public Speech(String title, String orator, String webUrl, String wikipediaURL, int year) {
        this.title = title;
        this.orator = orator;
        this.webUrl = webUrl;
        this.wikipediaURL = wikipediaURL;
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrator() {
        return orator;
    }

    public void setOrator(String orator) {
        this.orator = orator;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
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
}
