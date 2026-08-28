package com.codesignal.klayvio.mock7;

import java.util.*;

public interface BookCatalog {

    /**
     addBook
     - Add a new book.
     - Return true if the book was added.
     - Return false if bookId already exists.
     - Return false if bookId, title, or author is null or blank.
     **/
    boolean addBook(
            String bookId,
            String title,
            String author);

    /**
     removeBook
     - Remove the book with the given bookId.
     - Return true if it existed and was removed.
     - Return false if it did not exist.
     **/
    boolean removeBook(String bookId);

    /**
     getBookTitle
     - Return the title for the given bookId.
     - Return null if the book does not exist.
     */
    String getBookTitle(String bookId);

    /**
     getBookAuthor
     - Return the author for the given bookId.
     - Return null if the book does not exist.
     */
    String getBookAuthor(String bookId);

    List<String> getBooksByAuthor(String author);

    List<String> searchBooksByTitlePrefix(String prefix, int limit);

    boolean updateTitle(String bookId, String newTitle, long timestamp);

    boolean updateAuthor(String bookId, String newAuthor, long timestamp);

    String getTitleAt(String bookId, long timestamp);

    String getAuthorAt(String bookId, long timestamp);
}