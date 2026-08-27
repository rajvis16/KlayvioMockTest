package com.codesignal.klayvio.mock6;

import java.time.Duration;

import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BasicTests {

    private CourseCatalog catalog;

    @BeforeEach
    void setUp() {
        catalog = new CourseCatalogImpl();
    }

    @Test
    @Order(1)
    void test_basic1_createAndRead() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    Assertions.assertTrue(
                            catalog.createCourse("java101", "Engineering", 4)
                    );
                    Assertions.assertEquals(4, catalog.getCredits("java101"));
                    Assertions.assertEquals("Engineering", catalog.getDepartment("java101"));
                    Assertions.assertFalse(catalog.isPublished("java101"));
                }
        );
    }

    @Test
    @Order(2)
    void test_basic2_update() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    catalog.createCourse("java101", "Engineering", 4);

                    Assertions.assertTrue(
                            catalog.updateDepartment("java101", "Computer Science")
                    );

                    Assertions.assertTrue(
                            catalog.updateCredits("java101", 5)
                    );

                    Assertions.assertEquals(
                            "Computer Science",
                            catalog.getDepartment("java101")
                    );

                    Assertions.assertEquals(
                            5,
                            catalog.getCredits("java101")
                    );
                }
        );
    }

    @Test
    @Order(3)
    void test_basic3_publish() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    catalog.createCourse("java101", "Engineering", 4);

                    Assertions.assertTrue(
                            catalog.publishCourse("java101")
                    );

                    Assertions.assertTrue(
                            catalog.isPublished("java101")
                    );
                }
        );
    }

    @Test
    @Order(4)
    void test_basic4_departmentCredits() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    catalog.createCourse("c1", "Engineering", 4);
                    catalog.createCourse("c2", "Engineering", 3);
                    catalog.createCourse("c3", "Math", 5);

                    Assertions.assertEquals(
                            7,
                            catalog.getUnpublishedCredits("Engineering")
                    );

                    Assertions.assertEquals(
                            java.util.List.of("Engineering", "Math"),
                            catalog.getBusiestDepartments(10)
                    );
                }
        );
    }

    @Test
    @Order(5)
    void test_basic5_prerequisites() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    catalog.createCourse("intro", "CS", 3);
                    catalog.createCourse("advanced", "CS", 4);

                    Assertions.assertTrue(
                            catalog.addPrerequisite("advanced", "intro")
                    );

                    Assertions.assertFalse(
                            catalog.isReadyToPublish("advanced")
                    );

                    Assertions.assertFalse(
                            catalog.publishCourse("advanced")
                    );

                    Assertions.assertTrue(
                            catalog.publishCourse("intro")
                    );

                    Assertions.assertTrue(
                            catalog.isReadyToPublish("advanced")
                    );

                    Assertions.assertTrue(
                            catalog.publishCourse("advanced")
                    );
                }
        );
    }

    @Test
    @Order(6)
    void test_basic6_reopenBlocksDependentCourse() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    catalog.createCourse("intro", "CS", 3);
                    catalog.createCourse("advanced", "CS", 4);

                    catalog.addPrerequisite("advanced", "intro");

                    catalog.publishCourse("intro");

                    Assertions.assertTrue(
                            catalog.isReadyToPublish("advanced")
                    );

                    Assertions.assertTrue(
                            catalog.reopenCourse("intro")
                    );

                    Assertions.assertFalse(
                            catalog.isReadyToPublish("advanced")
                    );

                    Assertions.assertEquals(
                            java.util.List.of("advanced"),
                            catalog.getBlockedCourses(10)
                    );
                }
        );
    }
}
