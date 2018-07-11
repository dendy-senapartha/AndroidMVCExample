package com.example.dendy_s784.myapplication.addeditnotes;

import com.example.dendy_s784.myapplication.BasePresenter;
import com.example.dendy_s784.myapplication.BaseView;

public interface AddEditNoteContract {

    interface View extends BaseView<Presenter>
    {
        void showEmptyNoteError();

        void showNotesList();

        void setTitle(String title);

        void setDescription(String description);

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void saveNote(String title, String description);

        void populateNote();

        boolean isDataMissing();
    }
}
