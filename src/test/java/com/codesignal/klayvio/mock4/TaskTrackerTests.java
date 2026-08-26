package com.codesignal.klayvio.mock4;

import java.time.Duration;

import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskTrackerTests {

    private TaskTracker tracker;

    @BeforeEach
    public void setUp() {
        tracker = new TaskTrackerImpl();
    }

    @Test
    @Order(1)
    void test_01_duplicateCreatePreservesOriginal() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    Assertions.assertTrue(
                            tracker.createTask("a", "first", 5)
                    );

                    Assertions.assertFalse(
                            tracker.createTask("a", "second", 100)
                    );

                    Assertions.assertEquals(
                            5,
                            tracker.getPriority("a")
                    );

                    Assertions.assertFalse(
                            tracker.isCompleted("a")
                    );
                }
        );
    }

    @Test
    @Order(2)
    void test_02_missingTaskOperations() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    Assertions.assertFalse(
                            tracker.updatePriority("missing", 10)
                    );

                    Assertions.assertFalse(
                            tracker.completeTask("missing")
                    );

                    Assertions.assertFalse(
                            tracker.deleteTask("missing")
                    );

                    Assertions.assertEquals(
                            -1,
                            tracker.getPriority("missing")
                    );

                    Assertions.assertFalse(
                            tracker.isCompleted("missing")
                    );
                }
        );
    }

    @Test
    @Order(3)
    void test_03_completeTwice() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("a", "task", 7);

                    Assertions.assertTrue(
                            tracker.completeTask("a")
                    );

                    Assertions.assertFalse(
                            tracker.completeTask("a")
                    );

                    Assertions.assertTrue(
                            tracker.isCompleted("a")
                    );

                    Assertions.assertEquals(
                            7,
                            tracker.getPriority("a")
                    );
                }
        );
    }

    @Test
    @Order(4)
    void test_04_priorityCanChangeAfterCompletion() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("a", "task", 2);
                    tracker.completeTask("a");

                    Assertions.assertTrue(
                            tracker.updatePriority("a", 20)
                    );

                    Assertions.assertEquals(
                            20,
                            tracker.getPriority("a")
                    );

                    Assertions.assertTrue(
                            tracker.isCompleted("a")
                    );
                }
        );
    }

    @Test
    @Order(5)
    void test_05_multipleTasksIndependent() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("a", "alpha", 1);
                    tracker.createTask("b", "beta", 2);
                    tracker.createTask("c", "charlie", 3);

                    tracker.completeTask("b");
                    tracker.updatePriority("c", 30);

                    Assertions.assertEquals(
                            1,
                            tracker.getPriority("a")
                    );

                    Assertions.assertFalse(
                            tracker.isCompleted("a")
                    );

                    Assertions.assertEquals(
                            2,
                            tracker.getPriority("b")
                    );

                    Assertions.assertTrue(
                            tracker.isCompleted("b")
                    );

                    Assertions.assertEquals(
                            30,
                            tracker.getPriority("c")
                    );

                    Assertions.assertFalse(
                            tracker.isCompleted("c")
                    );
                }
        );
    }

    @Test
    @Order(6)
    void test_06_deleteThenRecreateStartsFresh() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("a", "old", 10);
                    tracker.completeTask("a");

                    Assertions.assertTrue(
                            tracker.deleteTask("a")
                    );

                    Assertions.assertTrue(
                            tracker.createTask("a", "new", 3)
                    );

                    Assertions.assertEquals(
                            3,
                            tracker.getPriority("a")
                    );

                    Assertions.assertFalse(
                            tracker.isCompleted("a")
                    );
                }
        );
    }

    @Test
    @Order(7)
    void test_07_repeatedPriorityUpdates() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("a", "task", 1);

                    for (int i = 2; i <= 25; i++) {
                        Assertions.assertTrue(
                                tracker.updatePriority("a", i)
                        );
                    }

                    Assertions.assertEquals(
                            25,
                            tracker.getPriority("a")
                    );

                    Assertions.assertFalse(
                            tracker.isCompleted("a")
                    );
                }
        );
    }

    @Test
    @Order(8)
    void test_08_deleteCompletedTask() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("a", "task", 5);
                    tracker.completeTask("a");

                    Assertions.assertTrue(
                            tracker.deleteTask("a")
                    );

                    Assertions.assertFalse(
                            tracker.isCompleted("a")
                    );

                    Assertions.assertEquals(
                            -1,
                            tracker.getPriority("a")
                    );

                    Assertions.assertFalse(
                            tracker.completeTask("a")
                    );
                }
        );
    }

    @Test
    @Order(9)
    void test_09_assignMissingTaskFails() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    Assertions.assertFalse(
                            tracker.assignTask("missing", "alice")
                    );

                    Assertions.assertEquals(
                            0,
                            tracker.getWorkload("alice")
                    );
                }
        );
    }

    @Test
    @Order(10)
    void test_10_reassignmentMovesWorkload() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("a", "task", 5);

                    Assertions.assertTrue(
                            tracker.assignTask("a", "alice")
                    );

                    Assertions.assertEquals(
                            1,
                            tracker.getWorkload("alice")
                    );

                    Assertions.assertTrue(
                            tracker.assignTask("a", "bob")
                    );

                    Assertions.assertEquals(
                            0,
                            tracker.getWorkload("alice")
                    );

                    Assertions.assertEquals(
                            1,
                            tracker.getWorkload("bob")
                    );
                }
        );
    }

    @Test
    @Order(11)
    void test_11_assignSameUserAgainDoesNotDuplicateWorkload() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("a", "task", 5);

                    Assertions.assertTrue(
                            tracker.assignTask("a", "alice")
                    );

                    Assertions.assertTrue(
                            tracker.assignTask("a", "alice")
                    );

                    Assertions.assertTrue(
                            tracker.assignTask("a", "alice")
                    );

                    Assertions.assertEquals(
                            1,
                            tracker.getWorkload("alice")
                    );
                }
        );
    }

    @Test
    @Order(12)
    void test_12_completionRemovesFromWorkload() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("a", "task", 5);
                    tracker.assignTask("a", "alice");

                    Assertions.assertEquals(
                            1,
                            tracker.getWorkload("alice")
                    );

                    Assertions.assertTrue(
                            tracker.completeTask("a")
                    );

                    Assertions.assertEquals(
                            0,
                            tracker.getWorkload("alice")
                    );

                    Assertions.assertTrue(
                            tracker.isCompleted("a")
                    );
                }
        );
    }

    @Test
    @Order(13)
    void test_13_completedTaskCannotBeAssigned() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("a", "task", 5);
                    tracker.completeTask("a");

                    Assertions.assertFalse(
                            tracker.assignTask("a", "alice")
                    );

                    Assertions.assertEquals(
                            0,
                            tracker.getWorkload("alice")
                    );
                }
        );
    }

    @Test
    @Order(14)
    void test_14_completedAssignedTaskCanBeUnassigned() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("a", "task", 5);
                    tracker.assignTask("a", "alice");
                    tracker.completeTask("a");

                    Assertions.assertEquals(
                            0,
                            tracker.getWorkload("alice")
                    );

                    Assertions.assertTrue(
                            tracker.unassignTask("a")
                    );

                    Assertions.assertFalse(
                            tracker.unassignTask("a")
                    );
                }
        );
    }

    @Test
    @Order(15)
    void test_15_unassignRemovesWorkload() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("a", "task", 5);
                    tracker.assignTask("a", "alice");

                    Assertions.assertEquals(
                            1,
                            tracker.getWorkload("alice")
                    );

                    Assertions.assertTrue(
                            tracker.unassignTask("a")
                    );

                    Assertions.assertEquals(
                            0,
                            tracker.getWorkload("alice")
                    );

                    Assertions.assertFalse(
                            tracker.unassignTask("a")
                    );
                }
        );
    }

    @Test
    @Order(16)
    void test_16_deleteAssignedTaskRemovesWorkload() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("a", "task", 5);
                    tracker.assignTask("a", "alice");

                    Assertions.assertEquals(
                            1,
                            tracker.getWorkload("alice")
                    );

                    Assertions.assertTrue(
                            tracker.deleteTask("a")
                    );

                    Assertions.assertEquals(
                            0,
                            tracker.getWorkload("alice")
                    );
                }
        );
    }

    @Test
    @Order(17)
    void test_17_workloadsAreIndependent() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("a", "A", 1);
                    tracker.createTask("b", "B", 2);
                    tracker.createTask("c", "C", 3);
                    tracker.createTask("d", "D", 4);

                    tracker.assignTask("a", "alice");
                    tracker.assignTask("b", "alice");
                    tracker.assignTask("c", "bob");
                    tracker.assignTask("d", "charlie");

                    Assertions.assertEquals(
                            2,
                            tracker.getWorkload("alice")
                    );

                    Assertions.assertEquals(
                            1,
                            tracker.getWorkload("bob")
                    );

                    Assertions.assertEquals(
                            1,
                            tracker.getWorkload("charlie")
                    );

                    Assertions.assertEquals(
                            0,
                            tracker.getWorkload("nobody")
                    );
                }
        );
    }

    @Test
    @Order(18)
    void test_18_priorityChangesDoNotAffectWorkload() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("a", "A", 1);
                    tracker.assignTask("a", "alice");

                    tracker.updatePriority("a", 100);
                    tracker.updatePriority("a", 0);

                    Assertions.assertEquals(
                            1,
                            tracker.getWorkload("alice")
                    );
                }
        );
    }

    @Test
    @Order(19)
    void test_19_deleteRecreateHasNoOldAssignment() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("a", "old", 1);
                    tracker.assignTask("a", "alice");

                    tracker.deleteTask("a");

                    Assertions.assertTrue(
                            tracker.createTask("a", "new", 2)
                    );

                    Assertions.assertEquals(
                            0,
                            tracker.getWorkload("alice")
                    );

                    Assertions.assertEquals(
                            2,
                            tracker.getPriority("a")
                    );

                    Assertions.assertFalse(
                            tracker.isCompleted("a")
                    );
                }
        );
    }

    @Test
    @Order(20)
    void test_20_missingTasksCannotBecomeDependencies() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("a", "A", 1);

                    Assertions.assertFalse(
                            tracker.addDependency("a", "missing")
                    );

                    Assertions.assertFalse(
                            tracker.addDependency("missing", "a")
                    );
                }
        );
    }

    @Test
    @Order(21)
    void test_21_taskCannotDependOnItself() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("a", "A", 1);

                    Assertions.assertFalse(
                            tracker.addDependency("a", "a")
                    );
                }
        );
    }

    @Test
    @Order(22)
    void test_22_duplicateDependencyRejected() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("a", "A", 1);
                    tracker.createTask("b", "B", 2);

                    Assertions.assertTrue(
                            tracker.addDependency("b", "a")
                    );

                    Assertions.assertFalse(
                            tracker.addDependency("b", "a")
                    );
                }
        );
    }

    @Test
    @Order(23)
    void test_23_allDependenciesMustBeCompleted() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("a", "A", 1);
                    tracker.createTask("b", "B", 2);
                    tracker.createTask("c", "C", 10);

                    tracker.assignTask("c", "alice");

                    tracker.addDependency("c", "a");
                    tracker.addDependency("c", "b");

                    Assertions.assertEquals(
                            java.util.List.of(),
                            tracker.getReadyTasks("alice", 10)
                    );

                    tracker.completeTask("a");

                    Assertions.assertEquals(
                            java.util.List.of(),
                            tracker.getReadyTasks("alice", 10)
                    );

                    tracker.completeTask("b");

                    Assertions.assertEquals(
                            java.util.List.of("c"),
                            tracker.getReadyTasks("alice", 10)
                    );
                }
        );
    }

    @Test
    @Order(24)
    void test_24_removeDependencyMayMakeTaskReady() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("a", "A", 1);
                    tracker.createTask("b", "B", 5);

                    tracker.assignTask("b", "alice");
                    tracker.addDependency("b", "a");

                    Assertions.assertEquals(
                            java.util.List.of(),
                            tracker.getReadyTasks("alice", 10)
                    );

                    Assertions.assertTrue(
                            tracker.removeDependency("b", "a")
                    );

                    Assertions.assertEquals(
                            java.util.List.of("b"),
                            tracker.getReadyTasks("alice", 10)
                    );

                    Assertions.assertFalse(
                            tracker.removeDependency("b", "a")
                    );
                }
        );
    }

    @Test
    @Order(25)
    void test_25_readyTaskOrdering() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("charlie", "C", 10);
                    tracker.createTask("alpha", "A", 20);
                    tracker.createTask("beta", "B", 20);
                    tracker.createTask("delta", "D", 5);

                    tracker.assignTask("charlie", "alice");
                    tracker.assignTask("alpha", "alice");
                    tracker.assignTask("beta", "alice");
                    tracker.assignTask("delta", "alice");

                    Assertions.assertEquals(
                            java.util.List.of(
                                    "alpha",
                                    "beta",
                                    "charlie"
                            ),
                            tracker.getReadyTasks("alice", 3)
                    );
                }
        );
    }

    @Test
    @Order(26)
    void test_26_completedTasksNotReturnedAsReady() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("a", "A", 10);
                    tracker.assignTask("a", "alice");

                    tracker.completeTask("a");

                    Assertions.assertEquals(
                            java.util.List.of(),
                            tracker.getReadyTasks("alice", 10)
                    );
                }
        );
    }

    @Test
    @Order(27)
    void test_27_unassignedTaskNotReady() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("a", "A", 10);

                    Assertions.assertEquals(
                            java.util.List.of(),
                            tracker.getReadyTasks("alice", 10)
                    );

                    tracker.assignTask("a", "alice");

                    Assertions.assertEquals(
                            java.util.List.of("a"),
                            tracker.getReadyTasks("alice", 10)
                    );

                    tracker.unassignTask("a");

                    Assertions.assertEquals(
                            java.util.List.of(),
                            tracker.getReadyTasks("alice", 10)
                    );
                }
        );
    }

    @Test
    @Order(28)
    void test_28_reassignmentChangesReadyUser() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("a", "A", 10);
                    tracker.assignTask("a", "alice");

                    Assertions.assertEquals(
                            java.util.List.of("a"),
                            tracker.getReadyTasks("alice", 10)
                    );

                    tracker.assignTask("a", "bob");

                    Assertions.assertEquals(
                            java.util.List.of(),
                            tracker.getReadyTasks("alice", 10)
                    );

                    Assertions.assertEquals(
                            java.util.List.of("a"),
                            tracker.getReadyTasks("bob", 10)
                    );
                }
        );
    }

    @Test
    @Order(29)
    void test_29_deletedDependencyNoLongerBlocksTask() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("a", "A", 1);
                    tracker.createTask("b", "B", 10);

                    tracker.assignTask("b", "alice");

                    tracker.addDependency("b", "a");

                    Assertions.assertEquals(
                            java.util.List.of(),
                            tracker.getReadyTasks("alice", 10)
                    );

                    Assertions.assertTrue(
                            tracker.deleteTask("a")
                    );

                    Assertions.assertEquals(
                            java.util.List.of("b"),
                            tracker.getReadyTasks("alice", 10)
                    );
                }
        );
    }

    @Test
    @Order(30)
    void test_30_deletingTaskRemovesItsOwnDependencies() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("a", "A", 1);
                    tracker.createTask("b", "B", 10);

                    tracker.addDependency("b", "a");

                    Assertions.assertTrue(
                            tracker.deleteTask("b")
                    );

                    Assertions.assertTrue(
                            tracker.createTask("b", "NEW", 20)
                    );

                    tracker.assignTask("b", "alice");

                    Assertions.assertEquals(
                            java.util.List.of("b"),
                            tracker.getReadyTasks("alice", 10)
                    );
                }
        );
    }

    @Test
    @Order(31)
    void test_31_priorityUpdateChangesReadyOrdering() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("a", "A", 5);
                    tracker.createTask("b", "B", 10);

                    tracker.assignTask("a", "alice");
                    tracker.assignTask("b", "alice");

                    Assertions.assertEquals(
                            java.util.List.of("b", "a"),
                            tracker.getReadyTasks("alice", 10)
                    );

                    tracker.updatePriority("a", 20);

                    Assertions.assertEquals(
                            java.util.List.of("a", "b"),
                            tracker.getReadyTasks("alice", 10)
                    );
                }
        );
    }

    @Test
    @Order(32)
    void test_32_dependencyCompletionChangesReadySet() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("foundation", "F", 1);
                    tracker.createTask("feature", "Feature", 100);

                    tracker.assignTask("foundation", "alice");
                    tracker.assignTask("feature", "alice");

                    tracker.addDependency(
                            "feature",
                            "foundation"
                    );

                    Assertions.assertEquals(
                            java.util.List.of("foundation"),
                            tracker.getReadyTasks("alice", 10)
                    );

                    tracker.completeTask("foundation");

                    Assertions.assertEquals(
                            java.util.List.of("feature"),
                            tracker.getReadyTasks("alice", 10)
                    );
                }
        );
    }

    @Test
    @Order(33)
    void test_33_reopenMissingTaskFails() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    Assertions.assertFalse(
                            tracker.reopenTask("missing")
                    );
                }
        );
    }

    @Test
    @Order(34)
    void test_34_reopenActiveTaskFailsWithoutMutation() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("a", "A", 10);
                    tracker.assignTask("a", "alice");

                    Assertions.assertFalse(
                            tracker.reopenTask("a")
                    );

                    Assertions.assertFalse(
                            tracker.isCompleted("a")
                    );

                    Assertions.assertEquals(
                            10,
                            tracker.getPriority("a")
                    );

                    Assertions.assertEquals(
                            java.util.List.of("a"),
                            tracker.getReadyTasks("alice", 10)
                    );
                }
        );
    }

    @Test
    @Order(35)
    void test_35_reopenPreservesAssignmentAndPriority() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("a", "A", 50);
                    tracker.assignTask("a", "alice");

                    tracker.completeTask("a");

                    Assertions.assertEquals(
                            0,
                            tracker.getWorkload("alice")
                    );

                    Assertions.assertTrue(
                            tracker.reopenTask("a")
                    );

                    Assertions.assertEquals(
                            50,
                            tracker.getPriority("a")
                    );

                    Assertions.assertEquals(
                            1,
                            tracker.getWorkload("alice")
                    );

                    Assertions.assertEquals(
                            java.util.List.of("a"),
                            tracker.getReadyTasks("alice", 10)
                    );
                }
        );
    }

    @Test
    @Order(36)
    void test_36_reopenPreservesDependencies() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("foundation", "F", 1);
                    tracker.createTask("feature", "Feature", 20);

                    tracker.assignTask("feature", "alice");

                    tracker.addDependency(
                            "feature",
                            "foundation"
                    );

                    tracker.completeTask("foundation");
                    tracker.completeTask("feature");

                    Assertions.assertTrue(
                            tracker.reopenTask("feature")
                    );

                    // foundation is still completed, so feature is ready
                    Assertions.assertEquals(
                            java.util.List.of("feature"),
                            tracker.getReadyTasks("alice", 10)
                    );

                    Assertions.assertTrue(
                            tracker.reopenTask("foundation")
                    );

                    // Dependency relationship must still exist.
                    Assertions.assertEquals(
                            java.util.List.of(),
                            tracker.getReadyTasks("alice", 10)
                    );

                    Assertions.assertEquals(
                            java.util.List.of("feature"),
                            tracker.getBlockedTasks("alice", 10)
                    );
                }
        );
    }

    @Test
    @Order(37)
    void test_37_blockedOrderingByIncompleteDependencyCount() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("d1", "D1", 1);
                    tracker.createTask("d2", "D2", 1);
                    tracker.createTask("d3", "D3", 1);

                    tracker.createTask("alpha", "A", 100);
                    tracker.createTask("beta", "B", 10);
                    tracker.createTask("gamma", "G", 50);

                    tracker.assignTask("alpha", "alice");
                    tracker.assignTask("beta", "alice");
                    tracker.assignTask("gamma", "alice");

                    // alpha -> 1 incomplete dependency
                    tracker.addDependency("alpha", "d1");

                    // beta -> 3 incomplete dependencies
                    tracker.addDependency("beta", "d1");
                    tracker.addDependency("beta", "d2");
                    tracker.addDependency("beta", "d3");

                    // gamma -> 2 incomplete dependencies
                    tracker.addDependency("gamma", "d1");
                    tracker.addDependency("gamma", "d2");

                    Assertions.assertEquals(
                            java.util.List.of(
                                    "beta",
                                    "gamma",
                                    "alpha"
                            ),
                            tracker.getBlockedTasks("alice", 10)
                    );
                }
        );
    }

    @Test
    @Order(38)
    void test_38_blockedOrderingUsesPriorityForEqualCounts() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("dependency", "D", 1);

                    tracker.createTask("low", "Low", 5);
                    tracker.createTask("high", "High", 50);

                    tracker.assignTask("low", "alice");
                    tracker.assignTask("high", "alice");

                    tracker.addDependency("low", "dependency");
                    tracker.addDependency("high", "dependency");

                    Assertions.assertEquals(
                            java.util.List.of("high", "low"),
                            tracker.getBlockedTasks("alice", 10)
                    );
                }
        );
    }

    @Test
    @Order(39)
    void test_39_blockedOrderingUsesAlphabeticalTieBreaker() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("dependency", "D", 1);

                    tracker.createTask("charlie", "C", 20);
                    tracker.createTask("alpha", "A", 20);
                    tracker.createTask("beta", "B", 20);

                    tracker.assignTask("charlie", "alice");
                    tracker.assignTask("alpha", "alice");
                    tracker.assignTask("beta", "alice");

                    tracker.addDependency("charlie", "dependency");
                    tracker.addDependency("alpha", "dependency");
                    tracker.addDependency("beta", "dependency");

                    Assertions.assertEquals(
                            java.util.List.of(
                                    "alpha",
                                    "beta",
                                    "charlie"
                            ),
                            tracker.getBlockedTasks("alice", 10)
                    );
                }
        );
    }

    @Test
    @Order(40)
    void test_40_completedDependencyReducesBlockingCount() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("d1", "D1", 1);
                    tracker.createTask("d2", "D2", 1);

                    tracker.createTask("a", "A", 10);
                    tracker.createTask("b", "B", 20);

                    tracker.assignTask("a", "alice");
                    tracker.assignTask("b", "alice");

                    tracker.addDependency("a", "d1");
                    tracker.addDependency("a", "d2");

                    tracker.addDependency("b", "d1");

                    // a has 2 blockers, b has 1
                    Assertions.assertEquals(
                            java.util.List.of("a", "b"),
                            tracker.getBlockedTasks("alice", 10)
                    );

                    tracker.completeTask("d1");

                    // a has 1 blocker
                    // b has 0 blockers and is now ready
                    Assertions.assertEquals(
                            java.util.List.of("a"),
                            tracker.getBlockedTasks("alice", 10)
                    );

                    Assertions.assertEquals(
                            java.util.List.of("b"),
                            tracker.getReadyTasks("alice", 10)
                    );
                }
        );
    }

    @Test
    @Order(41)
    void test_41_removeDependencyCanUnblockTask() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("dependency", "D", 1);
                    tracker.createTask("task", "Task", 100);

                    tracker.assignTask("task", "alice");

                    tracker.addDependency(
                            "task",
                            "dependency"
                    );

                    Assertions.assertEquals(
                            java.util.List.of("task"),
                            tracker.getBlockedTasks("alice", 10)
                    );

                    Assertions.assertTrue(
                            tracker.removeDependency(
                                    "task",
                                    "dependency"
                            )
                    );

                    Assertions.assertEquals(
                            java.util.List.of(),
                            tracker.getBlockedTasks("alice", 10)
                    );

                    Assertions.assertEquals(
                            java.util.List.of("task"),
                            tracker.getReadyTasks("alice", 10)
                    );
                }
        );
    }

    @Test
    @Order(42)
    void test_42_deletedDependencyCanUnblockTask() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("dependency", "D", 1);
                    tracker.createTask("task", "Task", 100);

                    tracker.assignTask("task", "alice");

                    tracker.addDependency(
                            "task",
                            "dependency"
                    );

                    Assertions.assertEquals(
                            java.util.List.of("task"),
                            tracker.getBlockedTasks("alice", 10)
                    );

                    tracker.deleteTask("dependency");

                    Assertions.assertEquals(
                            java.util.List.of(),
                            tracker.getBlockedTasks("alice", 10)
                    );

                    Assertions.assertEquals(
                            java.util.List.of("task"),
                            tracker.getReadyTasks("alice", 10)
                    );
                }
        );
    }

    @Test
    @Order(43)
    void test_43_completedTaskNeverReturnedAsBlocked() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("dependency", "D", 1);
                    tracker.createTask("task", "Task", 100);

                    tracker.assignTask("task", "alice");

                    tracker.addDependency(
                            "task",
                            "dependency"
                    );

                    tracker.completeTask("task");

                    Assertions.assertEquals(
                            java.util.List.of(),
                            tracker.getBlockedTasks("alice", 10)
                    );
                }
        );
    }

    @Test
    @Order(44)
    void test_44_reassignmentChangesBlockedUser() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("dependency", "D", 1);
                    tracker.createTask("task", "Task", 100);

                    tracker.assignTask("task", "alice");
                    tracker.addDependency(
                            "task",
                            "dependency"
                    );

                    Assertions.assertEquals(
                            java.util.List.of("task"),
                            tracker.getBlockedTasks("alice", 10)
                    );

                    tracker.assignTask("task", "bob");

                    Assertions.assertEquals(
                            java.util.List.of(),
                            tracker.getBlockedTasks("alice", 10)
                    );

                    Assertions.assertEquals(
                            java.util.List.of("task"),
                            tracker.getBlockedTasks("bob", 10)
                    );
                }
        );
    }

    @Test
    @Order(45)
    void test_45_blockedLimit() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    tracker.createTask("dependency", "D", 1);

                    tracker.createTask("a", "A", 40);
                    tracker.createTask("b", "B", 30);
                    tracker.createTask("c", "C", 20);
                    tracker.createTask("d", "D", 10);

                    tracker.assignTask("a", "alice");
                    tracker.assignTask("b", "alice");
                    tracker.assignTask("c", "alice");
                    tracker.assignTask("d", "alice");

                    tracker.addDependency("a", "dependency");
                    tracker.addDependency("b", "dependency");
                    tracker.addDependency("c", "dependency");
                    tracker.addDependency("d", "dependency");

                    Assertions.assertEquals(
                            java.util.List.of("a", "b"),
                            tracker.getBlockedTasks("alice", 2)
                    );
                }
        );
    }
}