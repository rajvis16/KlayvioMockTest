package com.codesignal.klayvio.mock2;

import java.time.Duration;

import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WalletTests {

    private Wallet wallet;

    @BeforeEach
    public void setUp() {
        wallet = new WalletImpl();
    }

    /**
     * Multiple deposits must accumulate.
     */
    @Test
    @Order(1)
    void test_01_multipleDeposits() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            Assertions.assertTrue(wallet.createAccount("a"));

            Assertions.assertEquals(10, wallet.deposit("a", 10));
            Assertions.assertEquals(25, wallet.deposit("a", 15));
            Assertions.assertEquals(30, wallet.deposit("a", 5));

            Assertions.assertEquals(30, wallet.getBalance("a"));
        });
    }

    /**
     * Exact-balance withdrawal is allowed.
     */
    @Test
    @Order(2)
    void test_02_exactWithdrawal() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("a");
            wallet.deposit("a", 50);

            Assertions.assertTrue(wallet.withdraw("a", 50));
            Assertions.assertEquals(0, wallet.getBalance("a"));
        });
    }

    /**
     * Insufficient funds must not modify state.
     */
    @Test
    @Order(3)
    void test_03_insufficientFundsPreservesBalance() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("a");
            wallet.deposit("a", 30);

            Assertions.assertFalse(wallet.withdraw("a", 31));
            Assertions.assertEquals(30, wallet.getBalance("a"));

            Assertions.assertTrue(wallet.withdraw("a", 30));
            Assertions.assertEquals(0, wallet.getBalance("a"));
        });
    }

    /**
     * Accounts must remain independent.
     */
    @Test
    @Order(4)
    void test_04_multipleAccounts() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("alice");
            wallet.createAccount("bob");
            wallet.createAccount("carol");

            wallet.deposit("alice", 100);
            wallet.deposit("bob", 50);
            wallet.deposit("carol", 75);

            wallet.withdraw("alice", 30);
            wallet.withdraw("carol", 25);

            Assertions.assertEquals(70, wallet.getBalance("alice"));
            Assertions.assertEquals(50, wallet.getBalance("bob"));
            Assertions.assertEquals(50, wallet.getBalance("carol"));
        });
    }

    /**
     * Failed operations against missing accounts must not
     * accidentally create those accounts.
     */
    @Test
    @Order(5)
    void test_05_missingOperationsDoNotCreateAccount() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            Assertions.assertEquals(-1, wallet.deposit("ghost", 10));
            Assertions.assertFalse(wallet.withdraw("ghost", 5));
            Assertions.assertEquals(-1, wallet.getBalance("ghost"));

            Assertions.assertTrue(wallet.createAccount("ghost"));
            Assertions.assertEquals(0, wallet.getBalance("ghost"));
        });
    }

    /**
     * Repeated operations on the same account.
     */
    @Test
    @Order(6)
    void test_06_repeatedOperations() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("x");

            for (int i = 0; i < 20; i++) {
                Assertions.assertEquals(
                        (i + 1) * 5,
                        wallet.deposit("x", 5)
                );
            }

            for (int i = 0; i < 10; i++) {
                Assertions.assertTrue(wallet.withdraw("x", 3));
            }

            Assertions.assertEquals(70, wallet.getBalance("x"));
        });
    }

    /**
     * Activity accumulates independently from current balance.
     *
     * alice:
     * deposit 100  -> activity 100
     * withdraw 100 -> activity 200
     * balance 0
     *
     * bob:
     * deposit 150 -> activity 150
     * balance 150
     *
     * alice must rank first despite having lower balance.
     */
    @Test
    @Order(7)
    void test_07_activityIsNotBalance() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("alice");
            wallet.createAccount("bob");

            wallet.deposit("alice", 100);
            wallet.withdraw("alice", 100);

            wallet.deposit("bob", 150);

            Assertions.assertEquals(
                    java.util.List.of("alice", "bob"),
                    wallet.getMostActiveAccounts(2)
            );

            Assertions.assertEquals(0, wallet.getBalance("alice"));
            Assertions.assertEquals(150, wallet.getBalance("bob"));
        });
    }

    /**
     * Failed withdrawals must NOT increase activity.
     */
    @Test
    @Order(8)
    void test_08_failedWithdrawalDoesNotCount() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("alice");
            wallet.createAccount("bob");

            wallet.deposit("alice", 50);   // activity 50

            wallet.deposit("bob", 40);     // activity 40
            Assertions.assertFalse(wallet.withdraw("bob", 100));

            Assertions.assertEquals(
                    java.util.List.of("alice", "bob"),
                    wallet.getMostActiveAccounts(2)
            );
        });
    }

    /**
     * Operations on missing accounts must not create activity
     * or create accounts.
     */
    @Test
    @Order(9)
    void test_09_missingOperationsDoNotAffectRanking() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("alice");
            wallet.deposit("alice", 10);

            Assertions.assertEquals(-1, wallet.deposit("ghost", 1000));
            Assertions.assertFalse(wallet.withdraw("ghost", 500));

            Assertions.assertEquals(
                    java.util.List.of("alice"),
                    wallet.getMostActiveAccounts(10)
            );

            Assertions.assertEquals(-1, wallet.getBalance("ghost"));
        });
    }

    /**
     * Ties are resolved alphabetically by accountId.
     */
    @Test
    @Order(10)
    void test_10_activityTieBreaking() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("charlie");
            wallet.createAccount("alice");
            wallet.createAccount("bob");

            wallet.deposit("charlie", 50);
            wallet.deposit("alice", 50);
            wallet.deposit("bob", 50);

            Assertions.assertEquals(
                    java.util.List.of("alice", "bob", "charlie"),
                    wallet.getMostActiveAccounts(3)
            );
        });
    }

    /**
     * Accounts with zero activity are still included.
     */
    @Test
    @Order(11)
    void test_11_zeroActivityAccountsIncluded() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("charlie");
            wallet.createAccount("alice");
            wallet.createAccount("bob");

            wallet.deposit("bob", 10);

            Assertions.assertEquals(
                    java.util.List.of("bob", "alice", "charlie"),
                    wallet.getMostActiveAccounts(10)
            );
        });
    }

    /**
     * n limits the returned result.
     */
    @Test
    @Order(12)
    void test_12_limitResults() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("a");
            wallet.createAccount("b");
            wallet.createAccount("c");
            wallet.createAccount("d");

            wallet.deposit("a", 10);
            wallet.deposit("b", 40);
            wallet.deposit("c", 30);
            wallet.deposit("d", 20);

            Assertions.assertEquals(
                    java.util.List.of("b", "c"),
                    wallet.getMostActiveAccounts(2)
            );
        });
    }

    /**
     * Repeated successful withdrawals all contribute to activity.
     *
     * x:
     * deposit 100 -> activity 100
     * 10 withdrawals of 3 -> activity 130
     *
     * y:
     * deposit 120 -> activity 120
     *
     * x ranks first.
     */
    @Test
    @Order(13)
    void test_13_repeatedActivity() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("x");
            wallet.createAccount("y");

            wallet.deposit("x", 100);

            for (int i = 0; i < 10; i++) {
                Assertions.assertTrue(wallet.withdraw("x", 3));
            }

            wallet.deposit("y", 120);

            Assertions.assertEquals(
                    java.util.List.of("x", "y"),
                    wallet.getMostActiveAccounts(2)
            );

            // Level 1 state must still be correct.
            Assertions.assertEquals(70, wallet.getBalance("x"));
            Assertions.assertEquals(120, wallet.getBalance("y"));
        });
    }

    /**
     * Activity must accumulate TRANSACTION AMOUNTS,
     * not resulting balances.
     *
     * alice:
     * deposit 100 -> activity 100
     * deposit 50  -> activity 150
     *
     * bob:
     * deposit 200 -> activity 200
     *
     * Therefore bob must rank before alice.
     */
    @Test
    @Order(14)
    void test_14_multipleDepositsUseTransactionAmount() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("alice");
            wallet.createAccount("bob");

            wallet.deposit("alice", 100);
            wallet.deposit("alice", 50);

            wallet.deposit("bob", 200);

            Assertions.assertEquals(
                    java.util.List.of("bob", "alice"),
                    wallet.getMostActiveAccounts(2)
            );
        });
    }

    /**
     * Exact-balance transfer is allowed.
     */
    @Test
    @Order(15)
    void test_15_exactBalanceTransfer() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("a");
            wallet.createAccount("b");

            wallet.deposit("a", 50);

            Assertions.assertTrue(wallet.transfer("a", "b", 50));

            Assertions.assertEquals(0, wallet.getBalance("a"));
            Assertions.assertEquals(50, wallet.getBalance("b"));
        });
    }

    /**
     * Insufficient funds must preserve BOTH balances.
     */
    @Test
    @Order(16)
    void test_16_failedTransferPreservesBalances() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("a");
            wallet.createAccount("b");

            wallet.deposit("a", 30);
            wallet.deposit("b", 10);

            Assertions.assertFalse(wallet.transfer("a", "b", 31));

            Assertions.assertEquals(30, wallet.getBalance("a"));
            Assertions.assertEquals(10, wallet.getBalance("b"));
        });
    }

    /**
     * A missing source account causes failure.
     * No account should be created implicitly.
     */
    @Test
    @Order(17)
    void test_17_missingSource() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("b");

            Assertions.assertFalse(
                    wallet.transfer("ghost", "b", 10)
            );

            Assertions.assertEquals(-1, wallet.getBalance("ghost"));
            Assertions.assertEquals(0, wallet.getBalance("b"));
        });
    }

    /**
     * A missing target account causes failure.
     * The source balance must remain unchanged.
     */
    @Test
    @Order(18)
    void test_18_missingTarget() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("a");
            wallet.deposit("a", 40);

            Assertions.assertFalse(
                    wallet.transfer("a", "ghost", 10)
            );

            Assertions.assertEquals(40, wallet.getBalance("a"));
            Assertions.assertEquals(-1, wallet.getBalance("ghost"));
        });
    }

    /**
     * Transferring to the same account is not allowed.
     */
    @Test
    @Order(19)
    void test_19_sameAccountTransferFails() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("a");
            wallet.deposit("a", 100);

            Assertions.assertFalse(
                    wallet.transfer("a", "a", 25)
            );

            Assertions.assertEquals(100, wallet.getBalance("a"));
        });
    }

    /**
     * Successful transfer contributes to activity for BOTH accounts.
     *
     * alice:
     * deposit 100   -> activity 100
     * transfer 40   -> activity 140
     *
     * bob:
     * deposit 50    -> activity 50
     * receive 40    -> activity 90
     *
     * charlie:
     * deposit 120   -> activity 120
     *
     * ranking:
     * alice   140
     * charlie 120
     * bob      90
     */
    @Test
    @Order(20)
    void test_20_transferUpdatesBothActivities() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("alice");
            wallet.createAccount("bob");
            wallet.createAccount("charlie");

            wallet.deposit("alice", 100);
            wallet.deposit("bob", 50);
            wallet.deposit("charlie", 120);

            Assertions.assertTrue(
                    wallet.transfer("alice", "bob", 40)
            );

            Assertions.assertEquals(
                    java.util.List.of("alice", "charlie", "bob"),
                    wallet.getMostActiveAccounts(3)
            );
        });
    }

    /**
     * Failed transfer must NOT affect activity.
     *
     * alice activity = 50
     * bob activity   = 40
     *
     * Failed transfer of 100 from alice to bob.
     *
     * Ranking must remain alice, bob.
     */
    @Test
    @Order(21)
    void test_21_failedTransferDoesNotAffectActivity() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("alice");
            wallet.createAccount("bob");

            wallet.deposit("alice", 50);
            wallet.deposit("bob", 40);

            Assertions.assertFalse(
                    wallet.transfer("alice", "bob", 100)
            );

            Assertions.assertEquals(
                    java.util.List.of("alice", "bob"),
                    wallet.getMostActiveAccounts(2)
            );
        });
    }

    /**
     * Repeated transfers must accumulate correctly.
     *
     * a:
     * deposit 100 -> activity 100
     * send 10     -> 110
     * send 20     -> 130
     * send 30     -> 160
     *
     * b:
     * receives 10 + 20 + 30 -> activity 60
     */
    @Test
    @Order(22)
    void test_22_repeatedTransfers() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("a");
            wallet.createAccount("b");

            wallet.deposit("a", 100);

            Assertions.assertTrue(wallet.transfer("a", "b", 10));
            Assertions.assertTrue(wallet.transfer("a", "b", 20));
            Assertions.assertTrue(wallet.transfer("a", "b", 30));

            Assertions.assertEquals(40, wallet.getBalance("a"));
            Assertions.assertEquals(60, wallet.getBalance("b"));

            Assertions.assertEquals(
                    java.util.List.of("a", "b"),
                    wallet.getMostActiveAccounts(2)
            );
        });
    }

    /**
     * Transfer + later withdrawal must preserve all Level 1/2 behavior.
     *
     * a:
     * deposit 100
     * transfer 40 to b
     * withdraw 10
     *
     * balance = 50
     *
     * b:
     * receives 40
     * withdraw 15
     *
     * balance = 25
     */
    @Test
    @Order(23)
    void test_23_transferInteractsWithExistingOperations() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("a");
            wallet.createAccount("b");

            wallet.deposit("a", 100);

            Assertions.assertTrue(wallet.transfer("a", "b", 40));

            Assertions.assertTrue(wallet.withdraw("a", 10));
            Assertions.assertTrue(wallet.withdraw("b", 15));

            Assertions.assertEquals(50, wallet.getBalance("a"));
            Assertions.assertEquals(25, wallet.getBalance("b"));
        });
    }

    /**
     * Scheduling does not require enough funds immediately.
     *
     * a starts with balance 10.
     * Schedule 50 succeeds.
     *
     * First execution fails.
     * Deposit 50.
     * Retry succeeds.
     */
    @Test
    @Order(24)
    void test_24_failedExecutionCanBeRetried() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("a");
            wallet.createAccount("b");

            wallet.deposit("a", 10);

            String transferId =
                    wallet.scheduleTransfer("a", "b", 50);

            Assertions.assertNotNull(transferId);

            Assertions.assertFalse(
                    wallet.executeScheduledTransfer(transferId)
            );

            Assertions.assertEquals(10, wallet.getBalance("a"));
            Assertions.assertEquals(0, wallet.getBalance("b"));

            wallet.deposit("a", 50);

            Assertions.assertTrue(
                    wallet.executeScheduledTransfer(transferId)
            );

            Assertions.assertEquals(10, wallet.getBalance("a"));
            Assertions.assertEquals(50, wallet.getBalance("b"));
        });
    }

    /**
     * A successfully executed transfer cannot execute twice.
     */
    @Test
    @Order(25)
    void test_25_successfulTransferCannotExecuteTwice() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("a");
            wallet.createAccount("b");

            wallet.deposit("a", 100);

            String transferId =
                    wallet.scheduleTransfer("a", "b", 30);

            Assertions.assertTrue(
                    wallet.executeScheduledTransfer(transferId)
            );

            Assertions.assertFalse(
                    wallet.executeScheduledTransfer(transferId)
            );

            Assertions.assertEquals(70, wallet.getBalance("a"));
            Assertions.assertEquals(30, wallet.getBalance("b"));
        });
    }

    /**
     * Unknown transfer IDs fail safely.
     */
    @Test
    @Order(26)
    void test_26_unknownTransferId() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("a");
            wallet.deposit("a", 100);

            Assertions.assertFalse(
                    wallet.executeScheduledTransfer("does-not-exist")
            );

            Assertions.assertEquals(100, wallet.getBalance("a"));
        });
    }

    /**
     * Cannot schedule from a missing source.
     */
    @Test
    @Order(27)
    void test_27_missingSourceCannotSchedule() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("b");

            Assertions.assertNull(
                    wallet.scheduleTransfer("ghost", "b", 10)
            );
        });
    }

    /**
     * Cannot schedule to a missing target.
     */
    @Test
    @Order(28)
    void test_28_missingTargetCannotSchedule() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("a");

            Assertions.assertNull(
                    wallet.scheduleTransfer("a", "ghost", 10)
            );
        });
    }

    /**
     * Cannot schedule a transfer to the same account.
     */
    @Test
    @Order(29)
    void test_29_sameAccountCannotSchedule() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("a");

            Assertions.assertNull(
                    wallet.scheduleTransfer("a", "a", 10)
            );
        });
    }

    /**
     * Scheduled transfer execution contributes to activity
     * for BOTH accounts.
     *
     * alice:
     * deposit 100 -> activity 100
     * execute 40  -> activity 140
     *
     * bob:
     * deposit 60  -> activity 60
     * receive 40  -> activity 100
     *
     * charlie:
     * deposit 120 -> activity 120
     *
     * ranking:
     * alice   140
     * charlie 120
     * bob     100
     */
    @Test
    @Order(30)
    void test_30_scheduledExecutionUpdatesActivity() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("alice");
            wallet.createAccount("bob");
            wallet.createAccount("charlie");

            wallet.deposit("alice", 100);
            wallet.deposit("bob", 60);
            wallet.deposit("charlie", 120);

            String transferId =
                    wallet.scheduleTransfer("alice", "bob", 40);

            Assertions.assertTrue(
                    wallet.executeScheduledTransfer(transferId)
            );

            Assertions.assertEquals(
                    java.util.List.of("alice", "charlie", "bob"),
                    wallet.getMostActiveAccounts(3)
            );
        });
    }

    /**
     * A failed scheduled execution must not affect activity.
     *
     * alice activity = 20
     * bob activity   = 10
     *
     * Scheduled transfer of 50 fails due to insufficient funds.
     *
     * Ranking must remain unchanged.
     */
    @Test
    @Order(31)
    void test_31_failedExecutionDoesNotAffectActivity() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("alice");
            wallet.createAccount("bob");

            wallet.deposit("alice", 20);
            wallet.deposit("bob", 10);

            String transferId =
                    wallet.scheduleTransfer("alice", "bob", 50);

            Assertions.assertFalse(
                    wallet.executeScheduledTransfer(transferId)
            );

            Assertions.assertEquals(
                    java.util.List.of("alice", "bob"),
                    wallet.getMostActiveAccounts(2)
            );
        });
    }

    /**
     * Multiple scheduled transfers must remain independent.
     *
     * a = 100
     *
     * schedule:
     * t1: a -> b 20
     * t2: a -> c 30
     *
     * Execute t2 first, then t1.
     *
     * Final:
     * a = 50
     * b = 20
     * c = 30
     */
    @Test
    @Order(32)
    void test_32_multipleScheduledTransfersIndependent() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("a");
            wallet.createAccount("b");
            wallet.createAccount("c");

            wallet.deposit("a", 100);

            String t1 =
                    wallet.scheduleTransfer("a", "b", 20);

            String t2 =
                    wallet.scheduleTransfer("a", "c", 30);

            Assertions.assertNotNull(t1);
            Assertions.assertNotNull(t2);
            Assertions.assertNotEquals(t1, t2);

            Assertions.assertTrue(
                    wallet.executeScheduledTransfer(t2)
            );

            Assertions.assertTrue(
                    wallet.executeScheduledTransfer(t1)
            );

            Assertions.assertEquals(50, wallet.getBalance("a"));
            Assertions.assertEquals(20, wallet.getBalance("b"));
            Assertions.assertEquals(30, wallet.getBalance("c"));
        });
    }

    /**
     * Repeated failed executions remain retryable.
     *
     * a starts with 5.
     * Scheduled transfer needs 20.
     *
     * Fail twice.
     * Deposit 15.
     * Then succeed.
     */
    @Test
    @Order(33)
    void test_33_repeatedFailuresRemainRetryable() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("a");
            wallet.createAccount("b");

            wallet.deposit("a", 5);

            String transferId =
                    wallet.scheduleTransfer("a", "b", 20);

            Assertions.assertFalse(
                    wallet.executeScheduledTransfer(transferId)
            );

            Assertions.assertFalse(
                    wallet.executeScheduledTransfer(transferId)
            );

            wallet.deposit("a", 15);

            Assertions.assertTrue(
                    wallet.executeScheduledTransfer(transferId)
            );

            Assertions.assertEquals(0, wallet.getBalance("a"));
            Assertions.assertEquals(20, wallet.getBalance("b"));
        });
    }

    /**
     * Existing direct transfer behavior must remain unchanged.
     */
    @Test
    @Order(34)
    void test_34_directTransferStillWorks() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            wallet.createAccount("a");
            wallet.createAccount("b");

            wallet.deposit("a", 100);

            Assertions.assertTrue(
                    wallet.transfer("a", "b", 25)
            );

            Assertions.assertEquals(75, wallet.getBalance("a"));
            Assertions.assertEquals(25, wallet.getBalance("b"));
        });
    }
}