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
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static com.google.common.base.Preconditions.checkNotNull;

public class NotesLocalDataSource implements NotesDataSource{

    private static volatile NotesLocalDataSource INSTANCE;

    private NotesDao mNotesDao;

    // Prevent direct instantiation.
    private NotesLocalDataSource(@NonNull NotesDao notesDao) {
        mNotesDao = notesDao;

    }

    public static NotesLocalDataSource getInstance(@NonNull NotesDao notesDao) {
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
        mNotesDao.getNotes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Note>>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull
                                               List<Note> notes) throws Exception {
                        callback.onNotesLoaded(notes);
                    }
                });
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
        mNotesDao.getNoteById(noteId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Note>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull
                                               Note note) throws Exception {
                        callback.onNoteLoaded(note);
                    }
                });
    }

    @Override
    public void saveNote(@NonNull final Note note) {
        checkNotNull(note);
        //By using Completable we agree to ignore the onNext event and
        // only handle onComplete and onError. The API client then becomes:
        //https://android.jlelse.eu/making-your-rxjava-intentions-clearer-with-single-and-completable-f064d98d53a8
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
    }
}
