package com.joshipinak.jotdown.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.joshipinak.jotdown.DBHelper.DBOpenHelper;
import com.joshipinak.jotdown.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class NotesCursorAdapter extends CursorAdapter {

    public NotesCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.note_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String noteText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
        String noteTimeStampText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_CREATED));
        int pos = noteText.indexOf(10);
        if (pos != -1) {
            noteText = noteText.substring(0, pos) + "...";
        }
        TextView tvNote = view.findViewById(R.id.tvNote);
        TextView tvTimeStamp = view.findViewById(R.id.tvNoteTimestamp);
        tvNote.setText(noteText);
        tvTimeStamp.setText(formatDate(noteTimeStampText));
    }

    /*
    Formatting timestamp to 'MMM d* format
    * input: 2018-01-09 10:11:45
    *  output: September 1 2018
    */
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmtIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = fmtIn.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d", Locale.getDefault());
            return fmtOut.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}
