package com.codesignal.klayvio.mock6;

import java.util.List;

public interface CourseCatalog {

    default boolean createCourse(String courseId, String department, int credits) {
        return false;
    }

    default boolean updateDepartment(String courseId, String department) {
        return false;
    }

    default boolean updateCredits(String courseId, int credits) {
        return false;
    }

    default boolean publishCourse(String courseId) {
        return false;
    }

    default int getCredits(String courseId) {
        return -1;
    }

    default String getDepartment(String courseId) {
        return null;
    }

    default boolean isPublished(String courseId) {
        return false;
    }

    default boolean deleteCourse(String courseId) {
        return false;
    }

    default int getUnpublishedCredits(String department) {
        return 0;
    }

    default List<String> getBusiestDepartments(int limit) {
        return List.of();
    }

    default boolean addPrerequisite(String courseId, String prerequisiteCourseId) {
        return false;
    }

    default boolean removePrerequisite(String courseId, String prerequisiteCourseId) {
        return false;
    }

    default boolean isReadyToPublish(String courseId) {
        return false;
    }

    default boolean reopenCourse(String courseId) {
        return false;
    }

    default List<String> getBlockedCourses(int limit) {
        return List.of();
    }
}
