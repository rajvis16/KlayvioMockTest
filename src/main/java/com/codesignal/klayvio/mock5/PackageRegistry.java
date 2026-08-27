package com.codesignal.klayvio.mock5;

import java.util.List;

public interface PackageRegistry {

    /**
     * Registers a package.
     *
     * A newly registered package:
     * - has the supplied weight
     * - has the supplied destination
     * - is NOT delivered
     *
     * Returns true if the package was registered.
     *
     * Returns false if packageId already exists.
     *
     * A failed registration must not modify the existing package.
     */
    default boolean registerPackage(
            String packageId,
            String destination,
            int weight) {
        return false;
    }

    /**
     * Changes the destination of an existing package.
     *
     * Returns true only if:
     * - the package exists
     * - the package has NOT been delivered
     *
     * Returns false otherwise.
     *
     * A failed update must not modify the package.
     */
    default boolean updateDestination(
            String packageId,
            String newDestination) {
        return false;
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
    default boolean updateWeight(
            String packageId,
            int newWeight) {
        return false;
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
    default boolean deliverPackage(String packageId) {
        return false;
    }

    /**
     * Returns the package weight.
     *
     * Returns -1 if the package does not exist.
     */
    default int getWeight(String packageId) {
        return -1;
    }

    /**
     * Returns the current destination.
     *
     * Returns null if the package does not exist.
     */
    default String getDestination(String packageId) {
        return null;
    }

    /**
     * Returns true only if the package exists and has been delivered.
     *
     * Missing packages return false.
     */
    default boolean isDelivered(String packageId) {
        return false;
    }

    /**
     * Deletes a package.
     *
     * Delivered and undelivered packages may both be deleted.
     *
     * Returns true if the package existed and was deleted.
     * Returns false otherwise.
     */
    default boolean deletePackage(String packageId) {
        return false;
    }

    /**
     * Returns the total weight of all currently undelivered packages
     * whose current destination equals destination.
     *
     * Delivered packages do NOT contribute.
     *
     * Returns 0 if there are no qualifying packages.
     */
    default int getPendingWeight(String destination) {
        return 0;
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
    default List<String> getBusiestDestinations(int limit) {
        return List.of();
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
    default boolean addDependency(
            String packageId,
            String dependencyPackageId) {
        return false;
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
    default boolean removeDependency(
            String packageId,
            String dependencyPackageId) {
        return false;
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
    default boolean isReadyForDelivery(String packageId) {
        return false;
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
    default boolean returnPackage(String packageId) {
        return false;
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
    default List<String> getBlockedPackages(int limit) {
        return List.of();
    }
}