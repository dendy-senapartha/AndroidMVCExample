package com.example.dendy_s784.myapplication.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.dendy_s784.myapplication.data.local.NotesLocalDataSource;
import com.example.dendy_s784.myapplication.data.local.ToDoDatabase;
import com.example.dendy_s784.myapplication.data.source.NotesRepository;

import static com.google.common.base.Preconditions.checkNotNull;

public class Injection {
    public static NotesRepository provideNotesRepository(@NonNull Context context) {
        checkNotNull(context);

        ToDoDatabase database = ToDoDatabase.getInstance(context);
        //for now, using only local DB
        return NotesRepository.getInstance(
                NotesLocalDataSource.getInstance(new AppExecutors(),database.notesDao())
        );
    }
}
