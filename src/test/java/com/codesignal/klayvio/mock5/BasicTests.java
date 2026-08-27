package com.codesignal.klayvio.mock5;

import java.time.Duration;

import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BasicTests {

    private PackageRegistry registry;

    @BeforeEach
    public void setUp() {
        registry = new PackageRegistryImpl();
    }

    @Test
    @Order(1)
    void test_basic1_registerAndRead() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    Assertions.assertTrue(
                            registry.registerPackage(
                                    "p1",
                                    "Boston",
                                    10
                            )
                    );

                    Assertions.assertEquals(
                            10,
                            registry.getWeight("p1")
                    );

                    Assertions.assertEquals(
                            "Boston",
                            registry.getDestination("p1")
                    );

                    Assertions.assertFalse(
                            registry.isDelivered("p1")
                    );
                }
        );
    }

    @Test
    @Order(2)
    void test_basic2_update() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    registry.registerPackage(
                            "p1",
                            "Boston",
                            10
                    );

                    Assertions.assertTrue(
                            registry.updateDestination(
                                    "p1",
                                    "Chicago"
                            )
                    );

                    Assertions.assertTrue(
                            registry.updateWeight(
                                    "p1",
                                    25
                            )
                    );

                    Assertions.assertEquals(
                            "Chicago",
                            registry.getDestination("p1")
                    );

                    Assertions.assertEquals(
                            25,
                            registry.getWeight("p1")
                    );
                }
        );
    }

    @Test
    @Order(3)
    void test_basic3_deliver() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    registry.registerPackage(
                            "p1",
                            "Boston",
                            10
                    );

                    Assertions.assertTrue(
                            registry.deliverPackage("p1")
                    );

                    Assertions.assertTrue(
                            registry.isDelivered("p1")
                    );
                }
        );
    }

    @Test
    @Order(4)
    void test_basic4_pendingWeight() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    registry.registerPackage("p1", "Boston", 10);
                    registry.registerPackage("p2", "Boston", 20);
                    registry.registerPackage("p3", "Chicago", 25);

                    Assertions.assertEquals(
                            30,
                            registry.getPendingWeight("Boston")
                    );

                    Assertions.assertEquals(
                            java.util.List.of("Boston", "Chicago"),
                            registry.getBusiestDestinations(10)
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
                    registry.registerPackage("truck", "Boston", 100);
                    registry.registerPackage("box", "Boston", 10);

                    Assertions.assertTrue(
                            registry.addDependency("box", "truck")
                    );

                    Assertions.assertFalse(
                            registry.isReadyForDelivery("box")
                    );

                    Assertions.assertFalse(
                            registry.deliverPackage("box")
                    );

                    Assertions.assertTrue(
                            registry.deliverPackage("truck")
                    );

                    Assertions.assertTrue(
                            registry.isReadyForDelivery("box")
                    );

                    Assertions.assertTrue(
                            registry.deliverPackage("box")
                    );
                }
        );
    }

    @Test
    @Order(6)
    void test_basic6_returnCanBlockDependentPackage() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    registry.registerPackage("truck", "Boston", 100);
                    registry.registerPackage("box", "Boston", 10);

                    registry.addDependency("box", "truck");

                    registry.deliverPackage("truck");

                    Assertions.assertTrue(
                            registry.isReadyForDelivery("box")
                    );

                    Assertions.assertTrue(
                            registry.returnPackage("truck")
                    );

                    Assertions.assertFalse(
                            registry.isReadyForDelivery("box")
                    );

                    Assertions.assertEquals(
                            java.util.List.of("box"),
                            registry.getBlockedPackages(10)
                    );
                }
        );
    }
}