package com.example.dendy_s784.myapplication.notes;

import android.support.annotation.NonNull;

import com.example.dendy_s784.myapplication.data.Note;
import com.example.dendy_s784.myapplication.data.source.NotesRepository;
import com.example.dendy_s784.myapplication.data.source.NotesDataSource;
import com.example.dendy_s784.myapplication.utils.EspressoIdlingResource;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class NotesPresenter implements NotesContract.Presenter{
    private final NotesRepository mNotesRepository;

    private final NotesContract.View mNotesView;

    private NotesFilterType mCurrentFiltering = NotesFilterType.ALL_NOTES;

    private boolean mFirstLoad = true;

    public NotesPresenter(@NonNull NotesRepository notesRepository, @NonNull NotesContract.View notesView) {
        mNotesRepository = checkNotNull(notesRepository, "tasksRepository cannot be null");
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
        if (forceUpdate) {
            mNotesRepository.refreshNotes();
        }

        // The network request might be handled in a different thread so make sure Espresso knows
        // that the app is busy until the response is handled.
        EspressoIdlingResource.increment(); // App is busy until further notice

        mNotesRepository.getNotes(new NotesDataSource.LoadNoteCallback() {
                @Override
                public void onNotesLoaded(List<Note> notes) {
                    List<Note> notesToShow = new ArrayList<Note>();
                    // This callback may be called twice, once for the cache and once for loading
                    // the data from the server API, so we check before decrementing, otherwise
                    // it throws "Counter has been corrupted!" exception.
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                        EspressoIdlingResource.decrement(); // Set app as idle.
                    }

                    // We filter the tasks based on the requestType
                    for (Note note : notes) {
                        switch (mCurrentFiltering) {
                           // case ALL_NOTES:
                             //   notesToShow.add(note);
                             //   break;
                            default:
                                notesToShow.add(note);
                                break;
                        }
                    }
                    //some step required
                    if (showLoadingUI) {
                        mNotesView.setLoadingIndicator(false);
                    }
                    processNotes(notesToShow);
                }
                @Override
                public void onDataNotAvailable() {
                    mNotesView.showLoadingNotesError();
                }
            }
        );
    }

    private void processNotes(List<Note> notes) {
        if (notes.isEmpty()) {
            // Show a message indicating there are no tasks for that filter type.
            processEmptyTasks();
        } else {
            // Show the list of tasks
            mNotesView.showNotes(notes);
            // Set the filter label's text.
            showFilterLabel();
        }
    }

    private void showFilterLabel() {
        switch (mCurrentFiltering) {
            case ACTIVE_NOTES:
                //mTasksView.showActiveFilterLabel();
                break;
            default:
                mNotesView.showAllFilterLabel();
                break;
        }
    }

    private void processEmptyTasks() {
        switch (mCurrentFiltering) {
            default:
                mNotesView.showNoTasks();
                break;
        }
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
     *
     * @param requestType Can be {@link NotesFilterType#ALL_NOTES},
     *                    {@link NotesFilterType#COMPLETED_NOTES}, or
     *                    {@link NotesFilterType#ACTIVE_NOTES}
     */
    public void setFiltering(NotesFilterType requestType)
    {
        mCurrentFiltering = requestType;
    }

    @Override
    public void DeleteMarkedNotes(List<Note> markedNote) {
        mNotesRepository.deleteMarkedNotes(markedNote);
        mNotesView.showMarkedNotesDelete();
        loadNotes(false,false);
    }

    public NotesFilterType getFiltering()
    {
        return mCurrentFiltering;
    }

    @Override
    public void start() {
        loadNotes(false);
    }
}
