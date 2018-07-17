package com.example.dendy_s784.myapplication.addeditnotes.domain.usecase;

import com.example.dendy_s784.myapplication.UseCase;
import com.example.dendy_s784.myapplication.data.Note;
import com.example.dendy_s784.myapplication.data.source.NotesDataSource;
import com.example.dendy_s784.myapplication.data.source.NotesRepository;

import java.util.List;

import io.reactivex.annotations.NonNull;

import static com.google.common.base.Preconditions.checkNotNull;

public class GetNote extends UseCase<GetNote.RequestValues, GetNote.ResponseValue>{

    private final NotesRepository mNotesRepository;

    public GetNote(@NonNull NotesRepository notesRepository)
    {
        mNotesRepository = checkNotNull(notesRepository, "notesRepository cannot be nulL!");
    }

    @Override
    protected void executeUseCase(final RequestValues requestValues) {
        mNotesRepository.getNote(requestValues.getNoteID(), new NotesDataSource.GetNoteCallback() {
            @Override
            public void onNoteLoaded(Note note) {
                if (note != null) {
                    ResponseValue responseValue = new ResponseValue(note);
                    getUseCaseCallback().onSuccess(responseValue);
                } else {
                    getUseCaseCallback().onError();
                }
            }

            @Override
            public void onDataNotAvailable() {
                getUseCaseCallback().onError();
            }
        });
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final String mNoteID;

        public RequestValues(@NonNull String  noteID) {
            this.mNoteID = checkNotNull(noteID, "noteID cannot be null!");;
        }

        public String getNoteID()
        {
            return mNoteID;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        private Note mNote;

        public ResponseValue(@NonNull Note note) {
            mNote= checkNotNull(note , "note cannot be Null!");
            System.out.println("ane responnya bang!");
        }

        public Note getNote() {
            return mNote;
        }
    }
}
