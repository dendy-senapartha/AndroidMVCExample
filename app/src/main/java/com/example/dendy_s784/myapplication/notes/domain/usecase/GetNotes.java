package com.example.dendy_s784.myapplication.notes.domain.usecase;

import com.example.dendy_s784.myapplication.UseCase;
import com.example.dendy_s784.myapplication.data.Note;
import com.example.dendy_s784.myapplication.data.source.NotesDataSource;
import com.example.dendy_s784.myapplication.data.source.NotesRepository;

import java.util.List;

import io.reactivex.annotations.NonNull;

import static com.google.common.base.Preconditions.checkNotNull;

public class GetNotes extends UseCase<GetNotes.RequestValues, GetNotes.ResponseValue> {
    private final NotesRepository mNotesRepository;

    public GetNotes(@NonNull NotesRepository mNotesRepository) {
        this.mNotesRepository = checkNotNull(mNotesRepository, "mNotesRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(final RequestValues requestValues) {
        if(requestValues.isForceUpdate())
        {
            mNotesRepository.refreshNotes();
        }

        mNotesRepository.getNotes(new NotesDataSource.LoadNoteCallback() {
            @Override
            public void onNotesLoaded(List<Note> tasks) {
                ResponseValue responseValue = new ResponseValue(tasks);
                getUseCaseCallback().onSuccess(responseValue);
            }

            @Override
            public void onDataNotAvailable() {
                getUseCaseCallback().onError();
            }
        });
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final boolean mForceUpdate;

        public RequestValues(boolean forceUpdate) {
            mForceUpdate = forceUpdate;
        }

        public boolean isForceUpdate() {
            return mForceUpdate;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        private final List<Note> mNotes;

        public ResponseValue(@android.support.annotation.NonNull List<Note> notes) {
            mNotes = checkNotNull(notes, "notes cannot be null!");
        }

        public List<Note> getNotes() {
            return mNotes;
        }
    }
}
