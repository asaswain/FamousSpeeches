package edu.nyu.scps.SpeechPlayer.View;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.nyu.scps.SpeechPlayer.R;

/**
 * This class is a custom adapter for displaying a list of speeches in the listView in MainActivity
 */
public class SpeechAdapter extends CursorAdapter {
    Context context;
    private LayoutInflater cursorInflater;

    public SpeechAdapter(Context context, Cursor cursor, int flags) {
        super(context,cursor,flags);
        this.context = context;

        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // load data into speech_listview_item XML document
        String oratorColName = context.getResources().getString(R.string.sql_orator_column);
        String titleColName = context.getResources().getString(R.string.sql_title_column);
        String yearColName = context.getResources().getString(R.string.sql_year_column);
        String lengthColName = context.getResources().getString(R.string.sql_length_column);

        String orator = cursor.getString(cursor.getColumnIndex(oratorColName));
        String title = cursor.getString(cursor.getColumnIndex(titleColName));
        String year = cursor.getString(cursor.getColumnIndex(yearColName));
        String length = cursor.getString(cursor.getColumnIndex(lengthColName));

        // this converts recording length of list item from seconds to a hours:minutes:seconds timestamp
        Integer lengthInSeconds = Integer.valueOf(length);
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss", Locale.getDefault());
        String lengthTimeStamp = formatter.format(new Date(lengthInSeconds*1000));

        TextView textView = (TextView) view.findViewById(R.id.title);
        textView.setText(title);
        textView = (TextView) view.findViewById(R.id.orator);
        textView.setText(orator);
        textView = (TextView) view.findViewById(R.id.year);
        textView.setText(year);
        textView = (TextView) view.findViewById(R.id.length);
        textView.setText(lengthTimeStamp);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(R.layout.speech_listview_item, parent, false);
    }
}

