package edu.nyu.scps.SpeechPlayer.Model;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import edu.nyu.scps.SpeechPlayer.R;

/**
 * This class contains an HashMap of speeches, indexed by a HashCode of the name of the speaker and the name of the speech
 */
public class SpeechList {

    private HashMap<Integer, Speech> speechList;
    private OratorList oratorList;

    // this constructor builds a hashmap of speeches to use in the app
    public SpeechList(Context context) {

        speechList = new HashMap<>();
        oratorList = new OratorList(context);

        // read data from file
        try
        {
            InputStream inputStream = context.getResources().openRawResource(R.raw.speech_data);
            BufferedReader inBuffer = new BufferedReader(new InputStreamReader(inputStream));

            while(true) {
                String input = inBuffer.readLine();        //read a line
                if (input == null) {              //if input is null  end of file
                    break;
                } else {
                    // create speech object from raw data in speech_data file
                    String title = "";
                    Orator orator = null;
                    String webRecordingURL = "";
                    String wikipediaURL = "";
                    Integer year = 0;
                    Integer length = 0;

                    String tokenArray[] = input.split("\\*");
                    if (tokenArray.length >= 1) {
                        title = tokenArray[0];
                    }
                    if (tokenArray.length >= 2) {
                        String oratorName = tokenArray[1];
                        orator = oratorList.getOrator(oratorName);
                    }
                    if (tokenArray.length >= 3) {
                        webRecordingURL = tokenArray[2];
                    }
                    if (tokenArray.length >= 4) {
                        wikipediaURL = tokenArray[3];
                    }
                    if (tokenArray.length >= 5) {
                        year = Integer.valueOf(tokenArray[4]);
                    }
                    if (tokenArray.length >= 6) {
                        length = Integer.valueOf((tokenArray[5]));
                    }
                    Speech tmpSpeech = new Speech(title, orator, webRecordingURL, wikipediaURL, year, length);
                    // add to list
                    speechList.put(tmpSpeech.hashCode(), tmpSpeech);
                }
            }
            inBuffer.close( );
        }
        catch (IOException e)					//catch all other I/O exceptions
        {
            // do nothing
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
