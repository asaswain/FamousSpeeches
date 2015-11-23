package edu.nyu.scps.SpeechPlayer.View;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

import edu.nyu.scps.SpeechPlayer.Model.Speech;
import edu.nyu.scps.SpeechPlayer.Model.SpeechList;
import edu.nyu.scps.SpeechPlayer.R;

// this class reads the transcript of the speech from the Assets folder and displays it

public class SpeechTextActivity extends AppCompatActivity {

    private String orator;
    private String title;
    private Speech mySpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_text);

        Bundle extras = getIntent().getExtras();
        if (extras.getString("oratorData") != null) {
            orator = extras.getString("oratorData");
        }
        if (extras.getString("titleData") != null) {
            title = extras.getString("titleData");
        }

        SpeechList mySpeechList = new SpeechList(this);
        mySpeech = mySpeechList.getSpeech(orator, title);

        TextView titleview = (TextView) findViewById(R.id.speechTitle);
        titleview.setText(mySpeech.getTitle());

        TextView oratorView = (TextView) findViewById(R.id.oratorNameAndYear);
        String oratorAndYear = mySpeech.getOrator().getFullName() + " (" + mySpeech.getYear() + ")";
        oratorView.setText(oratorAndYear);

        AssetManager assetManager = getAssets();
        try {
            String assetName = orator + "*" + title + ".txt";
            InputStream input = assetManager.open(assetName);

            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();

            // byte buffer into a string
            String speechText = new String(buffer);
            TextView speechTextView = (TextView) findViewById(R.id.speechText);
            speechTextView.setText(speechText);
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Cannot load transcript of this speech", Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
