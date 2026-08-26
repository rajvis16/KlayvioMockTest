package com.codesignal.klayvio.mock4;

import java.util.*;
import java.util.stream.Collectors;

class TaskTrackerImpl implements TaskTracker {

    public static final String COMPLETED = "COMPLETED";
    public static final String NOT_COMPLETED = "NOT_COMPLETED";

    // taskId -> new Task (desc, priority, status);
    private final Map<String, Task> taskMap = new HashMap<>();

    public TaskTrackerImpl() {
    }

    @Override
    public boolean createTask(String taskId, String description, int priority) {

        if (taskMap.containsKey(taskId)) {
            return false;
        }

        Task task = new Task(taskId, description, priority, NOT_COMPLETED);
        taskMap.put(taskId, task);

        return true;
    }

    @Override
    public boolean updatePriority(String taskId, int newPriority) {

        if (!taskMap.containsKey(taskId)) {
            return false;
        }

        Task existingTask = taskMap.get(taskId);
        existingTask.priority = newPriority;

        return true;
    }

    @Override
    public boolean completeTask(String taskId) {
        if (!taskMap.containsKey(taskId)) {
            return false;
        }

        Task existingTask = taskMap.get(taskId);
        if (existingTask.status.equals(COMPLETED)) {
            return false;
        }

        existingTask.status = COMPLETED;

        return true;

    }

    @Override
    public int getPriority(String taskId) {

        if (!taskMap.containsKey(taskId)) {
            return -1;
        }

        Task existingTask = taskMap.get(taskId);
        return existingTask.priority;
    }

    @Override
    public boolean isCompleted(String taskId) {

        if (taskMap.containsKey(taskId)) {
            Task existingTask = taskMap.get(taskId);
            return existingTask.status.equals(COMPLETED);
        }

        return false;
    }

    @Override
    public boolean deleteTask(String taskId) {

        if (taskMap.containsKey(taskId)) {

            for (Map.Entry<String, Task> entry : taskMap.entrySet()) {
                Task dependentTasks = entry.getValue();
                if (dependentTasks.dependentTasks != null) {
                    dependentTasks.dependentTasks.remove(taskId);
                }
            }

            taskMap.remove(taskId);

            return true;
        }

        return false;
    }

    @Override
    public boolean assignTask(String taskId, String userId) {

        if (!taskMap.containsKey(taskId)) {
            return false;
        }

        Task existingTask = taskMap.get(taskId);
        if (existingTask.status.equals(COMPLETED)) {
            return false;
        }

        existingTask.assignedUser = userId;

        return true;
    }

    @Override
    public boolean unassignTask(String taskId) {

        if (!taskMap.containsKey(taskId)) {
            return false;
        }

        Task task = taskMap.get(taskId);
        if (task.assignedUser == null) {
            return false;
        }

        task.assignedUser = null;

        return true;

    }

    @Override
    public int getWorkload(String userId) {

        List<Task> tasks = taskMap.values().stream()
                .filter(task -> {
                    if (task.assignedUser != null) {
                        return task.assignedUser.equals(userId);
                    } else {
                        return false;
                    }
                })
                .collect(Collectors.toCollection(ArrayList::new));

        int count = 0;
        for (Task task : tasks) {

            if (task!= null && task.status.equals(NOT_COMPLETED)) {
                count++;
            }
        }

        return count;

    }

    @Override
    public boolean addDependency(String taskId, String dependencyTaskId) {

        if (taskId == null || dependencyTaskId == null) {
            return false;
        }

        if (taskId.equals(dependencyTaskId)) {
            return false;
        }

        if (!taskMap.containsKey(taskId) || !taskMap.containsKey(dependencyTaskId)) {
            return false;
        }

        Task task = taskMap.get(taskId);
        if (task.dependentTasks.contains(dependencyTaskId)) {
            return false;
        }

        task.dependentTasks.add(dependencyTaskId);

        return true;
    }

    @Override
    public boolean removeDependency(String taskId, String dependencyTaskId) {

        Task task = taskMap.get(taskId);
        return task != null && task.dependentTasks.remove(dependencyTaskId);
    }

    @Override
    public List<String> getReadyTasks(String userId, int limit) {

        List<Task> tasks = taskMap.values().stream()
                .filter(task -> {
                    if (task.assignedUser != null) {
                        return task.assignedUser.equals(userId);
                    } else {
                        return false;
                    }
                })
                .collect(Collectors.toCollection(ArrayList::new));


        List<Task> readyTaskIds = new ArrayList<>();


        for (Task task : tasks) {

            if (task.assignedUser == null) {
                continue;
            }

            if (task.status.equals(COMPLETED)) {
                continue;
            }

            if (!task.dependentTasks.isEmpty()) {
                boolean allTasksCompleted = true;
                for (String dependentTaskId : task.dependentTasks) {
                    Task dependentTask = taskMap.get(dependentTaskId);
                    if (dependentTask != null && !dependentTask.status.equals(COMPLETED)) {
                        allTasksCompleted = false;
                        break;
                    }
                }

                if (allTasksCompleted) {
                    readyTaskIds.add(task);
                }
            } else {
                readyTaskIds.add(task);
            }

        }

        readyTaskIds.sort((t1, t2) -> {
            int cmp = Integer.compare(t2.priority, t1.priority);
            if (cmp == 0) {
                return t1.taskId.compareTo(t2.taskId);
            }

            return cmp;
        });

        limit = Math.min(readyTaskIds.size(), limit);

        List<String> answer = new ArrayList<>();
        if (!readyTaskIds.isEmpty()) {
            for (int i = 0; i < limit; i++) {
                answer.add(readyTaskIds.get(i).taskId);
            }
        }

        return answer;
    }

    @Override
    public boolean reopenTask(String taskId) {

        if (!taskMap.containsKey(taskId)) {
            return false;
        }

        Task task = taskMap.get(taskId);
        if (task == null) {
            return false;
        }

        if (task.status.equals(NOT_COMPLETED)) {
            return false;
        }

        task.status = NOT_COMPLETED;

        return true;
    }

    @Override
    public List<String> getBlockedTasks(String userId, int limit) {

        List<Task> tasks = taskMap.values().stream()
                .filter(entry -> {
                    if (entry.assignedUser != null) {
                        return entry.assignedUser.equals(userId);
                    } else {
                        return false;
                    }
                })
                .collect(Collectors.toCollection(ArrayList::new));

        List<Task> blockedTask = new ArrayList<>();

        for (Task task : tasks) {

            if (task.status.equals(NOT_COMPLETED)) {

                int incompleteTask = 0;

                for (String dependentTaskId : task.dependentTasks) {
                    Task dependentTask = taskMap.get(dependentTaskId);
                    if (dependentTask != null && dependentTask.status.equals(NOT_COMPLETED)) {
                        incompleteTask++;
                    }
                }

                if (incompleteTask > 0) {
                    task.incompleteTasks = incompleteTask;
                    blockedTask.add(task);
                }
            }
        }

        return blockedTask.stream().sorted((t1, t2) -> {
            int cmp = Integer.compare(t2.incompleteTasks, t1.incompleteTasks);
            if (cmp == 0) {
                cmp = Integer.compare(t2.priority, t1.priority);
                if (cmp == 0) {
                    return t1.taskId.compareTo(t2.taskId);
                }
            }

            return cmp;
        }).map(task -> task.taskId).limit(limit).collect(Collectors.toCollection(ArrayList::new));
    }

    private static class Task {
        String taskId;
        String description;
        int priority;
        String status;
        String assignedUser;
        Set<String> dependentTasks = new HashSet<>();
        int incompleteTasks;

        Task(String taskId, String description, int priority, String status) {
            this.taskId = taskId;
            this.description = description;
            this.priority = priority;
            this.status = status;
        }
    }
}