package com.example.dendy_s784.myapplication.data.source;

import android.support.annotation.NonNull;

import com.example.dendy_s784.myapplication.data.Note;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Concrete implementation to load notes from the data sources into a cache.
 * <p>
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 */
public class NotesRepository implements NotesDataSource{
    private static NotesRepository INSTANCE = null;

    private final NotesDataSource mNotesRemoteDataSource;

    private final NotesDataSource mNotesLocalDataSource;

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    Map<String, Note> mCachedNotes;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    boolean mCacheIsDirty = false;

    // Prevent direct instantiation.
    private NotesRepository(@NonNull NotesDataSource tasksRemoteDataSource,
                            @NonNull NotesDataSource tasksLocalDataSource) {
        mNotesRemoteDataSource = checkNotNull(tasksRemoteDataSource);
        mNotesLocalDataSource = checkNotNull(tasksLocalDataSource);
    }

    /**
     * singleton method
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param notesRemoteDataSource the backend data source
     * @param notesLocalDataSource  the device storage data source
     * @return the {@link NotesRepository} instance
     */
    public static NotesRepository getInstance(NotesDataSource notesRemoteDataSource,
                                              NotesDataSource notesLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new NotesRepository(notesRemoteDataSource, notesLocalDataSource);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(NotesDataSource, NotesDataSource)} to create a new instance
     * next time it's called.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    /**
     * Gets tasks from cache, local data source (SQLite) or remote data source, whichever is
     * available first.
     * <p>
     * Note: {@link LoadNoteCallback#onDataNotAvailable()} is fired if all data sources fail to
     * get the data.
     */
    @Override
    public void getNotes(@NonNull final LoadNoteCallback callback) {
        checkNotNull(callback);

        // Respond immediately with cache if available and not dirty
        if (mCachedNotes != null && !mCacheIsDirty) {
            callback.onNotesLoaded(new ArrayList<>(mCachedNotes.values()));
            return;
        }

        if (mCacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            getNotesFromRemoteDataSource(callback);
        } else {
            // Query the local storage if available. If not, query the network.
            mNotesRemoteDataSource.getNotes(new LoadNoteCallback() {
                @Override
                public void onNotesLoaded(List<Note> notes) {
                    refreshCache(notes);
                    callback.onNotesLoaded(new ArrayList<>(mCachedNotes.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    getNotesFromRemoteDataSource(callback);
                }
            });
        }
    }

    private void getNotesFromRemoteDataSource(@NonNull final LoadNoteCallback callback) {
        mNotesRemoteDataSource.getNotes(new LoadNoteCallback() {

            @Override
            public void onNotesLoaded(List<Note> tasks) {
                refreshCache(tasks);
                refreshLocalDataSource(tasks);
                callback.onNotesLoaded(new ArrayList<>(mCachedNotes.values()));
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    private void refreshCache(List<Note> notes) {
        if (mCachedNotes == null) {
            mCachedNotes = new LinkedHashMap<>();
        }
        mCachedNotes.clear();
        for (Note note : notes) {
            mCachedNotes.put(note.getId(), note);
        }
        mCacheIsDirty = false;
    }

    private void refreshLocalDataSource(List<Note> notes) {
        mNotesRemoteDataSource.deleteAllNotes();
        for (Note task : notes) {
            mNotesRemoteDataSource.saveNote(task);
        }
    }

    @Override
    public void getNote(@NonNull String taskId, @NonNull GetNoteCallback callback) {

    }

    @Override
    public void saveNote(@NonNull Note note) {
        checkNotNull(note);
        mNotesLocalDataSource.saveNote(note);
        //mTasksLocalDataSource.saveTask(note);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedNotes== null) {
            mCachedNotes= new LinkedHashMap<>();
        }
        mCachedNotes.put(note.getId(), note);
    }

    @Override
    public void clearCompletedNotes() {

    }

    @Override
    public void refreshNotes() {
        mCacheIsDirty = true;
    }

    @Override
    public void deleteAllNotes() {
        mNotesLocalDataSource.deleteAllNotes();
        //mTasksLocalDataSource.deleteAllTasks();

        if (mCachedNotes == null) {
            mCachedNotes = new LinkedHashMap<>();
        }
        mCachedNotes.clear();
    }

    @Override
    public void deleteNotes(@NonNull String taskId) {
        //mTasksRemoteDataSource.deleteTask(checkNotNull(taskId));
        mNotesLocalDataSource.deleteNotes(checkNotNull(taskId));

        mCachedNotes.remove(taskId);
    }
}
