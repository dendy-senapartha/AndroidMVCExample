package com.example.dendy_s784.myapplication.addeditnotes.dagger;

import com.example.dendy_s784.myapplication.addeditnotes.AddEditNotesActivity;
import com.example.dendy_s784.myapplication.application.Application;
import com.example.dendy_s784.myapplication.application.builder.ApplicationComponent;
import com.example.dendy_s784.myapplication.notes.dagger.NotesContextModule;

import dagger.Component;

@AddEditNotesScope
@Component(modules = {AddEditNoteContextModule.class, AddEditNoteModule.class}, dependencies = {ApplicationComponent.class})
public interface AddEditNoteComponent {
    void inject(AddEditNotesActivity activity);
}
