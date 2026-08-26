package com.codesignal.klayvio.mock3;

import java.time.Duration;

import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DocumentStoreTests {

    private DocumentStore store;

    @BeforeEach
    public void setUp() {
        store = new DocumentStoreImpl();
    }

    @Test
    @Order(1)
    void test_01_duplicateCreatePreservesOriginal() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            Assertions.assertTrue(store.create("a", "first"));
            Assertions.assertFalse(store.create("a", "second"));

            Assertions.assertEquals("first", store.get("a"));
        });
    }

    @Test
    @Order(2)
    void test_02_updateMissingDocument() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            Assertions.assertFalse(store.update("missing", "content"));
            Assertions.assertNull(store.get("missing"));
        });
    }

    @Test
    @Order(3)
    void test_03_deleteMissingDocument() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            Assertions.assertFalse(store.delete("missing"));
        });
    }

    @Test
    @Order(4)
    void test_04_multipleDocumentsIndependent() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("a", "alpha");
            store.create("b", "beta");
            store.create("c", "charlie");

            store.update("b", "BETA");
            store.delete("c");

            Assertions.assertEquals("alpha", store.get("a"));
            Assertions.assertEquals("BETA", store.get("b"));
            Assertions.assertNull(store.get("c"));
        });
    }

    @Test
    @Order(5)
    void test_05_deleteThenRecreate() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            Assertions.assertTrue(store.create("a", "old"));
            Assertions.assertTrue(store.delete("a"));

            Assertions.assertTrue(store.create("a", "new"));
            Assertions.assertEquals("new", store.get("a"));
        });
    }

    @Test
    @Order(6)
    void test_06_repeatedUpdates() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("a", "v0");

            for (int i = 1; i <= 20; i++) {
                Assertions.assertTrue(
                        store.update("a", "v" + i)
                );
            }

            Assertions.assertEquals("v20", store.get("a"));
        });
    }

    @Test
    @Order(7)
    void test_07_operationsAfterDelete() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("a", "hello");
            store.delete("a");

            Assertions.assertFalse(store.update("a", "changed"));
            Assertions.assertFalse(store.delete("a"));
            Assertions.assertNull(store.get("a"));
        });
    }

    @Test
    @Order(8)
    void test_08_emptyContentIsValid() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            Assertions.assertTrue(store.create("a", ""));
            Assertions.assertEquals("", store.get("a"));

            Assertions.assertTrue(store.update("a", "hello"));
            Assertions.assertTrue(store.update("a", ""));

            Assertions.assertEquals("", store.get("a"));
        });
    }

    @Test
    @Order(9)
    void test_09_prefixOrderingByLengthThenAlphabetical() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("car", "1");
            store.create("cat", "2");
            store.create("cart", "3");
            store.create("carbon", "4");
            store.create("camera", "5");

            Assertions.assertEquals(
                    java.util.List.of("car", "cat", "cart", "camera"),
                    store.findByPrefix("ca", 4)
            );
        });
    }

    @Test
    @Order(10)
    void test_10_limitResults() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("a", "1");
            store.create("ab", "2");
            store.create("abc", "3");
            store.create("abcd", "4");

            Assertions.assertEquals(
                    java.util.List.of("a", "ab"),
                    store.findByPrefix("a", 2)
            );
        });
    }

    @Test
    @Order(11)
    void test_11_equalLengthAlphabeticalOrdering() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("dog", "1");
            store.create("dot", "2");
            store.create("don", "3");
            store.create("door", "4");

            Assertions.assertEquals(
                    java.util.List.of("dog", "don", "dot", "door"),
                    store.findByPrefix("do", 10)
            );
        });
    }

    @Test
    @Order(12)
    void test_12_deletedDocumentsExcluded() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("app", "1");
            store.create("apple", "2");
            store.create("apply", "3");

            store.delete("apple");

            Assertions.assertEquals(
                    java.util.List.of("app", "apply"),
                    store.findByPrefix("app", 10)
            );
        });
    }

    @Test
    @Order(13)
    void test_13_recreatedDocumentIncludedAgain() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("app", "old");
            store.delete("app");

            Assertions.assertEquals(
                    java.util.List.of(),
                    store.findByPrefix("app", 10)
            );

            store.create("app", "new");

            Assertions.assertEquals(
                    java.util.List.of("app"),
                    store.findByPrefix("app", 10)
            );
        });
    }

    @Test
    @Order(14)
    void test_14_updateDoesNotAffectSearch() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("alpha", "one");
            store.create("alpine", "two");

            store.update("alpha", "changed");

            Assertions.assertEquals(
                    java.util.List.of("alpha", "alpine"),
                    store.findByPrefix("al", 10)
            );
        });
    }

    @Test
    @Order(15)
    void test_15_emptyPrefixMatchesAllDocuments() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("b", "1");
            store.create("aa", "2");
            store.create("a", "3");
            store.create("ccc", "4");

            Assertions.assertEquals(
                    java.util.List.of("a", "b", "aa", "ccc"),
                    store.findByPrefix("", 10)
            );
        });
    }

    @Test
    @Order(16)
    void test_16_noMatches() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("apple", "1");
            store.create("banana", "2");

            Assertions.assertEquals(
                    java.util.List.of(),
                    store.findByPrefix("zzz", 10)
            );
        });
    }

    @Test
    @Order(17)
    void test_17_manyDocumentsAndLimit() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("x", "1");
            store.create("xa", "2");
            store.create("xb", "3");
            store.create("xaa", "4");
            store.create("xab", "5");
            store.create("xabc", "6");

            Assertions.assertEquals(
                    java.util.List.of("x", "xa", "xb", "xaa"),
                    store.findByPrefix("x", 4)
            );
        });
    }

    @Test
    @Order(18)
    void test_18_createdDocumentStartsAtVersionOne() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("a", "original");

            Assertions.assertEquals(1, store.getVersionCount("a"));
            Assertions.assertEquals("original", store.getVersion("a", 1));
            Assertions.assertNull(store.getVersion("a", 2));
        });
    }

    @Test
    @Order(19)
    void test_19_repeatedUpdatesCreateVersions() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("a", "v1");

            for (int i = 2; i <= 10; i++) {
                Assertions.assertTrue(store.update("a", "v" + i));
            }

            Assertions.assertEquals(10, store.getVersionCount("a"));

            for (int i = 1; i <= 10; i++) {
                Assertions.assertEquals(
                        "v" + i,
                        store.getVersion("a", i)
                );
            }

            Assertions.assertEquals("v10", store.get("a"));
        });
    }

    @Test
    @Order(20)
    void test_20_failedUpdateDoesNotCreateVersion() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            Assertions.assertFalse(store.update("missing", "something"));

            Assertions.assertEquals(0, store.getVersionCount("missing"));
            Assertions.assertNull(store.getVersion("missing", 1));

            store.create("a", "v1");

            Assertions.assertEquals(1, store.getVersionCount("a"));
        });
    }

    @Test
    @Order(21)
    void test_21_duplicateCreateDoesNotCreateVersion() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            Assertions.assertTrue(store.create("a", "first"));
            Assertions.assertFalse(store.create("a", "second"));

            Assertions.assertEquals(1, store.getVersionCount("a"));
            Assertions.assertEquals("first", store.getVersion("a", 1));
            Assertions.assertEquals("first", store.get("a"));
        });
    }

    @Test
    @Order(22)
    void test_22_deleteRemovesVersionHistory() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("a", "v1");
            store.update("a", "v2");
            store.update("a", "v3");

            Assertions.assertEquals(3, store.getVersionCount("a"));

            Assertions.assertTrue(store.delete("a"));

            Assertions.assertEquals(0, store.getVersionCount("a"));
            Assertions.assertNull(store.getVersion("a", 1));
            Assertions.assertNull(store.getVersion("a", 2));
            Assertions.assertNull(store.getVersion("a", 3));
        });
    }

    @Test
    @Order(23)
    void test_23_recreateStartsFreshHistory() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("a", "old-v1");
            store.update("a", "old-v2");
            store.update("a", "old-v3");

            store.delete("a");

            Assertions.assertTrue(store.create("a", "new-v1"));

            Assertions.assertEquals(1, store.getVersionCount("a"));
            Assertions.assertEquals("new-v1", store.getVersion("a", 1));
            Assertions.assertNull(store.getVersion("a", 2));
            Assertions.assertEquals("new-v1", store.get("a"));
        });
    }

    @Test
    @Order(24)
    void test_24_historiesAreIndependent() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("a", "a1");
            store.create("b", "b1");

            store.update("a", "a2");
            store.update("a", "a3");

            store.update("b", "b2");

            Assertions.assertEquals(3, store.getVersionCount("a"));
            Assertions.assertEquals(2, store.getVersionCount("b"));

            Assertions.assertEquals("a1", store.getVersion("a", 1));
            Assertions.assertEquals("a3", store.getVersion("a", 3));

            Assertions.assertEquals("b1", store.getVersion("b", 1));
            Assertions.assertEquals("b2", store.getVersion("b", 2));
        });
    }

    @Test
    @Order(25)
    void test_25_emptyContentPreservedAsVersion() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("a", "");
            store.update("a", "hello");
            store.update("a", "");

            Assertions.assertEquals(3, store.getVersionCount("a"));

            Assertions.assertEquals("", store.getVersion("a", 1));
            Assertions.assertEquals("hello", store.getVersion("a", 2));
            Assertions.assertEquals("", store.getVersion("a", 3));
        });
    }

    @Test
    @Order(26)
    void test_26_versioningDoesNotBreakPrefixSearch() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("app", "v1");
            store.create("apple", "v1");
            store.create("application", "v1");

            store.update("apple", "v2");
            store.update("apple", "v3");
            store.update("app", "v2");

            Assertions.assertEquals(
                    java.util.List.of("app", "apple", "application"),
                    store.findByPrefix("app", 10)
            );

            Assertions.assertEquals(2, store.getVersionCount("app"));
            Assertions.assertEquals(3, store.getVersionCount("apple"));
            Assertions.assertEquals(1, store.getVersionCount("application"));
        });
    }

    @Test
    @Order(27)
    void test_27_outOfRangeVersionsReturnNull() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("a", "v1");
            store.update("a", "v2");

            Assertions.assertNull(store.getVersion("a", 3));
            Assertions.assertNull(store.getVersion("a", 100));
        });
    }

    @Test
    @Order(28)
    void test_28_restoreCreatesNewVersion() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("a", "one");
            store.update("a", "two");
            store.update("a", "three");

            Assertions.assertTrue(store.restoreVersion("a", 2));

            Assertions.assertEquals("two", store.get("a"));
            Assertions.assertEquals(4, store.getVersionCount("a"));

            Assertions.assertEquals("one", store.getVersion("a", 1));
            Assertions.assertEquals("two", store.getVersion("a", 2));
            Assertions.assertEquals("three", store.getVersion("a", 3));
            Assertions.assertEquals("two", store.getVersion("a", 4));
        });
    }

    @Test
    @Order(29)
    void test_29_restoreMissingVersionFailsWithoutMutation() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("a", "v1");
            store.update("a", "v2");

            Assertions.assertFalse(store.restoreVersion("a", 100));

            Assertions.assertEquals("v2", store.get("a"));
            Assertions.assertEquals(2, store.getVersionCount("a"));
        });
    }

    @Test
    @Order(30)
    void test_30_restoreMissingDocumentFails() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            Assertions.assertFalse(
                    store.restoreVersion("missing", 1)
            );

            Assertions.assertEquals(
                    0,
                    store.getVersionCount("missing")
            );
        });
    }

    @Test
    @Order(31)
    void test_31_multipleRestoresCreateMultipleVersions() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("a", "v1");
            store.update("a", "v2");
            store.update("a", "v3");

            Assertions.assertTrue(store.restoreVersion("a", 1));
            Assertions.assertTrue(store.restoreVersion("a", 2));
            Assertions.assertTrue(store.restoreVersion("a", 1));

            Assertions.assertEquals(6, store.getVersionCount("a"));

            Assertions.assertEquals("v1", store.getVersion("a", 4));
            Assertions.assertEquals("v2", store.getVersion("a", 5));
            Assertions.assertEquals("v1", store.getVersion("a", 6));

            Assertions.assertEquals("v1", store.get("a"));
        });
    }

    @Test
    @Order(32)
    void test_32_findLatestVersionWithContent() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("a", "x");      // 1
            store.update("a", "y");      // 2
            store.update("a", "x");      // 3
            store.update("a", "z");      // 4
            store.restoreVersion("a", 1); // 5 -> x

            Assertions.assertEquals(
                    5,
                    store.findLatestVersionWithContent("a", "x")
            );

            Assertions.assertEquals(
                    2,
                    store.findLatestVersionWithContent("a", "y")
            );

            Assertions.assertEquals(
                    4,
                    store.findLatestVersionWithContent("a", "z")
            );
        });
    }

    @Test
    @Order(33)
    void test_33_findLatestMissingContent() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("a", "hello");
            store.update("a", "world");

            Assertions.assertEquals(
                    -1,
                    store.findLatestVersionWithContent("a", "missing")
            );
        });
    }

    @Test
    @Order(34)
    void test_34_findLatestMissingDocument() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            Assertions.assertEquals(
                    -1,
                    store.findLatestVersionWithContent("missing", "anything")
            );
        });
    }

    @Test
    @Order(35)
    void test_35_deleteClearsRestoreHistory() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("a", "old1");
            store.update("a", "old2");

            store.delete("a");

            Assertions.assertFalse(store.restoreVersion("a", 1));
            Assertions.assertEquals(
                    -1,
                    store.findLatestVersionWithContent("a", "old1")
            );

            store.create("a", "new1");

            Assertions.assertEquals(1, store.getVersionCount("a"));
            Assertions.assertNull(store.getVersion("a", 2));
            Assertions.assertEquals(
                    -1,
                    store.findLatestVersionWithContent("a", "old1")
            );
            Assertions.assertEquals(
                    1,
                    store.findLatestVersionWithContent("a", "new1")
            );
        });
    }

    @Test
    @Order(36)
    void test_36_restoreDoesNotBreakPrefixSearch() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("app", "one");
            store.create("apple", "two");
            store.create("application", "three");

            store.update("apple", "changed");
            store.restoreVersion("apple", 1);

            Assertions.assertEquals(
                    java.util.List.of("app", "apple", "application"),
                    store.findByPrefix("app", 10)
            );
        });
    }

    @Test
    @Order(37)
    void test_37_emptyContentCanBeRestoredAndFound() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("a", "");
            store.update("a", "hello");

            Assertions.assertTrue(
                    store.restoreVersion("a", 1)
            );

            Assertions.assertEquals("", store.get("a"));

            Assertions.assertEquals(
                    3,
                    store.findLatestVersionWithContent("a", "")
            );
        });
    }
}