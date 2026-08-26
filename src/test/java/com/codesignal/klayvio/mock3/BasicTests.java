package com.codesignal.klayvio.mock3;

import java.time.Duration;

import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BasicTests {

    private DocumentStore store;

    @BeforeEach
    public void setUp() {
        store = new DocumentStoreImpl();
    }

    @Test
    @Order(1)
    void test_basic1_createAndGet() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            Assertions.assertTrue(store.create("doc1", "hello"));
            Assertions.assertEquals("hello", store.get("doc1"));
        });
    }

    @Test
    @Order(2)
    void test_basic2_update() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            Assertions.assertTrue(store.create("doc1", "hello"));
            Assertions.assertTrue(store.update("doc1", "updated"));

            Assertions.assertEquals("updated", store.get("doc1"));
        });
    }

    @Test
    @Order(3)
    void test_basic3_delete() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            Assertions.assertTrue(store.create("doc1", "hello"));
            Assertions.assertTrue(store.delete("doc1"));

            Assertions.assertNull(store.get("doc1"));
        });
    }

    @Test
    @Order(4)
    void test_basic4_findByPrefix() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("app", "A");
            store.create("apple", "B");
            store.create("application", "C");
            store.create("banana", "D");

            Assertions.assertEquals(
                    java.util.List.of("app", "apple", "application"),
                    store.findByPrefix("app", 10)
            );
        });
    }

    @Test
    @Order(5)
    void test_basic5_versions() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("doc1", "first");
            store.update("doc1", "second");
            store.update("doc1", "third");

            Assertions.assertEquals(3, store.getVersionCount("doc1"));

            Assertions.assertEquals("first", store.getVersion("doc1", 1));
            Assertions.assertEquals("second", store.getVersion("doc1", 2));
            Assertions.assertEquals("third", store.getVersion("doc1", 3));

            Assertions.assertEquals("third", store.get("doc1"));
        });
    }

    @Test
    @Order(6)
    void test_basic6_restoreVersion() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            store.create("doc1", "v1");
            store.update("doc1", "v2");
            store.update("doc1", "v3");

            Assertions.assertTrue(
                    store.restoreVersion("doc1", 1)
            );

            Assertions.assertEquals("v1", store.get("doc1"));
            Assertions.assertEquals(4, store.getVersionCount("doc1"));
            Assertions.assertEquals("v1", store.getVersion("doc1", 4));
        });
    }
}