package com.joshipinak.jotdown.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.joshipinak.jotdown.Model.Note;

import java.util.ArrayList;
import java.util.List;

public class DBOpenHelper extends SQLiteOpenHelper {

    //Constants for db name and version
    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 3;

    //Constants for identifying table and columns
    public static final String TABLE_NOTES = "notes";
    public static final String NOTE_ID = "_id";
    public static final String NOTE_TEXT = "noteText";
    public static final String NOTE_CREATED = "noteCreated";

    public static final String[] ALL_COLUMNS = {NOTE_ID, NOTE_TEXT, NOTE_CREATED};

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NOTES + " (" +
                    NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NOTE_TEXT + " TEXT, " +
                    NOTE_CREATED + " DATETIME default (datetime('now','localtime'))" +
                    ")";

    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // called only once when the app is installed.
    @Override
    public void onCreate(SQLiteDatabase db) {

        // creates notes table
        db.execSQL(TABLE_CREATE);
    }

    // called when an update is released.
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        //drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        //create tables again
        onCreate(db);
    }

    // Inserting Data
    /* InsertNote(): insert a new record into the database */
    public long insertNote(String note) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //'id' and 'timestamp' will be inserted automatically
        // no need to add them
        values.put(Note.NOTE_TEXT, note);
        long id = db.insert(Note.TABLE_NOTES, null, values);
        db.close();
        return id;
    }

    // Reading Data
    /*
    * getNote(): Takes already existing note id and fetches the note object
    * getAllNotes(): Fetches all the notes in descending order by timestamp.
    * getNotesCount(): returns the count of notes stored in database.
    */

    public Note getNote(long id) {
        // get readable database as we aren't inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Note.TABLE_NOTES, new String[]{Note.NOTE_ID, Note.NOTE_TEXT, Note.NOTE_CREATED},
                Note.NOTE_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        Note note = new Note(
                cursor.getInt(cursor.getColumnIndex(Note.NOTE_ID)),
                cursor.getString(cursor.getColumnIndex(Note.NOTE_TEXT)),
                cursor.getString(cursor.getColumnIndex(Note.NOTE_CREATED)));

        cursor.close();

        return note;
    }

    public List<Note> getAllNotes(){
        List<Note> notes = new ArrayList<>();

        // Select all query
        String selectQuery = "SELECT  * FROM " + Note.TABLE_NOTES + " ORDER BY " +
                Note.NOTE_CREATED + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all the rows and adding to the list
        if (cursor.moveToFirst()){
            do {
                Note note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndex(Note.NOTE_ID)));
                note.setNote(cursor.getString(cursor.getColumnIndex(Note.NOTE_TEXT)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(Note.NOTE_CREATED)));

                notes.add(note);

            }while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        //return notes list
        return notes;
    }

    public int getNotesCount(){
        String countQuery = "SELECT * FROM " + Note.TABLE_NOTES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery,null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    // Updating Data
    public int updateNote(Note note){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Note.NOTE_TEXT, note.getNote());

        // updating row
        return db.update(Note.TABLE_NOTES,values,Note.NOTE_ID + "=?",
                new String[]{String.valueOf(note.getId())});

    }

    // Deleting data
    public void deleteNote(Note note){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(Note.TABLE_NOTES, Note.NOTE_ID + "=?",
                new String[]{String.valueOf(note.getId())});
        db.close();
    }
}
