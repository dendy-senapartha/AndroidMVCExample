package com.example.dendy_s784.myapplication.notes.dagger;

import com.example.dendy_s784.myapplication.application.Application;
import com.example.dendy_s784.myapplication.application.builder.ApplicationComponent;
import com.example.dendy_s784.myapplication.notes.NotesActivity;

import dagger.Component;

@NotesScope
@Component(modules = {NotesContextModule.class, NotesModule.class}, dependencies = {ApplicationComponent.class})
public interface NotesComponent {
    void inject(NotesActivity activity);
}
