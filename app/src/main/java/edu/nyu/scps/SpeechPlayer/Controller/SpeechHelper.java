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

/**
 * This class creates an SQL database to store a table of the speeches in the HashMap (to make sorting them easier)
 */

public class SpeechHelper extends SQLiteOpenHelper {
    public SpeechHelper(Context context, String name) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE speeches ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "orator TEXT,"
                + "title TEXT,"
                + "year INTEGER"
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
            contentValues.put("orator", tmpSpeech.getOrator());
            contentValues.put("title", tmpSpeech.getTitle());
            contentValues.put("year", tmpSpeech.getYear());
            if (db.insert("speeches", null, contentValues) <= 0) {
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
        Cursor cursor = db.rawQuery("SELECT _id, orator, title, year FROM speeches ORDER BY orator;", null);
        cursor.moveToFirst();
        return cursor;
    }
}

