package net.ripe.commons.ip;

import static junit.framework.Assert.*;
import org.junit.Test;

public class StartAndSizeComparatorTest {

    private static final Asn _10 = Asn.of(10l);
    private static final Asn _9 = _10.previous();
    private static final Asn _11 = _10.next();
    private static final Asn _100 = Asn.of(100l);
    private static final Asn _99 = _100.previous();
    private static final Asn _101 = _100.next();

    private StartAndSizeComparator<Asn, AsnRange> comparator = StartAndSizeComparator.getInstance();

    @Test
    public void testCompare() {
        // range      |----------|   [10, 100]
        // other      .|--------|.   [11, 99]
        // other      .|---------|   [11, 100]
        // other      .|----------|  [11, 101]
        // other     |----------|.   [9, 99]
        // other     |-----------|   [9, 100]
        // other     |------------|  [9, 101]
        // other      |---------|.   [10, 99]
        // other      |----------|   [10, 100]
        // other      |-----------|  [10, 101]
        AsnRange range = AsnRange.from(_10).to(_100);
        assertLessThan(range, AsnRange.from(_11).to(_99));
        assertLessThan(range, AsnRange.from(_11).to(_100));
        assertLessThan(range, AsnRange.from(_11).to(_101));
        assertGreaterThan(range, AsnRange.from(_9).to(_99));
        assertGreaterThan(range, AsnRange.from(_9).to(_100));
        assertGreaterThan(range, AsnRange.from(_9).to(_101));
        assertGreaterThan(range, AsnRange.from(_10).to(_99));
        assertEqual(range, AsnRange.from(_10).to(_100));
        assertLessThan(range, AsnRange.from(_10).to(_101));
    }

    private void assertLessThan(AsnRange range, AsnRange other) {
        assertTrue(comparator.compare(range, other) < 0);
    }

    private void assertGreaterThan(AsnRange range, AsnRange other) {
        assertTrue(comparator.compare(range, other) > 0);
    }

    private void assertEqual(AsnRange range, AsnRange other) {
        assertTrue(comparator.compare(range, other) == 0);
    }
}
