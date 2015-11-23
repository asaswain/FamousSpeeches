package edu.nyu.scps.SpeechPlayer.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import edu.nyu.scps.SpeechPlayer.Controller.DownloadImageTask;
import edu.nyu.scps.SpeechPlayer.R;

/**
 * This class displays a list of speeches
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // load capitol image
        loadTitleImage();

        // set listener for capitol image, to go to list of speeches
        ImageView capitol = (ImageView) findViewById(R.id.capitol);
        capitol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ListActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Load webview with title image
     */
    private void loadTitleImage() {
        new DownloadImageTask((ImageView) findViewById(R.id.capitol)).execute(getResources().getString(R.string.capitol_image));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // don't build menu
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
