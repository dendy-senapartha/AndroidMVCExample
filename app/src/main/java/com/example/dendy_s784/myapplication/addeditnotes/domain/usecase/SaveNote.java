package com.example.dendy_s784.myapplication.addeditnotes.domain.usecase;

import android.support.annotation.NonNull;

import com.example.dendy_s784.myapplication.UseCase;
import com.example.dendy_s784.myapplication.data.Note;
import com.example.dendy_s784.myapplication.data.source.NotesRepository;

import static com.google.common.base.Preconditions.checkNotNull;

public class SaveNote extends UseCase<SaveNote.RequestValues, SaveNote.ResponseValue>{

    private final NotesRepository mNotesRepository;

    public SaveNote(NotesRepository mNotesRepository) {
        this.mNotesRepository = checkNotNull(mNotesRepository, "mNotesRepository cannot be null!");;
    }


    @Override
    protected void executeUseCase(RequestValues requestValues) {
        Note note = requestValues.getNote();
        mNotesRepository.saveNote(note);

        getUseCaseCallback().onSuccess(new ResponseValue(note));
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final Note mNote;

        public RequestValues(@NonNull Note note) {
            mNote = checkNotNull(note, "note cannot be null!");
        }

        public Note getNote() {
            return mNote;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final Note mNote;

        public ResponseValue(@NonNull Note note) {
            mNote = checkNotNull(note, "note cannot be null!");
        }

        public Note getNote() {
            return mNote;
        }
    }
}
