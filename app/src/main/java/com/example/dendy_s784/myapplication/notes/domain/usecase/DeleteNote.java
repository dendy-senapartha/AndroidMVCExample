package com.example.dendy_s784.myapplication.notes.domain.usecase;

import com.example.dendy_s784.myapplication.UseCase;
import com.example.dendy_s784.myapplication.data.Note;
import com.example.dendy_s784.myapplication.data.source.NotesRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class DeleteNote extends UseCase<DeleteNote.RequestValues, DeleteNote.ResponseValue>{

    private final NotesRepository mNotesRepository;

    public DeleteNote(NotesRepository mNotesRepository) {
        this.mNotesRepository = checkNotNull(mNotesRepository, "mNotesRepository cannot be null!") ;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mNotesRepository.deleteMarkedNotes(requestValues.getMarkedNote());
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues
    {
        private final List<Note> markedNote;

        public RequestValues(List<Note> markedNote) {
            this.markedNote = checkNotNull(markedNote, "taskId cannot be null!");
        }

        public List<Note> getMarkedNote() {
            return markedNote;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue
    {

    }
}
