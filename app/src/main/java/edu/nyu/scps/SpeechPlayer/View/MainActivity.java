package edu.nyu.scps.SpeechPlayer.View;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import edu.nyu.scps.SpeechPlayer.Controller.SpeechHelper;
import edu.nyu.scps.SpeechPlayer.R;

/**
 * This class displays a list of speeches
 */

public class MainActivity extends AppCompatActivity {
    private String databaseName = "speechdata.db";
    private SpeechHelper helper;   //Can't initialize this field before onCreate.
    private SQLiteDatabase db;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listView = (ListView) findViewById(R.id.listView);
        TextView textView = (TextView)findViewById(R.id.empty);
        listView.setEmptyView(textView);   //Display this TextView when table contains no records.

        adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                null,
                new String[] {"orator",             "title"},
                new int[]    {android.R.id.text1, android.R.id.text2},
                0    //don't need any flags
        );
        listView.setAdapter(adapter);

        helper = new SpeechHelper(this, databaseName);
        CreateTask createTask = new CreateTask();
        createTask.execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, final long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position); //downcast
                int oratorIndex = cursor.getColumnIndex("orator");
                int titleIndex = cursor.getColumnIndex("title");

                Intent intent = new Intent(getBaseContext(), PlayerActivity.class);
                intent.putExtra("orator", cursor.getString(oratorIndex));
                intent.putExtra("title", cursor.getString(titleIndex));
                startActivity(intent);
            }
        });
    }

    private class CreateTask extends AsyncTask<Void, Void, SQLiteDatabase> {
        @Override
        protected SQLiteDatabase doInBackground(Void... params) {
            return helper.getWritableDatabase();
        }

        @Override
        protected void onPostExecute(SQLiteDatabase db) {
            MainActivity.this.db = db;
            Cursor cursor = helper.getCursor();
            //Cursor cursor = db.query(tableName, columns, null, null, null, null, null);
            adapter.swapCursor(cursor);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
