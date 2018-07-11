package com.example.dendy_s784.myapplication.data.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.dendy_s784.myapplication.data.Note;

import java.util.List;


/**
 * Data Access Object for the notes table.
 * https://developer.android.com/reference/android/arch/persistence/room/package-summary
 * Room is a Database Object Mapping library that makes it easy to access database on Android applications.
 */
@Dao
public interface NotesDao {
    /**
     * Select all notes from the tasks table.
     *
     * @return all notes.
     */
    @Query("SELECT * FROM Notes")
    List<Note> getNotes();

    /**
     * Select a note by id.
     *
     * @param noteId the note id.
     * @return the note with noteId.
     */
    @Query("SELECT * FROM Notes WHERE entryid = :noteId")
    Note getNoteById(String noteId);

    /**
     * Insert a note in the database. If the note already exists, replace it.
     *
     * @param note the note to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(Note note);

    /**
     * Update a note.
     *
     * @param note note to be updated
     * @return the number of note updated. This should always be 1.
     */
    @Update
    int updateNote(Note note);

    /**
     * Delete a note by id.
     *
     * @return the number of notes deleted. This should always be 1.
     */
    @Query("DELETE FROM Notes WHERE entryid = :noteId")
    int deleteTaskById(String noteId);

    /**
     * Delete all notes.
     */
    @Query("DELETE FROM Notes")
    void deleteTasks();
}
