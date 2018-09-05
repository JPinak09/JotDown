package com.joshipinak.jotdown;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.joshipinak.jotdown.Adapter.NotesAdapter;
import com.joshipinak.jotdown.DBHelper.DBOpenHelper;
import com.joshipinak.jotdown.Model.Note;
import com.joshipinak.jotdown.utils.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private NotesAdapter notesAdapter;
    private final List<Note> notesList = new ArrayList<>();
    private RelativeLayout noNotesView;

    private DBOpenHelper dbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sample AdMob app ID: ca-app-pub-2585351524810756~4648568865
        MobileAds.initialize(this, "ca-app-pub-2585351524810756~4648568865");
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        noNotesView = findViewById(R.id.empty_desc);

        dbOpenHelper = new DBOpenHelper(this);
        notesList.addAll(dbOpenHelper.getAllNotes());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoteDialog(false, null, -1);
            }
        });

        notesAdapter = new NotesAdapter(this, notesList);
        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(notesAdapter);

        toggleEmptyNotes();



        /*
         * On Long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit or Delete
         */
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {

            @Override
            public void onClick(View view, int position) {
                Toast.makeText(MainActivity.this, "Long click on item to edit", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));
    }

    /* Inserting a new note in db and refreshing the list */
    private void createNote(String note) {

        // inserting a note in db and
        // getting newly inserted note id
        long id = dbOpenHelper.insertNote(note);

        //get the newly inserted note from db
        Note n = dbOpenHelper.getNote(id);

        if (n != null) {
            //adding new note to array list at 0 position
            notesList.add(0, n);

            //refreshing the list
            notesAdapter.notifyDataSetChanged();

            toggleEmptyNotes();
        }

    }

    /*
     * Updating notes in database and updating items in the list
     * by its position
     */
    private void updateNote(String note, int position) {
        Note n = notesList.get(position);
        // updating note text
        n.setNote(note);

        //updating note in db
        dbOpenHelper.updateNote(n);

        //refreshing the list
        notesList.set(position, n);
        notesAdapter.notifyDataSetChanged();

        toggleEmptyNotes();
    }

    /*
     * Deleting note from SQLite and removing the
     * item from the list by its position
     */
    private void deleteNote(int position) {
        //deleting the note from db
        dbOpenHelper.deleteNote(notesList.get(position));

        //removing the note from the list
        notesList.remove(position);
        notesAdapter.notifyItemRemoved(position);

        toggleEmptyNotes();
    }

    /*
     * Open Dialog with Edit/Delete Options
     */
    private void showActionsDialog(final int position) {
        CharSequence choices[] = new CharSequence[]{"Edit current note", "Delete current note"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose operation");
        builder.setItems(choices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showNoteDialog(true, notesList.get(position), position);
                } else {
                    deleteNote(position);
                }
            }
        });
        builder.show();
    }

    /**
     * Shows alert dialog with EditText options to enter / edit
     * a note.
     * when shouldUpdate=true, it automatically displays old note and changes the
     * button text to UPDATE
     */

    private void showNoteDialog(final boolean shouldUpdate, final Note note, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.custom_dialog_layout, null);
        AlertDialog.Builder alertDialogBuilderUserInput =
                new AlertDialog.Builder(MainActivity.this,android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputNote = view.findViewById(R.id.note);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.new_note) : getString(R.string.edit_note));
        if (shouldUpdate && note != null) {
            inputNote.setText(note.getNote());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        Toast.makeText(MainActivity.this, "Press and Hold to edit", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputNote.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter note!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating note
                if (shouldUpdate && note != null) {
                    // update note by it's id
                    updateNote(inputNote.getText().toString(), position);
                } else {
                    // create new note
                    createNote(inputNote.getText().toString());
                }
            }
        });
    }

    private void toggleEmptyNotes() {
        // you can check notesList.size() > 0

        if (dbOpenHelper.getNotesCount() > 0) {
            noNotesView.setVisibility(View.GONE);
        } else {
            noNotesView.setVisibility(View.VISIBLE);
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
        int id = item.getItemId();

        switch (id) {
            case R.id.action_delete_all:
                dbOpenHelper.deleteAllNotes();
                notesAdapter.notifyDataSetChanged();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        notesAdapter.clear();
//    }
}
