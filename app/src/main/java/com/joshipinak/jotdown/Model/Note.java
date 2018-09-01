package com.joshipinak.jotdown.Model;

public class Note {

    //Constants for identifying table and columns
    public static final String TABLE_NOTES = "notes"; // table name
    public static final String NOTE_ID = "_id";       // to uniquely identify
    public static final String NOTE_TEXT = "noteText"; // stores the actual note text
    public static final String NOTE_CREATED = "noteCreated"; // timestamp of when your note is created

    private int id;
    private String note;
    private String timestamp;

    // create table SQL query
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NOTES + " (" +
                    NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NOTE_TEXT + " TEXT, " +
                    NOTE_CREATED + " DATETIME default (datetime('now','localtime'))" +
                    ")";


    public Note() {
    }

    public Note(int id, String note, String timestamp) {
        this.id = id;
        this.note = note;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
