package edu.nyu.scps.SpeechPlayer.Model;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import edu.nyu.scps.SpeechPlayer.R;

/**
 * A list of orators to use when building speeches
 */
public class OratorList {

    private HashMap<Integer, Orator> oratorList;

    public OratorList(Context context) {

        oratorList = new HashMap<>();

        // read data from file
        try
        {
            InputStream inputStream = context.getResources().openRawResource(R.raw.orator_data);
            BufferedReader inBuffer = new BufferedReader(new InputStreamReader(inputStream));

            while(true) {
                String input = inBuffer.readLine();        //read a line
                if (input == null) {              //if input is null  end of file
                    break;
                } else {
                    // create orator object from raw data in "orator_data" file
                    String tokenArray[] = input.split("\\*");
                    String name = "";
                    String imageURL = "";

                    if (tokenArray.length >= 1) {
                        name = tokenArray[0];
                    }
                    if (tokenArray.length >= 2) {
                        imageURL = tokenArray[1];
                    }
                    Orator tmpOrator = new Orator(name, imageURL);
                    // add to list
                    oratorList.put(tmpOrator.hashCode(), tmpOrator);
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
     * Return portrait object as indexed by name of subject in portrait
     * @param orator = name of subject
     * @return Orator object
     */
    public Orator getOrator(String orator) {
        try {
            Integer hash = Orator.getHashCode(orator);
            return oratorList.get(hash);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
}
