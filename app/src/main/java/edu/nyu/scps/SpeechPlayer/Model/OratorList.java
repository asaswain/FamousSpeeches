package edu.nyu.scps.SpeechPlayer.Model;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * A list of orators to use when building speeches
 */
public class OratorList {

    private HashMap<Integer, Orator> portraitList;

    public OratorList() {
        LinkedList<Orator> tmpList = new LinkedList<>();

        // initialize portraits

        tmpList.add(new Orator("Franklin Delano Roosevelt",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b8/FDR_in_1933.jpg/1024px-FDR_in_1933.jpg"));

        tmpList.add(new Orator("Richard Milhous Nixon",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4c/Richard_M._Nixon%2C_head-and-shoulders_portrait%2C_facing_front.tif/lossy-page1-1024px-Richard_M._Nixon%2C_head-and-shoulders_portrait%2C_facing_front.tif.jpg"));

        tmpList.add(new Orator("John Fitzgerald Kennedy",
                "https://upload.wikimedia.org/wikipedia/commons/5/5e/John_F._Kennedy%2C_White_House_photo_portrait%2C_looking_up.jpg"));

        tmpList.add(new Orator("Abraham Lincoln",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/4/44/Abraham_Lincoln_head_on_shoulders_photo_portrait.jpg/1024px-Abraham_Lincoln_head_on_shoulders_photo_portrait.jpg"));

        tmpList.add(new Orator("Douglas MacArthur",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/8/89/DouglasMacArthur1945.jpg/1024px-DouglasMacArthur1945.jpg"));

        // add portraits
        portraitList = new HashMap<>();
        for(Orator newPortrait : tmpList) {
            portraitList.put(newPortrait.hashCode(), newPortrait);
        }
    }

    /**
     * Return portrait object as indexed by name of subject in portrait
     * @param orator = name of subject
     * @return Orator object
     */
    public Orator getOrator(String orator) {
        try {
            Integer hash = Orator.getHashCode(orator);
            return portraitList.get(hash);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
}
