package com.example.dendy_s784.myapplication.addeditnotes;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.dendy_s784.myapplication.data.Note;
import com.example.dendy_s784.myapplication.data.source.NotesDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

class AddEditNotePresenter implements AddEditNoteContract.Presenter
    ,NotesDataSource.GetNoteCallback
{

    @NonNull
    private final NotesDataSource mNotesRepository;

    @NonNull
    private final AddEditNoteContract.View mAddNoteView;

    @Nullable
    private String mNoteId;

    private boolean mIsDataMissing;

    /**
     * Creates a presenter for the add/edit view.
     *
     * @param noteId ID of the task to edit or null for a new task
     * @param noteRepository a repository of data for tasks
     * @param addNoteView the add/edit view
     * @param shouldLoadDataFromRepo whether data needs to be loaded or not (for config changes)
     */
    public AddEditNotePresenter(@Nullable String noteId, @NonNull NotesDataSource noteRepository,
                                @NonNull AddEditNoteContract.View addNoteView, boolean shouldLoadDataFromRepo) {
        mNoteId = noteId;
        mNotesRepository = checkNotNull(noteRepository);
        mAddNoteView = checkNotNull(addNoteView);
        mIsDataMissing = shouldLoadDataFromRepo;

        mAddNoteView.setPresenter(this);
    }

    @Override
    public void saveNote(String title, String description) {
        if (isNewNote()) {
            createNote(title, description);
        } else {
            updateNote(title, description);
        }
    }

    private boolean isNewNote() {
        return mNoteId == null;
    }

    private void createNote(String title, String description) {
        Note newNote = new Note(title, description);
        if (newNote.isEmpty()) {
            mAddNoteView.showEmptyNoteError();
        } else {
            mNotesRepository.saveNote(newNote);
            mAddNoteView.showNotesList();
        }
    }

    private void updateNote(String title, String description) {
        if (isNewNote()) {
            throw new RuntimeException("updateNote() was called but note is new.");
        }
        mNotesRepository.saveNote(new Note(mNoteId, title, description ));
        mAddNoteView.showNotesList(); // After an edit, go back to the list.
    }

    @Override
    public void populateNote() {

    }

    @Override
    public boolean isDataMissing() {
        return false;
    }

    @Override
    public void start() {

    }

    @Override
    public void onNoteLoaded(Note task) {

    }

    @Override
    public void onDataNotAvailable() {

    }
}
