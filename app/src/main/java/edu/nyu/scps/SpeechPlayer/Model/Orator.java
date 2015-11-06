package edu.nyu.scps.SpeechPlayer.Model;

/**
 * An orator consists of a full name and a URL of a portrait image
 */
public class Orator {

    private String fullName;
    private String portraitURL;

    // copy constructor
    public Orator(Orator other) {
        this.fullName = other.fullName;
        this.portraitURL = other.portraitURL;
    }

    public Orator(String subjectName) {
        this.fullName = subjectName;
    }

    public Orator(String subjectName, String imageURL) {
        this.fullName = subjectName;
        this.portraitURL = imageURL;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPortraitURL() {
        return portraitURL;
    }

    public void setPortraitURL(String portraitURL) {
        this.portraitURL = portraitURL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Orator portrait = (Orator) o;

        return fullName.equals(portrait.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

    // this method is designed to allow a user to generate a hashcode with just a title and an orator,
    // without having to create a new Speech object
    public static int getHashCode(String subjectName) {
        int hash = 0;
        if (subjectName != null) {
            Orator dummySpeech = new Orator(subjectName);
            hash = dummySpeech.hashCode();
        }
        return hash;
    }
}
