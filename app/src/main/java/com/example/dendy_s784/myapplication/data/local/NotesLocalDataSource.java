package com.example.dendy_s784.myapplication.data.local;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.dendy_s784.myapplication.data.Note;
import com.example.dendy_s784.myapplication.data.source.NotesDataSource;
//import com.example.dendy_s784.myapplication.utils.AppExecutors;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.google.common.base.Preconditions.checkNotNull;

public class NotesLocalDataSource implements NotesDataSource{

    private static volatile NotesLocalDataSource INSTANCE;

    private NotesDao mNotesDao;

    //private AppExecutors mAppExecutors;

    // Prevent direct instantiation.
    private NotesLocalDataSource(//@NonNull AppExecutors appExecutors,
                                 @NonNull NotesDao notesDao) {
       // mAppExecutors = appExecutors;
        mNotesDao = notesDao;
    }

    public static NotesLocalDataSource getInstance(//@NonNull AppExecutors appExecutors,
                                                   @NonNull NotesDao notesDao) {
        if (INSTANCE == null) {
            synchronized (NotesLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NotesLocalDataSource(//appExecutors,
                            notesDao);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Note: {@link LoadNoteCallback#onDataNotAvailable()} is fired if the local database error
     * or the table is empty.
     */
    @Override
    public void getNotes(@NonNull final LoadNoteCallback callback) {
        //Load Notes from DB via its DAO
        //the implementation DAO are generated automatically by Room library
        // loading proccess are handled by RXjava
        Disposable disposable = mNotesDao.getNotes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Note>>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull
                                               List<Note> notes) throws Exception {
                        callback.onNotesLoaded(notes);
                    }
                });
        /**
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
                        //if (notes.isEmpty()) {
                            // This will be called if the table is new or just empty.
                            //callback.onDataNotAvailable();
                        //} else
                            {
                            callback.onNotesLoaded(notes);
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);*/
    }

    /**
     * Note: {@link LoadNoteCallback#onDataNotAvailable()} is fired if the local database error
     * or the table is empty.
     */
    @Override
    public void getNote(@NonNull final String noteId, @NonNull final GetNoteCallback callback) {
        //Load Notes from DB via its DAO
        //the implementation DAO are generated automatically by Room library
        // loading proccess are handled by RXjava
        Disposable disposable = mNotesDao.getNoteById(noteId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Note>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull
                                               Note note) throws Exception {
                        callback.onNoteLoaded(note);
                    }
                });
        /**
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Note note= mNotesDao.getNoteById(noteId);

                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        //if (note != null) {
                          //  callback.onNoteLoaded(note);
                        //} else
                            {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);*/
    }

    @Override
    public void saveNote(@NonNull final Note note) {
        checkNotNull(note);
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                mNotesDao.insertNote(note);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                Log.d("saveNote","save complete");
            }

            @Override
            public void onError(Throwable e) {
                Log.d("saveNote","onError"+e.getMessage());
            }
        });
        /*
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                mNotesDao.insertNote(note);
            }
        };
        mAppExecutors.diskIO().execute(saveRunnable);*/
    }

    @Override
    public void deleteMarkedNotes(final List<Note> markedNote) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                int markedNoteSize = markedNote.size();
                for(int i=0 ; i<markedNoteSize ; i++)
                {
                    mNotesDao.deleteNoteById(markedNote.get(i).getId());
                }
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        Log.d("deleteMarkedNotes","delete complete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("deleteMarkedNotes","onError"+e.getMessage());
                    }
                }
        );
        /*
        Runnable clearTasksRunnable = new Runnable() {
            @Override
            public void run() {
                //need to delete based on list of marked item
                int markedNoteSize = markedNote.size();
                for(int i=0 ; i<markedNoteSize ; i++)
                {
                    mNotesDao.deleteNoteById(markedNote.get(i).getId());
                }
            }
        };

        mAppExecutors.diskIO().execute(clearTasksRunnable);*/
    }

    @Override
    public void refreshNotes() {

    }

    @Override
    public void deleteAllNotes() {

        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                mNotesDao.deleteNotes();
            }
        }).observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                Log.d("deleteAllNotes","delete complete");
            }

            @Override
            public void onError(Throwable e) {
                Log.d("deleteAllNotes","onError"+e.getMessage());
            }
        });
        /*
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mNotesDao.deleteNotes();
            }
        };

        mAppExecutors.diskIO().execute(deleteRunnable);*/
    }

    @Override
    public void deleteNotes(@NonNull final String noteId) {

        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                mNotesDao.deleteNoteById(noteId);
            }
        }).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                Log.d("deleteNotes","delete complete");
            }

            @Override
            public void onError(Throwable e) {
                Log.d("deleteNotes","onError"+e.getMessage());
            }
        });
        /*
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mNotesDao.deleteNoteById(noteId);
            }
        };

        mAppExecutors.diskIO().execute(deleteRunnable);*/
    }
}
