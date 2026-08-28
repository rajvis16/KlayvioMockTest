package com.codesignal.klayvio.mock7;

import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class BookCatalogTests {

    @Test
    void addBookAndReadFields() {

        BookCatalog catalog = new BookCatalogImpl();

        assertTrue(catalog.addBook("B1", "Effective Java", "Joshua Bloch"));

        assertEquals("Effective Java", catalog.getBookTitle("B1"));
        assertEquals("Joshua Bloch", catalog.getBookAuthor("B1"));
    }

    @Test
    void duplicateBookIdIsRejected() {

        BookCatalog catalog = new BookCatalogImpl();

        assertTrue(catalog.addBook("B1", "Effective Java", "Joshua Bloch"));
        assertFalse(catalog.addBook("B1", "Java Concurrency", "Brian Goetz"));

        assertEquals("Effective Java", catalog.getBookTitle("B1"));
        assertEquals("Joshua Bloch", catalog.getBookAuthor("B1"));
    }

    @Test
    void removeExistingBook() {

        BookCatalog catalog = new BookCatalogImpl();

        assertTrue(catalog.addBook("B1", "Effective Java", "Joshua Bloch"));

        assertTrue(catalog.removeBook("B1"));

        assertNull(catalog.getBookTitle("B1"));
        assertNull(catalog.getBookAuthor("B1"));
    }

    @Test
    void removeMissingBookReturnsFalse() {

        BookCatalog catalog = new BookCatalogImpl();

        assertFalse(catalog.removeBook("UNKNOWN"));
    }

    @Test
    void missingBookReturnsNull() {

        BookCatalog catalog = new BookCatalogImpl();

        assertNull(catalog.getBookTitle("UNKNOWN"));
        assertNull(catalog.getBookAuthor("UNKNOWN"));
    }

    @Test
    void invalidBookDataIsRejected() {

        BookCatalog catalog = new BookCatalogImpl();

        assertFalse(catalog.addBook(null, "Title", "Author"));
        assertFalse(catalog.addBook("", "Title", "Author"));
        assertFalse(catalog.addBook("   ", "Title", "Author"));

        assertFalse(catalog.addBook("B1", null, "Author"));
        assertFalse(catalog.addBook("B1", "", "Author"));
        assertFalse(catalog.addBook("B1", "   ", "Author"));

        assertFalse(catalog.addBook("B1", "Title", null));
        assertFalse(catalog.addBook("B1", "Title", ""));
        assertFalse(catalog.addBook("B1", "Title", "   "));
    }

    @Test
    void multipleBooksAreIndependent() {

        BookCatalog catalog = new BookCatalogImpl();

        assertTrue(catalog.addBook("B1", "Effective Java", "Joshua Bloch"));
        assertTrue(catalog.addBook("B2", "Clean Code", "Robert Martin"));
        assertTrue(catalog.addBook("B3", "Designing Data-Intensive Applications", "Martin Kleppmann"));

        assertEquals("Effective Java", catalog.getBookTitle("B1"));
        assertEquals("Clean Code", catalog.getBookTitle("B2"));
        assertEquals(
                "Designing Data-Intensive Applications",
                catalog.getBookTitle("B3")
        );

        assertTrue(catalog.removeBook("B2"));

        assertEquals("Effective Java", catalog.getBookTitle("B1"));
        assertNull(catalog.getBookTitle("B2"));
        assertEquals(
                "Designing Data-Intensive Applications",
                catalog.getBookTitle("B3")
        );
    }

    @Test
    void getBooksByAuthorReturnsMatchingBooksSortedByTitle() {

        BookCatalog catalog = new BookCatalogImpl();

        catalog.addBook("B1", "Effective Java", "Joshua Bloch");
        catalog.addBook("B2", "Java Puzzlers", "Joshua Bloch");
        catalog.addBook("B3", "Clean Code", "Robert Martin");
        catalog.addBook("B4", "Java Concurrency in Practice", "Brian Goetz");

        assertEquals(
                List.of("B1", "B2"),
                catalog.getBooksByAuthor("Joshua Bloch")
        );
    }

    @Test
    void getBooksByAuthorUsesBookIdAsTieBreaker() {

        BookCatalog catalog = new BookCatalogImpl();

        catalog.addBook("B3", "Java Guide", "Author A");
        catalog.addBook("B1", "Java Guide", "Author A");
        catalog.addBook("B2", "Algorithms", "Author A");

        assertEquals(
                List.of("B2", "B1", "B3"),
                catalog.getBooksByAuthor("Author A")
        );
    }

    @Test
    void getBooksByAuthorReturnsEmptyForMissingOrInvalidAuthor() {

        BookCatalog catalog = new BookCatalogImpl();

        catalog.addBook("B1", "Effective Java", "Joshua Bloch");

        assertEquals(List.of(), catalog.getBooksByAuthor("Unknown"));
        assertEquals(List.of(), catalog.getBooksByAuthor(null));
        assertEquals(List.of(), catalog.getBooksByAuthor(""));
        assertEquals(List.of(), catalog.getBooksByAuthor("   "));
    }

    @Test
    void searchBooksByTitlePrefixReturnsSortedMatches() {

        BookCatalog catalog = new BookCatalogImpl();

        catalog.addBook("B1", "Effective Java", "Joshua Bloch");
        catalog.addBook("B2", "Effective Java in Practice", "Author B");
        catalog.addBook("B3", "Clean Code", "Robert Martin");
        catalog.addBook("B4", "Effective Java", "Author C");
        catalog.addBook("B5", "Effective Kotlin", "Author D");

        assertEquals(
                List.of("B1", "B4", "B2", "B5"),
                catalog.searchBooksByTitlePrefix("Effective", 10)
        );
    }

    @Test
    void searchBooksByTitlePrefixHonorsLimit() {

        BookCatalog catalog = new BookCatalogImpl();

        catalog.addBook("B1", "Java A", "Author");
        catalog.addBook("B2", "Java B", "Author");
        catalog.addBook("B3", "Java C", "Author");
        catalog.addBook("B4", "Java D", "Author");

        assertEquals(
                List.of("B1", "B2"),
                catalog.searchBooksByTitlePrefix("Java", 2)
        );
    }

    @Test
    void searchBooksByTitlePrefixUsesBookIdAsTieBreaker() {

        BookCatalog catalog = new BookCatalogImpl();

        catalog.addBook("B9", "Java", "Author A");
        catalog.addBook("B2", "Java", "Author B");
        catalog.addBook("B5", "Java", "Author C");

        assertEquals(
                List.of("B2", "B5", "B9"),
                catalog.searchBooksByTitlePrefix("Java", 10)
        );
    }

    @Test
    void searchBooksByTitlePrefixHandlesInvalidInput() {

        BookCatalog catalog = new BookCatalogImpl();

        catalog.addBook("B1", "Effective Java", "Joshua Bloch");

        assertEquals(List.of(), catalog.searchBooksByTitlePrefix(null, 10));
        assertEquals(List.of(), catalog.searchBooksByTitlePrefix("", 10));
        assertEquals(List.of(), catalog.searchBooksByTitlePrefix("   ", 10));

        assertEquals(List.of(), catalog.searchBooksByTitlePrefix("Effective", 0));
        assertEquals(List.of(), catalog.searchBooksByTitlePrefix("Effective", -1));
    }

    @Test
    void removedBooksDoNotAppearInQueries() {

        BookCatalog catalog = new BookCatalogImpl();

        catalog.addBook("B1", "Effective Java", "Joshua Bloch");
        catalog.addBook("B2", "Java Puzzlers", "Joshua Bloch");

        assertTrue(catalog.removeBook("B1"));

        assertEquals(
                List.of("B2"),
                catalog.getBooksByAuthor("Joshua Bloch")
        );

        assertEquals(
                List.of(),
                catalog.searchBooksByTitlePrefix("Effective", 10)
        );
    }

    @Test
    void titleHistoryCanBeQueriedByTimestamp() {

        BookCatalog catalog = new BookCatalogImpl();

        catalog.addBook("B1", "Old Title", "Author");

        assertTrue(catalog.updateTitle("B1", "Middle Title", 100));
        assertTrue(catalog.updateTitle("B1", "New Title", 200));

        assertEquals("Old Title", catalog.getTitleAt("B1", 0));
        assertEquals("Old Title", catalog.getTitleAt("B1", 99));
        assertEquals("Middle Title", catalog.getTitleAt("B1", 100));
        assertEquals("Middle Title", catalog.getTitleAt("B1", 199));
        assertEquals("New Title", catalog.getTitleAt("B1", 200));
        assertEquals("New Title", catalog.getTitleAt("B1", 999));
    }

    @Test
    void authorHistoryCanBeQueriedByTimestamp() {

        BookCatalog catalog = new BookCatalogImpl();

        catalog.addBook("B1", "Title", "Author A");

        assertTrue(catalog.updateAuthor("B1", "Author B", 10));
        assertTrue(catalog.updateAuthor("B1", "Author C", 20));

        assertEquals("Author A", catalog.getAuthorAt("B1", 0));
        assertEquals("Author A", catalog.getAuthorAt("B1", 9));
        assertEquals("Author B", catalog.getAuthorAt("B1", 10));
        assertEquals("Author B", catalog.getAuthorAt("B1", 19));
        assertEquals("Author C", catalog.getAuthorAt("B1", 20));
        assertEquals("Author C", catalog.getAuthorAt("B1", 100));
    }

    @Test
    void currentValuesReflectLatestUpdates() {

        BookCatalog catalog = new BookCatalogImpl();

        catalog.addBook("B1", "Original Title", "Original Author");

        assertTrue(catalog.updateTitle("B1", "Updated Title", 50));
        assertTrue(catalog.updateAuthor("B1", "Updated Author", 60));

        assertEquals("Updated Title", catalog.getBookTitle("B1"));
        assertEquals("Updated Author", catalog.getBookAuthor("B1"));
    }

    @Test
    void historyHandlesOutOfOrderUpdates() {

        BookCatalog catalog = new BookCatalogImpl();

        catalog.addBook("B1", "Original", "Author");

        assertTrue(catalog.updateTitle("B1", "Newest", 300));
        assertTrue(catalog.updateTitle("B1", "Middle", 200));
        assertTrue(catalog.updateTitle("B1", "Older", 100));

        assertEquals("Original", catalog.getTitleAt("B1", 50));
        assertEquals("Older", catalog.getTitleAt("B1", 100));
        assertEquals("Middle", catalog.getTitleAt("B1", 250));
        assertEquals("Newest", catalog.getTitleAt("B1", 300));
        assertEquals("Newest", catalog.getTitleAt("B1", 1000));

        assertEquals("Newest", catalog.getBookTitle("B1"));
    }

    @Test
    void sameTimestampUpdateReplacesPreviousValue() {

        BookCatalog catalog = new BookCatalogImpl();

        catalog.addBook("B1", "Original", "Author");

        assertTrue(catalog.updateTitle("B1", "Title A", 100));
        assertTrue(catalog.updateTitle("B1", "Title B", 100));

        assertEquals("Title B", catalog.getTitleAt("B1", 100));
        assertEquals("Title B", catalog.getBookTitle("B1"));
    }

    @Test
    void invalidUpdatesAreRejected() {

        BookCatalog catalog = new BookCatalogImpl();

        catalog.addBook("B1", "Original", "Author");

        assertFalse(catalog.updateTitle("UNKNOWN", "New", 10));
        assertFalse(catalog.updateAuthor("UNKNOWN", "New", 10));

        assertFalse(catalog.updateTitle("B1", null, 10));
        assertFalse(catalog.updateTitle("B1", "", 10));
        assertFalse(catalog.updateTitle("B1", "   ", 10));

        assertFalse(catalog.updateAuthor("B1", null, 10));
        assertFalse(catalog.updateAuthor("B1", "", 10));
        assertFalse(catalog.updateAuthor("B1", "   ", 10));

        assertFalse(catalog.updateTitle("B1", "New", -1));
        assertFalse(catalog.updateAuthor("B1", "New", -1));

        assertEquals("Original", catalog.getBookTitle("B1"));
        assertEquals("Author", catalog.getBookAuthor("B1"));
    }

    @Test
    void historyQueriesHandleMissingBookAndInvalidTimestamp() {

        BookCatalog catalog = new BookCatalogImpl();

        catalog.addBook("B1", "Title", "Author");

        assertNull(catalog.getTitleAt("UNKNOWN", 100));
        assertNull(catalog.getAuthorAt("UNKNOWN", 100));

        assertNull(catalog.getTitleAt("B1", -1));
        assertNull(catalog.getAuthorAt("B1", -1));
    }

    @Test
    void removedBookHasNoHistory() {

        BookCatalog catalog = new BookCatalogImpl();

        catalog.addBook("B1", "Title", "Author");
        catalog.updateTitle("B1", "Updated", 100);

        assertTrue(catalog.removeBook("B1"));

        assertNull(catalog.getTitleAt("B1", 100));
        assertNull(catalog.getAuthorAt("B1", 100));
    }

    @Test
    void level2QueriesUseUpdatedCurrentValues() {

        BookCatalog catalog = new BookCatalogImpl();

        catalog.addBook("B1", "Old Title", "Old Author");

        catalog.updateTitle("B1", "New Title", 100);
        catalog.updateAuthor("B1", "New Author", 100);

        assertEquals(
                List.of("B1"),
                catalog.getBooksByAuthor("New Author")
        );

        assertEquals(
                List.of("B1"),
                catalog.searchBooksByTitlePrefix("New", 10)
        );

        assertEquals(List.of(), catalog.getBooksByAuthor("Old Author"));
        assertEquals(List.of(), catalog.searchBooksByTitlePrefix("Old", 10));
    }

    @Test
    void updateAtTimestampZeroReplacesOriginalValue() {

        BookCatalog catalog = new BookCatalogImpl();

        catalog.addBook("B1", "Original", "Author");

        assertTrue(catalog.updateTitle("B1", "Replacement", 0));

        assertEquals("Replacement", catalog.getTitleAt("B1", 0));
        assertEquals("Replacement", catalog.getBookTitle("B1"));
    }

}