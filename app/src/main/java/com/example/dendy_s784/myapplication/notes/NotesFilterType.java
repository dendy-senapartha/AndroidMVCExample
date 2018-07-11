package com.example.dendy_s784.myapplication.notes;

/**
 * Used with the filter spinner in the notes list.
 */
public enum NotesFilterType {
    /**
     * Do not filter notes.
     */
    ALL_NOTES,

    /**
     * Filters only the active (not completed yet) notes.
     */
    ACTIVE_NOTES,

    /**
     * Filters only the completed notes.
     */
    COMPLETED_NOTES
}
