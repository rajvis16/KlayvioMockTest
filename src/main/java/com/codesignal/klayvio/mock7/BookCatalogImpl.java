package com.codesignal.klayvio.mock7;

import java.util.*;

public class BookCatalogImpl implements BookCatalog {

    private final Map<String, Book> bookMap = new HashMap<>();

    /**
     addBook
     - Add a new book.
     - Return true if the book was added.
     - Return false if bookId already exists.
     - Return false if bookId, title, or author is null or blank.
     **/
    @Override
    public boolean addBook(
            String bookId,
            String title,
            String author) {


        if (bookId == null || title == null || author == null ||
                bookId.isBlank() || title.isBlank() || author.isBlank()) {
            return false;
        }

        if (bookMap.containsKey(bookId)) {
            return false;
        }

        Book book = new Book(bookId, title, author);
        bookMap.put(bookId, book);

        return true;

    }

    /**
     removeBook
     - Remove the book with the given bookId.
     - Return true if it existed and was removed.
     - Return false if it did not exist.
     **/
    @Override
    public boolean removeBook(String bookId) {

        if (!bookMap.containsKey(bookId)) {
            return false;
        }

        bookMap.remove(bookId);

        return true;

    }

    /**
     getBookTitle
     - Return the title for the given bookId.
     - Return null if the book does not exist.
     */
    @Override
    public String getBookTitle(String bookId) {

        if (!bookMap.containsKey(bookId)) {
            return null;
        }

        Book book = bookMap.get(bookId);
        if (book == null) {
            return null;
        }

        long timestamp = book.updatedTitleMapAt.lastKey();

        return book.updatedTitleMapAt.get(timestamp);
    }

    /**
     getBookAuthor
     - Return the author for the given bookId.
     - Return null if the book does not exist.
     */
    @Override
    public String getBookAuthor(String bookId) {


        if (!bookMap.containsKey(bookId)) {
            return null;
        }

        Book book = bookMap.get(bookId);
        if (book == null) {
            return null;
        }


        return book.updatedAuthorMapAt.lastEntry().getValue();
    }

    /**
     getBooksByAuthor(author)

     - Return the bookIds of all books whose author exactly matches `author`.
     - Sort the results by title alphabetically.
     - If two books have the same title, sort those by bookId alphabetically.
     - Return an empty list if there are no matches.
     - Return an empty list if author is null or blank.
     */

    @Override
    public List<String> getBooksByAuthor(String author) {

        if (author == null || author.isBlank()) {
            return List.of();
        }

        return bookMap
                .values()
                .stream()
                .filter(b -> b.updatedAuthorMapAt.lastEntry().getValue().equals(author))
                .sorted((b1, b2) -> {

                    String title1 = b1.updatedTitleMapAt.lastEntry().getValue();
                    String title2 = b2.updatedTitleMapAt.lastEntry().getValue();

                    int cmp = title1.compareTo(title2);

                    if (cmp == 0) {
                        return b1.bookId.compareTo(b2.bookId);
                    }

                    return cmp;
                })
                .map(b -> b.bookId)
                .toList();
    }
    /**
     searchBooksByTitlePrefix(prefix, limit)

     - Return bookIds whose title starts with `prefix`.
     - Matching is case-sensitive.
     - Sort matching books by title alphabetically.
     - If two books have the same title, sort those by bookId alphabetically.
     - Return at most `limit` bookIds.
     - Return an empty list if prefix is null or blank.
     - Return an empty list if limit <= 0.
     */
    @Override
    public List<String> searchBooksByTitlePrefix(String prefix, int limit) {

        if (prefix == null || prefix.isBlank()) {
            return List.of();
        }

        if (limit <= 0) {
            return List.of();
        }

        return bookMap
                .values()
                .stream()
                .filter(b -> b.updatedTitleMapAt.lastEntry().getValue().startsWith(prefix))
                .sorted((b1, b2) -> {

                    String title1 = b1.updatedTitleMapAt.lastEntry().getValue();
                    String title2 = b2.updatedTitleMapAt.lastEntry().getValue();

                    int cmp = title1.compareTo(title2);

                    if (cmp == 0) {
                        return b1.bookId.compareTo(b2.bookId);
                    }

                    return cmp;
                })
                .map(b -> b.bookId)
                .limit(limit)
                .toList();
    }

    /**
     updateTitle(bookId, newTitle, timestamp)

     - Update the title of an existing book.
     - Return false if the book does not exist.
     - Return false if newTitle is null or blank.
     - Return false if timestamp < 0.
     - Return true when the update succeeds.
     - The update must be recorded with its timestamp.
     */
    @Override
    public boolean updateTitle(String bookId, String newTitle, long timestamp) {

        if (timestamp < 0 ) {
            return false;
        }

        if (newTitle == null || newTitle.isBlank()) {
            return false;
        }

        if (!bookMap.containsKey(bookId)) {
            return false;
        }

        Book book = bookMap.get(bookId);
        if (book == null) {
            return false;
        }

        book.updatedTitleMapAt.put(timestamp, newTitle);

        return true;

    }

    /**
     updateAuthor(bookId, newAuthor, timestamp)

     - Update the author of an existing book.
     - Return false if the book does not exist.
     - Return false if newAuthor is null or blank.
     - Return false if timestamp < 0.
     - Return true when the update succeeds.
     - The update must be recorded with its timestamp.
     */
    @Override
    public boolean updateAuthor(String bookId, String newAuthor, long timestamp) {

        if (timestamp < 0 ) {
            return false;
        }

        if (newAuthor == null || newAuthor.isBlank()) {
            return false;
        }

        if (!bookMap.containsKey(bookId)) {
            return false;
        }

        Book book = bookMap.get(bookId);
        if (book == null) {
            return false;
        }

        book.updatedAuthorMapAt.put(timestamp, newAuthor);

        return true;

    }

    /**
     getTitleAt(bookId, timestamp)

     - Return the title that was in effect at the given timestamp.
     - Use the most recent title change whose timestamp is <= the requested timestamp.
     - Return null if the book does not exist.
     - Return null if timestamp < 0.
     */
    @Override
    public String getTitleAt(String bookId, long timestamp) {

        if (!bookMap.containsKey(bookId)) {
            return null;
        }

        if (timestamp < 0) {
            return null;
        }

        Book book = bookMap.get(bookId);
        if (book == null) {
            return null;
        }

        return book.updatedTitleMapAt.floorEntry(timestamp).getValue();

    }

    /**
     getAuthorAt(bookId, timestamp)

     - Return the author that was in effect at the given timestamp.
     - Use the most recent author change whose timestamp is <= the requested timestamp.
     - Return null if the book does not exist.
     - Return null if timestamp < 0.
     */
    @Override
    public String getAuthorAt(String bookId, long timestamp) {

        if (!bookMap.containsKey(bookId)) {
            return null;
        }

        if (timestamp < 0) {
            return null;
        }

        Book book = bookMap.get(bookId);
        if (book == null) {
            return null;
        }

        return book.updatedAuthorMapAt.floorEntry(timestamp).getValue();

    }


    private static class Book {

        String bookId;
        TreeMap<Long, String> updatedAuthorMapAt = new TreeMap<>();
        TreeMap<Long, String> updatedTitleMapAt = new TreeMap<>();

        Book(String bookId, String title, String author) {

            this.bookId = bookId;
            this.updatedTitleMapAt.put(0L, title);
            this.updatedAuthorMapAt.put(0L, author);
        }

    }
}