package com.example.dendy_s784.myapplication.data.source;

import android.support.annotation.NonNull;

import com.example.dendy_s784.myapplication.data.Note;

import java.util.List;

/**
 * Main entry point for accessing notes data.
 * <p>
 * For simplicity, only getNotes() and getNotes() have callbacks. Consider adding callbacks to other
 * methods to inform the user of network/database errors or successful operations.
 * For example, when a new task is created, it's synchronously stored in cache but usually every
 * operation on database or network should be executed in a different thread.
 */
public interface NotesDataSource {
    interface LoadNoteCallback {

        void onNotesLoaded(List<Note> tasks);

        void onDataNotAvailable();
    }

    interface GetNoteCallback {

        void onNoteLoaded(Note task);

        void onDataNotAvailable();
    }

    void getNotes(@NonNull LoadNoteCallback callback);

    void getNote(@NonNull String taskId, @NonNull GetNoteCallback callback);

    void saveNote(@NonNull Note note);

    void clearCompletedNotes();

    void refreshNotes();

    void deleteAllNotes();

    void deleteNotes(@NonNull String taskId);
}
