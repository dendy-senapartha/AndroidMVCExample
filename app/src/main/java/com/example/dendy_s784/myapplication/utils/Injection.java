package com.example.dendy_s784.myapplication.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.dendy_s784.myapplication.UseCaseHandler;
import com.example.dendy_s784.myapplication.notes.domain.usecase.DeleteNote;
import com.example.dendy_s784.myapplication.addeditnotes.domain.usecase.GetNote;
import com.example.dendy_s784.myapplication.addeditnotes.domain.usecase.SaveNote;
import com.example.dendy_s784.myapplication.data.local.NotesLocalDataSource;
import com.example.dendy_s784.myapplication.data.local.ToDoDatabase;
import com.example.dendy_s784.myapplication.data.source.NotesRepository;
import com.example.dendy_s784.myapplication.notes.domain.usecase.GetNotes;

import static com.google.common.base.Preconditions.checkNotNull;

public class Injection {
    public static NotesRepository provideNotesRepository(@NonNull Context context) {
        checkNotNull(context);

        ToDoDatabase database = ToDoDatabase.getInstance(context);
        //for now, using only local DB
        return NotesRepository.getInstance(
                NotesLocalDataSource.getInstance(database.notesDao())
        );
    }

    public static UseCaseHandler provideUseCaseHandler() {
        return UseCaseHandler.getInstance();
    }

    public static GetNotes provideGetNotes(@NonNull Context context) {
        return new GetNotes(provideNotesRepository(context));
    }
    public static GetNote provideGetNote(@NonNull Context context) {
        return new GetNote(Injection.provideNotesRepository(context));
    }

    public static SaveNote provideSaveNote(@NonNull Context context) {
        return new SaveNote(Injection.provideNotesRepository(context));
    }

    public static DeleteNote provideDeleteNote(@NonNull Context context) {
        return new DeleteNote(Injection.provideNotesRepository(context));
    }
}
