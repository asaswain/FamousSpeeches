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
        this.wikipediaURL = "";
        this.year = 0;
    }

    // copy constructor
    public Speech(Speech other) {
        this.title = other.title;
        this.orator = other.orator;
        this.webUrl = other.webUrl;
        this.wikipediaURL = other.wikipediaURL;
        this.year = other.year;
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
        int result = title.hashCode();
        result = 31 * result + orator.hashCode();
        return result;
    }

    // this method is designed to allow a user to generat a hashcode with just a title and an orator,
    // without having to create a new Speech object
    public static int getHashCode(String title, String orator) {
        int result = title.hashCode();
        result = 31 * result + orator.hashCode();
        return result;
    }
}
