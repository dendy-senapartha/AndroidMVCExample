package com.example.dendy_s784.myapplication.addeditnotes.dagger;

import dagger.Module;
import dagger.Provides;

import com.example.dendy_s784.myapplication.R;
import com.example.dendy_s784.myapplication.addeditnotes.*;
import com.example.dendy_s784.myapplication.utils.ActivityUtils;
import com.example.dendy_s784.myapplication.utils.Injection;

@Module
public class AddEditNoteModule {

    @AddEditNotesScope
    @Provides
    AddEditNotePresenter providePresenter(AddEditNotesActivity appContext, AddEditNoteFragment fragment)
    {
        return new AddEditNotePresenter(Injection.provideUseCaseHandler(),
                fragment,
                Injection.provideGetNote(appContext),
                Injection.provideSaveNote(appContext)
                );
    }

    @AddEditNotesScope
    @Provides
    AddEditNoteFragment provideFragment(AddEditNotesActivity context)
    {
        AddEditNoteFragment notesFragment = (AddEditNoteFragment) context.getSupportFragmentManager().
                findFragmentById(R.id.contentFrame);
        if(notesFragment == null)
        {
            //create fragment
            notesFragment = AddEditNoteFragment.newInstance();
            ActivityUtils.addFragmentToActivity(context.getSupportFragmentManager(),
                    notesFragment,R.id.contentFrame);
        }
        return notesFragment;
    }
}
