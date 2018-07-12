package com.example.dendy_s784.myapplication.notes;

import android.support.annotation.NonNull;

import java.util.List;
import com.example.dendy_s784.myapplication.*;
import com.example.dendy_s784.myapplication.data.Note;

public interface NotesContract {
    interface View extends BaseView<Presenter> {
        void showNotes(List<Note> tasks);

        void showAddNote();

        void showNoteDetailsUi(String taskId);
        void showAllFilterLabel();
        void setLoadingIndicator(boolean active);
        void showLoadingNotesError();
        void showNoTasks();

        void showMarkedNotesDelete();
    }

    interface Presenter extends BasePresenter{
        void loadNotes(boolean forceUpdate);
        void addNewNotes();
        void openNoteDetails(@NonNull Note requestedTask);
        void setFiltering(NotesFilterType requestType);
        void DeleteMarkedNotes(List<Note> markedNote);
        NotesFilterType getFiltering();
    }
}
