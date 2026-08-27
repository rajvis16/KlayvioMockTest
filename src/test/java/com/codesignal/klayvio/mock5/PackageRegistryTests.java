package com.codesignal.klayvio.mock5;

import java.time.Duration;

import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PackageRegistryTests {

    private PackageRegistry registry;

    @BeforeEach
    public void setUp() {
        registry = new PackageRegistryImpl();
    }

    @Test
    @Order(1)
    void test_01_duplicateRegistrationPreservesOriginal() {
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

                    Assertions.assertFalse(
                            registry.registerPackage(
                                    "p1",
                                    "Seattle",
                                    100
                            )
                    );

                    Assertions.assertEquals(
                            "Boston",
                            registry.getDestination("p1")
                    );

                    Assertions.assertEquals(
                            10,
                            registry.getWeight("p1")
                    );

                    Assertions.assertFalse(
                            registry.isDelivered("p1")
                    );
                }
        );
    }

    @Test
    @Order(2)
    void test_02_missingPackageOperations() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    Assertions.assertFalse(
                            registry.updateDestination(
                                    "missing",
                                    "Boston"
                            )
                    );

                    Assertions.assertFalse(
                            registry.updateWeight(
                                    "missing",
                                    10
                            )
                    );

                    Assertions.assertFalse(
                            registry.deliverPackage("missing")
                    );

                    Assertions.assertFalse(
                            registry.deletePackage("missing")
                    );

                    Assertions.assertEquals(
                            -1,
                            registry.getWeight("missing")
                    );

                    Assertions.assertNull(
                            registry.getDestination("missing")
                    );

                    Assertions.assertFalse(
                            registry.isDelivered("missing")
                    );
                }
        );
    }

    @Test
    @Order(3)
    void test_03_deliveryCanOnlyHappenOnce() {
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

                    Assertions.assertFalse(
                            registry.deliverPackage("p1")
                    );

                    Assertions.assertTrue(
                            registry.isDelivered("p1")
                    );

                    Assertions.assertEquals(
                            10,
                            registry.getWeight("p1")
                    );

                    Assertions.assertEquals(
                            "Boston",
                            registry.getDestination("p1")
                    );
                }
        );
    }

    @Test
    @Order(4)
    void test_04_deliveredPackageCannotChangeDestination() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    registry.registerPackage(
                            "p1",
                            "Boston",
                            10
                    );

                    registry.deliverPackage("p1");

                    Assertions.assertFalse(
                            registry.updateDestination(
                                    "p1",
                                    "Chicago"
                            )
                    );

                    Assertions.assertEquals(
                            "Boston",
                            registry.getDestination("p1")
                    );

                    Assertions.assertTrue(
                            registry.isDelivered("p1")
                    );
                }
        );
    }

    @Test
    @Order(5)
    void test_05_deliveredPackageCannotChangeWeight() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    registry.registerPackage(
                            "p1",
                            "Boston",
                            10
                    );

                    registry.deliverPackage("p1");

                    Assertions.assertFalse(
                            registry.updateWeight(
                                    "p1",
                                    50
                            )
                    );

                    Assertions.assertEquals(
                            10,
                            registry.getWeight("p1")
                    );

                    Assertions.assertTrue(
                            registry.isDelivered("p1")
                    );
                }
        );
    }

    @Test
    @Order(6)
    void test_06_multiplePackagesIndependent() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    registry.registerPackage(
                            "a",
                            "Boston",
                            10
                    );

                    registry.registerPackage(
                            "b",
                            "Chicago",
                            20
                    );

                    registry.registerPackage(
                            "c",
                            "Seattle",
                            30
                    );

                    registry.updateWeight("b", 25);
                    registry.updateDestination(
                            "c",
                            "Denver"
                    );
                    registry.deliverPackage("a");

                    Assertions.assertEquals(
                            10,
                            registry.getWeight("a")
                    );

                    Assertions.assertEquals(
                            "Boston",
                            registry.getDestination("a")
                    );

                    Assertions.assertTrue(
                            registry.isDelivered("a")
                    );

                    Assertions.assertEquals(
                            25,
                            registry.getWeight("b")
                    );

                    Assertions.assertEquals(
                            "Chicago",
                            registry.getDestination("b")
                    );

                    Assertions.assertFalse(
                            registry.isDelivered("b")
                    );

                    Assertions.assertEquals(
                            30,
                            registry.getWeight("c")
                    );

                    Assertions.assertEquals(
                            "Denver",
                            registry.getDestination("c")
                    );

                    Assertions.assertFalse(
                            registry.isDelivered("c")
                    );
                }
        );
    }

    @Test
    @Order(7)
    void test_07_repeatedUpdates() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    registry.registerPackage(
                            "p1",
                            "A",
                            1
                    );

                    for (int i = 2; i <= 20; i++) {
                        Assertions.assertTrue(
                                registry.updateWeight(
                                        "p1",
                                        i
                                )
                        );
                    }

                    Assertions.assertEquals(
                            20,
                            registry.getWeight("p1")
                    );

                    Assertions.assertFalse(
                            registry.isDelivered("p1")
                    );
                }
        );
    }

    @Test
    @Order(8)
    void test_08_deleteUndeliveredPackage() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    registry.registerPackage(
                            "p1",
                            "Boston",
                            10
                    );

                    Assertions.assertTrue(
                            registry.deletePackage("p1")
                    );

                    Assertions.assertEquals(
                            -1,
                            registry.getWeight("p1")
                    );

                    Assertions.assertNull(
                            registry.getDestination("p1")
                    );

                    Assertions.assertFalse(
                            registry.isDelivered("p1")
                    );
                }
        );
    }

    @Test
    @Order(9)
    void test_09_deleteDeliveredPackage() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    registry.registerPackage(
                            "p1",
                            "Boston",
                            10
                    );

                    registry.deliverPackage("p1");

                    Assertions.assertTrue(
                            registry.deletePackage("p1")
                    );

                    Assertions.assertEquals(
                            -1,
                            registry.getWeight("p1")
                    );

                    Assertions.assertNull(
                            registry.getDestination("p1")
                    );

                    Assertions.assertFalse(
                            registry.isDelivered("p1")
                    );
                }
        );
    }

    @Test
    @Order(10)
    void test_10_deleteThenRegisterStartsFresh() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    registry.registerPackage(
                            "p1",
                            "Boston",
                            10
                    );

                    registry.deliverPackage("p1");
                    registry.deletePackage("p1");

                    Assertions.assertTrue(
                            registry.registerPackage(
                                    "p1",
                                    "Miami",
                                    5
                            )
                    );

                    Assertions.assertEquals(
                            "Miami",
                            registry.getDestination("p1")
                    );

                    Assertions.assertEquals(
                            5,
                            registry.getWeight("p1")
                    );

                    Assertions.assertFalse(
                            registry.isDelivered("p1")
                    );
                }
        );
    }

    @Test
    @Order(11)
    void test_11_deliveredPackagesExcludedFromPendingWeight() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    registry.registerPackage("p1", "Boston", 10);
                    registry.registerPackage("p2", "Boston", 20);

                    registry.deliverPackage("p1");

                    Assertions.assertEquals(
                            20,
                            registry.getPendingWeight("Boston")
                    );
                }
        );
    }

    @Test
    @Order(12)
    void test_12_destinationUpdateMovesPendingWeight() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    registry.registerPackage("p1", "Boston", 30);

                    Assertions.assertEquals(
                            30,
                            registry.getPendingWeight("Boston")
                    );

                    Assertions.assertTrue(
                            registry.updateDestination("p1", "Chicago")
                    );

                    Assertions.assertEquals(
                            0,
                            registry.getPendingWeight("Boston")
                    );

                    Assertions.assertEquals(
                            30,
                            registry.getPendingWeight("Chicago")
                    );
                }
        );
    }

    @Test
    @Order(13)
    void test_13_weightUpdateChangesPendingWeight() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    registry.registerPackage("p1", "Boston", 10);
                    registry.registerPackage("p2", "Boston", 15);

                    Assertions.assertEquals(
                            25,
                            registry.getPendingWeight("Boston")
                    );

                    Assertions.assertTrue(
                            registry.updateWeight("p1", 50)
                    );

                    Assertions.assertEquals(
                            65,
                            registry.getPendingWeight("Boston")
                    );
                }
        );
    }

    @Test
    @Order(14)
    void test_14_deleteRemovesPendingWeight() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    registry.registerPackage("p1", "Boston", 10);
                    registry.registerPackage("p2", "Boston", 20);

                    registry.deletePackage("p1");

                    Assertions.assertEquals(
                            20,
                            registry.getPendingWeight("Boston")
                    );
                }
        );
    }

    @Test
    @Order(15)
    void test_15_busiestDestinationsOrderByWeight() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    registry.registerPackage("p1", "Boston", 10);
                    registry.registerPackage("p2", "Boston", 20);

                    registry.registerPackage("p3", "Chicago", 40);

                    registry.registerPackage("p4", "Denver", 25);

                    Assertions.assertEquals(
                            java.util.List.of(
                                    "Chicago",
                                    "Boston",
                                    "Denver"
                            ),
                            registry.getBusiestDestinations(10)
                    );
                }
        );
    }

    @Test
    @Order(16)
    void test_16_busiestDestinationsAlphabeticalTie() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    registry.registerPackage("p1", "Seattle", 30);
                    registry.registerPackage("p2", "Boston", 30);
                    registry.registerPackage("p3", "Chicago", 30);

                    Assertions.assertEquals(
                            java.util.List.of(
                                    "Boston",
                                    "Chicago",
                                    "Seattle"
                            ),
                            registry.getBusiestDestinations(10)
                    );
                }
        );
    }

    @Test
    @Order(17)
    void test_17_multiplePackagesAggregateByDestination() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    registry.registerPackage("p1", "Boston", 10);
                    registry.registerPackage("p2", "Boston", 15);
                    registry.registerPackage("p3", "Boston", 5);

                    registry.registerPackage("p4", "Chicago", 25);

                    Assertions.assertEquals(
                            30,
                            registry.getPendingWeight("Boston")
                    );

                    Assertions.assertEquals(
                            java.util.List.of(
                                    "Boston",
                                    "Chicago"
                            ),
                            registry.getBusiestDestinations(10)
                    );
                }
        );
    }

    @Test
    @Order(18)
    void test_18_fullyDeliveredDestinationDisappears() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    registry.registerPackage("p1", "Boston", 10);
                    registry.registerPackage("p2", "Boston", 20);
                    registry.registerPackage("p3", "Chicago", 15);

                    registry.deliverPackage("p1");
                    registry.deliverPackage("p2");

                    Assertions.assertEquals(
                            0,
                            registry.getPendingWeight("Boston")
                    );

                    Assertions.assertEquals(
                            java.util.List.of("Chicago"),
                            registry.getBusiestDestinations(10)
                    );
                }
        );
    }

    @Test
    @Order(19)
    void test_19_deliveredPackageUpdateStillRejectedAndSummaryStable() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    registry.registerPackage("p1", "Boston", 20);
                    registry.deliverPackage("p1");

                    Assertions.assertFalse(
                            registry.updateDestination("p1", "Chicago")
                    );

                    Assertions.assertFalse(
                            registry.updateWeight("p1", 100)
                    );

                    Assertions.assertEquals(
                            0,
                            registry.getPendingWeight("Boston")
                    );

                    Assertions.assertEquals(
                            0,
                            registry.getPendingWeight("Chicago")
                    );

                    Assertions.assertEquals(
                            java.util.List.of(),
                            registry.getBusiestDestinations(10)
                    );
                }
        );
    }

    @Test
    @Order(20)
    void test_20_limitBusiestDestinations() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    registry.registerPackage("p1", "A", 50);
                    registry.registerPackage("p2", "B", 40);
                    registry.registerPackage("p3", "C", 30);
                    registry.registerPackage("p4", "D", 20);

                    Assertions.assertEquals(
                            java.util.List.of("A", "B"),
                            registry.getBusiestDestinations(2)
                    );
                }
        );
    }

    @Test
    @Order(21)
    void test_21_noPendingDestinations() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    registry.registerPackage("p1", "Boston", 10);
                    registry.deliverPackage("p1");

                    Assertions.assertEquals(
                            java.util.List.of(),
                            registry.getBusiestDestinations(10)
                    );
                }
        );
    }

    @Test
    @Order(22)
    void test_22_packageWithoutDependenciesIsReady() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    registry.registerPackage("a", "Boston", 10);

                    Assertions.assertTrue(
                            registry.isReadyForDelivery("a")
                    );

                    Assertions.assertFalse(
                            registry.isReadyForDelivery("missing")
                    );
                }
        );
    }

    @Test
    @Order(23)
    void test_23_allDependenciesMustBeDelivered() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    registry.registerPackage("a", "Boston", 10);
                    registry.registerPackage("b", "Boston", 20);
                    registry.registerPackage("c", "Boston", 30);

                    Assertions.assertTrue(
                            registry.addDependency("c", "a")
                    );

                    Assertions.assertTrue(
                            registry.addDependency("c", "b")
                    );

                    Assertions.assertFalse(
                            registry.isReadyForDelivery("c")
                    );

                    Assertions.assertTrue(
                            registry.deliverPackage("a")
                    );

                    Assertions.assertFalse(
                            registry.isReadyForDelivery("c")
                    );

                    Assertions.assertFalse(
                            registry.deliverPackage("c")
                    );

                    Assertions.assertTrue(
                            registry.deliverPackage("b")
                    );

                    Assertions.assertTrue(
                            registry.isReadyForDelivery("c")
                    );

                    Assertions.assertTrue(
                            registry.deliverPackage("c")
                    );
                }
        );
    }

    @Test
    @Order(24)
    void test_24_duplicateAndSelfDependencyRejected() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    registry.registerPackage("a", "Boston", 10);
                    registry.registerPackage("b", "Boston", 20);

                    Assertions.assertFalse(
                            registry.addDependency("a", "a")
                    );

                    Assertions.assertTrue(
                            registry.addDependency("a", "b")
                    );

                    Assertions.assertFalse(
                            registry.addDependency("a", "b")
                    );

                    Assertions.assertFalse(
                            registry.addDependency("missing", "b")
                    );

                    Assertions.assertFalse(
                            registry.addDependency("a", "missing")
                    );
                }
        );
    }

    @Test
    @Order(25)
    void test_25_removeDependencyMayMakePackageReady() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    registry.registerPackage("a", "Boston", 10);
                    registry.registerPackage("b", "Boston", 20);

                    registry.addDependency("a", "b");

                    Assertions.assertFalse(
                            registry.isReadyForDelivery("a")
                    );

                    Assertions.assertTrue(
                            registry.removeDependency("a", "b")
                    );

                    Assertions.assertTrue(
                            registry.isReadyForDelivery("a")
                    );

                    Assertions.assertFalse(
                            registry.removeDependency("a", "b")
                    );
                }
        );
    }

    @Test
    @Order(26)
    void test_26_deliveredPackageDependenciesCannotChange() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    registry.registerPackage("a", "Boston", 10);
                    registry.registerPackage("b", "Boston", 20);
                    registry.registerPackage("c", "Boston", 30);

                    registry.addDependency("a", "b");
                    registry.deliverPackage("b");
                    registry.deliverPackage("a");

                    Assertions.assertFalse(
                            registry.addDependency("a", "c")
                    );

                    Assertions.assertFalse(
                            registry.removeDependency("a", "b")
                    );

                    Assertions.assertTrue(
                            registry.isDelivered("a")
                    );
                }
        );
    }

    @Test
    @Order(27)
    void test_27_deleteDependencyRemovesReference() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    registry.registerPackage("a", "Boston", 10);
                    registry.registerPackage("b", "Boston", 20);

                    registry.addDependency("a", "b");

                    Assertions.assertFalse(
                            registry.isReadyForDelivery("a")
                    );

                    Assertions.assertTrue(
                            registry.deletePackage("b")
                    );

                    Assertions.assertTrue(
                            registry.isReadyForDelivery("a")
                    );

                    Assertions.assertTrue(
                            registry.deliverPackage("a")
                    );
                }
        );
    }

    @Test
    @Order(28)
    void test_28_deletePackageRemovesItsOwnDependencies() {
        Assertions.assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    registry.registerPackage("a", "Boston", 10);
                    registry.registerPackage("b", "Boston", 20);

                    registry.addDependency("a", "b");

                    registry.deletePackage("a");

                    Assertions.assertTrue(
                            registry.registerPackage(
                                    "a",
                                    "Chicago",
                                    5
                            )
                    );

                    Assertions.assertTrue(
                            registry.isReadyForDelivery("a")
                    );
                }
        );
    }

    @Test
    @Order(29)
    void test_29_returnMissingOrUndeliveredFails() {
        Assertions.assertFalse(registry.returnPackage("missing"));

        registry.registerPackage("a", "Boston", 10);

        Assertions.assertFalse(registry.returnPackage("a"));
        Assertions.assertFalse(registry.isDelivered("a"));
    }

    @Test
    @Order(30)
    void test_30_returnPreservesPackageState() {
        registry.registerPackage("a", "Boston", 25);

        registry.deliverPackage("a");

        Assertions.assertTrue(registry.returnPackage("a"));

        Assertions.assertEquals("Boston", registry.getDestination("a"));
        Assertions.assertEquals(25, registry.getWeight("a"));
        Assertions.assertFalse(registry.isDelivered("a"));
    }

    @Test
    @Order(31)
    void test_31_blockedOrdering() {
        registry.registerPackage("d1", "X", 1);
        registry.registerPackage("d2", "X", 1);
        registry.registerPackage("d3", "X", 1);

        registry.registerPackage("alpha", "X", 100);
        registry.registerPackage("beta", "X", 10);
        registry.registerPackage("gamma", "X", 50);

        registry.addDependency("alpha", "d1");

        registry.addDependency("beta", "d1");
        registry.addDependency("beta", "d2");
        registry.addDependency("beta", "d3");

        registry.addDependency("gamma", "d1");
        registry.addDependency("gamma", "d2");

        Assertions.assertEquals(
                java.util.List.of("beta", "gamma", "alpha"),
                registry.getBlockedPackages(10)
        );
    }

    @Test
    @Order(32)
    void test_32_deliveredPackageIsNotBlocked() {
        registry.registerPackage("dep", "X", 1);
        registry.registerPackage("a", "X", 10);

        registry.addDependency("a", "dep");

        // Make dependency deliverable first, then a.
        registry.deliverPackage("dep");
        registry.deliverPackage("a");

        // Return the dependency, so a's dependency is now undelivered.
        registry.returnPackage("dep");

        // a itself is delivered, therefore must not appear as blocked.
        Assertions.assertEquals(
                java.util.List.of(),
                registry.getBlockedPackages(10)
        );
    }

    @Test
    @Order(33)
    void test_33_limitBlockedPackages() {
        registry.registerPackage("dep", "X", 1);

        registry.registerPackage("a", "X", 30);
        registry.registerPackage("b", "X", 20);
        registry.registerPackage("c", "X", 10);

        registry.addDependency("a", "dep");
        registry.addDependency("b", "dep");
        registry.addDependency("c", "dep");

        Assertions.assertEquals(
                java.util.List.of("a", "b"),
                registry.getBlockedPackages(2)
        );
    }
}