package com.codesignal.klayvio.mock5;

import java.util.*;
import java.util.stream.Collectors;

class PackageRegistryImpl implements PackageRegistry {

    private final Map<String, Package> packageRegistrationMap = new HashMap<>();

    public PackageRegistryImpl() {
        // TODO
    }

    @Override
    public boolean registerPackage(String packageId, String destination, int weight) {

        if (packageRegistrationMap.containsKey(packageId)) {
            return false;
        }

        Package pack = packageRegistrationMap.get(packageId);
        if (pack != null) {
            return false;
        }

        Package package1 = new Package(packageId, destination, weight);
        packageRegistrationMap.put(packageId, package1);

        return true;
    }

    @Override
    public boolean updateDestination(String packageId, String newDestination) {

        if (!packageRegistrationMap.containsKey(packageId)) {
            return false;
        }

        Package pack = packageRegistrationMap.get(packageId);
        if (pack == null) {
            return false;
        }

        pack = packageRegistrationMap.get(packageId);
        if (pack.delivered) {
            return false;
        }

        pack.destination = newDestination;

        return true;
    }

    /**
     * Changes the weight of an existing package.
     *
     * Returns true only if:
     * - the package exists
     * - the package has NOT been delivered
     *
     * Returns false otherwise.
     *
     * A failed update must not modify the package.
     */
    @Override
    public boolean updateWeight(String packageId, int newWeight) {

        if (!packageRegistrationMap.containsKey(packageId)) {
            return false;
        }

        Package pack = packageRegistrationMap.get(packageId);
        if (pack == null) {
            return false;
        }

        pack = packageRegistrationMap.get(packageId);
        if (pack.delivered) {
            return false;
        }

        pack.weight = newWeight;

        return true;
    }

    /**
     * Marks an existing package as delivered.
     *
     * Returns true if:
     * - the package exists
     * - it was NOT already delivered
     *
     * Returns false if:
     * - the package does not exist
     * - the package was already delivered
     *
     * Calling this repeatedly must not otherwise modify the package.
     */
    @Override
    public boolean deliverPackage(String packageId) {

        if (!packageRegistrationMap.containsKey(packageId)) {
            return false;
        }

        Package pack = packageRegistrationMap.get(packageId);
        if (pack == null) {
            return false;
        }

        pack = packageRegistrationMap.get(packageId);
        if (pack.delivered) {
            return false;
        }

        for (String packId : pack.dependencyPackageIds) {
            if (packageRegistrationMap.get(packId) != null && !packageRegistrationMap.get(packId).delivered) {
                return false;
            }
        }

        pack.delivered = true;

        return true;
    }

    /**
     * Returns the package weight.
     *
     * Returns -1 if the package does not exist.
     */
    @Override
    public int getWeight(String packageId) {

        if (!packageRegistrationMap.containsKey(packageId)) {
            return -1;
        }


        return packageRegistrationMap.get(packageId).weight;
    }


    /**
     * Returns the current destination.
     *
     * Returns null if the package does not exist.
     */
    @Override
    public String getDestination(String packageId) {

        if (!packageRegistrationMap.containsKey(packageId)) {
            return null;
        }

        return packageRegistrationMap.get(packageId).destination;
    }

    /**
     * Returns true only if the package exists and has been delivered.
     *
     * Missing packages return false.
     */
    @Override
    public boolean isDelivered(String packageId) {

        if (!packageRegistrationMap.containsKey(packageId)) {
            return false;
        }

        return packageRegistrationMap.get(packageId).delivered;
    }

    /**
     * Deletes a package.
     *
     * Delivered and undelivered packages may both be deleted.
     *
     * Returns true if the package existed and was deleted.
     * Returns false otherwise.
     */
    @Override
    public boolean deletePackage(String packageId) {

        if (!packageRegistrationMap.containsKey(packageId)) {
            return false;
        }

        for (Map.Entry<String, Package> entry : packageRegistrationMap.entrySet()) {
            entry.getValue().dependencyPackageIds.remove(packageId);
        }

        packageRegistrationMap.remove(packageId);

        return true;
    }

    /**
     * Returns the total weight of all currently undelivered packages
     * whose current destination equals destination.
     *
     * Delivered packages do NOT contribute.
     *
     * Returns 0 if there are no qualifying packages.
     */
    public int getPendingWeight(String destination) {

        List<Package> undeliveredPackages = packageRegistrationMap.values().stream().filter(pack -> {
            if (pack.destination != null) {
                return pack.destination.equals(destination);
            } else {
                return false;
            }
        }).filter(pack -> !pack.delivered).collect(Collectors.toCollection(ArrayList::new));

        int sum = 0;
        for (Package pack :  undeliveredPackages) {
            sum+= pack.weight;
        }

        return sum;
    }

    /**
     * Returns up to `limit` destinations that currently have
     * at least one undelivered package.
     *
     * Ordering:
     * 1. total pending weight DESCENDING
     * 2. if total pending weight is equal,
     *    destination alphabetically ASCENDING
     *
     * Each destination appears at most once.
     *
     * Delivered packages do not contribute to pending weight
     * and do not cause a destination to appear.
     */
    public List<String> getBusiestDestinations(int limit) {

        Map<String, Integer> destByTotalWeightMap = packageRegistrationMap.values()
                .stream()
                .filter(p -> !p.delivered)
                .collect(Collectors.groupingBy(p -> p.destination, Collectors.summingInt(p -> p.weight)));

        return destByTotalWeightMap.entrySet()
                .stream()
                .sorted((p1, p2) -> {
                    int cmp = Integer.compare(p2.getValue(), p1.getValue());
                    if (cmp == 0) {
                        return p1.getKey().compareTo(p2.getKey());
                    }

                    return cmp;
                })
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();

    }

    /**
     * Adds dependencyPackageId as a delivery dependency of packageId.
     *
     * Returns true only if:
     * - both packages exist
     * - packageId and dependencyPackageId are different
     * - packageId has NOT been delivered
     * - the dependency does not already exist
     *
     * Returns false otherwise.
     *
     * Adding a dependency does NOT deliver either package.
     */
    @Override
    public boolean addDependency(String packageId, String dependencyPackageId) {

        if (packageId.equals(dependencyPackageId)) {
            return false;
        }

        if (!packageRegistrationMap.containsKey(packageId) || !packageRegistrationMap.containsKey(dependencyPackageId)) {
            return false;
        }

        Package pack = packageRegistrationMap.get(packageId);
        if (pack.delivered) {
            return false;
        }

        if (pack.dependencyPackageIds.contains(dependencyPackageId)) {
            return false;
        }

        pack.dependencyPackageIds.add(dependencyPackageId);


        return true;
    }

    /**
     * Removes an existing delivery dependency.
     *
     * Returns true only if:
     * - packageId exists
     * - packageId has NOT been delivered
     * - the specified dependency currently exists
     *
     * Returns false otherwise.
     */
    @Override
    public boolean removeDependency(String packageId, String dependencyPackageId) {

        if (!packageRegistrationMap.containsKey(packageId) || !packageRegistrationMap.containsKey(dependencyPackageId)) {
            return false;
        }

        Package pack = packageRegistrationMap.get(packageId);
        if (pack.delivered) {
            return false;
        }

        if (!pack.dependencyPackageIds.contains(dependencyPackageId)) {
            return false;
        }

        pack.dependencyPackageIds.remove(dependencyPackageId);

        return true;
    }

    /**
     * Returns true if packageId currently has no undelivered
     * dependencies.
     *
     * Returns false if packageId does not exist.
     *
     * A package with no dependencies is ready.
     *
     * The delivered/undelivered state of packageId itself does NOT
     * affect this method. This method answers only whether its
     * dependencies are satisfied.
     */
    @Override
    public boolean isReadyForDelivery(String packageId) {

        if (!packageRegistrationMap.containsKey(packageId)) {
            return false;
        }

        Package pack = packageRegistrationMap.get(packageId);

        if (pack.dependencyPackageIds.isEmpty()) {
            return true;
        }

        for (String packId : pack.dependencyPackageIds) {
            if (packageRegistrationMap.get(packId) != null && !packageRegistrationMap.get(packId).delivered) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns a delivered package back to the undelivered state.
     *
     * Returns true only if:
     * - the package exists
     * - the package is currently delivered
     *
     * On success:
     * - delivered becomes false
     * - destination remains unchanged
     * - weight remains unchanged
     * - dependencies remain unchanged
     *
     * Returns false otherwise.
     */
    public boolean returnPackage(String packageId) {


        if (!packageRegistrationMap.containsKey(packageId)) {
            return false;
        }

        Package pack = packageRegistrationMap.get(packageId);
        if (!pack.delivered) {
            return false;
        }

        pack.delivered = false;

        return true;
    }

    /**
     * Returns up to limit package IDs that are currently blocked
     * from delivery because at least one dependency is undelivered.
     *
     * Only undelivered packages are considered.
     *
     * Ordering:
     * 1. number of undelivered dependencies DESCENDING
     * 2. weight DESCENDING
     * 3. packageId alphabetically ASCENDING
     */
    @Override
    public List<String> getBlockedPackages(int limit) {

        List<Package> blockedPackages =
                packageRegistrationMap.values()
                        .stream()
                        .filter(p -> !p.delivered)
                        .filter(p -> countUndeliveredDependencies(p) > 0)
                        .toList();

        return blockedPackages.stream().sorted((p1, p2) -> {

                            int count1 = countUndeliveredDependencies(p1);
                            int count2 = countUndeliveredDependencies(p2);

                            int cmp = Integer.compare(count2, count1);
                            if (cmp == 0) {
                                cmp = Integer.compare(p2.weight, p1.weight);
                                if (cmp == 0) {
                                    return p1.packageId.compareTo(p2.packageId);
                                }
                            }

                            return cmp;
                })
                .map(p -> p.packageId)
                .limit(limit)
                .toList();
    }

    private int countUndeliveredDependencies(Package pack) {

        int undeliveredCount = 0;

        for (String packageId : pack.dependencyPackageIds) {
            Package p = packageRegistrationMap.get(packageId);
            if (p != null && !p.delivered) {
                undeliveredCount++;
            }
        }

        return undeliveredCount;
    }

    private static class Package {

        String packageId;
        String destination;
        int weight;
        boolean delivered;
        Set<String> dependencyPackageIds = new HashSet<>();

        Package(String packageId, String destination, int weight) {
            this.packageId = packageId;
            this.destination = destination;
            this.weight = weight;

        }
    }
}