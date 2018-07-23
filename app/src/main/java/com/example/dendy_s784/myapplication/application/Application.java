package com.example.dendy_s784.myapplication.application;

import com.example.dendy_s784.myapplication.addeditnotes.AddEditNotesActivity;
import com.example.dendy_s784.myapplication.application.builder.ApplicationComponent;
import com.example.dendy_s784.myapplication.application.builder.ApplicationModule;

import com.example.dendy_s784.myapplication.application.builder.DaggerApplicationComponent;
import com.example.dendy_s784.myapplication.notes.NotesActivity;

public class Application extends android.app.Application{
    private static ApplicationComponent component;

    public void onCreate()
    {
        super.onCreate();

        component = DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this)).build();
        component.inject(this);
    }

    public static ApplicationComponent getComponent(){
        return component;
    }
}
