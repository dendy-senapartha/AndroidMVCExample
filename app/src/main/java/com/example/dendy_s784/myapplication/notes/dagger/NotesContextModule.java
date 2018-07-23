package com.example.dendy_s784.myapplication.notes.dagger;

import com.example.dendy_s784.myapplication.notes.NotesActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class NotesContextModule {
    NotesActivity noteContext;

    public NotesContextModule(NotesActivity noteContext)
    {
        this.noteContext = noteContext;
    }

    @NotesScope
    @Provides
    NotesActivity provideNotesContext(){
        return noteContext;
    }
}
