package com.example.dendy_s784.myapplication.addeditnotes;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dendy_s784.myapplication.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.google.common.base.Preconditions.checkNotNull;

public class AddEditNoteFragment extends Fragment implements AddEditNoteContract.View{

    public static final String ARGUMENT_EDIT_NOTE_ID = "EDIT_NOTE_ID";

    private AddEditNoteContract.Presenter mPresenter;

    @BindView(R.id.add_task_title) TextView mTitle;
    @BindView(R.id.add_task_description) TextView mDescription;

    public static AddEditNoteFragment newInstance() {
        return new AddEditNoteFragment();
    }

    public AddEditNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_edit_note_done);
        fab.setImageResource(R.drawable.ic_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.saveNote(mTitle.getText().toString(), mDescription.getText().toString());
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.addnote_frag, container, false);
        //bind view
        ButterKnife.bind(this, root);

        //mTitle = (TextView) root.findViewById(R.id.add_task_title);
        //mDescription = (TextView) root.findViewById(R.id.add_task_description);
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void showEmptyNoteError() {

    }

    @Override
    public void showNotesList() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public void setDescription(String description) {
        mDescription.setText(description);
    }

    @Override
    public void setPresenter(@NonNull AddEditNoteContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }
}
