package com.codesignal.klayvio.streampractice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PackageStreamPractice {

    public static void main(String[] args) {

        List<Package> packages = createPackages();

        // We will call each exercise method from here
        // one by one as we build them.

        System.out.println(getUndeliveredPackages(packages));
        System.out.println(getUndeliveredPackageIds(packages));
        System.out.println(getUndeliveredPackagesByWeight(packages));
        System.out.println(getUndeliveredPackageIdsByWeight(packages));
        System.out.println(getThreeLightestUndeliveredPackageIds(packages));
        System.out.println(getThreeHeaviestUndeliveredPackageIds(packages));
        System.out.println(getUndeliveredPackageIdsByWeightAndId(packages));
        System.out.println(countUndeliveredPackages(packages));
        System.out.println(countUndeliveredPackagesForBoston(packages));
        System.out.println(getTotalPackageWeight(packages));
        System.out.println(getTotalUndeliveredPackageWeight(packages));
        System.out.println(getPendingWeightForBoston(packages));
        System.out.println(groupPackagesByDestination(packages));
        System.out.println(groupUndeliveredPackagesByDestination(packages));
        System.out.println(getPendingWeightByDestination(packages));
        System.out.println(getWeightByDeliveryStatus(packages));
        System.out.println(getDeliveredWeightByDestination(packages));
        System.out.println(getWeightByCategory(packages));

        Map<String, Integer> pendingWeight = new HashMap<>();

        pendingWeight.put("Boston", 30);
        pendingWeight.put("Denver", 50);
        pendingWeight.put("Chicago", 25);
        pendingWeight.put("Miami", 25);
        pendingWeight.put("Seattle", 15);

        System.out.println(sortDestinationsByWeight(pendingWeight));
        System.out.println(countPackagesByDestination(packages));
        System.out.println(countUndeliveredPackagesByDestination(packages));
        System.out.println(getBusiestDestinationsByPackageCount(packages));
    }

    private static List<Package> createPackages() {

        List<Package> packages = new ArrayList<>();

        packages.add(new Package("p1", "Boston", 10, false));
        packages.add(new Package("p2", "Chicago", 30, true));
        packages.add(new Package("p3", "Boston", 20, false));
        packages.add(new Package("p4", "Denver", 15, false));
        packages.add(new Package("p5", "Chicago", 25, false));
        packages.add(new Package("p6", "Boston", 40, true));
        packages.add(new Package("p7", "Denver", 35, false));
        packages.add(new Package("p8", "Miami", 25, false));
        packages.add(new Package("p9", "Seattle", 25, false));

        return packages;
    }

    // Exercise 1:
    // Return only undelivered packages.
    private static List<Package> getUndeliveredPackages(List<Package> packages) {

        return packages.stream().filter(p -> !p.delivered).collect(Collectors.toCollection(ArrayList::new));

    }

    // Exercise 2:
    // Return the package IDs of all undelivered packages.
    private static List<String> getUndeliveredPackageIds(List<Package> packages) {

        return packages
                .stream()
                .filter(p -> !p.delivered)
                .map(k -> k.packageId)
                .collect(Collectors.toCollection(ArrayList::new));

    }

    // Exercise 3:
    // Return undelivered packages sorted by weight ASCENDING.
    private static List<Package> getUndeliveredPackagesByWeight(List<Package> packages) {

        return packages.stream().filter(pack -> !pack.delivered).sorted((p1, p2) -> {
            return Integer.compare(p1.weight, p2.weight);
        }).collect(Collectors.toCollection(ArrayList::new));

    }

    // Exercise 4:
    // Return package IDs of undelivered packages,
    // sorted by package weight ASCENDING.
    private static List<String> getUndeliveredPackageIdsByWeight(List<Package> packages) {

        return packages.stream()
                .filter(pack -> !pack.delivered)
                .sorted((p1, p2) -> {
                    return Integer.compare(p1.weight, p2.weight);
                }).map(pack -> pack.packageId)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // Exercise 5:
    // Return IDs of the 3 lightest undelivered packages.
    private static List<String> getThreeLightestUndeliveredPackageIds(List<Package> packages) {

        return packages.stream()
                .filter(p -> !p.delivered)
                .sorted((p1, p2) -> {
                    return Integer.compare(p1.weight, p2.weight);
                })
                .limit(3)
                .map(p -> p.packageId)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // Exercise 6:
    // Return IDs of the 3 heaviest undelivered packages.
    private static List<String> getThreeHeaviestUndeliveredPackageIds(List<Package> packages) {

        return packages.stream()
                .filter(pack -> !pack.delivered)
                .sorted((p1, p2) -> {
                    return Integer.compare(p2.weight, p1.weight);
                })
                .limit(3)
                .map(pack -> pack.packageId)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // Exercise 7:
    // Return IDs of undelivered packages sorted by:
    // 1. weight DESCENDING
    // 2. packageId ASCENDING when weights are equal
    private static List<String> getUndeliveredPackageIdsByWeightAndId(List<Package> packages) {

        return packages.stream()
                .filter(p -> !p.delivered)
                .sorted((p1, p2) -> {
                    int cmp = Integer.compare(p2.weight, p1.weight);
                    if (cmp == 0) {
                        return p1.packageId.compareTo(p2.packageId);
                    }
                    return cmp;
                }).map(p -> p.packageId)
                .toList();
    }

    // Exercise 8:
    // Return the number of undelivered packages.
    private static long countUndeliveredPackages(List<Package> packages) {

        return packages.stream().filter(p -> !p.delivered).count();
    }

    // Exercise 9:
    // Return the number of undelivered packages
    // whose destination is Boston.
    private static long countUndeliveredPackagesForBoston(List<Package> packages) {
        return packages.stream().filter(p -> !p.delivered).filter(p -> p.destination.equals("Boston")).count();
    }

    // Exercise 10:
    // Return the total weight of all packages.
    private static int getTotalPackageWeight(List<Package> packages) {
        return packages.stream().mapToInt(p -> p.weight).sum();
    }

    // Exercise 11:
    // Return the total weight of all undelivered packages.
    private static int getTotalUndeliveredPackageWeight(List<Package> packages) {
        return packages.stream().filter(p -> !p.delivered).mapToInt(p -> p.weight).sum();
    }

    // Exercise 12:
    // Return the total weight of undelivered packages
    // whose destination is Boston.
    private static int getPendingWeightForBoston(List<Package> packages) {
        return packages
                .stream()
                .filter(p -> !p.delivered && "Boston".equals(p.destination))
                .mapToInt(p -> p.weight)
                .sum();
    }

    // Exercise 13:
    // Group all packages by destination.
    private static Map<String, List<Package>> groupPackagesByDestination(List<Package> packages) {
        return packages.stream().collect(Collectors.groupingBy(p -> p.destination));
    }

    // Exercise 14:
    // Group only undelivered packages by destination.
    private static Map<String, List<Package>> groupUndeliveredPackagesByDestination(List<Package> packages) {
        return packages.stream().filter(p -> !p.delivered).collect(Collectors.groupingBy( p -> p.destination));
    }

    // Exercise 15:
    // For undelivered packages, return the total
    // package weight grouped by destination.
    private static Map<String, Integer> getPendingWeightByDestination(List<Package> packages) {
        return packages
                .stream()
                .filter(p -> !p.delivered)
                .collect(Collectors.groupingBy(p -> p.destination, Collectors.summingInt(p -> p.weight)));
    }

    // Exercise 16:
    // Return total package weight grouped by delivery status.
    private static Map<Boolean, Integer> getWeightByDeliveryStatus(List<Package> packages) {
        return packages.stream().collect(Collectors.groupingBy(p -> p.delivered, Collectors.summingInt(p -> p.weight)));
    }

    // Exercise 17:
    // For delivered packages only,
    // return total weight grouped by destination.
    private static Map<String, Integer> getDeliveredWeightByDestination(List<Package> packages) {
        return packages
                .stream()
                .filter(p -> p.delivered)
                .collect(Collectors.groupingBy(p -> p.destination, Collectors.summingInt(p -> p.weight)));
    }

    // Exercise 18:
    // Group packages into LIGHT (< 25) and HEAVY (>= 25),
    // and return the total weight of each group.
    private static Map<String, Integer> getWeightByCategory(List<Package> packages) {
        return packages
                .stream()
                .collect(Collectors.groupingBy(p -> p.weight < 25 ? "LIGHT" : "HEAVY", Collectors.summingInt(p -> p.weight)));
    }

    // Exercise 19:
    // Given destination -> total pending weight,
    // return destinations sorted by weight DESCENDING.
    private static List<String> sortDestinationsByWeight(Map<String, Integer> pendingWeight) {

        return pendingWeight.entrySet().stream().sorted((p1, p2) -> {
            int cmp = Integer.compare(p2.getValue(), p1.getValue());
            if (cmp == 0) {
                return p1.getKey().compareTo(p2.getKey());
            }
            return cmp;
        }).map(p -> p.getKey()).toList();
    }

    // Exercise 20:
    // Return package count grouped by destination.
    private static Map<String, Long> countPackagesByDestination(List<Package> packages) {
        return packages
                .stream()
                .collect(Collectors.groupingBy(p -> p.destination, Collectors.counting()));
    }

    // Exercise 21:
    // Count undelivered packages grouped by destination.
    private static Map<String, Long> countUndeliveredPackagesByDestination(List<Package> packages) {
        return packages
                .stream()
                .filter(p -> !p.delivered)
                .collect(Collectors.groupingBy(p -> p.destination, Collectors.counting()));
    }

    // Exercise 22:
    // Return destinations ordered by:
    // 1. number of undelivered packages DESCENDING
    // 2. destination ASCENDING when counts are equal
    private static List<String> getBusiestDestinationsByPackageCount(List<Package> packages) {
        return packages
                .stream()
                .filter(p -> !p.delivered)
                .collect(Collectors.groupingBy(p -> p.destination, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted((p1, p2) -> {
                    int cmp = Long.compare(p2.getValue(), p1.getValue());
                    if (cmp == 0) {
                        return p1.getKey().compareTo(p2.getKey());
                    }
                    return cmp;
                }).map(p -> p.getKey())
                .toList();
    }

    private static class Package {

        String packageId;
        String destination;
        int weight;
        boolean delivered;

        Package(
                String packageId,
                String destination,
                int weight,
                boolean delivered) {

            this.packageId = packageId;
            this.destination = destination;
            this.weight = weight;
            this.delivered = delivered;
        }

        @Override
        public String toString() {
            return "Package{" +
                    "packageId='" + packageId + '\'' +
                    ", destination='" + destination + '\'' +
                    ", weight=" + weight +
                    ", delivered=" + delivered +
                    '}';
        }
    }
}