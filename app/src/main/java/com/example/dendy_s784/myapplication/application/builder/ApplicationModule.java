package com.example.dendy_s784.myapplication.application.builder;

import android.content.Context;

import com.example.dendy_s784.myapplication.application.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private final Application application;

    public ApplicationModule(Application application)
    {

        this.application = application;
    }

    @Provides
    @Singleton
    Context provideApplicationContext()
    {
        return application;
    }
}
