package com.codesignal.klayvio.actual;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ParcelTrackingSystemTest {

    private ParcelTrackingSystem system;

    @BeforeEach
    void setUp() {
        system = new ParcelTrackingSystemImpl();
    }


    // =========================================================
    // LEVEL 1
    // Basic set / get / CAS / remove
    // =========================================================

    @Test
    void setAndGetAttribute() {

        system.setAttribute(
                1,
                "parcel1",
                "weight",
                100
        );

        assertEquals(
                Optional.of(100),
                system.getAttribute(
                        1,
                        "parcel1",
                        "weight"
                )
        );
    }


    @Test
    void getMissingParcelReturnsEmpty() {

        assertEquals(
                Optional.empty(),
                system.getAttribute(
                        1,
                        "missing",
                        "weight"
                )
        );
    }


    @Test
    void getMissingAttributeReturnsEmpty() {

        system.setAttribute(
                1,
                "parcel1",
                "weight",
                100
        );

        assertEquals(
                Optional.empty(),
                system.getAttribute(
                        2,
                        "parcel1",
                        "height"
                )
        );
    }


    @Test
    void setOverwritesAttribute() {

        system.setAttribute(
                1,
                "parcel1",
                "weight",
                100
        );

        system.setAttribute(
                5,
                "parcel1",
                "weight",
                200
        );

        assertEquals(
                Optional.of(200),
                system.getAttribute(
                        5,
                        "parcel1",
                        "weight"
                )
        );
    }


    @Test
    void differentParcelsRemainIndependent() {

        system.setAttribute(
                1,
                "parcel1",
                "weight",
                100
        );

        system.setAttribute(
                2,
                "parcel2",
                "weight",
                500
        );

        assertEquals(
                Optional.of(100),
                system.getAttribute(
                        3,
                        "parcel1",
                        "weight"
                )
        );

        assertEquals(
                Optional.of(500),
                system.getAttribute(
                        3,
                        "parcel2",
                        "weight"
                )
        );
    }


    @Test
    void updateIfMatchSucceeds() {

        system.setAttribute(
                1,
                "parcel1",
                "weight",
                100
        );

        assertTrue(
                system.updateIfMatch(
                        5,
                        "parcel1",
                        "weight",
                        100,
                        200
                )
        );

        assertEquals(
                Optional.of(200),
                system.getAttribute(
                        5,
                        "parcel1",
                        "weight"
                )
        );
    }


    @Test
    void updateIfMatchFailsWhenExpectedValueIsWrong() {

        system.setAttribute(
                1,
                "parcel1",
                "weight",
                100
        );

        assertFalse(
                system.updateIfMatch(
                        5,
                        "parcel1",
                        "weight",
                        999,
                        200
                )
        );

        assertEquals(
                Optional.of(100),
                system.getAttribute(
                        5,
                        "parcel1",
                        "weight"
                )
        );
    }


    @Test
    void removeIfMatchSucceeds() {

        system.setAttribute(
                1,
                "parcel1",
                "weight",
                100
        );

        assertTrue(
                system.removeIfMatch(
                        5,
                        "parcel1",
                        "weight",
                        100
                )
        );

        assertEquals(
                Optional.empty(),
                system.getAttribute(
                        5,
                        "parcel1",
                        "weight"
                )
        );
    }


    @Test
    void removeIfMatchFailsWhenValueDoesNotMatch() {

        system.setAttribute(
                1,
                "parcel1",
                "weight",
                100
        );

        assertFalse(
                system.removeIfMatch(
                        5,
                        "parcel1",
                        "weight",
                        999
                )
        );

        assertEquals(
                Optional.of(100),
                system.getAttribute(
                        5,
                        "parcel1",
                        "weight"
                )
        );
    }


    // =========================================================
    // LEVEL 2
    // Scan / sorting / prefix
    // =========================================================

    @Test
    void listAttributesSortedLexicographically() {

        system.setAttribute(1, "parcel1", "weight", 100);
        system.setAttribute(2, "parcel1", "age", 20);
        system.setAttribute(3, "parcel1", "article", 30);

        assertEquals(
                List.of(
                        "age(20)",
                        "article(30)",
                        "weight(100)"
                ),
                system.listAttributes(
                        4,
                        "parcel1"
                )
        );
    }


    @Test
    void listAttributesReturnsEmptyForMissingParcel() {

        assertEquals(
                List.of(),
                system.listAttributes(
                        10,
                        "missing"
                )
        );
    }


    @Test
    void listAttributesByPrefix() {

        system.setAttribute(1, "parcel1", "flower", 230);
        system.setAttribute(2, "parcel1", "floors", 8);
        system.setAttribute(3, "parcel1", "food", 100);
        system.setAttribute(4, "parcel1", "age", 20);

        assertEquals(
                List.of(
                        "floors(8)",
                        "flower(230)"
                ),
                system.listAttributesByPrefix(
                        5,
                        "parcel1",
                        "flo"
                )
        );
    }


    @Test
    void listByPrefixReturnsEmptyWhenNothingMatches() {

        system.setAttribute(
                1,
                "parcel1",
                "weight",
                100
        );

        assertEquals(
                List.of(),
                system.listAttributesByPrefix(
                        2,
                        "parcel1",
                        "abc"
                )
        );
    }


    // =========================================================
    // LEVEL 3
    // TTL
    // =========================================================

    @Test
    void attributeWithExpiryExistsBeforeExpiry() {

        system.setAttributeWithExpiry(
                10,
                "parcel1",
                "weight",
                100,
                20
        );

        assertEquals(
                Optional.of(100),
                system.getAttribute(
                        29,
                        "parcel1",
                        "weight"
                )
        );
    }


    @Test
    void attributeExpiresExactlyAtTimestampPlusTtl() {

        system.setAttributeWithExpiry(
                10,
                "parcel1",
                "weight",
                100,
                20
        );

        /*
         * Valid interval:
         *
         * [10, 30)
         *
         * At 30 it is expired.
         */

        assertEquals(
                Optional.empty(),
                system.getAttribute(
                        30,
                        "parcel1",
                        "weight"
                )
        );
    }


    @Test
    void expiredAttributeDoesNotAppearInList() {

        system.setAttributeWithExpiry(
                10,
                "parcel1",
                "weight",
                100,
                5
        );

        system.setAttribute(
                11,
                "parcel1",
                "height",
                200
        );

        assertEquals(
                List.of(
                        "height(200)"
                ),
                system.listAttributes(
                        20,
                        "parcel1"
                )
        );
    }


    @Test
    void expiredAttributeDoesNotAppearInPrefixScan() {

        system.setAttributeWithExpiry(
                10,
                "parcel1",
                "field1",
                100,
                5
        );

        system.setAttribute(
                11,
                "parcel1",
                "field2",
                200
        );

        assertEquals(
                List.of(
                        "field2(200)"
                ),
                system.listAttributesByPrefix(
                        20,
                        "parcel1",
                        "field"
                )
        );
    }


    @Test
    void updateIfMatchFailsOnExpiredAttribute() {

        system.setAttributeWithExpiry(
                10,
                "parcel1",
                "weight",
                100,
                5
        );

        assertFalse(
                system.updateIfMatch(
                        20,
                        "parcel1",
                        "weight",
                        100,
                        200
                )
        );
    }


    @Test
    void removeIfMatchFailsOnExpiredAttribute() {

        system.setAttributeWithExpiry(
                10,
                "parcel1",
                "weight",
                100,
                5
        );

        assertFalse(
                system.removeIfMatch(
                        20,
                        "parcel1",
                        "weight",
                        100
                )
        );
    }


    @Test
    void updateIfMatchWithExpirySucceeds() {

        system.setAttributeWithExpiry(
                10,
                "parcel1",
                "weight",
                100,
                50
        );

        assertTrue(
                system.updateIfMatchWithExpiry(
                        20,
                        "parcel1",
                        "weight",
                        100,
                        200,
                        10
                )
        );

        assertEquals(
                Optional.of(200),
                system.getAttribute(
                        29,
                        "parcel1",
                        "weight"
                )
        );
    }


    @Test
    void updateIfMatchWithExpiryCreatesNewExpiry() {

        system.setAttributeWithExpiry(
                10,
                "parcel1",
                "weight",
                100,
                100
        );

        system.updateIfMatchWithExpiry(
                20,
                "parcel1",
                "weight",
                100,
                200,
                10
        );

        /*
         * New TTL starts at 20.
         *
         * New expiry = 30.
         */

        assertEquals(
                Optional.of(200),
                system.getAttribute(
                        29,
                        "parcel1",
                        "weight"
                )
        );

        assertEquals(
                Optional.empty(),
                system.getAttribute(
                        30,
                        "parcel1",
                        "weight"
                )
        );
    }


    @Test
    void updateIfMatchWithExpiryFailsWhenExpectedDoesNotMatch() {

        system.setAttributeWithExpiry(
                10,
                "parcel1",
                "weight",
                100,
                100
        );

        assertFalse(
                system.updateIfMatchWithExpiry(
                        20,
                        "parcel1",
                        "weight",
                        999,
                        200,
                        10
                )
        );

        assertEquals(
                Optional.of(100),
                system.getAttribute(
                        20,
                        "parcel1",
                        "weight"
                )
        );
    }


    // =========================================================
    // LEVEL 4
    // Historical lookup
    // =========================================================

    @Test
    void getHistoricalValue() {

        system.setAttribute(
                10,
                "parcel1",
                "weight",
                100
        );

        system.setAttribute(
                20,
                "parcel1",
                "weight",
                200
        );

        system.setAttribute(
                30,
                "parcel1",
                "weight",
                300
        );

        assertEquals(
                Optional.of(100),
                system.getAttributeAt(
                        100,
                        "parcel1",
                        "weight",
                        15
                )
        );

        assertEquals(
                Optional.of(200),
                system.getAttributeAt(
                        100,
                        "parcel1",
                        "weight",
                        25
                )
        );

        assertEquals(
                Optional.of(300),
                system.getAttributeAt(
                        100,
                        "parcel1",
                        "weight",
                        35
                )
        );
    }


    @Test
    void getHistoricalValueBeforeParcelExists() {

        system.setAttribute(
                10,
                "parcel1",
                "weight",
                100
        );

        assertEquals(
                Optional.empty(),
                system.getAttributeAt(
                        100,
                        "parcel1",
                        "weight",
                        5
                )
        );
    }


    @Test
    void historicalLookupRespectsTtl() {

        system.setAttributeWithExpiry(
                10,
                "parcel1",
                "weight",
                100,
                10
        );

        // Historical time 19: still alive
        assertEquals(
                Optional.of(100),
                system.getAttributeAt(
                        100,
                        "parcel1",
                        "weight",
                        19
                )
        );

        // Historical time 20: expired
        assertEquals(
                Optional.empty(),
                system.getAttributeAt(
                        100,
                        "parcel1",
                        "weight",
                        20
                )
        );
    }


    @Test
    void historicalLookupAfterUpdateReturnsOldValue() {

        system.setAttribute(
                10,
                "parcel1",
                "weight",
                100
        );

        system.updateIfMatch(
                20,
                "parcel1",
                "weight",
                100,
                200
        );

        assertEquals(
                Optional.of(100),
                system.getAttributeAt(
                        100,
                        "parcel1",
                        "weight",
                        15
                )
        );

        assertEquals(
                Optional.of(200),
                system.getAttributeAt(
                        100,
                        "parcel1",
                        "weight",
                        25
                )
        );
    }


    @Test
    void historicalLookupAfterDeleteDoesNotDestroyEarlierHistory() {

        system.setAttribute(
                10,
                "parcel1",
                "weight",
                100
        );

        system.removeIfMatch(
                20,
                "parcel1",
                "weight",
                100
        );

        assertEquals(
                Optional.of(100),
                system.getAttributeAt(
                        100,
                        "parcel1",
                        "weight",
                        15
                )
        );

        assertEquals(
                Optional.empty(),
                system.getAttributeAt(
                        100,
                        "parcel1",
                        "weight",
                        25
                )
        );
    }


    // =========================================================
    // IMPORTANT EDGE CASES
    // =========================================================

    @Test
    void sameAttributeCanExistOnDifferentParcels() {

        system.setAttribute(
                10,
                "parcel1",
                "status",
                1
        );

        system.setAttribute(
                10,
                "parcel2",
                "status",
                2
        );

        assertEquals(
                Optional.of(1),
                system.getAttribute(
                        10,
                        "parcel1",
                        "status"
                )
        );

        assertEquals(
                Optional.of(2),
                system.getAttribute(
                        10,
                        "parcel2",
                        "status"
                )
        );
    }


    @Test
    void multipleAttributesSurviveLaterChanges() {

        system.setAttribute(
                10,
                "parcel1",
                "weight",
                100
        );

        system.setAttribute(
                20,
                "parcel1",
                "height",
                50
        );

        system.setAttribute(
                30,
                "parcel1",
                "weight",
                200
        );

        assertEquals(
                List.of(
                        "height(50)",
                        "weight(200)"
                ),
                system.listAttributes(
                        30,
                        "parcel1"
                )
        );
    }
}