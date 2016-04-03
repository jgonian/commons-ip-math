/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2011-2014, Yannis Gonianakis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.jgonian.ipmath;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

public abstract class AbstractRangeTest<C extends Rangeable<C, R>, R extends AbstractRange<C, R>> {

    protected abstract C from(String s);

    protected abstract C to(String s);

    protected abstract C item(String s);

    protected abstract R getTestRange(C start, C end);

    protected abstract R getFullRange();

    @Test
    public abstract void shouldCalculateRangeSize();

    @Test
    public abstract void testToString();

    @Test
    public abstract void testIterator();

    @Test(expected = UnsupportedOperationException.class)
    public void testIteratorDoesNotSupportRemove() {
        getTestRange(from("1"), to("10")).iterator().remove();
    }

    @Test
    public void testEqualsContract() {
        EqualsVerifier.forClass(AbstractRange.class).suppress(Warning.NULL_FIELDS).verify();
    }

    //---------------------------------------------------------------
    // boolean overlaps(R arg)
    //---------------------------------------------------------------

    @Test
    public void testOverlaps() {
        // range      |------|      [10, 20]
        // other |---|.      .      [2, 9]
        // other  |---|      .      [5, 10]
        // other      |      .      [10, 10]
        // other    |---|    .      [5, 15]
        // other      |---|  .      [10, 15]
        // other      . |--| .      [13, 15]
        // other      .  |---|      [15, 20]
        // other      .    |---|    [15, 25]
        // other      .      |      [20, 20]
        // other      .      |---|  [20, 25]
        // other      .      .|---| [21, 25]
        // other    |--------|      [5, 20]
        // other      |------|      [10, 20]
        // other      |--------|    [10, 25]
        // other    |----------|    [5, 25]
        R range = getTestRange(from("10"), to("20"));

        assertNotOverlapping(range, getTestRange(from("2"), to("9")));
        assertOverlapping(range, getTestRange(from("5"), to("10")));
        assertOverlapping(range, getTestRange(from("10"), to("10")));
        assertOverlapping(range, getTestRange(from("5"), to("15")));
        assertOverlapping(range, getTestRange(from("10"), to("15")));
        assertOverlapping(range, getTestRange(from("13"), to("15")));
        assertOverlapping(range, getTestRange(from("15"), to("20")));
        assertOverlapping(range, getTestRange(from("15"), to("25")));
        assertOverlapping(range, getTestRange(from("20"), to("20")));
        assertOverlapping(range, getTestRange(from("20"), to("25")));
        assertNotOverlapping(range, getTestRange(from("21"), to("25")));
        assertOverlapping(range, getTestRange(from("5"), to("20")));
        assertOverlapping(range, getTestRange(from("10"), to("20")));
        assertOverlapping(range, getTestRange(from("10"), to("25")));
        assertOverlapping(range, getTestRange(from("5"), to("25")));
        assertFalse(range.overlaps((R) null));
    }

    private void assertOverlapping(R range, R other) {
        assertTrue(range.overlaps(other));
        assertTrue(other.overlaps(range));
    }

    private void assertNotOverlapping(R range, R other) {
        assertFalse(range.overlaps(other));
        assertFalse(other.overlaps(range));
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
    public void testContains() {
        // range      |------|   [10, 20]
        // item      |.      .   [9]
        // item       |      .   [10]
        // item       .  |   .   [15]
        // item       .      |   [20]
        // item       .      .|  [21]
        R range = getTestRange(from("10"), to("20"));

        assertFalse(range.contains(item("9")));
        assertTrue(range.contains(item("10")));
        assertTrue(range.contains(item("15")));
        assertTrue(range.contains(item("20")));
        assertFalse(range.contains(item("21")));
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
        assertFalse(range.isConsecutive((R) null));
        verifyNotConsecutiveRanges(range, getFullRange());
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
    // boolean isEmpty()
    //---------------------------------------------------------------

    @Test
    public void testIsEmpty() {
        // empty         |          [10]
        // notEmpty      |------|   [10, 20]
        assertTrue(getTestRange(from("10"), to("10")).isEmpty());
        assertFalse(getTestRange(from("10"), to("20")).isEmpty());
    }

    //---------------------------------------------------------------
    // R merge(R other)
    //---------------------------------------------------------------

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotMergeNonConsecutiveWhenOtherIsBefore() {
        // range       |------|    [10, 20]
        // other |---|             [2, 8]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("2"), to("8"));
        range.merge(other);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotMergeNonConsecutiveWhenOtherIsAfter() {
        // range      |------|         [10, 20]
        // other               |---|   [22, 25]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("22"), to("25"));
        range.merge(other);
    }

    @Test
    public void shouldMergeConsecutiveWhenOtherIsBefore() {
        // range         |------|    [10, 20]
        // other    |---|            [2, 9]
        // expected |-----------|    [2, 20]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("2"), to("9"));
        R expected = getTestRange(from("2"), to("20"));
        assertEquals(expected, range.merge(other));
        assertEquals(expected, other.merge(range));
    }

    @Test
    public void shouldMergeConsecutiveWhenOtherIsAfter() {
        // range      |------|       [10, 20]
        // other              |---|  [21, 25]
        // expected   |-----------|  [10, 25]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("21"), to("25"));
        R expected = getTestRange(from("10"), to("25"));
        assertEquals(expected, range.merge(other));
        assertEquals(expected, other.merge(range));
    }

    @Test
    public void shouldMergeConsecutiveWhenOtherMeetsBefore() {
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
    public void shouldMergeConsecutiveWhenOtherOnStart() {
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
    public void shouldMergeConsecutiveWhenOtherOverlapsBefore() {
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
    public void shouldMergeConsecutiveWhenOtherIsContainedAndOnStart() {
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
    public void shouldMergeConsecutiveWhenOtherIsContained() {
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
    public void shouldMergeConsecutiveWhenOtherIsContainedAndOnFinish() {
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
    public void shouldMergeConsecutiveWhenOtherOverlapsAfter() {
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
    public void shouldMergeConsecutiveWhenOtherOnFinish() {
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
    public void shouldMergeConsecutiveWhenOtherMeetsAfter() {
        // range      |------|      [10, 20]
        // other             |---|  [20, 25]
        // expected   |------|      [10, 25]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("20"), to("25"));
        R expected = getTestRange(from("10"), to("25"));
        assertEquals(expected, range.merge(other));
        assertEquals(expected, other.merge(range));
    }

    @Test
    public void shouldMergeConsecutiveWhenOtherOverlapsStartAndOnFinish() {
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
    public void shouldMergeConsecutiveWhenOtherEquals() {
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
    public void shouldMergeConsecutiveWhenOtherOverlapsFinishAndOnStart() {
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
    public void shouldMergeConsecutiveWhenOtherOverlapsStartAndFinish() {
        // range      |------|      [10, 20]
        // other    |----------|    [5, 25]
        // expected |----------|    [5, 25]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("5"), to("25"));
        R expected = getTestRange(from("5"), to("25"));
        assertEquals(expected, range.merge(other));
        assertEquals(expected, other.merge(range));
    }

    //---------------------------------------------------------------
    // boolean isSameRange(R arg)
    //---------------------------------------------------------------

    @Test
    public void testIsSameRange() {
        // range      |------|      [10, 20]
        // other |---|.      .      [2, 9]
        // other  |---|      .      [5, 10]
        // other      |      .      [10, 10]
        // other    |---|    .      [5, 15]
        // other      |---|  .      [10, 15]
        // other      . |--| .      [13, 15]
        // other      .  |---|      [15, 20]
        // other      .    |---|    [15, 25]
        // other      .      |      [20, 20]
        // other      .      |---|  [20, 25]
        // other      .      .|---| [21, 25]
        // other    |--------|      [5, 20]
        // other      |------|      [10, 20]
        // other      |--------|    [10, 25]
        // other    |----------|    [5, 25]
        R range = getTestRange(from("10"), to("20"));

        assertNotSame(range, getTestRange(from("2"), to("9")));
        assertNotSame(range, getTestRange(from("5"), to("10")));
        assertNotSame(range, getTestRange(from("10"), to("10")));
        assertNotSame(range, getTestRange(from("5"), to("15")));
        assertNotSame(range, getTestRange(from("10"), to("15")));
        assertNotSame(range, getTestRange(from("13"), to("15")));
        assertNotSame(range, getTestRange(from("15"), to("20")));
        assertNotSame(range, getTestRange(from("15"), to("25")));
        assertNotSame(range, getTestRange(from("20"), to("20")));
        assertNotSame(range, getTestRange(from("20"), to("25")));
        assertNotSame(range, getTestRange(from("21"), to("25")));
        assertNotSame(range, getTestRange(from("5"), to("20")));
        assertSame(range, getTestRange(from("10"), to("20")));
        assertNotSame(range, getTestRange(from("10"), to("25")));
        assertNotSame(range, getTestRange(from("5"), to("25")));
    }

    private void assertSame(R range, R other) {
        assertTrue(range.isSameRange(other));
        assertTrue(other.isSameRange(range));
    }

    private void assertNotSame(R range, R other) {
        assertFalse(range.isSameRange(other));
        assertFalse(other.isSameRange(range));
    }

    //---------------------------------------------------------------
    // List<R> exclude(R other)
    //---------------------------------------------------------------

    @Test
    public void shouldNotExcludeIfOtherIsBefore() {
        // range       |------|      [10, 20]
        // other  |---|              [2, 9]
        // result      |------|      [10, 20]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("2"), to("9"));
        List<R> result = Collections.singletonList(range);
        assertEquals(result, range.exclude(other));
    }

    @Test
    public void shouldExcludeIfOtherMeetsBefore() {
        // range      |------|      [10, 20]
        // other  |---|             [5, 10]
        // result      |-----|      [11, 20]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("5"), to("10"));
        List<R> result = Collections.singletonList(getTestRange(from("11"), to("20")));
        assertEquals(result, range.exclude(other));
    }

    @Test
    public void shouldExcludeIfOtherOnStart() {
        // range      |------|      [10, 20]
        // other      |             [10, 10]
        // result      |-----|      [11, 20]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("10"), to("10"));
        List<R> result = Collections.singletonList(getTestRange(from("11"), to("20")));
        assertEquals(result, range.exclude(other));
    }

    @Test
    public void shouldExcludeIfOtherOverlapsBefore() {
        // range      |------|      [10, 20]
        // other    |---|           [5, 15]
        // result        |---|      [16, 20]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("5"), to("15"));
        List<R> result = Collections.singletonList(getTestRange(from("16"), to("20")));
        assertEquals(result, range.exclude(other));
    }

    @Test
    public void shouldRemoveIfOtherIsContainedAndOnStart() {
        // range      |------|      [10, 20]
        // other      |---|         [10, 15]
        // result          |-|      [16, 20]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("10"), to("15"));
        List<R> result = Collections.singletonList(getTestRange(from("16"), to("20")));
        assertEquals(result, range.exclude(other));
    }

    @Test
    public void shouldRemoveIfOtherIsContained() {
        // range      |---------|    [10, 20]
        // other         |--|        [13, 15]
        // result     |-|    |--|    [10, 12] [16, 20]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("13"), to("15"));
        List<R> result = new ArrayList<R>() {{
            add(getTestRange(from("10"), to("12")));
            add(getTestRange(from("16"), to("20")));
        }};
        assertEquals(result, range.exclude(other));
    }

    @Test
    public void shouldRemoveIfOtherIsContainedAndOnFinish() {
        // range      |------|      [10, 20]
        // other         |---|      [15, 20]
        // result     |-|           [10, 14]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("15"), to("20"));
        List<R> result = Collections.singletonList(getTestRange(from("10"), to("14")));
        assertEquals(result, range.exclude(other));
    }

    @Test
    public void shouldExcludeIfOtherOverlapsAfter() {
        // range      |------|      [10, 20]
        // other           |---|    [15, 25]
        // result     |---|         [10, 14]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("15"), to("25"));
        List<R> result = Collections.singletonList(getTestRange(from("10"), to("14")));
        assertEquals(result, range.exclude(other));
    }

    @Test
    public void shouldExcludeIfOtherOnFinish() {
        // range      |------|      [10, 20]
        // other             |      [20, 20]
        // result     |-----|       [10, 19]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("20"), to("20"));
        List<R> result = Collections.singletonList(getTestRange(from("10"), to("19")));
        assertEquals(result, range.exclude(other));
    }

    @Test
    public void shouldExcludeIfOtherMeetsAfter() {
        // range      |------|      [10, 20]
        // other             |---|  [20, 25]
        // result     |-----|       [10, 19]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("20"), to("25"));
        List<R> result = Collections.singletonList(getTestRange(from("10"), to("19")));
        assertEquals(result, range.exclude(other));
    }

    @Test
    public void shouldExcludeIfOtherIsAfter() {
        // range      |------|      [10, 20]
        // other              |---| [21, 25]
        // result     |------|      [10, 20]
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("21"), to("25"));
        List<R> result = Collections.singletonList(range);
        assertEquals(result, range.exclude(other));
    }

    @Test
    public void shouldExcludeIfOtherOverlapsStartAndOnFinish() {
        // range      |------|      [10, 20]
        // other    |--------|      [5, 20]
        // result                   []
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("5"), to("20"));
        List<R> result = Collections.emptyList();
        assertEquals(result, range.exclude(other));
    }

    @Test
    public void shouldExcludeIfOtherEquals() {
        // range      |------|      [10, 20]
        // other      |------|      [10, 20]
        // result                   []
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("10"), to("20"));
        List<R> result = Collections.emptyList();
        assertEquals(result, range.exclude(other));
    }

    @Test
    public void shouldExcludeIfOtherOverlapsFinishAndOnStart() {
        // range      |------|      [10, 20]
        // other      |--------|    [10, 25]
        // result                   []
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("10"), to("25"));
        List<R> result = Collections.emptyList();
        assertEquals(result, range.exclude(other));
    }

    @Test
    public void shouldExcludeIfOtherOverlapsStartAndFinish() {
        // range      |------|      [10, 20]
        // other    |----------|    [5, 25]
        // result                   []
        R range = getTestRange(from("10"), to("20"));
        R other = getTestRange(from("5"), to("25"));
        List<R> result = Collections.emptyList();
        assertEquals(result, range.exclude(other));
    }
}
