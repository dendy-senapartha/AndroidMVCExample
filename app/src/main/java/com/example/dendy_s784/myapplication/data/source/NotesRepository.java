package com.example.dendy_s784.myapplication.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.dendy_s784.myapplication.data.Note;

import java.util.ArrayList;
import java.util.Iterator;
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

    //used to load remote data source from inet. no we dont use it yet
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
    private NotesRepository(@NonNull NotesDataSource tasksLocalDataSource) {
        mNotesLocalDataSource = checkNotNull(tasksLocalDataSource);
    }

    /**
     * singleton method
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param notesLocalDataSource  the device storage data source
     * @return the {@link NotesRepository} instance
     */
    public static NotesRepository getInstance(NotesDataSource notesLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new NotesRepository(notesLocalDataSource);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(NotesDataSource)} to create a new instance
     * next time it's called.
     */
    public static void destroyInstance() {
        //mNotesLocalDataSource
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
            // If the cache is dirty we need to fetch new data.
            getNotesDataSource(callback);
        } else {
            // Query the local storage if available. If not, query the network.
            mNotesLocalDataSource.getNotes(new LoadNoteCallback() {
                @Override
                public void onNotesLoaded(List<Note> notes) {
                    refreshCache(notes);
                    callback.onNotesLoaded(new ArrayList<>(mCachedNotes.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    callback.onDataNotAvailable();
                }
            });
        }
    }

    @Override
    public void getNote(@NonNull String noteId, @NonNull final GetNoteCallback callback) {
        checkNotNull(noteId);
        checkNotNull(callback);
        Note cachedTask = getNoteWithId(noteId);
        // Respond immediately with cache if available
        if (cachedTask != null) {
            callback.onNoteLoaded(cachedTask);
            return;
        }
        // Load from server/persisted if needed.
        // Is the task in the local data source? If not, query the network.
        mNotesLocalDataSource.getNote(noteId, new GetNoteCallback() {
            @Override
            public void onNoteLoaded(Note note) {
                // Do in memory cache update to keep the app UI up to date
                if (mCachedNotes == null) {
                    mCachedNotes = new LinkedHashMap<>();
                }
                mCachedNotes.put(note.getId(), note);
                callback.onNoteLoaded(note);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void getNotesDataSource(@NonNull final LoadNoteCallback callback) {
        mNotesLocalDataSource.getNotes(new LoadNoteCallback() {
            @Override
            public void onNotesLoaded(List<Note> notes) {
                refreshCache(notes);
                refreshLocalDataSource(notes);
                callback.onNotesLoaded(new ArrayList<>(mCachedNotes.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
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
        mNotesLocalDataSource.deleteAllNotes();
        for (Note task : notes) {
            mNotesLocalDataSource.saveNote(task);
        }
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
    public void deleteMarkedNotes(List<Note> markedNote) {
        mNotesLocalDataSource.deleteMarkedNotes(markedNote);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedNotes == null) {
            mCachedNotes= new LinkedHashMap<>();
        }
        Iterator<Map.Entry<String, Note>> it = mCachedNotes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Note> entry = it.next();
            //need to delete based on list of marked notes
            for (int i=0 ; i<markedNote.size(); i++)
            {
                if(markedNote.get(i).getId().equals(entry.getValue().getId()))
                {
                    it.remove();
                }
            }
        }
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

    @Nullable
    private Note getNoteWithId(@NonNull String id) {
        checkNotNull(id);
        if (mCachedNotes == null || mCachedNotes.isEmpty()) {
            return null;
        } else {
            return mCachedNotes.get(id);
        }
    }
}
