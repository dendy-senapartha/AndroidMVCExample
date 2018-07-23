package com.example.dendy_s784.myapplication.application.builder;

import com.example.dendy_s784.myapplication.addeditnotes.AddEditNotesActivity;
import com.example.dendy_s784.myapplication.application.Application;
import com.example.dendy_s784.myapplication.notes.NotesActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    //void inject(AddEditNotesActivity addEditNotesActivity);
    //void inject(NotesActivity notesActivity);
    void inject(Application application);
}
