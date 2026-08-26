package com.codesignal.klayvio.mock4;

import java.time.Duration;

import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BasicTests {

    private TaskTracker tracker;

    @BeforeEach
    public void setUp() {
        tracker = new TaskTrackerImpl();
    }

    @Test
    @Order(1)
    void test_basic1_create() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    Assertions.assertTrue(
                            tracker.createTask(
                                    "t1",
                                    "Fix production bug",
                                    5
                            )
                    );

                    Assertions.assertEquals(
                            5,
                            tracker.getPriority("t1")
                    );

                    Assertions.assertFalse(
                            tracker.isCompleted("t1")
                    );
                }
        );
    }

    @Test
    @Order(2)
    void test_basic2_updateAndComplete() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask(
                            "t1",
                            "Fix production bug",
                            5
                    );

                    Assertions.assertTrue(
                            tracker.updatePriority("t1", 10)
                    );

                    Assertions.assertEquals(
                            10,
                            tracker.getPriority("t1")
                    );

                    Assertions.assertTrue(
                            tracker.completeTask("t1")
                    );

                    Assertions.assertTrue(
                            tracker.isCompleted("t1")
                    );
                }
        );
    }

    @Test
    @Order(3)
    void test_basic3_delete() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask(
                            "t1",
                            "Something",
                            3
                    );

                    Assertions.assertTrue(
                            tracker.deleteTask("t1")
                    );

                    Assertions.assertEquals(
                            -1,
                            tracker.getPriority("t1")
                    );

                    Assertions.assertFalse(
                            tracker.isCompleted("t1")
                    );
                }
        );
    }

    @Test
    @Order(4)
    void test_basic4_assignmentAndWorkload() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("t1", "one", 1);
                    tracker.createTask("t2", "two", 2);

                    Assertions.assertTrue(
                            tracker.assignTask("t1", "alice")
                    );

                    Assertions.assertTrue(
                            tracker.assignTask("t2", "alice")
                    );

                    Assertions.assertEquals(
                            2,
                            tracker.getWorkload("alice")
                    );

                    tracker.completeTask("t1");

                    Assertions.assertEquals(
                            1,
                            tracker.getWorkload("alice")
                    );
                }
        );
    }

    @Test
    @Order(5)
    void test_basic5_dependencies() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("build", "Build", 10);
                    tracker.createTask("test", "Test", 20);

                    tracker.assignTask("build", "alice");
                    tracker.assignTask("test", "alice");

                    Assertions.assertTrue(
                            tracker.addDependency("test", "build")
                    );

                    Assertions.assertEquals(
                            java.util.List.of("build"),
                            tracker.getReadyTasks("alice", 10)
                    );

                    tracker.completeTask("build");

                    Assertions.assertEquals(
                            java.util.List.of("test"),
                            tracker.getReadyTasks("alice", 10)
                    );
                }
        );
    }

    @Test
    @Order(6)
    void test_basic6_reopenBlocksDependentTask() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("build", "Build", 10);
                    tracker.createTask("test", "Test", 20);

                    tracker.assignTask("build", "alice");
                    tracker.assignTask("test", "alice");

                    tracker.addDependency("test", "build");

                    tracker.completeTask("build");

                    Assertions.assertEquals(
                            java.util.List.of("test"),
                            tracker.getReadyTasks("alice", 10)
                    );

                    Assertions.assertTrue(
                            tracker.reopenTask("build")
                    );

                    Assertions.assertEquals(
                            java.util.List.of("build"),
                            tracker.getReadyTasks("alice", 10)
                    );

                    Assertions.assertEquals(
                            java.util.List.of("test"),
                            tracker.getBlockedTasks("alice", 10)
                    );
                }
        );
    }
}