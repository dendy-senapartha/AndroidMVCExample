package com.example.dendy_s784.myapplication.notes.dagger;

import com.example.dendy_s784.myapplication.R;
import com.example.dendy_s784.myapplication.notes.NotesActivity;
import com.example.dendy_s784.myapplication.notes.NotesFragment;
import com.example.dendy_s784.myapplication.notes.NotesPresenter;
import com.example.dendy_s784.myapplication.utils.ActivityUtils;
import com.example.dendy_s784.myapplication.utils.Injection;

import dagger.Module;
import dagger.Provides;

@Module
public class NotesModule {

    @NotesScope
    @Provides
    NotesPresenter providePresenter(NotesActivity appContext, NotesFragment notesFragment) {
        return new NotesPresenter(
                Injection.provideUseCaseHandler(), Injection.provideGetNotes(appContext),
                Injection.provideDeleteNote(appContext),notesFragment);
    }

    @NotesScope
    @Provides
    NotesFragment provideNotesFragment(NotesActivity context) {
        NotesFragment notesFragment = (NotesFragment) context.getSupportFragmentManager().
                findFragmentById(R.id.contentFrame);
        if(notesFragment == null)
        {
            //create fragment
            notesFragment = NotesFragment.newInstance();
            ActivityUtils.addFragmentToActivity(context.getSupportFragmentManager(),
                    notesFragment,R.id.contentFrame);
        }
        return notesFragment;
    }
}
