package com.codesignal.klayvio.actual;

import java.util.*;

class ParcelTrackingSystemImpl implements ParcelTrackingSystem {

    /*
     * parcelId
     *    ->
     * timestamp
     *    ->
     * attribute
     *    ->
     * AttributeValue(value, expiryTime)
     */
    private final Map<
            String,
            NavigableMap<Integer, Map<String, AttributeValue>>
            > parcelMap = new HashMap<>();


    public ParcelTrackingSystemImpl() {
    }


    /**
     * Sets or overwrites an attribute.
     * This version does not expire.
     */
    @Override
    public void setAttribute(
            int timestamp,
            String parcelId,
            String attribute,
            int value) {

        NavigableMap<Integer, Map<String, AttributeValue>> history =
                parcelMap.computeIfAbsent(
                        parcelId,
                        k -> new TreeMap<>()
                );

        Map.Entry<Integer, Map<String, AttributeValue>> previousEntry =
                history.floorEntry(timestamp);

        Map<String, AttributeValue> attributes = new HashMap<>();

        if (previousEntry != null) {
            attributes.putAll(previousEntry.getValue());
        }

        attributes.put(
                attribute,
                new AttributeValue(value, null)
        );

        history.put(timestamp, attributes);
    }


    /**
     * Returns the value of an attribute at the supplied timestamp.
     */
    @Override
    public Optional<Integer> getAttribute(
            int timestamp,
            String parcelId,
            String attribute) {

        NavigableMap<Integer, Map<String, AttributeValue>> history =
                parcelMap.get(parcelId);

        if (history == null) {
            return Optional.empty();
        }

        Map.Entry<Integer, Map<String, AttributeValue>> entry =
                history.floorEntry(timestamp);

        if (entry == null) {
            return Optional.empty();
        }

        AttributeValue attributeValue =
                entry.getValue().get(attribute);

        if (attributeValue == null) {
            return Optional.empty();
        }

        /*
         * TTL interval:
         *
         * [createdTimestamp, expiryTime)
         *
         * Therefore exactly at expiryTime the value
         * is considered expired.
         */
        if (isExpired(attributeValue, timestamp)) {
            return Optional.empty();
        }

        return Optional.of(attributeValue.value);
    }


    /**
     * Updates an attribute only when its current value matches
     * expectedValue.
     */
    @Override
    public boolean updateIfMatch(
            int timestamp,
            String parcelId,
            String attribute,
            int expectedValue,
            int newValue) {

        NavigableMap<Integer, Map<String, AttributeValue>> history =
                parcelMap.get(parcelId);

        if (history == null) {
            return false;
        }

        Map.Entry<Integer, Map<String, AttributeValue>> entry =
                history.floorEntry(timestamp);

        if (entry == null) {
            return false;
        }

        AttributeValue existingValue =
                entry.getValue().get(attribute);

        if (existingValue == null) {
            return false;
        }

        if (isExpired(existingValue, timestamp)) {
            return false;
        }

        if (existingValue.value != expectedValue) {
            return false;
        }

        Map<String, AttributeValue> newAttributes =
                new HashMap<>(entry.getValue());

        newAttributes.put(
                attribute,
                new AttributeValue(newValue, null)
        );

        history.put(timestamp, newAttributes);

        return true;
    }


    /**
     * Removes an attribute only when its current value matches
     * expectedValue.
     */
    @Override
    public boolean removeIfMatch(
            int timestamp,
            String parcelId,
            String attribute,
            int expectedValue) {

        NavigableMap<Integer, Map<String, AttributeValue>> history =
                parcelMap.get(parcelId);

        if (history == null) {
            return false;
        }

        Map.Entry<Integer, Map<String, AttributeValue>> entry =
                history.floorEntry(timestamp);

        if (entry == null) {
            return false;
        }

        AttributeValue existingValue =
                entry.getValue().get(attribute);

        if (existingValue == null) {
            return false;
        }

        if (isExpired(existingValue, timestamp)) {
            return false;
        }

        if (existingValue.value != expectedValue) {
            return false;
        }

        Map<String, AttributeValue> newAttributes =
                new HashMap<>(entry.getValue());

        newAttributes.remove(attribute);

        history.put(timestamp, newAttributes);

        return true;
    }


    /**
     * Returns all non-expired attributes for the parcel,
     * lexicographically sorted by attribute name.
     */
    @Override
    public List<String> listAttributes(
            int timestamp,
            String parcelId) {

        NavigableMap<Integer, Map<String, AttributeValue>> history =
                parcelMap.get(parcelId);

        if (history == null) {
            return Collections.emptyList();
        }

        Map.Entry<Integer, Map<String, AttributeValue>> entry =
                history.floorEntry(timestamp);

        if (entry == null) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>();

        for (Map.Entry<String, AttributeValue> attributeEntry
                : entry.getValue().entrySet()) {

            String attribute = attributeEntry.getKey();
            AttributeValue attributeValue = attributeEntry.getValue();

            if (isExpired(attributeValue, timestamp)) {
                continue;
            }

            result.add(
                    attribute
                            + "("
                            + attributeValue.value
                            + ")"
            );
        }

        Collections.sort(result);

        return result;
    }


    /**
     * Returns all non-expired attributes beginning with prefix,
     * lexicographically sorted by attribute name.
     */
    @Override
    public List<String> listAttributesByPrefix(
            int timestamp,
            String parcelId,
            String prefix) {

        NavigableMap<Integer, Map<String, AttributeValue>> history =
                parcelMap.get(parcelId);

        if (history == null) {
            return Collections.emptyList();
        }

        Map.Entry<Integer, Map<String, AttributeValue>> entry =
                history.floorEntry(timestamp);

        if (entry == null) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>();

        for (Map.Entry<String, AttributeValue> attributeEntry
                : entry.getValue().entrySet()) {

            String attribute = attributeEntry.getKey();
            AttributeValue attributeValue =
                    attributeEntry.getValue();

            if (!attribute.startsWith(prefix)) {
                continue;
            }

            if (isExpired(attributeValue, timestamp)) {
                continue;
            }

            result.add(
                    attribute
                            + "("
                            + attributeValue.value
                            + ")"
            );
        }

        Collections.sort(result);

        return result;
    }


    /**
     * Sets an attribute with a TTL.
     *
     * expiryTime = timestamp + ttl
     */
    @Override
    public void setAttributeWithExpiry(
            int timestamp,
            String parcelId,
            String attribute,
            int value,
            int ttl) {

        NavigableMap<Integer, Map<String, AttributeValue>> history =
                parcelMap.computeIfAbsent(
                        parcelId,
                        k -> new TreeMap<>()
                );

        Map.Entry<Integer, Map<String, AttributeValue>> previousEntry =
                history.floorEntry(timestamp);

        Map<String, AttributeValue> attributes =
                new HashMap<>();

        if (previousEntry != null) {
            attributes.putAll(previousEntry.getValue());
        }

        attributes.put(
                attribute,
                new AttributeValue(
                        value,
                        timestamp + ttl
                )
        );

        history.put(timestamp, attributes);
    }


    /**
     * Conditional update with a new TTL.
     */
    @Override
    public boolean updateIfMatchWithExpiry(
            int timestamp,
            String parcelId,
            String attribute,
            int expectedValue,
            int newValue,
            int ttl) {

        NavigableMap<Integer, Map<String, AttributeValue>> history =
                parcelMap.get(parcelId);

        if (history == null) {
            return false;
        }

        Map.Entry<Integer, Map<String, AttributeValue>> entry =
                history.floorEntry(timestamp);

        if (entry == null) {
            return false;
        }

        AttributeValue existingValue =
                entry.getValue().get(attribute);

        if (existingValue == null) {
            return false;
        }

        if (isExpired(existingValue, timestamp)) {
            return false;
        }

        if (existingValue.value != expectedValue) {
            return false;
        }

        Map<String, AttributeValue> newAttributes =
                new HashMap<>(entry.getValue());

        newAttributes.put(
                attribute,
                new AttributeValue(
                        newValue,
                        timestamp + ttl
                )
        );

        history.put(timestamp, newAttributes);

        return true;
    }


    /**
     * Returns what the attribute's value was at atTimestamp.
     *
     * timestamp is the time of the query operation.
     * atTimestamp is the historical time being queried.
     */
    @Override
    public Optional<Integer> getAttributeAt(
            int timestamp,
            String parcelId,
            String attribute,
            int atTimestamp) {

        NavigableMap<Integer, Map<String, AttributeValue>> history =
                parcelMap.get(parcelId);

        if (history == null) {
            return Optional.empty();
        }

        Map.Entry<Integer, Map<String, AttributeValue>> entry =
                history.floorEntry(atTimestamp);

        if (entry == null) {
            return Optional.empty();
        }

        AttributeValue attributeValue =
                entry.getValue().get(attribute);

        if (attributeValue == null) {
            return Optional.empty();
        }

        /*
         * We are asking whether this value existed at
         * atTimestamp, so expiration must also be checked
         * against atTimestamp.
         */
        if (isExpired(attributeValue, atTimestamp)) {
            return Optional.empty();
        }

        return Optional.of(attributeValue.value);
    }


    /**
     * Helper for TTL checking.
     *
     * null expiryTime means that the attribute never expires.
     */
    private boolean isExpired(
            AttributeValue attributeValue,
            int timestamp) {

        return attributeValue.expiryTime != null
                && timestamp >= attributeValue.expiryTime;
    }


    /**
     * Internal representation of an attribute.
     *
     * Level 1 effectively only needed value.
     * Level 3 introduced expiryTime.
     */
    private static class AttributeValue {

        int value;
        Integer expiryTime;

        AttributeValue(
                int value,
                Integer expiryTime) {

            this.value = value;
            this.expiryTime = expiryTime;
        }

        @Override
        public String toString() {
            return "AttributeValue{" +
                    "value=" + value +
                    ", expiryTime=" + expiryTime +
                    '}';
        }
    }
}