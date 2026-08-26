package com.codesignal.klayvio.mock4;

import java.util.List;

public interface TaskTracker {

    /**
     * Creates a new task.
     *
     * A newly created task is NOT completed.
     *
     * Returns true if the task was created.
     *
     * Returns false if taskId already exists.
     * A failed create must not modify the existing task.
     */
    default boolean createTask(
            String taskId,
            String description,
            int priority) {
        return false;
    }

    /**
     * Changes the priority of an existing task.
     *
     * Returns true if the task exists.
     * Returns false if it does not exist.
     *
     * Updating priority must NOT change completion status.
     */
    default boolean updatePriority(
            String taskId,
            int newPriority) {
        return false;
    }

    /**
     * Marks an existing task as completed.
     *
     * Returns true if the task exists and was NOT already completed.
     *
     * Returns false if:
     * - the task does not exist
     * - the task was already completed
     *
     * Calling this multiple times must not otherwise modify the task.
     */
    default boolean completeTask(String taskId) {
        return false;
    }

    /**
     * Returns the priority of the task.
     *
     * Returns -1 if the task does not exist.
     */
    default int getPriority(String taskId) {
        return -1;
    }

    /**
     * Returns true only if the task exists and is completed.
     *
     * Missing tasks return false.
     */
    default boolean isCompleted(String taskId) {
        return false;
    }

    /**
     * Deletes a task.
     *
     * Returns true if the task existed and was deleted.
     * Returns false if the task did not exist.
     */
    default boolean deleteTask(String taskId) {
        return false;
    }

    /**
     * Assigns an existing task to a user.
     *
     * A task may have at most one current assignee.
     *
     * Returns true if:
     * - the task exists
     * - the task is NOT completed
     *
     * Assigning a task that is already assigned is allowed.
     * The new user replaces the previous assignee.
     *
     * Returns false if:
     * - the task does not exist
     * - the task is completed
     *
     * A failed assignment must not modify the task.
     */
    default boolean assignTask(String taskId, String userId) {
        return false;
    }

    /**
     * Removes the current assignment from a task.
     *
     * Returns true only if:
     * - the task exists
     * - the task currently has an assignee
     *
     * Returns false if:
     * - the task does not exist
     * - the task is currently unassigned
     *
     * Completion status does NOT prevent unassignment.
     */
    default boolean unassignTask(String taskId) {
        return false;
    }

    /**
     * Returns the number of currently assigned, NOT-completed
     * tasks for the specified user.
     *
     * Completed tasks must not contribute to workload.
     * Deleted tasks must not contribute to workload.
     * Unassigned tasks must not contribute to workload.
     *
     * Returns 0 if the user has no qualifying tasks.
     */
    default int getWorkload(String userId) {
        return 0;
    }

    /**
     * Adds a dependency to a task.
     *
     * taskId depends on dependencyTaskId.
     *
     * Returns true only if:
     * - both tasks exist
     * - taskId and dependencyTaskId are different
     * - this exact dependency does not already exist
     *
     * Otherwise returns false.
     *
     * Adding a dependency does not modify completion status,
     * priority, or assignment.
     */
    default boolean addDependency(
            String taskId,
            String dependencyTaskId) {
        return false;
    }

    /**
     * Removes an existing dependency.
     *
     * Returns true only if the specified dependency currently exists.
     *
     * Otherwise returns false.
     */
    default boolean removeDependency(
            String taskId,
            String dependencyTaskId) {
        return false;
    }

    /**
     * Returns up to `limit` ready task IDs assigned to userId.
     *
     * A task is ready only if:
     * - it is assigned to userId
     * - it is NOT completed
     * - every task it depends on is completed
     *
     * Tasks with no dependencies are ready.
     *
     * Ordering:
     * 1. priority descending
     * 2. if priorities are equal, taskId alphabetically ascending
     *
     * Completed tasks themselves are never returned.
     *
     * If fewer than limit tasks qualify, return all of them.
     */
    default List<String> getReadyTasks(
            String userId,
            int limit) {
        return List.of();
    }

    /**
     * Reopens a completed task.
     *
     * Returns true only if:
     * - the task exists
     * - the task is currently completed
     *
     * On success:
     * - the task becomes NOT completed
     * - its priority remains unchanged
     * - its assignment remains unchanged
     * - its dependencies remain unchanged
     *
     * Returns false if:
     * - the task does not exist
     * - the task is already NOT completed
     *
     * A failed reopen must not modify the task.
     */
    default boolean reopenTask(String taskId) {
        return false;
    }

    /**
     * Returns up to `limit` blocked tasks currently assigned to userId.
     *
     * A task is blocked when:
     * - it is assigned to userId
     * - it is NOT completed
     * - at least one of its dependencies is NOT completed
     *
     * Ordering:
     *
     * 1. Number of incomplete dependencies DESCENDING
     * 2. Priority DESCENDING
     * 3. taskId alphabetically ASCENDING
     *
     * Completed tasks are never returned.
     *
     * Tasks with no incomplete dependencies are ready, not blocked,
     * and therefore are not returned.
     *
     * If fewer than `limit` tasks qualify, return all qualifying tasks.
     */
    default List<String> getBlockedTasks(
            String userId,
            int limit) {
        return List.of();
    }
}