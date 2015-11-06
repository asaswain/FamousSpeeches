package edu.nyu.scps.SpeechPlayer.Model;

/**
 * This class contains the link and information about an audio recording of a single speech
 */
public class Speech {

    private String title;
    private Orator orator;
    private String webRecordingURL;
    private String wikipediaURL;
    int year;

    public Speech() {
        this.title = "";
        this.orator = null;
        this.webRecordingURL = "";
        this.wikipediaURL = "";
        this.year = 0;
    }

    // copy constructor
    public Speech(Speech other) {
        this.title = other.title;
        this.orator = other.orator;
        this.webRecordingURL = other.webRecordingURL;
        this.wikipediaURL = other.wikipediaURL;
        this.year = other.year;
    }

    public Speech(String title, Orator orator) {
        this.title = title;
        this.orator = orator;
        this.webRecordingURL = "";
        this.wikipediaURL = "";
        this.year = 0;
    }

    public Speech(String title, Orator orator, String webUrl, String wikipediaURL, int year) {
        this.title = title;
        this.orator = orator;
        this.webRecordingURL = webUrl;
        this.wikipediaURL = wikipediaURL;
        this.year = year;
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

    // this method is designed to allow a user to generate a hashcode with just a title and an orator,
    // without having to create a new Speech object
    public static int getHashCode(Orator orator, String title) {
        int hash = 0;
        if (orator != null && title != null) {
            Speech dummySpeech = new Speech(title, orator);
            hash = dummySpeech.hashCode();
        }
        return hash;
    }

}
