package com.codesignal.klayvio.mock2;

import java.util.List;

public interface Wallet {

    /**
     * Creates an account with the given accountId.
     *
     * A newly created account starts with balance 0.
     *
     * Returns true if the account was created.
     * Returns false if the account already exists.
     */
    default boolean createAccount(String accountId) {
        return false;
    }

    /**
     * Deposits amount into an existing account.
     *
     * Returns the new account balance.
     *
     * If the account does not exist, returns -1.
     */
    default int deposit(String accountId, int amount) {
        return -1;
    }

    /**
     * Withdraws amount from an existing account.
     *
     * The withdrawal succeeds only if the account has
     * at least the requested amount.
     *
     * Returns true on success.
     * Returns false if:
     * - the account does not exist
     * - the account has insufficient funds
     *
     * A failed withdrawal must not modify the balance.
     */
    default boolean withdraw(String accountId, int amount) {
        return false;
    }

    /**
     * Returns the current balance.
     *
     * Returns -1 if the account does not exist.
     */
    default int getBalance(String accountId) {
        return -1;
    }

    /**
     * Returns up to n account IDs ordered by account activity.
     *
     * Activity is the sum of amounts from all successful:
     * - deposits
     * - withdrawals
     *
     * Ordering:
     * 1. Higher activity first.
     * 2. If activity is equal, accountId alphabetically ascending.
     *
     * Accounts with zero activity ARE included.
     *
     * If fewer than n accounts exist, return all accounts.
     */
    default List<String> getMostActiveAccounts(int n) {
        return List.of();
    }

    /**
     * Transfers amount from sourceAccountId to targetAccountId.
     *
     * A transfer succeeds only if:
     * - both accounts exist
     * - sourceAccountId and targetAccountId are different
     * - the source account has at least amount available
     *
     * On success:
     * - decrease the source balance by amount
     * - increase the target balance by amount
     * - increase BOTH accounts' activity by amount
     * - return true
     *
     * On failure:
     * - do not modify either balance
     * - do not modify either account's activity
     * - return false
     */
    default boolean transfer(
            String sourceAccountId,
            String targetAccountId,
            int amount) {
        return false;
    }

    /**
     * Schedules a transfer for later execution.
     *
     * Returns a unique transferId.
     *
     * Scheduling succeeds only if:
     * - both accounts currently exist
     * - sourceAccountId and targetAccountId are different
     *
     * Scheduling does NOT:
     * - change either balance
     * - change either account's activity
     *
     * The source account is NOT required to currently have enough funds.
     *
     * transferId values must be unique and stable.
     */
    default String scheduleTransfer(
            String sourceAccountId,
            String targetAccountId,
            int amount) {
        return null;
    }

    /**
     * Attempts to execute a previously scheduled transfer.
     *
     * Execution succeeds only if:
     * - transferId exists
     * - it has not already been successfully executed
     * - the source account currently has enough funds
     *
     * On successful execution:
     * - decrease source balance by amount
     * - increase target balance by amount
     * - increase BOTH accounts' activity by amount
     * - mark this scheduled transfer as executed
     * - return true
     *
     * On failure:
     * - do not modify balances
     * - do not modify activity
     * - do not mark the transfer as executed
     * - return false
     *
     * A failed execution may be retried later.
     */
    default boolean executeScheduledTransfer(String transferId) {
        return false;
    }
}