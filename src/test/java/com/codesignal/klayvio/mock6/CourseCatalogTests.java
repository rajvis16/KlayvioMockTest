package com.codesignal.klayvio.mock6;

import java.time.Duration;

import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CourseCatalogTests {

    private CourseCatalog catalog;

    @BeforeEach
    void setUp() {
        catalog = new CourseCatalogImpl();
    }

    @Test @Order(1)
    void test_01_duplicateCreatePreservesOriginal() {
        Assertions.assertTrue(catalog.createCourse("c1", "Engineering", 4));
        Assertions.assertFalse(catalog.createCourse("c1", "Math", 100));
        Assertions.assertEquals("Engineering", catalog.getDepartment("c1"));
        Assertions.assertEquals(4, catalog.getCredits("c1"));
        Assertions.assertFalse(catalog.isPublished("c1"));
    }

    @Test @Order(2)
    void test_02_missingCourseOperations() {
        Assertions.assertFalse(catalog.updateDepartment("missing", "Math"));
        Assertions.assertFalse(catalog.updateCredits("missing", 5));
        Assertions.assertFalse(catalog.publishCourse("missing"));
        Assertions.assertFalse(catalog.deleteCourse("missing"));
        Assertions.assertEquals(-1, catalog.getCredits("missing"));
        Assertions.assertNull(catalog.getDepartment("missing"));
        Assertions.assertFalse(catalog.isPublished("missing"));
    }

    @Test @Order(3)
    void test_03_publishOnlyOnce() {
        catalog.createCourse("c1", "Engineering", 4);
        Assertions.assertTrue(catalog.publishCourse("c1"));
        Assertions.assertFalse(catalog.publishCourse("c1"));
        Assertions.assertTrue(catalog.isPublished("c1"));
    }

    @Test @Order(4)
    void test_04_publishedCourseCannotChangeDepartment() {
        catalog.createCourse("c1", "Engineering", 4);
        catalog.publishCourse("c1");
        Assertions.assertFalse(catalog.updateDepartment("c1", "Math"));
        Assertions.assertEquals("Engineering", catalog.getDepartment("c1"));
    }

    @Test @Order(5)
    void test_05_publishedCourseCannotChangeCredits() {
        catalog.createCourse("c1", "Engineering", 4);
        catalog.publishCourse("c1");
        Assertions.assertFalse(catalog.updateCredits("c1", 10));
        Assertions.assertEquals(4, catalog.getCredits("c1"));
    }

    @Test @Order(6)
    void test_06_multipleCoursesIndependent() {
        catalog.createCourse("a", "Engineering", 3);
        catalog.createCourse("b", "Math", 4);
        catalog.createCourse("c", "Science", 5);

        catalog.updateCredits("b", 10);
        catalog.publishCourse("a");

        Assertions.assertEquals(3, catalog.getCredits("a"));
        Assertions.assertTrue(catalog.isPublished("a"));
        Assertions.assertEquals(10, catalog.getCredits("b"));
        Assertions.assertFalse(catalog.isPublished("b"));
        Assertions.assertEquals("Science", catalog.getDepartment("c"));
    }

    @Test @Order(7)
    void test_07_deletePublishedCourse() {
        catalog.createCourse("c1", "Engineering", 4);
        catalog.publishCourse("c1");

        Assertions.assertTrue(catalog.deleteCourse("c1"));
        Assertions.assertEquals(-1, catalog.getCredits("c1"));
        Assertions.assertNull(catalog.getDepartment("c1"));
        Assertions.assertFalse(catalog.isPublished("c1"));
    }

    @Test @Order(8)
    void test_08_deleteThenCreateStartsFresh() {
        catalog.createCourse("c1", "Engineering", 4);
        catalog.publishCourse("c1");
        catalog.deleteCourse("c1");

        Assertions.assertTrue(catalog.createCourse("c1", "Math", 2));
        Assertions.assertEquals("Math", catalog.getDepartment("c1"));
        Assertions.assertEquals(2, catalog.getCredits("c1"));
        Assertions.assertFalse(catalog.isPublished("c1"));
    }

    @Test @Order(9)
    void test_09_publishedCoursesExcludedFromCredits() {
        catalog.createCourse("c1", "Engineering", 4);
        catalog.createCourse("c2", "Engineering", 3);
        catalog.publishCourse("c1");
        Assertions.assertEquals(3, catalog.getUnpublishedCredits("Engineering"));
    }

    @Test @Order(10)
    void test_10_departmentUpdateMovesCredits() {
        catalog.createCourse("c1", "Engineering", 4);
        Assertions.assertTrue(catalog.updateDepartment("c1", "Math"));
        Assertions.assertEquals(0, catalog.getUnpublishedCredits("Engineering"));
        Assertions.assertEquals(4, catalog.getUnpublishedCredits("Math"));
    }

    @Test @Order(11)
    void test_11_creditUpdateChangesDepartmentTotal() {
        catalog.createCourse("c1", "Engineering", 4);
        catalog.createCourse("c2", "Engineering", 3);
        catalog.updateCredits("c1", 10);
        Assertions.assertEquals(13, catalog.getUnpublishedCredits("Engineering"));
    }

    @Test @Order(12)
    void test_12_deleteRemovesCredits() {
        catalog.createCourse("c1", "Engineering", 4);
        catalog.createCourse("c2", "Engineering", 3);
        catalog.deleteCourse("c1");
        Assertions.assertEquals(3, catalog.getUnpublishedCredits("Engineering"));
    }

    @Test @Order(13)
    void test_13_busiestDepartmentsByCredits() {
        catalog.createCourse("c1", "Engineering", 4);
        catalog.createCourse("c2", "Engineering", 3);
        catalog.createCourse("c3", "Math", 10);
        catalog.createCourse("c4", "Science", 5);

        Assertions.assertEquals(
                java.util.List.of("Math", "Engineering", "Science"),
                catalog.getBusiestDepartments(10)
        );
    }

    @Test @Order(14)
    void test_14_busiestDepartmentsAlphabeticalTie() {
        catalog.createCourse("c1", "Science", 5);
        catalog.createCourse("c2", "Engineering", 5);
        catalog.createCourse("c3", "Math", 5);

        Assertions.assertEquals(
                java.util.List.of("Engineering", "Math", "Science"),
                catalog.getBusiestDepartments(10)
        );
    }

    @Test @Order(15)
    void test_15_multipleCoursesAggregate() {
        catalog.createCourse("c1", "Engineering", 2);
        catalog.createCourse("c2", "Engineering", 3);
        catalog.createCourse("c3", "Engineering", 4);
        catalog.createCourse("c4", "Math", 8);

        Assertions.assertEquals(9, catalog.getUnpublishedCredits("Engineering"));

        Assertions.assertEquals(
                java.util.List.of("Engineering", "Math"),
                catalog.getBusiestDepartments(10)
        );
    }

    @Test @Order(16)
    void test_16_fullyPublishedDepartmentDisappears() {
        catalog.createCourse("c1", "Engineering", 4);
        catalog.createCourse("c2", "Engineering", 3);
        catalog.createCourse("c3", "Math", 5);

        catalog.publishCourse("c1");
        catalog.publishCourse("c2");

        Assertions.assertEquals(0, catalog.getUnpublishedCredits("Engineering"));
        Assertions.assertEquals(
                java.util.List.of("Math"),
                catalog.getBusiestDepartments(10)
        );
    }

    @Test @Order(17)
    void test_17_limitBusiestDepartments() {
        catalog.createCourse("c1", "A", 10);
        catalog.createCourse("c2", "B", 8);
        catalog.createCourse("c3", "C", 6);
        catalog.createCourse("c4", "D", 4);

        Assertions.assertEquals(
                java.util.List.of("A", "B"),
                catalog.getBusiestDepartments(2)
        );
    }

    @Test @Order(18)
    void test_18_noUnpublishedDepartments() {
        catalog.createCourse("c1", "Engineering", 4);
        catalog.publishCourse("c1");

        Assertions.assertEquals(
                java.util.List.of(),
                catalog.getBusiestDepartments(10)
        );
    }

    @Test @Order(19)
    void test_19_noPrerequisitesMeansReady() {
        catalog.createCourse("a", "CS", 3);

        Assertions.assertTrue(catalog.isReadyToPublish("a"));
        Assertions.assertFalse(catalog.isReadyToPublish("missing"));
    }

    @Test @Order(20)
    void test_20_allPrerequisitesMustBePublished() {
        catalog.createCourse("a", "CS", 3);
        catalog.createCourse("b", "CS", 3);
        catalog.createCourse("c", "CS", 4);

        Assertions.assertTrue(catalog.addPrerequisite("c", "a"));
        Assertions.assertTrue(catalog.addPrerequisite("c", "b"));
        Assertions.assertFalse(catalog.isReadyToPublish("c"));

        catalog.publishCourse("a");
        Assertions.assertFalse(catalog.isReadyToPublish("c"));
        Assertions.assertFalse(catalog.publishCourse("c"));

        catalog.publishCourse("b");
        Assertions.assertTrue(catalog.isReadyToPublish("c"));
        Assertions.assertTrue(catalog.publishCourse("c"));
    }

    @Test @Order(21)
    void test_21_duplicateAndSelfPrerequisiteRejected() {
        catalog.createCourse("a", "CS", 3);
        catalog.createCourse("b", "CS", 3);

        Assertions.assertFalse(catalog.addPrerequisite("a", "a"));
        Assertions.assertTrue(catalog.addPrerequisite("a", "b"));
        Assertions.assertFalse(catalog.addPrerequisite("a", "b"));
        Assertions.assertFalse(catalog.addPrerequisite("missing", "b"));
        Assertions.assertFalse(catalog.addPrerequisite("a", "missing"));
    }

    @Test @Order(22)
    void test_22_removePrerequisiteCanMakeReady() {
        catalog.createCourse("a", "CS", 3);
        catalog.createCourse("b", "CS", 3);
        catalog.addPrerequisite("a", "b");

        Assertions.assertFalse(catalog.isReadyToPublish("a"));
        Assertions.assertTrue(catalog.removePrerequisite("a", "b"));
        Assertions.assertTrue(catalog.isReadyToPublish("a"));
        Assertions.assertFalse(catalog.removePrerequisite("a", "b"));
    }

    @Test @Order(23)
    void test_23_publishedCoursePrerequisitesCannotChange() {
        catalog.createCourse("a", "CS", 3);
        catalog.createCourse("b", "CS", 3);
        catalog.createCourse("c", "CS", 3);

        catalog.addPrerequisite("a", "b");
        catalog.publishCourse("b");
        catalog.publishCourse("a");

        Assertions.assertFalse(catalog.addPrerequisite("a", "c"));
        Assertions.assertFalse(catalog.removePrerequisite("a", "b"));
    }

    @Test @Order(24)
    void test_24_deletePrerequisiteRemovesReference() {
        catalog.createCourse("a", "CS", 3);
        catalog.createCourse("b", "CS", 3);

        catalog.addPrerequisite("a", "b");
        Assertions.assertFalse(catalog.isReadyToPublish("a"));

        Assertions.assertTrue(catalog.deleteCourse("b"));
        Assertions.assertTrue(catalog.isReadyToPublish("a"));
    }

    @Test @Order(25)
    void test_25_deleteCourseRemovesOwnPrerequisites() {
        catalog.createCourse("a", "CS", 3);
        catalog.createCourse("b", "CS", 3);

        catalog.addPrerequisite("a", "b");
        catalog.deleteCourse("a");

        Assertions.assertTrue(catalog.createCourse("a", "Math", 2));
        Assertions.assertTrue(catalog.isReadyToPublish("a"));
    }

    @Test @Order(26)
    void test_26_reopenMissingOrUnpublishedFails() {
        Assertions.assertFalse(catalog.reopenCourse("missing"));

        catalog.createCourse("a", "CS", 3);

        Assertions.assertFalse(catalog.reopenCourse("a"));
        Assertions.assertFalse(catalog.isPublished("a"));
    }

    @Test @Order(27)
    void test_27_reopenPreservesState() {
        catalog.createCourse("a", "Engineering", 5);
        catalog.publishCourse("a");

        Assertions.assertTrue(catalog.reopenCourse("a"));
        Assertions.assertEquals("Engineering", catalog.getDepartment("a"));
        Assertions.assertEquals(5, catalog.getCredits("a"));
        Assertions.assertFalse(catalog.isPublished("a"));
    }

    @Test @Order(28)
    void test_28_reopenPreservesPrerequisites() {
        catalog.createCourse("intro", "CS", 3);
        catalog.createCourse("advanced", "CS", 4);

        catalog.addPrerequisite("advanced", "intro");

        catalog.publishCourse("intro");
        catalog.publishCourse("advanced");

        Assertions.assertTrue(catalog.reopenCourse("advanced"));
        Assertions.assertTrue(catalog.isReadyToPublish("advanced"));

        Assertions.assertTrue(catalog.reopenCourse("intro"));
        Assertions.assertFalse(catalog.isReadyToPublish("advanced"));
    }

    @Test @Order(29)
    void test_29_blockedOrderingByPrerequisiteCount() {
        catalog.createCourse("p1", "CS", 1);
        catalog.createCourse("p2", "CS", 1);
        catalog.createCourse("p3", "CS", 1);

        catalog.createCourse("alpha", "CS", 100);
        catalog.createCourse("beta", "CS", 10);
        catalog.createCourse("gamma", "CS", 50);

        catalog.addPrerequisite("alpha", "p1");

        catalog.addPrerequisite("beta", "p1");
        catalog.addPrerequisite("beta", "p2");
        catalog.addPrerequisite("beta", "p3");

        catalog.addPrerequisite("gamma", "p1");
        catalog.addPrerequisite("gamma", "p2");

        Assertions.assertEquals(
                java.util.List.of("beta", "gamma", "alpha"),
                catalog.getBlockedCourses(10)
        );
    }

    @Test @Order(30)
    void test_30_blockedOrderingUsesCreditsOnTie() {
        catalog.createCourse("p", "CS", 1);

        catalog.createCourse("low", "CS", 5);
        catalog.createCourse("high", "CS", 50);

        catalog.addPrerequisite("low", "p");
        catalog.addPrerequisite("high", "p");

        Assertions.assertEquals(
                java.util.List.of("high", "low"),
                catalog.getBlockedCourses(10)
        );
    }

    @Test @Order(31)
    void test_31_blockedOrderingUsesCourseIdOnTie() {
        catalog.createCourse("p", "CS", 1);

        catalog.createCourse("charlie", "CS", 20);
        catalog.createCourse("alpha", "CS", 20);
        catalog.createCourse("beta", "CS", 20);

        catalog.addPrerequisite("charlie", "p");
        catalog.addPrerequisite("alpha", "p");
        catalog.addPrerequisite("beta", "p");

        Assertions.assertEquals(
                java.util.List.of("alpha", "beta", "charlie"),
                catalog.getBlockedCourses(10)
        );
    }

    @Test @Order(32)
    void test_32_publishingPrerequisiteReducesBlockCount() {
        catalog.createCourse("p1", "CS", 1);
        catalog.createCourse("p2", "CS", 1);

        catalog.createCourse("a", "CS", 10);
        catalog.createCourse("b", "CS", 20);

        catalog.addPrerequisite("a", "p1");
        catalog.addPrerequisite("a", "p2");
        catalog.addPrerequisite("b", "p1");

        Assertions.assertEquals(
                java.util.List.of("a", "b"),
                catalog.getBlockedCourses(10)
        );

        catalog.publishCourse("p1");

        Assertions.assertEquals(
                java.util.List.of("a"),
                catalog.getBlockedCourses(10)
        );

        Assertions.assertTrue(catalog.isReadyToPublish("b"));
    }

    @Test @Order(33)
    void test_33_removePrerequisiteCanUnblockCourse() {
        catalog.createCourse("p", "CS", 1);
        catalog.createCourse("a", "CS", 10);

        catalog.addPrerequisite("a", "p");

        Assertions.assertEquals(
                java.util.List.of("a"),
                catalog.getBlockedCourses(10)
        );

        catalog.removePrerequisite("a", "p");

        Assertions.assertEquals(
                java.util.List.of(),
                catalog.getBlockedCourses(10)
        );
    }

    @Test @Order(34)
    void test_34_publishedCourseNeverReturnedAsBlocked() {
        catalog.createCourse("p", "CS", 1);
        catalog.createCourse("a", "CS", 10);

        catalog.addPrerequisite("a", "p");

        catalog.publishCourse("p");
        catalog.publishCourse("a");

        catalog.reopenCourse("p");

        Assertions.assertEquals(
                java.util.List.of(),
                catalog.getBlockedCourses(10)
        );
    }

    @Test @Order(35)
    void test_35_limitBlockedCourses() {
        catalog.createCourse("p", "CS", 1);

        catalog.createCourse("a", "CS", 40);
        catalog.createCourse("b", "CS", 30);
        catalog.createCourse("c", "CS", 20);
        catalog.createCourse("d", "CS", 10);

        catalog.addPrerequisite("a", "p");
        catalog.addPrerequisite("b", "p");
        catalog.addPrerequisite("c", "p");
        catalog.addPrerequisite("d", "p");

        Assertions.assertEquals(
                java.util.List.of("a", "b"),
                catalog.getBlockedCourses(2)
        );
    }
}
