package com.example.dendy_s784.myapplication.data;
import android.arch.persistence.room.*;
import android.support.annotation.*;
//guava lib
import com.google.common.base.*;

import java.util.UUID;

@Entity(tableName = "notes")
public final class Note {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "entryid")
    private final String mId;

    @Nullable
    @ColumnInfo(name = "title")
    private final String mTitle;

    @Nullable
    @ColumnInfo(name = "description")
    private final String mDescription;

    /**
     * Use this constructor to create a new active Task.
     *
     * @param title       title of the task
     * @param description description of the task
     */
    @Ignore
    public Note(@Nullable String title, @Nullable String description) {
        this(UUID.randomUUID().toString(), title, description);
    }

    public Note(@NonNull String mId, @Nullable String mTitle, @Nullable String mDescription) {
        this.mId = mId;
        this.mTitle = mTitle;
        this.mDescription = mDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return Objects.equal(getId(), note.getId()) &&
                Objects.equal(getTitle(), note.getTitle()) &&
                Objects.equal(getDescription(), note.getDescription());
    }

    public boolean isEmpty() {
        return Strings.isNullOrEmpty(mTitle) &&
                Strings.isNullOrEmpty(mDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(), getTitle(), getDescription());
    }

    @Override
    public String toString() {
        return "Task with title " + getTitle();
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @Nullable
    public String getTitle() {
        return mTitle;
    }

    @Nullable
    public String getDescription() {
        return mDescription;
    }

    @Nullable
    public String getTitleForList() {
        if (!Strings.isNullOrEmpty(mTitle)) {
            return mTitle;
        } else {
            return mDescription;
        }
    }
}
