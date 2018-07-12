package com.example.dendy_s784.myapplication.data.local;

import android.support.annotation.NonNull;

import com.example.dendy_s784.myapplication.data.Note;
import com.example.dendy_s784.myapplication.data.source.NotesDataSource;
import com.example.dendy_s784.myapplication.utils.AppExecutors;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class NotesLocalDataSource implements NotesDataSource{

    private static volatile NotesLocalDataSource INSTANCE;

    private NotesDao mNotesDao;

    private AppExecutors mAppExecutors;

    // Prevent direct instantiation.
    private NotesLocalDataSource(@NonNull AppExecutors appExecutors,
                                 @NonNull NotesDao notesDao) {
        mAppExecutors = appExecutors;
        mNotesDao = notesDao;
    }

    public static NotesLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                   @NonNull NotesDao notesDao) {
        if (INSTANCE == null) {
            synchronized (NotesLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NotesLocalDataSource(appExecutors, notesDao);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Note: {@link LoadNoteCallback#onDataNotAvailable()} is fired if the database doesn't exist
     * or the table is empty.
     */
    @Override
    public void getNotes(@NonNull final LoadNoteCallback callback) {
        //create thread to load Notes from DB
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //Load Notes from DB via its DAO
                //the implementation DAO are generated automatically by Room library
                final List<Note> notes = mNotesDao.getNotes();
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (notes.isEmpty()) {
                            // This will be called if the table is new or just empty.
                            callback.onDataNotAvailable();
                        } else {
                            callback.onNotesLoaded(notes);
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getNote(@NonNull String taskId, @NonNull GetNoteCallback callback) {

    }

    @Override
    public void saveNote(@NonNull final Note note) {
        checkNotNull(note);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                mNotesDao.insertNote(note);
            }
        };
        mAppExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void deleteMarkedNotes(final List<Note> markedNote) {
        Runnable clearTasksRunnable = new Runnable() {
            @Override
            public void run() {
                //TODO : need to delete based on list of marked item
                int markedNoteSize = markedNote.size();
                for(int i=0 ; i<markedNoteSize ; i++)
                {
                    mNotesDao.deleteNoteById(markedNote.get(i).getId());
                }
            }
        };

        mAppExecutors.diskIO().execute(clearTasksRunnable);
    }

    @Override
    public void refreshNotes() {

    }

    @Override
    public void deleteAllNotes() {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mNotesDao.deleteNotes();
            }
        };

        mAppExecutors.diskIO().execute(deleteRunnable);
    }

    @Override
    public void deleteNotes(@NonNull final String noteId) {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mNotesDao.deleteNoteById(noteId);
            }
        };

        mAppExecutors.diskIO().execute(deleteRunnable);
    }
}
