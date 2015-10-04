package edu.nyu.scps.SpeechPlayer.Model;

import java.util.ArrayList;

/**
 * This class contains an ArrayList of speeches
 */
public class SpeechList {

    private ArrayList<Speech> list;

    // constructor should build a list of speeches to use in the app
    public SpeechList() {
        list = new ArrayList<>();

        // add speeches
        Speech Gettysburg = new Speech("Gettysburg Address","Abraham Lincoln","http://fiftiesweb.com/usa/gettysburg-address-jd.mp3","https://en.m.wikipedia.org/wiki/Gettysburg_address",1864);
        list.add(Gettysburg);
    }

    /**
     * Return speech object marked index number
     * @param i = index of speech to get
     * @return Speech object
     */
    public Speech getSpeech(int i) {
        try {
            return list.get(i);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

}
