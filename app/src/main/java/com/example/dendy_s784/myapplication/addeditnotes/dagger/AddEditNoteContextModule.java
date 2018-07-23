package com.example.dendy_s784.myapplication.addeditnotes.dagger;

import com.example.dendy_s784.myapplication.addeditnotes.AddEditNotesActivity;
import dagger.Module;
import dagger.Provides;

@Module
public class AddEditNoteContextModule {
    AddEditNotesActivity noteContext;

    public AddEditNoteContextModule(AddEditNotesActivity noteContext)
    {
        this.noteContext = noteContext;
    }

    @AddEditNotesScope
    @Provides
    AddEditNotesActivity provideAddEditNotesContext(){
        return noteContext;
    }
}
