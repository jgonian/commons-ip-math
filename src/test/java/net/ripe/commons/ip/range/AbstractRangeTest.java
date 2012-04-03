package net.ripe.commons.ip.range;

import static junit.framework.Assert.*;
import org.junit.Test;

public abstract class AbstractRangeTest<C extends Comparable<C>, R extends AbstractRange<C, R>> {


    protected abstract C from(String s);
    protected abstract C to(String s);
    protected abstract R getTestRange(C start, C end);

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
}
