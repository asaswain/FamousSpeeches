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
 * This class creates an SQL database to store a table of the speeches in the HashMap (to make sorting them easier)
 */

public class SpeechSQLHelper extends SQLiteOpenHelper {
    private Context context;

    // list of each column name in the SQL table
    private final String speechTableName;
    private final String oratorColName;
    private final String titleColName;
    private final String yearColName;
    private final String lengthColName;

    public SpeechSQLHelper(Context context, String name) {
        super(context, name, null, 1);
        this.context = context;
        speechTableName = context.getResources().getString(R.string.sql_table_name);
        oratorColName = context.getResources().getString(R.string.sql_orator_column);
        titleColName = context.getResources().getString(R.string.sql_title_column);
        yearColName = context.getResources().getString(R.string.sql_year_column);
        lengthColName = context.getResources().getString(R.string.sql_length_column);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + speechTableName + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + oratorColName + " TEXT,"
                + titleColName + " TEXT,"
                + yearColName + " INTEGER,"
                + lengthColName + " INTEGER"
                + ");";

        try {
            db.execSQL(createTable);
        } catch (SQLException sQLException) {
            Log.e("myTag", "couldn't create table", sQLException);
        }

        // build a new SpeechList object which creates a HashMap of all speeches
        SpeechList sqlSpeechList;
        sqlSpeechList = new SpeechList(context);

        // loop through each entry in HashMap and build SQL database
        HashMap<Integer, Speech> speechMap = sqlSpeechList.getHashMap();
        for (HashMap.Entry<Integer, Speech> entry : speechMap.entrySet()) {
            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            Speech tmpSpeech =  entry.getValue();
            ContentValues contentValues = new ContentValues();
            contentValues.put(oratorColName, tmpSpeech.getOrator().getFullName());
            contentValues.put(titleColName, tmpSpeech.getTitle());
            contentValues.put(yearColName, tmpSpeech.getYear());
            contentValues.put(lengthColName, tmpSpeech.getLengthInSeconds());
            if (db.insert(speechTableName, null, contentValues) <= 0) {
                Log.e("myTag", "insert failed");
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * Execute SQL select statement to select data for cursor
     * @param sortType String to indicate what item to sort by, either Title, Orator, or Year
     * @return cursor object
     */
    public Cursor getCursor(String sortType) {
        SQLiteDatabase db = getReadableDatabase();
        //can say "_id, name" instead of "*", but _id must be included.
        String sqlQuery = "SELECT _id, " + oratorColName + ", " + titleColName + ", " + yearColName + ", " + lengthColName + " FROM " + speechTableName;

        switch (sortType) {
            case "Title":
                sqlQuery += " ORDER BY " + titleColName;
                break;
            case "Orator":
                sqlQuery += " ORDER BY " + oratorColName;
                break;
            case "Length":
                sqlQuery += " ORDER BY " + lengthColName;
                break;
            default:
                sqlQuery += " ORDER BY " + yearColName;
                break;
        }

        Cursor cursor = db.rawQuery(sqlQuery, null);
        cursor.moveToFirst();
        return cursor;
    }
}

