package com.example.dendy_s784.myapplication.notes;

import android.support.annotation.NonNull;

import com.example.dendy_s784.myapplication.UseCase;
import com.example.dendy_s784.myapplication.UseCaseHandler;
import com.example.dendy_s784.myapplication.notes.domain.usecase.DeleteNote;
import com.example.dendy_s784.myapplication.data.Note;
import com.example.dendy_s784.myapplication.data.source.NotesDataSource;
import com.example.dendy_s784.myapplication.notes.domain.usecase.GetNotes;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class NotesPresenter implements NotesContract.Presenter{
    //private final NotesRepository mNotesRepository;

    private final NotesContract.View mNotesView;
    private final GetNotes mGetNotes;
    private final DeleteNote mDeleteNote;

    //private NotesFilterType mCurrentFiltering = NotesFilterType.ALL_NOTES;
    private final UseCaseHandler mUseCaseHandler;
    private boolean mFirstLoad = true;

    public void onCreate() {

    }

    public void onDestroy() {

    }

    public NotesPresenter()
    {

        mNotesView = null;
        mGetNotes = null;
        mDeleteNote = null;
        mUseCaseHandler = null;
    }


    public NotesPresenter(@NonNull UseCaseHandler useCaseHandler,
                          @NonNull GetNotes getNotes,
                          @NonNull DeleteNote deleteNote, @NonNull NotesContract.View notesView) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "usecaseHandler cannot be null");
        mGetNotes = checkNotNull(getNotes, "getNotes cannot be null");
        mDeleteNote = checkNotNull(deleteNote, "deleteNote cannot be null");
        mNotesView = checkNotNull(notesView, "notesView cannot be null!");

        mNotesView.setPresenter(this);
    }

    @Override
    public void loadNotes(boolean forceUpdate) {
        // Simplification for sample: a network reload will be forced on first load.
        loadNotes(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link NotesDataSource}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private void loadNotes(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            mNotesView.setLoadingIndicator(true);
        }

        GetNotes.RequestValues requestValues= new GetNotes.RequestValues(forceUpdate);
        mUseCaseHandler.execute(mGetNotes, requestValues,
                new UseCase.UseCaseCallback<GetNotes.ResponseValue>() {
                    @Override
                    public void onSuccess(GetNotes.ResponseValue response) {
                        List<Note> notes = response.getNotes();
                        // The view may not be able to handle UI updates anymore
                        if (showLoadingUI) {
                            mNotesView.setLoadingIndicator(false);
                        }
                        processNotes(notes);
                    }

                    @Override
                    public void onError() {

                    }
                });

        if (showLoadingUI) {
            mNotesView.setLoadingIndicator(false);
        }
    }

    private void processNotes(List<Note> notes) {
        if (notes.isEmpty()) {
            // Show a message indicating there are no tasks for that filter type.
            processEmptyTasks();
        } else {
            // Show the list of tasks
            mNotesView.showNotes(notes);
            // Set the filter label's text.
            //showFilterLabel();
        }
    }

    private void processEmptyTasks() {
        mNotesView.showNoTasks();
    }

    @Override
    public void addNewNotes() {
        System.out.println("addNewNotes");
        mNotesView.showAddNote();
    }

    @Override
    public void openNoteDetails(@NonNull Note requestedNote) {
        checkNotNull(requestedNote, "requestedNote cannot be null!");
        mNotesView.showNoteDetailsUi(requestedNote.getId());
    }

    /**
     * Sets the current task filtering type.

    public void setFiltering(NotesFilterType requestType)
    {
        //mCurrentFiltering = requestType;
    }*/

    @Override
    public void DeleteMarkedNotes(List<Note> markedNote) {
        DeleteNote.RequestValues requestValues= new DeleteNote.RequestValues(markedNote);
        mUseCaseHandler.execute(mDeleteNote, requestValues,
                new UseCase.UseCaseCallback<DeleteNote.ResponseValue>() {
                    @Override
                    public void onSuccess(DeleteNote.ResponseValue response) {
                        mNotesView.showMarkedNotesDelete();
                        loadNotes(false,false);
                    }

                    @Override
                    public void onError() {

                    }
                }
        );
    }
/*
    public NotesFilterType getFiltering()
    {
        return mCurrentFiltering;
    }*/

    @Override
    public void start() {
        loadNotes(false);
    }
}
