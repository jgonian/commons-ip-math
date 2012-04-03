package net.ripe.commons.ip.range;

import static junit.framework.Assert.*;
import org.junit.Test;

public abstract class AbstractRangeTest<C extends Comparable<C>, R extends AbstractRange<C, R>> {


    protected abstract C from(String s);
    protected abstract C to(String s);
    protected abstract C item(String s);
    protected abstract R getTestRange(C start, C end);

    @Test
    public abstract void testToString();

    //---------------------------------------------------------------
    // boolean overlaps(R arg)
    //---------------------------------------------------------------

    @Test
    public void shouldNotOverlapWhenOtherIsBefore() {
        // range      |------|      [10, 20]
        // other |---|              [2, 9]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("2"), to("9"));
        assertFalse(range.overlaps(other));
        assertFalse(other.overlaps(range));
    }

    @Test
    public void shouldOverlapWhenOtherMeetsBefore() {
        // range      |------|      [10, 20]
        // other  |---|             [5, 10]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("5"), to("10"));
        assertTrue(range.overlaps(other));
        assertTrue(other.overlaps(range));
    }

    @Test
    public void shouldOverlapWhenOtherOnStart() {
        // range      |------|      [10, 20]
        // other      |             [10, 10]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("10"), to("10"));
        assertTrue(range.overlaps(other));
        assertTrue(other.overlaps(range));
    }

    @Test
    public void shouldOverlapWhenOtherOverlapsBefore() {
        // range      |------|      [10, 20]
        // other    |---|           [5, 15]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("5"), to("15"));
        assertTrue(range.overlaps(other));
        assertTrue(other.overlaps(range));
    }

    @Test
    public void shouldOverlapWhenOtherIsContainedAndOnStart() {
        // range      |------|      [10, 20]
        // other      |---|         [10, 15]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("10"), to("15"));
        assertTrue(range.overlaps(other));
        assertTrue(other.overlaps(range));
    }

    @Test
    public void shouldOverlapWhenOtherIsContained() {
        // range      |------|      [10, 20]
        // other        |--|        [13, 15]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("13"), to("15"));
        assertTrue(range.overlaps(other));
        assertTrue(other.overlaps(range));
    }

    @Test
    public void shouldOverlapWhenOtherIsContainedAndOnFinish() {
        // range      |------|      [10, 20]
        // other         |---|      [15, 20]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("15"), to("20"));
        assertTrue(range.overlaps(other));
        assertTrue(other.overlaps(range));
    }

    @Test
    public void shouldOverlapWhenOtherOverlapsAfter() {
        // range      |------|      [10, 20]
        // other           |---|    [15, 25]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("15"), to("25"));
        assertTrue(range.overlaps(other));
        assertTrue(other.overlaps(range));
    }

    @Test
    public void shouldOverlapWhenOtherOnFinish() {
        // range      |------|      [10, 20]
        // other             |      [20, 20]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("20"), to("20"));
        assertTrue(range.overlaps(other));
        assertTrue(other.overlaps(range));
    }

    @Test
    public void shouldOverlapWhenOtherMeetsAfter() {
        // range      |------|      [10, 20]
        // other             |---|  [20, 25]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("20"), to("25"));
        assertTrue(range.overlaps(other));
        assertTrue(other.overlaps(range));
    }

    @Test
    public void shouldOverlapWhenOtherIsAfter() {
        // range      |------|      [10, 20]
        // other              |---| [21, 25]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("21"), to("25"));
        assertFalse(range.overlaps(other));
        assertFalse(other.overlaps(range));
    }

    @Test
    public void shouldOverlapWhenOtherOverlapsStartAndOnFinish() {
        // range      |------|      [10, 20]
        // other    |--------|      [5, 20]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("5"), to("20"));
        assertTrue(range.overlaps(other));
        assertTrue(other.overlaps(range));
    }

    @Test
    public void shouldOverlapWhenOtherEquals() {
        // range      |------|      [10, 20]
        // other      |------|      [10, 20]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("10"), to("20"));
        assertTrue(range.overlaps(other));
        assertTrue(other.overlaps(range));
    }

    @Test
    public void shouldOverlapWhenOtherOverlapsFinishAndOnStart() {
        // range      |------|      [10, 20]
        // other      |--------|    [10, 25]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("10"), to("25"));
        assertTrue(range.overlaps(other));
        assertTrue(other.overlaps(range));
    }

    @Test
    public void shouldOverlapWhenOtherOverlapsStartAndFinish() {
        // range      |------|      [10, 20]
        // other    |----------|    [5, 25]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("5"), to("25"));
        assertTrue(range.overlaps(other));
        assertTrue(other.overlaps(range));
    }

    //---------------------------------------------------------------
    // R intersection(R other)
    //---------------------------------------------------------------

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotIntersectWhenOtherIsBefore() {
        // range      |------|      [10, 20]
        // other |---|              [2, 9]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("2"), to("9"));
        range.intersection(other);
    }

    @Test
    public void shouldIntersectWhenOtherMeetsBefore() {
        // range      |------|      [10, 20]
        // other  |---|             [5, 10]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("5"), to("10"));
        R expected = getTestRange(from("10"), to("10"));
        assertEquals(expected, range.intersection(other));
        assertEquals(expected, other.intersection(range));
    }

    @Test
    public void shouldIntersectWhenOtherOnStart() {
        // range      |------|      [10, 20]
        // other      |             [10, 10]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("10"), to("10"));
        R expected = getTestRange(from("10"), to("10"));
        assertEquals(expected, range.intersection(other));
        assertEquals(expected, other.intersection(range));
    }

    @Test
    public void shouldIntersectWhenOtherOverlapsBefore() {
        // range      |------|      [10, 20]
        // other    |---|           [5, 15]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("5"), to("15"));
        R expected = getTestRange(from("10"), to("15"));
        assertEquals(expected, range.intersection(other));
        assertEquals(expected, other.intersection(range));
    }

    @Test
    public void shouldIntersectWhenOtherIsContainedAndOnStart() {
        // range      |------|      [10, 20]
        // other      |---|         [10, 15]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("10"), to("15"));
        R expected = getTestRange(from("10"), to("15"));
        assertEquals(expected, range.intersection(other));
        assertEquals(expected, other.intersection(range));
    }

    @Test
    public void shouldIntersectWhenOtherIsContained() {
        // range      |------|      [10, 20]
        // other        |--|        [13, 15]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("13"), to("15"));
        R expected = getTestRange(from("13"), to("15"));
        assertEquals(expected, range.intersection(other));
        assertEquals(expected, other.intersection(range));
    }

    @Test
    public void shouldIntersectWhenOtherIsContainedAndOnFinish() {
        // range      |------|      [10, 20]
        // other         |---|      [15, 20]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("15"), to("20"));
        R expected = getTestRange(from("15"), to("20"));
        assertEquals(expected, range.intersection(other));
        assertEquals(expected, other.intersection(range));
    }

    @Test
    public void shouldIntersectWhenOtherOverlapsAfter() {
        // range      |------|      [10, 20]
        // other           |---|    [15, 25]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("15"), to("25"));
        R expected = getTestRange(from("15"), to("20"));
        assertEquals(expected, range.intersection(other));
        assertEquals(expected, other.intersection(range));
    }

    @Test
    public void shouldIntersectWhenOtherOnFinish() {
        // range      |------|      [10, 20]
        // other             |      [20, 20]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("20"), to("20"));
        R expected = getTestRange(from("20"), to("20"));
        assertEquals(expected, range.intersection(other));
        assertEquals(expected, other.intersection(range));
    }

    @Test
    public void shouldIntersectWhenOtherMeetsAfter() {
        // range      |------|      [10, 20]
        // other             |---|  [20, 25]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("20"), to("25"));
        R expected = getTestRange(from("20"), to("20"));
        assertEquals(expected, range.intersection(other));
        assertEquals(expected, other.intersection(range));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldIntersectWhenOtherIsAfter() {
        // range      |------|      [10, 20]
        // other              |---| [21, 25]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("21"), to("25"));
        range.intersection(other);
    }

    @Test
    public void shouldIntersectWhenOtherOverlapsStartAndOnFinish() {
        // range      |------|      [10, 20]
        // other    |--------|      [5, 20]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("5"), to("20"));
        R expected = getTestRange(from("10"), to("20"));
        assertEquals(expected, range.intersection(other));
        assertEquals(expected, other.intersection(range));
    }

    @Test
    public void shouldIntersectWhenOtherEquals() {
        // range      |------|      [10, 20]
        // other      |------|      [10, 20]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("10"), to("20"));
        R expected = getTestRange(from("10"), to("20"));
        assertEquals(expected, range.intersection(other));
        assertEquals(expected, other.intersection(range));
    }

    @Test
    public void shouldIntersectWhenOtherOverlapsFinishAndOnStart() {
        // range      |------|      [10, 20]
        // other      |--------|    [10, 25]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("10"), to("25"));
        R expected = getTestRange(from("10"), to("20"));
        assertEquals(expected, range.intersection(other));
        assertEquals(expected, other.intersection(range));
    }

    @Test
    public void shouldIntersectWhenOtherOverlapsStartAndFinish() {
        // range      |------|      [10, 20]
        // other    |----------|    [5, 25]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("5"), to("25"));
        R expected = getTestRange(from("10"), to("20"));
        assertEquals(expected, range.intersection(other));
        assertEquals(expected, other.intersection(range));
    }

    //---------------------------------------------------------------
    // boolean contains(R other)
    //---------------------------------------------------------------

    @Test
    public void shouldNotContainWhenOtherIsBefore() {
        // range      |------|      [10, 20]
        // other |---|              [2, 9]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("2"), to("9"));
        assertFalse(range.contains(other));
        assertFalse(other.contains(range));
    }

    @Test
    public void shouldNotContainWhenOtherMeetsBefore() {
        // range      |------|      [10, 20]
        // other  |---|             [5, 10]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("5"), to("10"));
        assertFalse(range.contains(other));
        assertFalse(other.contains(range));
    }

    @Test
    public void shouldContainWhenOtherOnStart() {
        // range      |------|      [10, 20]
        // other      |             [10, 10]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("10"), to("10"));
        assertTrue(range.contains(other));
        assertFalse(other.contains(range));
    }

    @Test
    public void shouldNotContainWhenOtherOverlapsBefore() {
        // range      |------|      [10, 20]
        // other    |---|           [5, 15]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("5"), to("15"));
        assertFalse(range.contains(other));
        assertFalse(other.contains(range));
    }

    @Test
    public void shouldContainWhenOtherIsContainedAndOnStart() {
        // range      |------|      [10, 20]
        // other      |---|         [10, 15]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("10"), to("15"));
        assertTrue(range.contains(other));
        assertFalse(other.contains(range));
    }

    @Test
    public void shouldContainWhenOtherIsContained() {
        // range      |------|      [10, 20]
        // other        |--|        [13, 15]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("13"), to("15"));
        assertTrue(range.contains(other));
        assertFalse(other.contains(range));
    }

    @Test
    public void shouldContainWhenOtherIsContainedAndOnFinish() {
        // range      |------|      [10, 20]
        // other         |---|      [15, 20]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("15"), to("20"));
        assertTrue(range.contains(other));
        assertFalse(other.contains(range));
    }

    @Test
    public void shouldNotContainWhenOtherOverlapsAfter() {
        // range      |------|      [10, 20]
        // other           |---|    [15, 25]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("15"), to("25"));
        assertFalse(range.contains(other));
        assertFalse(other.contains(range));
    }

    @Test
    public void shouldContainWhenOtherOnFinish() {
        // range      |------|      [10, 20]
        // other             |      [20, 20]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("20"), to("20"));
        assertTrue(range.contains(other));
        assertFalse(other.contains(range));
    }

    @Test
    public void shouldNotContainWhenOtherMeetsAfter() {
        // range      |------|      [10, 20]
        // other             |---|  [20, 25]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("20"), to("25"));
        assertFalse(range.contains(other));
        assertFalse(other.contains(range));
    }

    @Test
    public void shouldNotContainWhenOtherIsAfter() {
        // range      |------|      [10, 20]
        // other              |---| [21, 25]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("21"), to("25"));
        assertFalse(range.contains(other));
        assertFalse(other.contains(range));
    }

    @Test
    public void shouldNotContainWhenOtherOverlapsStartAndOnFinish() {
        // range      |------|      [10, 20]
        // other    |--------|      [5, 20]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("5"), to("20"));
        assertFalse(range.contains(other));
        assertTrue(other.contains(range));
    }

    @Test
    public void shouldContainWhenOtherEquals() {
        // range      |------|      [10, 20]
        // other      |------|      [10, 20]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("10"), to("20"));
        assertTrue(range.contains(other));
        assertTrue(other.contains(range));
    }

    @Test
    public void shouldNotContainWhenOtherOverlapsFinishAndOnStart() {
        // range      |------|      [10, 20]
        // other      |--------|    [10, 25]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("10"), to("25"));
        assertFalse(range.contains(other));
        assertTrue(other.contains(range));
    }

    @Test
    public void shouldNotContainWhenOtherOverlapsStartAndFinish() {
        // range      |------|      [10, 20]
        // other    |----------|    [5, 25]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("5"), to("25"));
        assertFalse(range.contains(other));
        assertTrue(other.contains(range));
    }

    //---------------------------------------------------------------
    // boolean contains(C value)
    //---------------------------------------------------------------

    @Test
    public void shouldNotContainItemWhenIsLessThanStart() {
        // range      |------|      [10, 20]
        // item      |              [9]
        R range = getTestRange(from("10"), to("20"));
        assertFalse(range.contains(item("9")));
    }

    @Test
    public void shouldContainItemWhenIsEqualsToStart() {
        // range      |------|      [10, 20]
        // item       |             [10]
        R range = getTestRange(from("10"), to("20"));
        assertTrue(range.contains(item("10")));
    }

    @Test
    public void shouldContainItemWhenIsGreaterThanStartAndLessThanEnd() {
        // range      |------|      [10, 20]
        // item           |           [15]
        R range = getTestRange(from("10"), to("20"));
        assertTrue(range.contains(item("15")));
    }

    @Test
    public void shouldContainItemWhenIsEqualsToEnd() {
        // range      |------|      [10, 20]
        // item              |          [20]
        R range = getTestRange(from("10"), to("20"));
        assertTrue(range.contains(item("20")));
    }

    @Test
    public void shouldNotContainItemWhenIsGreaterThanEnd() {
        // range      |------|      [10, 20]
        // item               |         [21]
        R range = getTestRange(from("10"), to("20"));
        assertFalse(range.contains(item("21")));
    }

    //---------------------------------------------------------------
    // boolean isAdjacent(R other)
    //---------------------------------------------------------------

    @Test
    public void testIsAdjacent() {
        // range       |------|       [10, 20]
        // other |---|..      .       [5, 8]
        // other  |---|.      .       [5, 9]
        // other   |---|      .       [5, 10]
        // other     |---|    .       [5, 15]
        // other       |---|  .       [10, 15]
        // other       . |---|.       [15, 19]
        // other       .  |---|       [15, 20]
        // other       .    |---|     [15, 25]
        // other       .      |---|   [20, 25]
        // other       .      .|---|  [21, 25]
        // other       .      ..|---| [22, 25]
        // other    |------------|    [5, 25]
        // other       |------|       [10, 20]
        R range = getTestRange(from("10"), to("20"));

        verifyNotAdjacentRanges(range, getTestRange(from("5"), to("8")));
        verifyNotAdjacentRanges(range, getTestRange(from("5"), to("9")));
        verifyAdjacentRanges(range, getTestRange(from("5"), to("10")));
        verifyNotConsecutiveRanges(range, getTestRange(from("5"), to("15")));
        verifyNotConsecutiveRanges(range, getTestRange(from("10"), to("15")));
        verifyNotConsecutiveRanges(range, getTestRange(from("15"), to("19")));
        verifyNotConsecutiveRanges(range, getTestRange(from("15"), to("20")));
        verifyNotConsecutiveRanges(range, getTestRange(from("15"), to("25")));
        verifyNotConsecutiveRanges(range, getTestRange(from("20"), to("25")));
        verifyConsecutiveRanges(range, getTestRange(from("21"), to("25")));
        verifyNotConsecutiveRanges(range, getTestRange(from("22"), to("25")));
        verifyNotConsecutiveRanges(range, getTestRange(from("5"), to("25")));
        verifyNotConsecutiveRanges(range, getTestRange(from("10"), to("20")));
    }

    private void verifyAdjacentRanges(R range, R other) {
        assertTrue(range.isAdjacent(other));
        assertTrue(other.isAdjacent(range));
    }

    private void verifyNotAdjacentRanges(R range, R other) {
        assertFalse(range.isAdjacent(other));
        assertFalse(other.isAdjacent(range));
    }

    //---------------------------------------------------------------
    // boolean isConsecutive(R other)
    //---------------------------------------------------------------

    @Test
    public void testIsConsecutive() {
        // range       |------|       [10, 20]
        // other |---|..      .       [5, 8]
        // other  |---|.      .       [5, 9]
        // other   |---|      .       [5, 10]
        // other     |---|    .       [5, 15]
        // other       |---|  .       [10, 15]
        // other       . |---|.       [15, 19]
        // other       .  |---|       [15, 20]
        // other       .    |---|     [15, 25]
        // other       .      |---|   [20, 25]
        // other       .      .|---|  [21, 25]
        // other       .      ..|---| [22, 25]
        // other    |------------|    [5, 25]
        // other       |------|       [10, 20]
        R range = getTestRange(from("10"), to("20"));

        verifyNotConsecutiveRanges(range, getTestRange(from("5"), to("8")));
        verifyConsecutiveRanges(range, getTestRange(from("5"), to("9")));
        verifyNotConsecutiveRanges(range, getTestRange(from("5"), to("10")));
        verifyNotConsecutiveRanges(range, getTestRange(from("5"), to("15")));
        verifyNotConsecutiveRanges(range, getTestRange(from("10"), to("15")));
        verifyNotConsecutiveRanges(range, getTestRange(from("15"), to("19")));
        verifyNotConsecutiveRanges(range, getTestRange(from("15"), to("20")));
        verifyNotConsecutiveRanges(range, getTestRange(from("15"), to("25")));
        verifyNotConsecutiveRanges(range, getTestRange(from("20"), to("25")));
        verifyConsecutiveRanges(range, getTestRange(from("21"), to("25")));
        verifyNotConsecutiveRanges(range, getTestRange(from("22"), to("25")));
        verifyNotConsecutiveRanges(range, getTestRange(from("5"), to("25")));
        verifyNotConsecutiveRanges(range, getTestRange(from("10"), to("20")));
    }

    private void verifyConsecutiveRanges(R range, R other) {
        assertTrue(range.isConsecutive(other));
        assertTrue(other.isConsecutive(range));
    }

    private void verifyNotConsecutiveRanges(R range, R other) {
        assertFalse(range.isConsecutive(other));
        assertFalse(other.isConsecutive(range));
    }

    //---------------------------------------------------------------
    // R merge(R other)
    //---------------------------------------------------------------

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotMergeWhenOtherIsBefore() {
        // range      |------|      [10, 20]
        // other |---|              [2, 9]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("2"), to("9"));
        range.merge(other);
    }

    @Test
    public void shouldMergeWhenOtherMeetsBefore() {
        // range        |------|    [10, 20]
        // other    |---|           [5, 10]
        // expected |----------|    [5, 20]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("5"), to("10"));
        R expected = getTestRange(from("5"), to("20"));
        assertEquals(expected, range.merge(other));
        assertEquals(expected, other.merge(range));
    }

    @Test
    public void shouldMergeWhenOtherOnStart() {
        // range      |------|     [10, 20]
        // other      |            [10, 10]
        // expected   |------|     [10, 20]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("10"), to("10"));
        R expected = getTestRange(from("10"), to("20"));
        assertEquals(expected, range.merge(other));
        assertEquals(expected, other.merge(range));
    }

    @Test
    public void shouldMergeWhenOtherOverlapsBefore() {
        // range      |------|      [10, 20]
        // other    |---|           [5, 15]
        // expected |--------|      [5, 20]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("5"), to("15"));
        R expected = getTestRange(from("5"), to("20"));
        assertEquals(expected, range.merge(other));
        assertEquals(expected, other.merge(range));
    }

    @Test
    public void shouldMergeWhenOtherIsContainedAndOnStart() {
        // range      |------|      [10, 20]
        // other      |---|         [10, 15]
        // expected   |------|      [10, 20]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("10"), to("15"));
        R expected = getTestRange(from("10"), to("20"));
        assertEquals(expected, range.merge(other));
        assertEquals(expected, other.merge(range));
    }

    @Test
    public void shouldMergeWhenOtherIsContained() {
        // range      |------|      [10, 20]
        // other        |--|        [13, 15]
        // expected   |------|      [10, 20]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("13"), to("15"));
        R expected = getTestRange(from("10"), to("20"));
        assertEquals(expected, range.merge(other));
        assertEquals(expected, other.merge(range));
    }

    @Test
    public void shouldMergeWhenOtherIsContainedAndOnFinish() {
        // range      |------|      [10, 20]
        // other         |---|      [15, 20]
        // expected   |------|      [10, 20]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("15"), to("20"));
        R expected = getTestRange(from("10"), to("20"));
        assertEquals(expected, range.merge(other));
        assertEquals(expected, other.merge(range));
    }

    @Test
    public void shouldMergeWhenOtherOverlapsAfter() {
        // range      |------|      [10, 20]
        // other           |---|    [15, 25]
        // expected   |--------|    [10, 25]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("15"), to("25"));
        R expected = getTestRange(from("10"), to("25"));
        assertEquals(expected, range.merge(other));
        assertEquals(expected, other.merge(range));
    }

    @Test
    public void shouldMergeWhenOtherOnFinish() {
        // range      |------|      [10, 20]
        // other             |      [20, 20]
        // expected   |------|      [10, 20]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("20"), to("20"));
        R expected = getTestRange(from("10"), to("20"));
        assertEquals(expected, range.merge(other));
        assertEquals(expected, other.merge(range));
    }

    @Test
    public void shouldMergeWhenOtherMeetsAfter() {
        // range      |------|      [10, 20]
        // other             |---|  [20, 25]
        // expected   |------|      [10, 25]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("20"), to("25"));
        R expected = getTestRange(from("10"), to("25"));
        assertEquals(expected, range.merge(other));
        assertEquals(expected, other.merge(range));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldMergeWhenOtherIsAfter() {
        // range      |------|      [10, 20]
        // other              |---| [21, 25]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("21"), to("25"));
        range.merge(other);
    }

    @Test
    public void shouldMergeWhenOtherOverlapsStartAndOnFinish() {
        // range      |------|      [10, 20]
        // other    |--------|      [5, 20]
        // expected |--------|      [5, 20]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("5"), to("20"));
        R expected = getTestRange(from("5"), to("20"));
        assertEquals(expected, range.merge(other));
        assertEquals(expected, other.merge(range));
    }

    @Test
    public void shouldMergeWhenOtherEquals() {
        // range      |------|      [10, 20]
        // other      |------|      [10, 20]
        // expected   |------|      [10, 20]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("10"), to("20"));
        R expected = getTestRange(from("10"), to("20"));
        assertEquals(expected, range.merge(other));
        assertEquals(expected, other.merge(range));
    }

    @Test
    public void shouldMergeWhenOtherOverlapsFinishAndOnStart() {
        // range      |------|      [10, 20]
        // other      |--------|    [10, 25]
        // expected   |--------|    [10, 25]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("10"), to("25"));
        R expected = getTestRange(from("10"), to("25"));
        assertEquals(expected, range.merge(other));
        assertEquals(expected, other.merge(range));
    }

    @Test
    public void shouldMergeWhenOtherOverlapsStartAndFinish() {
        // range      |------|      [10, 20]
        // other    |----------|    [5, 25]
        // expected |----------|    [5, 25]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("5"), to("25"));
        R expected = getTestRange(from("5"), to("25"));
        assertEquals(expected, range.merge(other));
        assertEquals(expected, other.merge(range));
    }
}
