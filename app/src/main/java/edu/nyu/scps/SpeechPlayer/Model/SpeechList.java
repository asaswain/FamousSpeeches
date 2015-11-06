package edu.nyu.scps.SpeechPlayer.Model;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * This class contains an HashMap of speeches, indexed by a HashCode of the name of the speaker and the name of the speech
 */
public class SpeechList {

    private HashMap<Integer, Speech> speechList;
    private OratorList oratorList;

    // this constructor builds a hashmap of speeches to use in the app
    public SpeechList() {

        oratorList = new OratorList();

        LinkedList<Speech> tmpList = new LinkedList<>();

        // initialize speeches

        tmpList.add(new Speech("Pearl Harbor Address to the Nation",
                oratorList.getOrator("Franklin Delano Roosevelt"),
                "http://americanrhetoric.com/mp3clips/politicalspeeches/fdrwarmessage344.mp3",
                "https://en.m.wikipedia.org/wiki/Infamy_Speech",
                1941));

        tmpList.add(new Speech ("Farewell Address to Congress",
                oratorList.getOrator("Douglas MacArthur"),
                "http://www.americanrhetoric.com/mp3clips/politicalspeeches/douglasmacarthurfarewell34323.mp3",
                "https://en.wikisource.org/wiki/MacArthur%27s_Farewell_Speech_to_Congress",
                1951));

        tmpList.add(new Speech("Checkers",
                oratorList.getOrator("Richard Milhous Nixon"),
                "http://web2.millercenter.org/speeches/audio/spe_1952_0923_nixon.mp3",
                "https://en.wikipedia.org/wiki/Checkers_speech",
                1952));

        tmpList.add(new Speech("Ich Bin Ein Berliner",
                oratorList.getOrator("John Fitzgerald Kennedy"),
                "http://www.americanrhetoric.com/mp3clips/politicalspeeches/jfkberlinaddress14444444iiiiii4444444444444.mp3",
                "https://en.wikipedia.org/wiki/Ich_bin_ein_Berliner",
                1963));

        tmpList.add(new Speech("Gettysburg Address",
                oratorList.getOrator("Abraham Lincoln"),
                "http://fiftiesweb.com/usa/gettysburg-address-jd.mp3",
                "https://en.m.wikipedia.org/wiki/Gettysburg_address",
                1964));

        // add speeches
        speechList = new HashMap<>();
        for(Speech newSpeech : tmpList) {
            speechList.put(newSpeech.hashCode(), newSpeech);
        }
    }

    /**
     * Return speech object as indexes by name of speaker and title of speech
     * @param oratorName = name of speaker
     * @param title = title of speech
     * @return Speech object
     */
    public Speech getSpeech(String oratorName, String title) {
        try {
            Orator orator = oratorList.getOrator(oratorName);
            Integer hash = Speech.getHashCode(orator, title);
            return speechList.get(hash);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Return HashMap object containing all the speeches
     * @return HashMap containing all the speeches
     */
    public HashMap<Integer, Speech> getHashMap() {
        return new HashMap<>(speechList);
    }
}
