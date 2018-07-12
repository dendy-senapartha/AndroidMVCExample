package com.example.dendy_s784.myapplication.notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dendy_s784.myapplication.R;
import com.example.dendy_s784.myapplication.addeditnotes.AddEditNotesActivity;
import com.example.dendy_s784.myapplication.data.Note;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class NotesFragment extends Fragment implements NotesContract.View {

    private NotesContract.Presenter mPresenter;

    private NotesAdapter mListAdapter;
    private TextView mFilteringLabelView;
    private LinearLayout mNotesView;
    private View mNoNotesView;
    private ImageView mNoNotesIcon;
    private TextView mNoNotesMainView;

    private TextView mNoNotesAddView;

    private List<Note> mMarkedNotes;


    public static NotesFragment newInstance(){
        return new NotesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mMarkedNotes = new ArrayList<Note>(0);
        mListAdapter = new NotesAdapter(new ArrayList<Note>(0), mItemListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.notes_frag, container, false);

        // Set up tasks view
        ListView listView = (ListView) root.findViewById(R.id.tasks_list);
        listView.setAdapter(mListAdapter);
        mFilteringLabelView = (TextView) root.findViewById(R.id.filteringLabel);
        mNotesView = (LinearLayout) root.findViewById(R.id.tasksLL);

        // Set up  no tasks view
        mNoNotesView= root.findViewById(R.id.noTasks);
        mNoNotesIcon = (ImageView) root.findViewById(R.id.noTasksIcon);
        mNoNotesMainView = (TextView) root.findViewById(R.id.noTasksMain);
        mNoNotesAddView = (TextView) root.findViewById(R.id.noTasksAdd);
        mNoNotesAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNote();
            }
        });

        // Set up floating action button
        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_add_note);

        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addNewNotes();
            }
        });

        // Set up progress indicator
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout =
                (ScrollChildSwipeRefreshLayout) root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );

        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(listView);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadNotes(false);
            }
        });

        setHasOptionsMenu(true);
        return  root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete_marked:
                mPresenter.DeleteMarkedNotes(mMarkedNotes);
                break;
            case R.id.menu_refresh:
                mPresenter.loadNotes(true);
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.notes_fragment_menu, menu);
    }


    @Override
    public void showNoteDetailsUi(String taskId) {
        // in it's own Activity, since it makes more sense that way and it gives us the flexibility
        // to show some Intent stubbing.
        /*Intent intent = new Intent(getContext(), TaskDetailActivity.class);
        intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, taskId);
        startActivity(intent);*/
    }

    @Override
    public void showAllFilterLabel() {
        mFilteringLabelView.setText(getResources().getString(R.string.label_all));
    }

    @Override
    public void setLoadingIndicator(final boolean active) {
        if (getView() == null) {
            return;
        }
        final SwipeRefreshLayout srl =
                (SwipeRefreshLayout) getView().findViewById(R.id.refresh_layout);

        // Make sure setRefreshing() is called after the layout is done with everything else.
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });
    }

    @Override
    public void showLoadingNotesError() {
        showMessage(getString(R.string.loading_notes_error));
    }

    @Override
    public void showNoTasks() {
        showNoTasksViews(
                getResources().getString(R.string.no_notes_all),
                R.drawable.ic_assignment_turned_in_24dp,
                false
        );
    }

    @Override
    public void showMarkedNotesDelete() {
        showMessage(getString(R.string.marked_notes_delete));
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void showNotes(List<Note> tasks) {
        mListAdapter.replaceData(tasks);

        mNotesView.setVisibility(View.VISIBLE);
        mNoNotesView.setVisibility(View.GONE);
    }

    private void showNoTasksViews(String mainText, int iconRes, boolean showAddView) {
        mNotesView.setVisibility(View.GONE);
        mNoNotesView.setVisibility(View.VISIBLE);

        mNoNotesMainView.setText(mainText);
        mNoNotesIcon.setImageDrawable(getResources().getDrawable(iconRes));
        mNoNotesAddView.setVisibility(showAddView ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showAddNote() {
        Intent intent = new Intent(getContext(), AddEditNotesActivity.class);
        startActivityForResult(intent, AddEditNotesActivity.REQUEST_ADD_NOTE);
    }

    @Override
    public void setPresenter(NotesContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    //inner class
    private static class NotesAdapter extends BaseAdapter {

        private List<Note> mNotes;
        private NoteItemListener mItemListener;

        public NotesAdapter(List<Note> notes, NoteItemListener itemListener) {
            setList(notes);
            mItemListener = itemListener;
        }

        public void replaceData(List<Note> tasks){
            setList(tasks);
            notifyDataSetChanged();
        }

        private void setList(List<Note> notes) {
            mNotes = checkNotNull(notes);
        }

        @Override
        public int getCount() {
            return mNotes.size();
        }

        @Override
        public Object getItem(int i) {
            return mNotes.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowView = view;
            if(rowView == null)
            {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                rowView = inflater.inflate(R.layout.note_item, viewGroup, false);
            }
            final Note note = (Note) getItem(i);

            TextView titleTV = (TextView) rowView.findViewById(R.id.title);
            titleTV.setText(note.getTitleForList());

            CheckBox completeCB = (CheckBox) rowView.findViewById(R.id.complete);

            // Active/completed task UI
            /*completeCB.setChecked(note.isCompleted());
            if (task.isCompleted()) {
                rowView.setBackgroundDrawable(viewGroup.getContext()
                        .getResources().getDrawable(R.drawable.list_completed_touch_feedback));
            } else {
                rowView.setBackgroundDrawable(viewGroup.getContext()
                        .getResources().getDrawable(R.drawable.touch_feedback));
            }*/

            completeCB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemListener.onMarkedNoteClick(note);
                }
            });

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemListener.onNoteClick(note);
                }
            });
            return rowView;
        }
    }

    /**
     * Listener for clicks on notes in the ListView.
     */
    NoteItemListener mItemListener = new NoteItemListener() {

        @Override
        public void onNoteClick(Note clickedNote) {

            mPresenter.openNoteDetails(clickedNote);
        }

        @Override
        public void onMarkedNoteClick(Note note) {
            if(isNoteMarked(note))
            {
                mMarkedNotes.remove(note);
            }
            else
            {
                mMarkedNotes.add(note);
            }
        }

        private boolean isNoteMarked(Note note)
        {
            boolean result = false;
            int markedNoteSize = mMarkedNotes.size();
            for (int i=0 ; i<markedNoteSize; i++)
            {
                if(mMarkedNotes.get(i).getId().equals(note.getId()))
                {
                    result = true;
                }
            }
            return result;
        }

        @Override
        public void onActivateNoteClick(Note activatedNote) {
            //mPresenter.openNoteDetails(clickedNote);
        }
    };

    //inner interface
    public interface NoteItemListener {

        void onNoteClick(Note clickedNote);

        void onMarkedNoteClick(Note completedNote);

        void onActivateNoteClick(Note activatedNote);
    }
}
