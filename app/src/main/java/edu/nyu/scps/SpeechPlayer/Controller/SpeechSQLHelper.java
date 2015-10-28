package edu.nyu.scps.SpeechPlayer.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

import edu.nyu.scps.SpeechPlayer.Model.Speech;
import edu.nyu.scps.SpeechPlayer.Model.SpeechList;
import edu.nyu.scps.SpeechPlayer.R;

/**
 * This class creates an SQL database to store a table of the speeches in the HashMap (to make sorting them easier)
 */

public class SpeechSQLHelper extends SQLiteOpenHelper {
    private Context context;
    private final String speechTableName;
    private final String oratorColName;
    private final String titleColName;
    private final String yearColName;

    public SpeechSQLHelper(Context context, String name) {
        super(context, name, null, 1);
        speechTableName = context.getResources().getString(R.string.sql_table_name);
        oratorColName = context.getResources().getString(R.string.sql_orator_column);
        titleColName = context.getResources().getString(R.string.sql_title_column);
        yearColName = context.getResources().getString(R.string.sql_year_column);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + speechTableName + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + oratorColName + " TEXT,"
                + titleColName + " TEXT,"
                + yearColName + " INTEGER"
                + ");";

        try {
            db.execSQL(createTable);
        } catch (SQLException sQLException) {
            Log.e("myTag", "couldn't create table", sQLException);
        }

        // build a new SpeechList object which creates a HashMap of all speeches
        SpeechList sqlSpeechList;
        sqlSpeechList = new SpeechList();

        // loop through each entry in HashMap and build SQL database
        HashMap<Integer, Speech> speechMap = sqlSpeechList.getHashMap();
        for (HashMap.Entry<Integer, Speech> entry : speechMap.entrySet()) {
            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            Speech tmpSpeech =  entry.getValue();
            String x = tmpSpeech.getOrator();
            String y = tmpSpeech.getTitle();
            ContentValues contentValues = new ContentValues();
            contentValues.put(oratorColName, tmpSpeech.getOrator());
            contentValues.put(titleColName, tmpSpeech.getTitle());
            contentValues.put(yearColName, tmpSpeech.getYear());
            if (db.insert(speechTableName, null, contentValues) <= 0) {
                Log.e("myTag", "insert failed");
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor getCursor() {
        SQLiteDatabase db = getReadableDatabase();
        //can say "_id, name" instead of "*", but _id must be included.
        String sqlQuery = "SELECT _id, " + oratorColName + ", " + titleColName + ", " + yearColName;
        sqlQuery += " FROM " + speechTableName + " ORDER BY " + oratorColName;
        Cursor cursor = db.rawQuery(sqlQuery, null);
        cursor.moveToFirst();
        return cursor;
    }
}
