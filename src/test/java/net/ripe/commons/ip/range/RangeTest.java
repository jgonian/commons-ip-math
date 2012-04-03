package net.ripe.commons.ip.range;

import static junit.framework.Assert.*;
import static net.ripe.commons.ip.resource.Ipv4Address.*;
import org.junit.Test;

public class RangeTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotIntersectWhenOtherIsBefore() {
        // range      |------|      [10, 20]
        // other |---|              [2, 9]
        Ipv4Range range = new Ipv4Range($("1.0.0.10"), $("1.0.0.20"));
        Ipv4Range other = new Ipv4Range($("1.0.0.2"), $("1.0.0.9"));
        range.intersection(other);
    }

    @Test
    public void shouldIntersectWhenOtherMeetsBefore() {
        // range      |------|      [10, 20]
        // other  |---|             [5, 10]
        Ipv4Range range = new Ipv4Range($("1.0.0.10"), $("1.0.0.20"));
        Ipv4Range other = new Ipv4Range($("1.0.0.5"), $("1.0.0.10"));
        Ipv4Range expected = new Ipv4Range($("1.0.0.10"), $("1.0.0.10"));
        assertEquals(expected, range.intersection(other));
        assertEquals(expected, other.intersection(range));
    }

    @Test
    public void shouldIntersectWhenOtherOnStart() {
        // range      |------|      [10, 20]
        // other      |             [10, 10]
        Ipv4Range range = new Ipv4Range($("1.0.0.10"), $("1.0.0.20"));
        Ipv4Range other = new Ipv4Range($("1.0.0.10"), $("1.0.0.10"));
        Ipv4Range expected = new Ipv4Range($("1.0.0.10"), $("1.0.0.10"));
        assertEquals(expected, range.intersection(other));
        assertEquals(expected, other.intersection(range));
    }

    @Test
    public void shouldIntersectWhenOtherOverlapsBefore() {
        // range      |------|      [10, 20]
        // other    |---|           [5, 15]
        Ipv4Range range = new Ipv4Range($("1.0.0.10"), $("1.0.0.20"));
        Ipv4Range other = new Ipv4Range($("1.0.0.5"), $("1.0.0.15"));
        Ipv4Range expected = new Ipv4Range($("1.0.0.10"), $("1.0.0.15"));
        assertEquals(expected, range.intersection(other));
        assertEquals(expected, other.intersection(range));
    }

    @Test
    public void shouldIntersectWhenOtherIsContainedAndOnStart() {
        // range      |------|      [10, 20]
        // other      |---|         [10, 15]
        Ipv4Range range = new Ipv4Range($("1.0.0.10"), $("1.0.0.20"));
        Ipv4Range other = new Ipv4Range($("1.0.0.10"), $("1.0.0.15"));
        Ipv4Range expected = new Ipv4Range($("1.0.0.10"), $("1.0.0.15"));
        assertEquals(expected, range.intersection(other));
        assertEquals(expected, other.intersection(range));
    }

    @Test
    public void shouldIntersectWhenOtherIsContained() {
        // range      |------|      [10, 20]
        // other        |--|        [13, 15]
        Ipv4Range range = new Ipv4Range($("1.0.0.10"), $("1.0.0.20"));
        Ipv4Range other = new Ipv4Range($("1.0.0.13"), $("1.0.0.15"));
        Ipv4Range expected = new Ipv4Range($("1.0.0.13"), $("1.0.0.15"));
        assertEquals(expected, range.intersection(other));
        assertEquals(expected, other.intersection(range));
    }

    @Test
    public void shouldIntersectWhenOtherIsContainedAndOnFinish() {
        // range      |------|      [10, 20]
        // other         |---|      [15, 20]
        Ipv4Range range = new Ipv4Range($("1.0.0.10"), $("1.0.0.20"));
        Ipv4Range other = new Ipv4Range($("1.0.0.15"), $("1.0.0.20"));
        Ipv4Range expected = new Ipv4Range($("1.0.0.15"), $("1.0.0.20"));
        assertEquals(expected, range.intersection(other));
        assertEquals(expected, other.intersection(range));
    }

    @Test
    public void shouldIntersectWhenOtherOverlapsAfter() {
        // range      |------|      [10, 20]
        // other           |---|    [15, 25]
        Ipv4Range range = new Ipv4Range($("1.0.0.10"), $("1.0.0.20"));
        Ipv4Range other = new Ipv4Range($("1.0.0.15"), $("1.0.0.25"));
        Ipv4Range expected = new Ipv4Range($("1.0.0.15"), $("1.0.0.20"));
        assertEquals(expected, range.intersection(other));
        assertEquals(expected, other.intersection(range));
    }

    @Test
    public void shouldIntersectWhenOtherOnFinish() {
        // range      |------|      [10, 20]
        // other             |      [20, 20]
        Ipv4Range range = new Ipv4Range($("1.0.0.10"), $("1.0.0.20"));
        Ipv4Range other = new Ipv4Range($("1.0.0.20"), $("1.0.0.20"));
        Ipv4Range expected = new Ipv4Range($("1.0.0.20"), $("1.0.0.20"));
        assertEquals(expected, range.intersection(other));
        assertEquals(expected, other.intersection(range));
    }

    @Test
    public void shouldIntersectWhenOtherMeetsAfter() {
        // range      |------|      [10, 20]
        // other             |---|  [20, 25]
        Ipv4Range range = new Ipv4Range($("1.0.0.10"), $("1.0.0.20"));
        Ipv4Range other = new Ipv4Range($("1.0.0.20"), $("1.0.0.25"));
        Ipv4Range expected = new Ipv4Range($("1.0.0.20"), $("1.0.0.20"));
        assertEquals(expected, range.intersection(other));
        assertEquals(expected, other.intersection(range));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldIntersectWhenOtherIsAfter() {
        // range      |------|      [10, 20]
        // other              |---| [21, 25]
        Ipv4Range range = new Ipv4Range($("1.0.0.10"), $("1.0.0.20"));
        Ipv4Range other = new Ipv4Range($("1.0.0.21"), $("1.0.0.25"));
        range.intersection(other);
    }

    @Test
    public void shouldIntersectWhenOtherOverlapsStartAndOnFinish() {
        // range      |------|      [10, 20]
        // other    |--------|      [5, 20]
        Ipv4Range range = new Ipv4Range($("1.0.0.10"), $("1.0.0.20"));
        Ipv4Range other = new Ipv4Range($("1.0.0.5"), $("1.0.0.20"));
        Ipv4Range expected = new Ipv4Range($("1.0.0.10"), $("1.0.0.20"));
        assertEquals(expected, range.intersection(other));
        assertEquals(expected, other.intersection(range));
    }

    @Test
    public void shouldIntersectWhenOtherEquals() {
        // range      |------|      [10, 20]
        // other      |------|      [10, 20]
        Ipv4Range range = new Ipv4Range($("1.0.0.10"), $("1.0.0.20"));
        Ipv4Range other = new Ipv4Range($("1.0.0.10"), $("1.0.0.20"));
        Ipv4Range expected = new Ipv4Range($("1.0.0.10"), $("1.0.0.20"));
        assertEquals(expected, range.intersection(other));
        assertEquals(expected, other.intersection(range));
    }

    @Test
    public void shouldIntersectWhenOtherOverlapsFinishAndOnStart() {
        // range      |------|      [10, 20]
        // other      |--------|    [10, 25]
        Ipv4Range range = new Ipv4Range($("1.0.0.10"), $("1.0.0.20"));
        Ipv4Range other = new Ipv4Range($("1.0.0.10"), $("1.0.0.25"));
        Ipv4Range expected = new Ipv4Range($("1.0.0.10"), $("1.0.0.20"));
        assertEquals(expected, range.intersection(other));
        assertEquals(expected, other.intersection(range));
    }

    @Test
    public void shouldIntersectWhenOtherOverlapsStartAndFinish() {
        // range      |------|      [10, 20]
        // other    |----------|    [5, 25]
        Ipv4Range range = new Ipv4Range($("1.0.0.10"), $("1.0.0.20"));
        Ipv4Range other = new Ipv4Range($("1.0.0.5"), $("1.0.0.25"));
        Ipv4Range expected = new Ipv4Range($("1.0.0.10"), $("1.0.0.20"));
        assertEquals(expected, range.intersection(other));
        assertEquals(expected, other.intersection(range));
    }

}
