package com.codesignal.klayvio.actual;

import java.util.List;
import java.util.Optional;

public interface ParcelTrackingSystem {

    /**
     * Sets or overwrites an attribute for the given parcel.
     */
    void setAttribute(
            int timestamp,
            String parcelId,
            String attribute,
            int value
    );

    /**
     * Returns the value of the attribute for the parcel
     * as of the given timestamp.
     *
     * Returns Optional.empty() if the parcel or attribute
     * does not exist.
     */
    Optional<Integer> getAttribute(
            int timestamp,
            String parcelId,
            String attribute
    );

    /**
     * Updates the attribute only if its current value
     * equals expectedValue.
     *
     * Returns true if updated, otherwise false.
     */
    boolean updateIfMatch(
            int timestamp,
            String parcelId,
            String attribute,
            int expectedValue,
            int newValue
    );

    /**
     * Removes the attribute only if its current value
     * equals expectedValue.
     *
     * Returns true if removed, otherwise false.
     */
    boolean removeIfMatch(
            int timestamp,
            String parcelId,
            String attribute,
            int expectedValue
    );

    /**
     * Returns all attribute-value pairs for the parcel,
     * sorted lexicographically by attribute name.
     *
     * Format:
     *
     * attribute(value)
     */
    List<String> listAttributes(
            int timestamp,
            String parcelId
    );

    /**
     * Returns all attribute-value pairs whose attribute
     * starts with prefix.
     *
     * Results are sorted lexicographically by attribute name.
     */
    List<String> listAttributesByPrefix(
            int timestamp,
            String parcelId,
            String prefix
    );

    /**
     * Sets an attribute with a TTL.
     *
     * The value exists during:
     *
     * [timestamp, timestamp + ttl)
     */
    void setAttributeWithExpiry(
            int timestamp,
            String parcelId,
            String attribute,
            int value,
            int ttl
    );

    /**
     * Updates the attribute only if its current value equals
     * expectedValue, and assigns the new value a new TTL.
     */
    boolean updateIfMatchWithExpiry(
            int timestamp,
            String parcelId,
            String attribute,
            int expectedValue,
            int newValue,
            int ttl
    );

    /**
     * Returns the historical value of an attribute at
     * atTimestamp.
     *
     * timestamp represents when the query is being performed.
     * atTimestamp represents the historical point being queried.
     */
    Optional<Integer> getAttributeAt(
            int timestamp,
            String parcelId,
            String attribute,
            int atTimestamp
    );
}