package org.swain.asa.famous_pres_speeches.View;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.swain.asa.famous_pres_speeches.Controller.PlaybackController;
import org.swain.asa.famous_pres_speeches.Controller.SpeechSQLHelper;
import org.swain.asa.famous_pres_speeches.PresSpeechApplication;
import org.swain.asa.famous_pres_speeches.R;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Famous US Speeches Android Application
 * Copyright (C) 2015  Asa F. Swain
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * This class displays a list of speeches in a ListView field
 */

public class ListActivity extends AppCompatActivity {
    private String databaseName = "speechdata.db";
    private SpeechSQLHelper helper;   //Can't initialize this field before onCreate.
    private SQLiteDatabase db;
    private SpeechAdapter adapter;
    // old code
    //private SimpleCursorAdapter adapter;
    private ListView listView;
    private String sortType = "Title";
    // if user clicks on the same sort heading twice then change the sort order from ascending to descending
    private String oldSortType = "";
    private boolean isSortOrderDescending = true;

    // Google Analytics
    private Tracker mTracker;
    private final static String activityName = ListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = (ListView) findViewById(R.id.listView);
        TextView textView = (TextView) findViewById(R.id.empty);
        listView.setEmptyView(textView);   //Display this TextView when table contains no records.

        // old code
        /*adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                null,
                new String[] {"orator",             "title"},
                new int[]    {android.R.id.text1, android.R.id.text2},
                0    //don't need any flags
        );*/

        helper = new SpeechSQLHelper(this, databaseName);
        buildSpeechTable();

        // set listener for ListView to go to the PlayerActivity screen
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, final long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position); //downcast
                int oratorIndex = cursor.getColumnIndex(getResources().getString(R.string.sql_orator_column));
                int titleIndex = cursor.getColumnIndex(getResources().getString(R.string.sql_title_column));
                String orator = cursor.getString(oratorIndex);
                String title = cursor.getString(titleIndex);

                PlaybackController.loadPlayerScreen(ListActivity.this, orator, title);

                // Google Analytics code
                PresSpeechApplication application = (PresSpeechApplication) getApplication();
                application.logGoogleAnalysticsEvent(activityName, "SpeechItem", title+"/"+orator);
            }
        });

        // set listeners for table column headings to allow user to change sort order
        int[] columnHeadingsIds = {R.id.titleColumnHeading, R.id.oratorColumnHeading, R.id.yearColumnHeading, R.id.lengthColumnHeading};

        for (int id : columnHeadingsIds) {
            TextView colHeading = (TextView) findViewById(id);
            colHeading.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView textView = (TextView) view;
                    String newSortType = textView.getText().toString();
                    if (newSortType.equals("Title") || newSortType.equals("Orator") || newSortType.equals("Year") || newSortType.equals("Length")) {
                        sortType = newSortType;
                        // if user clicks on save sort type twice, reverse sort order
                        isSortOrderDescending = (sortType.equals(oldSortType) && !isSortOrderDescending);
                        oldSortType = sortType;
                        buildSpeechTable();
                    } else {
                        // invalid sort type, do nothing
                    }
                }
            });
        }

        // set listener for Nelp button
        FancyButton helpButton = (FancyButton) findViewById(R.id.helpButton);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), R.string.help_text, Toast.LENGTH_LONG).show();
                // Google Analytics code
                PresSpeechApplication application = (PresSpeechApplication) getApplication();
                application.logGoogleAnalysticsEvent(activityName, "HelpButton", "");
            }
        });

        // set listener for Credits button
        final FancyButton creditsButton = (FancyButton) findViewById(R.id.creditsButton);
        creditsButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), CreditsActivity.class);
                startActivity(intent);
                // Google Analytics code
                PresSpeechApplication application = (PresSpeechApplication) getApplication();
                application.logGoogleAnalysticsEvent(activityName, "CreditsButton", "");
            }
        });

        // Google Analytics code
        // Obtain the shared Tracker instance.
        PresSpeechApplication application = (PresSpeechApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // create speech playback view if speech is playing
        PlaybackController.createControllerView(ListActivity.this, ListActivity.class);

        // Google Analytics code
        Log.i(activityName, "Setting screen name: " + activityName);
        mTracker.setScreenName(activityName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    /**
     * Build a speech table from an SQL database (sorting by sortOrder column name)
     */
    private void buildSpeechTable() {
        CreateTask createTask = new CreateTask();
        createTask.execute();
    }

    private class CreateTask extends AsyncTask<Void, Void, SQLiteDatabase> {
        @Override
        protected SQLiteDatabase doInBackground(Void... params) {
            return helper.getWritableDatabase();
        }

        @Override
        protected void onPostExecute(SQLiteDatabase db) {
            ListActivity.this.db = db;
            Cursor cursor = helper.getCursor(sortType, isSortOrderDescending);
            adapter = new SpeechAdapter(ListActivity.this, cursor, 0);
            listView.setAdapter(adapter);
            adapter.swapCursor(cursor);
        }
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
