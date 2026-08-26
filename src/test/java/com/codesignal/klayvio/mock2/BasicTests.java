package com.codesignal.klayvio.mock2;

import java.time.Duration;

import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BasicTests {

    private Wallet wallet;

    @BeforeEach
    public void setUp() {
        wallet = new WalletImpl();
    }

    /**
     * Create alice -> balance 0
     * Deposit 100 -> balance 100
     * Withdraw 40 -> balance 60
     */
    @Test
    @Order(1)
    void test_basic1() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            Assertions.assertTrue(wallet.createAccount("alice"));
            Assertions.assertEquals(0, wallet.getBalance("alice"));

            Assertions.assertEquals(100, wallet.deposit("alice", 100));
            Assertions.assertTrue(wallet.withdraw("alice", 40));

            Assertions.assertEquals(60, wallet.getBalance("alice"));
        });
    }

    /**
     * Creating the same account twice is not allowed.
     * The original account must remain unchanged.
     */
    @Test
    @Order(2)
    void test_basic2() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            Assertions.assertTrue(wallet.createAccount("bob"));
            Assertions.assertFalse(wallet.createAccount("bob"));

            Assertions.assertEquals(0, wallet.getBalance("bob"));
        });
    }

    /**
     * Missing accounts:
     * - getBalance returns -1
     * - deposit returns -1
     * - withdraw returns false
     */
    @Test
    @Order(3)
    void test_basic3() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            Assertions.assertEquals(-1, wallet.getBalance("missing"));
            Assertions.assertEquals(-1, wallet.deposit("missing", 50));
            Assertions.assertFalse(wallet.withdraw("missing", 10));
        });
    }

    /**
     * alice:
     * deposit 100
     * withdraw 30
     * activity = 130
     *
     * bob:
     * deposit 80
     * activity = 80
     *
     * Result -> [alice, bob]
     */
    @Test
    @Order(4)
    void test_basic4_activityRanking() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("alice");
            wallet.createAccount("bob");

            wallet.deposit("alice", 100);
            wallet.withdraw("alice", 30);

            wallet.deposit("bob", 80);

            Assertions.assertEquals(
                    java.util.List.of("alice", "bob"),
                    wallet.getMostActiveAccounts(2)
            );
        });
    }

    /**
     * alice = 100
     * bob   = 20
     *
     * transfer alice -> bob 40
     *
     * alice = 60
     * bob   = 60
     *
     * Both accounts gain 40 activity.
     */
    @Test
    @Order(5)
    void test_basic5_transfer() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("alice");
            wallet.createAccount("bob");

            wallet.deposit("alice", 100);
            wallet.deposit("bob", 20);

            Assertions.assertTrue(
                    wallet.transfer("alice", "bob", 40)
            );

            Assertions.assertEquals(60, wallet.getBalance("alice"));
            Assertions.assertEquals(60, wallet.getBalance("bob"));
        });
    }

    /**
     * Schedule alice -> bob 40.
     *
     * Scheduling itself changes nothing.
     *
     * Execute:
     * alice 100 -> 60
     * bob    20 -> 60
     */
    @Test
    @Order(6)
    void test_basic6_scheduledTransfer() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("alice");
            wallet.createAccount("bob");

            wallet.deposit("alice", 100);
            wallet.deposit("bob", 20);

            String transferId =
                    wallet.scheduleTransfer("alice", "bob", 40);

            Assertions.assertNotNull(transferId);

            Assertions.assertEquals(100, wallet.getBalance("alice"));
            Assertions.assertEquals(20, wallet.getBalance("bob"));

            Assertions.assertTrue(
                    wallet.executeScheduledTransfer(transferId)
            );

            Assertions.assertEquals(60, wallet.getBalance("alice"));
            Assertions.assertEquals(60, wallet.getBalance("bob"));
        });
    }
}