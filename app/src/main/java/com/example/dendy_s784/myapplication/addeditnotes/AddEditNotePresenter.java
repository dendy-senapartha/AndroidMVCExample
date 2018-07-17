package com.example.dendy_s784.myapplication.addeditnotes;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.dendy_s784.myapplication.UseCase;
import com.example.dendy_s784.myapplication.UseCaseHandler;
import com.example.dendy_s784.myapplication.addeditnotes.domain.usecase.SaveNote;
import com.example.dendy_s784.myapplication.data.Note;
import com.example.dendy_s784.myapplication.addeditnotes.domain.usecase.GetNote;

import static com.google.common.base.Preconditions.checkNotNull;

class AddEditNotePresenter implements AddEditNoteContract.Presenter
{

    //@NonNull
    //private final NotesDataSource mNotesRepository;

    @NonNull
    private final AddEditNoteContract.View mAddNoteView;

    @Nullable
    private String mNoteId;

    private boolean mIsDataMissing;

    private final GetNote mGetNote;

    private final SaveNote mSaveNote;

    private final UseCaseHandler mUseCaseHandler;

    /**
     * Creates a presenter for the add/edit view.
     *
     * @param noteId ID of the task to edit or null for a new task
     * @param getNote is a note usecase
     * @param addNoteView the add/edit view
     * @param shouldLoadDataFromRepo whether data needs to be loaded or not (for config changes)
     */
    public AddEditNotePresenter(@NonNull UseCaseHandler useCaseHandler,@Nullable String noteId,
                                @NonNull AddEditNoteContract.View addNoteView, @NonNull GetNote getNote,
                                @NonNull SaveNote saveNote, boolean shouldLoadDataFromRepo) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "useCaseHandler cannot be null!");
        mNoteId = noteId;
        //mNotesRepository = checkNotNull(noteRepository , "notesRepository cannot be null!");
        mGetNote = checkNotNull(getNote, "getNote cannot be null!");
        mAddNoteView = checkNotNull(addNoteView, "mAddNoteView cannot be null!");
        mSaveNote = checkNotNull(saveNote, "saveNote cannot be null!");
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
           // mNotesRepository.saveNote(newNote);
            mUseCaseHandler.execute(mSaveNote, new SaveNote.RequestValues(newNote),
                    new UseCase.UseCaseCallback<SaveNote.ResponseValue>() {
                        @Override
                        public void onSuccess(SaveNote.ResponseValue response) {
                            mAddNoteView.showNotesList();
                        }

                        @Override
                        public void onError() {
                            //showSaveError();
                        }
                    });
        }
    }

    private void updateNote(String title, String description) {
        if (isNewNote()) {
            throw new RuntimeException("updateNote() was called but note is new.");
        }

        Note newNote = new Note(mNoteId, title, description);
        mUseCaseHandler.execute(mSaveNote, new SaveNote.RequestValues(newNote),
                new UseCase.UseCaseCallback<SaveNote.ResponseValue>() {
                    @Override
                    public void onSuccess(SaveNote.ResponseValue response) {
                        // After an edit, go back to the list.
                        mAddNoteView.showNotesList();
                    }

                    @Override
                    public void onError() {

                    }
                });

    }

    @Override
    public void populateNote() {
        if (isNewNote()) {
            throw new RuntimeException("populateNote() was called but note is new.");
        }
        mUseCaseHandler.execute(mGetNote, new GetNote.RequestValues(mNoteId),
                new UseCase.UseCaseCallback<GetNote.ResponseValue>() {
                    @Override
                    public void onSuccess(GetNote.ResponseValue response) {
                        showNote(response.getNote());
                    }

                    @Override
                    public void onError() {

                    }
                });

      //  mNotesRepository.getNote(mNoteId, this);
    }

    @Override
    public boolean isDataMissing() {
        return false;
    }

    @Override
    public void start() {
        if (!isNewNote() && mIsDataMissing) {
            populateNote();
        }
    }

    public void showNote(Note note) {
        // The view may not be able to handle UI updates anymore

        mAddNoteView.setTitle(note.getTitle());
        mAddNoteView.setDescription(note.getDescription());

        mIsDataMissing = false;
    }
}
