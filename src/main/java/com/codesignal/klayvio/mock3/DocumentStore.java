package com.codesignal.klayvio.mock3;

import java.util.List;

public interface DocumentStore {

    /**
     * Creates a document with the given documentId and content.
     *
     * Returns true if the document was created.
     * Returns false if a document with documentId already exists.
     *
     * A failed create must not modify the existing document.
     */
    default boolean create(String documentId, String content) {
        return false;
    }

    /**
     * Replaces the content of an existing document.
     *
     * Returns true if the document exists and was updated.
     * Returns false if the document does not exist.
     *
     * A failed update must not create a document.
     */
    default boolean update(String documentId, String content) {
        return false;
    }

    /**
     * Returns the current content of the document.
     *
     * Returns null if the document does not exist.
     */
    default String get(String documentId) {
        return null;
    }

    /**
     * Deletes the document.
     *
     * Returns true if the document existed and was deleted.
     * Returns false if the document does not exist.
     */
    default boolean delete(String documentId) {
        return false;
    }

    /**
     * Returns up to `limit` document IDs whose documentId starts
     * with the specified prefix.
     *
     * Results must be ordered by:
     * 1. documentId length ascending
     * 2. if lengths are equal, documentId alphabetically ascending
     *
     * Only documents that currently exist are included.
     *
     * If fewer than `limit` documents match, return all matches.
     *
     * If no documents match, return an empty list.
     */
    default List<String> findByPrefix(String prefix, int limit) {
        return List.of();
    }

    /**
     * Returns the number of versions currently available for
     * the specified document.
     *
     * A document has version 1 immediately after creation.
     *
     * Every successful update creates exactly one new version.
     *
     * Failed updates do not create versions.
     *
     * Returns 0 if the document does not currently exist.
     */
    default int getVersionCount(String documentId) {
        return 0;
    }

    /**
     * Returns the content stored at the specified version.
     *
     * Versions are numbered starting at 1.
     *
     * Version 1 is the content supplied when the document was created.
     * Version 2 is the content after the first successful update,
     * and so on.
     *
     * Returns null if:
     * - the document does not currently exist
     * - the requested version does not exist
     */
    default String getVersion(String documentId, int version) {
        return null;
    }

    /**
     * Restores the document to the content stored in the requested version.
     *
     * Restore succeeds only if:
     * - the document currently exists
     * - the requested version exists
     *
     * On success:
     * - the document's current content becomes the content from that version
     * - the restore itself creates a NEW version
     * - return true
     *
     * On failure:
     * - do not modify current content
     * - do not create a new version
     * - return false
     *
     * Example:
     *
     * create("a", "v1")     -> version 1
     * update("a", "v2")     -> version 2
     * update("a", "v3")     -> version 3
     * restoreVersion("a", v1)
     *
     * Current content becomes "v1",
     * but the history is now:
     *
     * version 1 -> v1
     * version 2 -> v2
     * version 3 -> v3
     * version 4 -> v1
     */
    default boolean restoreVersion(String documentId, int version) {
        return false;
    }

    /**
     * Returns the most recent version number whose content
     * exactly equals the specified content.
     *
     * Searches the CURRENT version history of the document.
     *
     * Returns -1 if:
     * - the document does not currently exist
     * - no version has exactly matching content
     *
     * If the same content appears in multiple versions,
     * return the highest version number.
     */
    default int findLatestVersionWithContent(
            String documentId,
            String content) {
        return -1;
    }
}