package edu.nyu.scps.SpeechPlayer.Model;

import java.util.HashMap;

/**
 * This class contains an HashMap of speeches, indexed by a HashCode of the name of the speaker and the name of the speech
 */
public class SpeechList {

    private HashMap<Integer, Speech> list;

    // this constructor builds a hashmap of speeches to use in the app
    public SpeechList() {

        // initialize speeches

        Speech Gettysburg = new Speech("Gettysburg Address",
                "Abraham Lincoln",
                "http://fiftiesweb.com/usa/gettysburg-address-jd.mp3",
                "https://en.m.wikipedia.org/wiki/Gettysburg_address",
                1964);

        Speech FDRPearlHabor = new Speech("Pearl Harbor Address to the Nation",
                "Franklin Delano Roosevelt",
                "http://americanrhetoric.com/mp3clips/politicalspeeches/fdrwarmessage344.mp3",
                "https://en.m.wikipedia.org/wiki/Infamy_Speech",
                1941);

        // add speeches
        list = new HashMap<>();
        list.put(Gettysburg.hashCode(), Gettysburg);
        list.put(FDRPearlHabor.hashCode(), FDRPearlHabor);
    }

    /**
     * Return speech object as indexes by name of speaker and title of speech
     * @param orator = name of speaker
     * @param title = title of speech
     * @return Speech object
     */
    public Speech getSpeech(String orator, String title) {
        try {
            Integer hash = Speech.getHashCode(orator,title);
            return list.get(hash);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Return HashMap object containing all the speeches
     * @return HashMap containing all the speeches
     */
    public HashMap<Integer, Speech> getHashMap() {
        return new HashMap<>(list);
    }
}
