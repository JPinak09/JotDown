package com.joshipinak.jotdown.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joshipinak.jotdown.DBHelper.DBOpenHelper;
import com.joshipinak.jotdown.Model.Note;
import com.joshipinak.jotdown.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {

    private Context context;
    private List<Note> notesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView note, timeStamp;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            note = itemView.findViewById(R.id.tvNote);
            timeStamp = itemView.findViewById(R.id.tvNoteTimestamp);
        }
    }

    public NotesAdapter(Context context, List<Note> notesList) {
        this.context = context;
        this.notesList = notesList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.note_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Note note = notesList.get(position);
        holder.note.setText(note.getNote());
        // Formatting and Displaying timestamp
        holder.timeStamp.setText(formatDate(note.getTimestamp()));

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

    @Override
    public int getItemCount() {
        return notesList.size();
    }

}
