package com.example.dendy_s784.myapplication.addeditnotes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.dendy_s784.myapplication.R;
import com.example.dendy_s784.myapplication.addeditnotes.dagger.AddEditNoteContextModule;
import com.example.dendy_s784.myapplication.addeditnotes.dagger.DaggerAddEditNoteComponent;
import com.example.dendy_s784.myapplication.application.Application;
import com.example.dendy_s784.myapplication.notes.dagger.NotesContextModule;
import com.example.dendy_s784.myapplication.utils.ActivityUtils;
import com.example.dendy_s784.myapplication.utils.Injection;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddEditNotesActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_NOTE = 1;

    public static final String SHOULD_LOAD_DATA_FROM_REPO_KEY = "SHOULD_LOAD_DATA_FROM_REPO_KEY";

    private ActionBar mActionBar;

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Inject
     AddEditNotePresenter mAddEditTaskPresenter;

    @Inject
    AddEditNoteFragment addEditNoteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addnote_act);
        //inject dependency
        DaggerAddEditNoteComponent.builder().applicationComponent(Application.getComponent())
                .addEditNoteContextModule(new AddEditNoteContextModule(this))
                .build().inject(this);
        //BindView
        ButterKnife.bind(this);

        // Set up the toolbar.
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);

        String noteId = getIntent().getStringExtra(AddEditNoteFragment.ARGUMENT_EDIT_NOTE_ID);
        setToolbarTitle(noteId);

        if (getIntent().hasExtra(AddEditNoteFragment.ARGUMENT_EDIT_NOTE_ID)) {
            Bundle bundle = new Bundle();
            bundle.putString(AddEditNoteFragment.ARGUMENT_EDIT_NOTE_ID, noteId);
            addEditNoteFragment.setArguments(bundle);
        }

        // set the presenter
        mAddEditTaskPresenter.setNoteId(noteId);

        boolean shouldLoadDataFromRepo = true;

        // Prevent the presenter from loading data from the repository if this is a config change.
        if (savedInstanceState != null) {
            // Data might not have loaded when the config change happen, so we saved the state.
            shouldLoadDataFromRepo = savedInstanceState.getBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY);
        }
        mAddEditTaskPresenter.setIsDataMissing(shouldLoadDataFromRepo);

    }

    private void setToolbarTitle(@Nullable String taskId) {
        if(taskId == null) {
            mActionBar.setTitle(R.string.add_note);
        } else {
            mActionBar.setTitle(R.string.edit_note);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY, mAddEditTaskPresenter.isDataMissing());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
