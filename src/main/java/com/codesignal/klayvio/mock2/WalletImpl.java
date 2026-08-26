package com.codesignal.klayvio.mock2;

import java.util.*;

class WalletImpl implements Wallet {

    private int incrementNoBy = 1;

    private final Map<String, Integer> accountWithBalanceMap = new HashMap<>();
    private final Map<String, Integer> activityMap = new HashMap<>();
    private final Map<String, ScheduleTransfer> scheduleTransferMap = new HashMap<>();

    public WalletImpl() {
    }

    @Override
    public boolean createAccount(String accountId) {
        if (accountWithBalanceMap.containsKey(accountId)) {
            return false;
        }

        accountWithBalanceMap.put(accountId, 0);
        activityMap.put(accountId, 0);

        return true;
    }

    @Override
    public int deposit(String accountId, int amount) {
        if (!accountWithBalanceMap.containsKey(accountId)) {
            return -1;
        }

        int currentDeposit = accountWithBalanceMap.get(accountId);
        int newBalance = currentDeposit + amount;

        accountWithBalanceMap.put(accountId, newBalance);
        activityMap.put(accountId, activityMap.getOrDefault(accountId, 0) + amount);

        return newBalance;

    }

    @Override
    public boolean withdraw(String accountId, int amount) {

        if (!accountWithBalanceMap.containsKey(accountId)) {
            return false;
        }

        int currentBalance = accountWithBalanceMap.get(accountId);
        if (currentBalance < amount) {
            return false;
        }

        int newBalance = currentBalance - amount;
        accountWithBalanceMap.put(accountId, newBalance);
        activityMap.put(accountId, activityMap.getOrDefault(accountId, 0) + amount);

        return true;
    }

    @Override
    public int getBalance(String accountId) {

        if (!accountWithBalanceMap.containsKey(accountId)) {
            return -1;
        }

        return accountWithBalanceMap.get(accountId);
    }

    @Override
    public List<String> getMostActiveAccounts(int n) {

        List<Map.Entry<String, Integer>> list = new ArrayList<>(activityMap.entrySet());

        Collections.sort(list, (a1, a2) -> {
            int cmp = Integer.compare(a2.getValue(), a1.getValue());
            if (cmp == 0) {
                return a1.getKey().compareTo(a2.getKey());
            }

            return cmp;
        });

        int size = n < list.size() ? n : list.size();

        List<String> mostActiveAccounts = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < size; i++) {
            mostActiveAccounts.add(list.get(count).getKey());
            count++;
        }


        return mostActiveAccounts;
    }

    @Override
    public boolean transfer(String sourceAccountId, String targetAccountId, int amount) {

        if (sourceAccountId.equals(targetAccountId)) {
            return false;
        }

        if (!accountWithBalanceMap.containsKey(sourceAccountId) || !accountWithBalanceMap.containsKey(targetAccountId)) {
            return false;
        }

        int currentBalanceForSource = accountWithBalanceMap.get(sourceAccountId);
        if (currentBalanceForSource < amount) {
            return false;
        }

        int newBalanceForSource = currentBalanceForSource - amount;
        accountWithBalanceMap.put(sourceAccountId, newBalanceForSource);
        activityMap.put(sourceAccountId, activityMap.getOrDefault(sourceAccountId, 0) + amount);

        int currentBalanceForTarget = accountWithBalanceMap.get(targetAccountId);

        int newBalanceForTarget = currentBalanceForTarget + amount;
        accountWithBalanceMap.put(targetAccountId, newBalanceForTarget);
        activityMap.put(targetAccountId, activityMap.getOrDefault(targetAccountId, 0) + amount);

        return true;
    }

    @Override
    public String scheduleTransfer(String sourceAccountId, String targetAccountId, int amount) {

        if (sourceAccountId.equals(targetAccountId)) {
            return null;
        }

        if (!accountWithBalanceMap.containsKey(sourceAccountId) || !accountWithBalanceMap.containsKey(targetAccountId)) {
            return null;
        }

        String transferId = "transfer"+ (incrementNoBy++);

        scheduleTransferMap.put(transferId, new ScheduleTransfer(sourceAccountId, targetAccountId, amount));

        return transferId;
    }

    @Override
    public boolean executeScheduledTransfer(String transferId) {


        if (!scheduleTransferMap.containsKey(transferId)) {
            return false;
        }

        ScheduleTransfer scheduleTransfer = scheduleTransferMap.get(transferId);
        if (scheduleTransfer.executed) {
            return false;
        }

        if (transfer(scheduleTransfer.sourceAccountId, scheduleTransfer.targetAccountId, scheduleTransfer.amount)) {
            scheduleTransfer.executed = true;
            return true;
        }

        return false;
    }

    private static class ScheduleTransfer {

        String sourceAccountId;
        String targetAccountId;
        int amount;
        boolean executed;

        ScheduleTransfer(String sourceAccountId, String targetAccountId, int amount) {
            this.sourceAccountId = sourceAccountId;
            this.targetAccountId = targetAccountId;
            this.amount = amount;
        }

    }
}