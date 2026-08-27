package com.codesignal.klayvio.mock6;

import java.util.*;
import java.util.stream.Collectors;

class CourseCatalogImpl implements CourseCatalog {

    private final Map<String, Course> courseMap = new HashMap<>();

    public CourseCatalogImpl() {
    }

    @Override
    public boolean createCourse(String courseId, String department, int credits) {

        if (courseMap.containsKey(courseId)) {
            return false;
        }

        Course course = new Course(courseId, department, credits);
        courseMap.put(courseId, course);

        return true;
    }

    @Override
    public boolean updateDepartment(String courseId, String department) {

        if (!courseMap.containsKey(courseId)) {
            return false;
        }

        Course course = courseMap.get(courseId);
        if (course == null) {
            return false;
        }

        if (course.published) {
            return false;
        }

        course.department = department;

        return true;
    }

    @Override
    public boolean updateCredits(String courseId, int credits) {

        if (!courseMap.containsKey(courseId)) {
            return false;
        }

        Course course = courseMap.get(courseId);
        if (course == null) {
            return false;
        }

        if (course.published) {
            return false;
        }

        course.credits = credits;

        return true;
    }

    @Override
    public boolean publishCourse(String courseId) {

        if (!courseMap.containsKey(courseId)) {
            return false;
        }

        Course course = courseMap.get(courseId);
        if (course == null) {
            return false;
        }

        if (course.published) {
            return false;
        }

        if (!allPrereqPublished(course)) {
            return false;
        }

        course.published = true;

        return true;
    }

    @Override
    public int getCredits(String courseId) {

        if (!courseMap.containsKey(courseId)) {
            return -1;
        }

        Course course = courseMap.get(courseId);
        if (course == null) {
            return -1;
        }

        return course.credits;
    }

    @Override
    public String getDepartment(String courseId) {

        if (!courseMap.containsKey(courseId)) {
            return null;
        }

        Course course = courseMap.get(courseId);
        if (course == null) {
            return null;
        }

        return course.department;
    }

    @Override
    public boolean isPublished(String courseId) {

        if (!courseMap.containsKey(courseId)) {
            return false;
        }

        Course course = courseMap.get(courseId);
        if (course == null) {
            return false;
        }

        return course.published;
    }

    @Override
    public boolean deleteCourse(String courseId) {

        if (!courseMap.containsKey(courseId)) {
            return false;
        }

        Course course = courseMap.get(courseId);
        if (course == null) {
            return false;
        }

        for (Map.Entry<String, Course> entry : courseMap.entrySet()) {
            entry.getValue().prerequisites.remove(courseId);
        }

        courseMap.remove(courseId);

        return true;
    }

    @Override
    public int getUnpublishedCredits(String department) {

        return courseMap
                .values()
                .stream()
                .filter(c -> department.equals(c.department) && !c.published)
                .mapToInt(c -> c.credits)
                .sum();
    }

    @Override
    public List<String> getBusiestDepartments(int limit) {

        // department - > credits
        Map<String, Integer> creditsByDepartment =
                courseMap.values()
                        .stream()
                        .filter(c -> !c.published)
                        .collect(Collectors.groupingBy(
                                c -> c.department,
                                Collectors.summingInt(c -> c.credits)
                        ));

        return creditsByDepartment
                .entrySet()
                .stream()
                .sorted((c1, c2) -> {

                    int cmp = Integer.compare(c2.getValue(), c1.getValue());
                    if (cmp == 0) {
                        return c1.getKey().compareTo(c2.getKey());
                    }

                    return cmp;
                })
                .map(c -> c.getKey())
                .distinct()
                .limit(limit)
                .toList();
    }

    @Override
    public boolean addPrerequisite(String courseId, String prerequisiteCourseId) {

        if (courseId.equals(prerequisiteCourseId)) {
            return false;
        }

        if (!courseMap.containsKey(courseId) || !courseMap.containsKey(prerequisiteCourseId)) {
            return false;
        }

        Course course = courseMap.get(courseId);
        if (course == null) {
            return false;
        }

        if (course.published) {
            return false;
        }

        if (course.prerequisites.contains(prerequisiteCourseId)) {
            return false;
        }

        course.prerequisites.add(prerequisiteCourseId);

        return true;
    }

    @Override
    public boolean removePrerequisite(String courseId, String prerequisiteCourseId) {

        if (courseId.equals(prerequisiteCourseId)) {
            return false;
        }

        if (!courseMap.containsKey(courseId) || !courseMap.containsKey(prerequisiteCourseId)) {
            return false;
        }

        Course course = courseMap.get(courseId);
        if (course == null) {
            return false;
        }

        if (course.published) {
            return false;
        }

        if (!course.prerequisites.contains(prerequisiteCourseId)) {
            return false;
        }

        course.prerequisites.remove(prerequisiteCourseId);

        return true;
    }

    @Override
    public boolean isReadyToPublish(String courseId) {

        if (!courseMap.containsKey(courseId)) {
            return false;
        }

        Course course = courseMap.get(courseId);
        if (course == null) {
            return false;
        }

        return course.prerequisites.isEmpty() || allPrereqPublished(course);
    }

    private boolean allPrereqPublished(Course course) {

        for (String courseId : course.prerequisites) {
            if (courseMap.get(courseId) != null && !courseMap.get(courseId).published) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean reopenCourse(String courseId) {

        if (!courseMap.containsKey(courseId)) {
            return false;
        }

        Course course = courseMap.get(courseId);
        if (course == null) {
            return false;
        }

        if (!course.published) {
            return false;
        }

        course.published = false;

        return true;
    }

    @Override
    public List<String> getBlockedCourses(int limit) {

        return courseMap
                .values()
                .stream()
                .filter(c -> !c.published)
                .filter(c -> getTotalNoOfUnpublishedPreReq(c) > 0)
                .sorted((c1, c2) -> {

                    int count1 = getTotalNoOfUnpublishedPreReq(c1);
                    int count2 = getTotalNoOfUnpublishedPreReq(c2);

                    int cmp = Integer.compare(count2, count1);

                    if (cmp == 0) {
                        cmp = Integer.compare(c2.credits, c1.credits);

                        if (cmp == 0) {
                            return c1.courseId.compareTo(c2.courseId);
                        }
                    }

                    return cmp;
                })
                .limit(limit)
                .map(c -> c.courseId)
                .toList();
    }

    private int getTotalNoOfUnpublishedPreReq(Course course) {

        int count = 0;

        for (String courseId : course.prerequisites) {
            if (courseMap.get(courseId) != null && !courseMap.get(courseId).published) {
                count++;
            }
        }

        return count;
    }

    private static class Course {

        String courseId;
        String department;
        int credits;
        boolean published;
        Set<String> prerequisites = new HashSet<>();

        Course(String courseId, String department, int credits) {
            this.courseId = courseId;
            this.department = department;
            this.credits = credits;
        }
    }
}
