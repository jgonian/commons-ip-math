/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2011-2017, Yannis Gonianakis
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Before;
import org.junit.Test;

public class SortedRangeSetTest {

    private static SortedRangeSet<Asn, AsnRange> subject;

    @Before
    public void before() {
        subject = new SortedRangeSet<Asn, AsnRange>();
    }

    private static void initSubject() {
        subject.add(new AsnRange(Asn.of(0l), Asn.of(5l)));
        subject.add(new AsnRange(Asn.of(10l), Asn.of(15l)));
        subject.add(new AsnRange(Asn.of(20l), Asn.of(25l)));
    }

    @Test
    public void testEqualsContract() {
        EqualsVerifier.forClass(SortedRangeSet.class).suppress(Warning.NULL_FIELDS).verify();
    }

    //---------------------------------------------------------------
    // void add(R range)
    //---------------------------------------------------------------

    @Test
    public void testAddOverlappingRange() {
        initSubject();
        // subject     |--|  |--|  |--|  [0,5] [10,15] [20,25]
        // add           |----|          [4,11]
        // result      |--------|  |--|  [0,15] [20,25]
        subject.add(new AsnRange(Asn.of(4l), Asn.of(11l)));

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(0l), Asn.of(15l)));
        result.add(new AsnRange(Asn.of(20l), Asn.of(25l)));

        Set<AsnRange> actual = subject.unmodifiableSet();
        assertEquals(result, actual);
    }

    @Test
    public void testAddOverlappingRanges() {
        initSubject();
        // subject     |--|  |--|  |--|  [0,5] [10,15] [20,25]
        // add                 |----|    [13,22]
        // add           |----|          [4,11]
        // result      |--------------|  [0,25]
        subject.add(new AsnRange(Asn.of(13l), Asn.of(22l)));
        subject.add(new AsnRange(Asn.of(4l), Asn.of(11l)));

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(0l), Asn.of(25l)));

        assertEquals(result, subject.unmodifiableSet());
    }

    @Test
    public void testAddAdjacentRanges() {
        initSubject();
        // subject     |--|  |--|  |--|  [0,5] [10,15] [20,25]
        // add            |--|  |--|     [5,10] [15,20]
        // result      |--------------|  [0,25]
        subject.add(new AsnRange(Asn.of(5l), Asn.of(10l)));
        subject.add(new AsnRange(Asn.of(15l), Asn.of(20l)));

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(0l), Asn.of(25l)));

        assertEquals(result, subject.unmodifiableSet());
    }

    @Test
    public void testAddConsecutiveRanges() {
        initSubject();
        // subject     |--|    |--|    |--|  [0,5] [10,15] [20,25]
        // add             |--|    |--|      [6,9] [16,19]
        // result      |------------------|  [0,25]
        subject.add(new AsnRange(Asn.of(6l), Asn.of(9l)));
        subject.add(new AsnRange(Asn.of(16l), Asn.of(19l)));

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(0l), Asn.of(25l)));

        assertEquals(result, subject.unmodifiableSet());
    }

    //---------------------------------------------------------------
    // void addAll(SortedRangeSet<C, R> ranges)
    //---------------------------------------------------------------

    @Test
    public void testAddAllOverlappingRanges() {
        initSubject();
        // subject     |--|  |--|  |--|  [0,5] [10,15] [20,25]
        // add all       |----||----|    [13,22] [4,11]
        // result      |--------------|  [0,25]
        SortedRangeSet<Asn, AsnRange> setToAdd = new SortedRangeSet<Asn, AsnRange>();
        setToAdd.add(new AsnRange(Asn.of(13l), Asn.of(22l)));
        setToAdd.add(new AsnRange(Asn.of(4l), Asn.of(11l)));
        subject.addAll(setToAdd);

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(0l), Asn.of(25l)));

        assertEquals(result, subject.unmodifiableSet());
    }

    @Test
    public void testAddAllAdjacentRanges() {
        initSubject();
        // subject     |--|  |--|  |--|  [0,5] [10,15] [20,25]
        // add            |--|  |--|     [5,10] [15,20]
        // result      |--------------|  [0,25]
        SortedRangeSet<Asn, AsnRange> setToAdd = new SortedRangeSet<Asn, AsnRange>();
        setToAdd.add(new AsnRange(Asn.of(5l), Asn.of(10l)));
        setToAdd.add(new AsnRange(Asn.of(15l), Asn.of(20l)));
        subject.addAll(setToAdd);

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(0l), Asn.of(25l)));

        assertEquals(result, subject.unmodifiableSet());
    }

    @Test
    public void testAddAllConsecutiveRanges() {
        initSubject();
        // subject     |--|    |--|    |--|  [0,5] [10,15] [20,25]
        // add             |--|    |--|      [6,9] [16,19]
        // result      |------------------|  [0,25]
        SortedRangeSet<Asn, AsnRange> setToAdd = new SortedRangeSet<Asn, AsnRange>();
        setToAdd.add(new AsnRange(Asn.of(6l), Asn.of(9l)));
        setToAdd.add(new AsnRange(Asn.of(16l), Asn.of(19l)));
        subject.addAll(setToAdd);

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(0l), Asn.of(25l)));

        assertEquals(result, subject.unmodifiableSet());
    }

    //---------------------------------------------------------------
    // void addAll(Collection<R> ranges)
    //---------------------------------------------------------------

    @Test
    public void testAddAllOverlappingRangesFromCollection() {
        initSubject();
        // subject     |--|  |--|  |--|  [0,5] [10,15] [20,25]
        // add all       |----||----|    [13,22] [4,11]
        // result      |--------------|  [0,25]
        List<AsnRange> setToAdd = new ArrayList<AsnRange>();
        setToAdd.add(new AsnRange(Asn.of(13l), Asn.of(22l)));
        setToAdd.add(new AsnRange(Asn.of(4l), Asn.of(11l)));
        subject.addAll(setToAdd);

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(0l), Asn.of(25l)));

        assertEquals(result, subject.unmodifiableSet());
    }

    @Test
    public void testAddAllAdjacentRangesFromCollection() {
        initSubject();
        // subject     |--|  |--|  |--|  [0,5] [10,15] [20,25]
        // add            |--|  |--|     [5,10] [15,20]
        // result      |--------------|  [0,25]
        Set<AsnRange> setToAdd = new HashSet<AsnRange>();
        setToAdd.add(new AsnRange(Asn.of(5l), Asn.of(10l)));
        setToAdd.add(new AsnRange(Asn.of(15l), Asn.of(20l)));
        subject.addAll(setToAdd);

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(0l), Asn.of(25l)));

        assertEquals(result, subject.unmodifiableSet());
    }

    @Test
    public void testAddAllConsecutiveRangesFromCollection() {
        initSubject();
        // subject     |--|    |--|    |--|  [0,5] [10,15] [20,25]
        // add             |--|    |--|      [6,9] [16,19]
        // result      |------------------|  [0,25]
        Vector<AsnRange> setToAdd = new Vector<AsnRange>();
        setToAdd.add(new AsnRange(Asn.of(6l), Asn.of(9l)));
        setToAdd.add(new AsnRange(Asn.of(16l), Asn.of(19l)));
        subject.addAll(setToAdd);

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(0l), Asn.of(25l)));

        assertEquals(result, subject.unmodifiableSet());
    }

    //---------------------------------------------------------------
    // void removeAll(SortedRangeSet<C, R> ranges)
    //---------------------------------------------------------------

    @Test
    public void testRemoveAllOverlappingRanges() {
        initSubject();
        // subject     |---|  |---|  |---|  [0,5] [10,15] [20,25]
        // add all        |----| |----|     [4,11] [13,22]
        // result      |-|      |      |-|  [0,3][12][23,25]
        SortedRangeSet<Asn, AsnRange> setToRemove = new SortedRangeSet<Asn, AsnRange>();
        setToRemove.add(new AsnRange(Asn.of(13l), Asn.of(22l)));
        setToRemove.add(new AsnRange(Asn.of(4l), Asn.of(11l)));
        subject.removeAll(setToRemove);

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(0l), Asn.of(3l)));
        result.add(new AsnRange(Asn.of(12l), Asn.of(12l)));
        result.add(new AsnRange(Asn.of(23l), Asn.of(25l)));

        assertEquals(result, subject.unmodifiableSet());
    }

    @Test
    public void testRemoveAllAdjacentRanges() {
        initSubject();
        // subject     |--|  |---|  |--|  [0,5] [10,15] [20,25]
        // add            |--|   |--|     [5,10] [15,20]
        // result      |-|    |-|    |-|  [0,4] [11,14] [21,25]
        SortedRangeSet<Asn, AsnRange> setToremove = new SortedRangeSet<Asn, AsnRange>();
        setToremove.add(new AsnRange(Asn.of(5l), Asn.of(10l)));
        setToremove.add(new AsnRange(Asn.of(15l), Asn.of(20l)));
        subject.removeAll(setToremove);

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(0l), Asn.of(4l)));
        result.add(new AsnRange(Asn.of(11l), Asn.of(14l)));
        result.add(new AsnRange(Asn.of(21l), Asn.of(25l)));

        assertEquals(result, subject.unmodifiableSet());
    }

    @Test
    public void testRemoveAllConsecutiveRanges() {
        initSubject();
        // subject     |--|    |--|    |--|  [0,5] [10,15] [20,25]
        // add             |--|    |--|      [6,9] [16,19]
        // result      |--|    |--|    |--|  [0,5] [10,15] [20,25]
        SortedRangeSet<Asn, AsnRange> setToRemove = new SortedRangeSet<Asn, AsnRange>();
        setToRemove.add(new AsnRange(Asn.of(6l), Asn.of(9l)));
        setToRemove.add(new AsnRange(Asn.of(16l), Asn.of(19l)));
        subject.removeAll(setToRemove);

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(0l), Asn.of(5l)));
        result.add(new AsnRange(Asn.of(10l), Asn.of(15l)));
        result.add(new AsnRange(Asn.of(20l), Asn.of(25l)));

        assertEquals(result, subject.unmodifiableSet());
    }

    //---------------------------------------------------------------
    // void removeAll(Collection<R> ranges)
    //---------------------------------------------------------------

    @Test
    public void testRemoveAllOverlappingRangesFromCollection() {
        initSubject();
        // subject     |---|  |---|  |---|  [0,5] [10,15] [20,25]
        // add all        |----| |----|     [4,11] [13,22]
        // result      |-|      |      |-|  [0,3][12][23,25]
        Set<AsnRange> setToRemove = new HashSet<AsnRange>();
        setToRemove.add(new AsnRange(Asn.of(13l), Asn.of(22l)));
        setToRemove.add(new AsnRange(Asn.of(4l), Asn.of(11l)));
        subject.removeAll(setToRemove);

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(0l), Asn.of(3l)));
        result.add(new AsnRange(Asn.of(12l), Asn.of(12l)));
        result.add(new AsnRange(Asn.of(23l), Asn.of(25l)));

        assertEquals(result, subject.unmodifiableSet());
    }

    @Test
    public void testRemoveAllAdjacentRangesFromCollection() {
        initSubject();
        // subject     |--|  |---|  |--|  [0,5] [10,15] [20,25]
        // add            |--|   |--|     [5,10] [15,20]
        // result      |-|    |-|    |-|  [0,4] [11,14] [21,25]
        Set<AsnRange> setToremove = new HashSet<AsnRange>();
        setToremove.add(new AsnRange(Asn.of(5l), Asn.of(10l)));
        setToremove.add(new AsnRange(Asn.of(15l), Asn.of(20l)));
        subject.removeAll(setToremove);

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(0l), Asn.of(4l)));
        result.add(new AsnRange(Asn.of(11l), Asn.of(14l)));
        result.add(new AsnRange(Asn.of(21l), Asn.of(25l)));

        assertEquals(result, subject.unmodifiableSet());
    }

    @Test
    public void testRemoveAllConsecutiveRangesFromCollection() {
        initSubject();
        // subject     |--|    |--|    |--|  [0,5] [10,15] [20,25]
        // add             |--|    |--|      [6,9] [16,19]
        // result      |--|    |--|    |--|  [0,5] [10,15] [20,25]
        Set<AsnRange> setToRemove = new HashSet<AsnRange>();
        setToRemove.add(new AsnRange(Asn.of(6l), Asn.of(9l)));
        setToRemove.add(new AsnRange(Asn.of(16l), Asn.of(19l)));
        subject.removeAll(setToRemove);

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(0l), Asn.of(5l)));
        result.add(new AsnRange(Asn.of(10l), Asn.of(15l)));
        result.add(new AsnRange(Asn.of(20l), Asn.of(25l)));

        assertEquals(result, subject.unmodifiableSet());
    }

    //---------------------------------------------------------------
    // boolean remove(R range)
    //---------------------------------------------------------------

    @Test
    public void testRemoveOverlappingRange() {
        initSubject();
        // subject    |---|  |---|  |---|  [0,5] [10,15] [20,25]
        // remove     |---|                [0,5]
        // remove     |---|                [0,5]
        // result            |---|  |---|        [10,15] [20,25]
        assertTrue(subject.remove(new AsnRange(Asn.of(0l), Asn.of(5l))));
        assertFalse(subject.remove(new AsnRange(Asn.of(0l), Asn.of(5l))));

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(10l), Asn.of(15l)));
        result.add(new AsnRange(Asn.of(20l), Asn.of(25l)));

        Set<AsnRange> actual = subject.unmodifiableSet();
        assertEquals(result, actual);
    }

    @Test
    public void testRemoveFullyContainedRange() {
        initSubject();
        // subject    |---|  |---|  |---|  [0,5] [10,15] [20,25]
        // remove             |-|                [11,14]
        // result     |---|  |   |  |---|  [0,5][10,10][15,15][20,25]
        assertTrue(subject.remove(new AsnRange(Asn.of(11l), Asn.of(14l))));

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(0l), Asn.of(5l)));
        result.add(new AsnRange(Asn.of(10l), Asn.of(10l)));
        result.add(new AsnRange(Asn.of(15l), Asn.of(15l)));
        result.add(new AsnRange(Asn.of(20l), Asn.of(25l)));

        Set<AsnRange> actual = subject.unmodifiableSet();
        assertEquals(result, actual);
    }

    @Test
    public void testRemoveRangesThatAreContained() {
        initSubject();
        // subject    |--|  |--|  |--|  [0,5] [10,15] [20,25]
        // remove          |----------|       [9,26]
        // result     |---|             [0,5]
        assertTrue(subject.remove(new AsnRange(Asn.of(9l), Asn.of(26l))));

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(0l), Asn.of(5l)));

        Set<AsnRange> actual = subject.unmodifiableSet();
        assertEquals(result, actual);
    }
    
    @Test
    public void testRemoveRangesThatAreContainedOnLimits() {
        initSubject();
        // subject    |--|  |--|  |--|  [0,5] [10,15] [20,25]
        // remove           |--------|        [10,25]
        // result     |---|             [0,5]
        assertTrue(subject.remove(new AsnRange(Asn.of(10l), Asn.of(25l))));

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(0l), Asn.of(5l)));

        Set<AsnRange> actual = subject.unmodifiableSet();
        assertEquals(result, actual);
    }

    @Test
    public void testRemoveOverlappingRanges() {
        initSubject();
        // subject    |---| |---| |---|  [0,5] [10,15] [20,25]
        // remove              |---|     [14,22]
        // remove        |---|           [4,12]
        // result     |-|     |     |-|  [0,3][13,13][23,25]
        assertTrue(subject.remove(new AsnRange(Asn.of(14l), Asn.of(22l))));
        assertTrue(subject.remove(new AsnRange(Asn.of(4l), Asn.of(12l))));

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(0l), Asn.of(3l)));
        result.add(new AsnRange(Asn.of(13l), Asn.of(13l)));
        result.add(new AsnRange(Asn.of(23l), Asn.of(25l)));

        assertEquals(result, subject.unmodifiableSet());
    }

    @Test
    public void testRemoveAdjacentRanges() {
        initSubject();
        // subject    |---|   |---|   |---|  [0,5] [10,15] [20,25]
        // remove         |---|   |---|      [5,10] [15,20]
        // result     |--|     |-|     |--|  [0,4] [11,14] [21,25]
        assertTrue(subject.remove(new AsnRange(Asn.of(5l), Asn.of(10l))));
        assertTrue(subject.remove(new AsnRange(Asn.of(15l), Asn.of(20l))));

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(0l), Asn.of(4l)));
        result.add(new AsnRange(Asn.of(11l), Asn.of(14l)));
        result.add(new AsnRange(Asn.of(21l), Asn.of(25l)));

        assertEquals(result, subject.unmodifiableSet());
    }

    @Test
    public void testRemoveConsecutiveRanges() {
        initSubject();
        // subject     |--|    |--|    |--|  [0,5] [10,15] [20,25]
        // remove          |--|    |--|      [6,9] [16,19]
        // result      |--|    |--|    |--|  [0,5] [10,15] [20,25]
        assertFalse(subject.remove(new AsnRange(Asn.of(6l), Asn.of(9l))));
        assertFalse(subject.remove(new AsnRange(Asn.of(16l), Asn.of(19l))));

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(0l), Asn.of(5l)));
        result.add(new AsnRange(Asn.of(10l), Asn.of(15l)));
        result.add(new AsnRange(Asn.of(20l), Asn.of(25l)));

        assertEquals(result, subject.unmodifiableSet());
    }

    @Test
    public void testContainsRange() {
        // subject    |-|     |--------|      [0,2] [10,20]
        // contains   |-|     |--------|      [0,2] [10,20]
        // contains   |-| |--|.        .      [0,2] [6,9]
        // contains   |-|  |--|        .      [0,2] [7,10]
        // contains   |-|   |--|       .      [0,2] [8,11]
        // contains   |-|     |--|     .      [0,2] [10,13]
        // contains   |-|     .  |--|  .      [0,2] [13,16]
        // contains   |-|     .     |--|      [0,2] [17,20]
        // contains   |-|     .       |--|    [0,2] [18,21]
        // contains   |-|     .        |--|   [0,2] [20,23]
        // contains   |-|     .        .|--|  [0,2] [21,24]
        // contains   |-|   |------------|    [0,2] [8,22]
        subject.add(new AsnRange(Asn.of(0l), Asn.of(2l)));
        subject.add(new AsnRange(Asn.of(10l), Asn.of(20l)));

        assertTrue(subject.contains(new AsnRange(Asn.of(10l), Asn.of(20l))));
        assertFalse(subject.contains(new AsnRange(Asn.of(6l), Asn.of(9l))));
        assertFalse(subject.contains(new AsnRange(Asn.of(7l), Asn.of(10l))));
        assertFalse(subject.contains(new AsnRange(Asn.of(8l), Asn.of(11l))));
        assertTrue(subject.contains(new AsnRange(Asn.of(10l), Asn.of(13l))));
        assertTrue(subject.contains(new AsnRange(Asn.of(13l), Asn.of(16l))));
        assertTrue(subject.contains(new AsnRange(Asn.of(17l), Asn.of(20l))));
        assertFalse(subject.contains(new AsnRange(Asn.of(18l), Asn.of(21l))));
        assertFalse(subject.contains(new AsnRange(Asn.of(20l), Asn.of(23l))));
        assertFalse(subject.contains(new AsnRange(Asn.of(8l), Asn.of(22l))));
    }

    @Test
    public void testClear() {
        // subject     |--|    |--|    |--|  [0,5] [10,15] [20,25]
        // clear
        subject.add(new AsnRange(Asn.of(0l), Asn.of(5l)));
        subject.add(new AsnRange(Asn.of(10l), Asn.of(15l)));
        subject.add(new AsnRange(Asn.of(20l), Asn.of(25l)));
        subject.clear();
        assertTrue(subject.isEmpty());
    }

    @Test
    public void testIsEmpty() {
        assertTrue(subject.isEmpty());
        subject.add(new AsnRange(Asn.of(0l), Asn.of(5l)));
        assertFalse(subject.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToGetSingleRangeWhenSubjectIsEmpty() {
        assertTrue(subject.isEmpty());
        subject.getSingleRange();
    }

    @Test
    public void testSize() {
        subject.clear();
        assertEquals(0, subject.size());
        subject.add(AsnRange.parse("0-10"));
        assertEquals(1, subject.size());
        subject.add(AsnRange.parse("20-30"));
        assertEquals(2, subject.size());
        subject.add(AsnRange.parse("10-40"));
        assertEquals(1, subject.size());
        subject.remove(AsnRange.parse("0-100"));
        assertEquals(0, subject.size());
    }

    @Test
    public void shouldGetSingleRange() {
        AsnRange range = new AsnRange(Asn.of(0l), Asn.of(5l));
        subject.add(range);
        assertEquals(range, subject.getSingleRange());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToGetSingleRangeWhenSubjectContainsMultipleRanges() {
        subject.add(new AsnRange(Asn.of(10l), Asn.of(20l)));
        subject.add(new AsnRange(Asn.of(30l), Asn.of(40l)));
        subject.getSingleRange();
    }

    @Test
    public void testIterator() {
        AsnRange range1 = new AsnRange(Asn.of(0l), Asn.of(5l));
        AsnRange range2 = new AsnRange(Asn.of(10l), Asn.of(20l));
        subject.add(range1);
        subject.add(range2);
        Iterator<AsnRange> it = subject.iterator();
        assertTrue(it.hasNext());
        assertEquals(range1, it.next());
        it.remove();
        assertTrue(it.hasNext());
        assertEquals(range2, it.next());
        it.remove();
        assertTrue(subject.isEmpty());
    }

    @Test
    public void testCopy() {
        AsnRange range1 = new AsnRange(Asn.of(0l), Asn.of(5l));
        subject.add(range1);

        Set<AsnRange> copy = subject.modifiableSet();
        assertEquals(1, copy.size());

        AsnRange range2 = new AsnRange(Asn.of(10l), Asn.of(20l));
        subject.add(range2);

        assertEquals(1, copy.size());
    }

    @Test
    public void testUnmodifiableSetIsNotACopy() {
        AsnRange range1 = new AsnRange(Asn.of(0l), Asn.of(5l));
        subject.add(range1);

        Set<AsnRange> unmodiableSet = subject.unmodifiableSet();
        assertEquals(1, unmodiableSet.size());

        AsnRange range2 = new AsnRange(Asn.of(10l), Asn.of(20l));
        subject.add(range2);

        assertEquals(2, unmodiableSet.size());
    }

    @Test
    public void testToString() {
        subject.add(new AsnRange(Asn.of(0l), Asn.of(5l)));
        subject.add(new AsnRange(Asn.of(10l), Asn.of(20l)));
        assertEquals("[AS0-AS5, AS10-AS20]", subject.toString());
    }
    
    @Test
    public void shouldEqualsWhenEqual() {
        SortedRangeSet<Asn, AsnRange> other = new SortedRangeSet<Asn, AsnRange>();
        subject.add(new AsnRange(Asn.of(0l), Asn.of(0l)));
        other.add(new AsnRange(Asn.of(0l), Asn.of(0l)));
        assertTrue(subject.equals(other));
    }
    
    @Test
    public void shouldNotConsiderEqualsWhenNotEqualSets() {
        SortedRangeSet<Asn, AsnRange> other = new SortedRangeSet<Asn, AsnRange>();
        subject.add(new AsnRange(Asn.of(0l), Asn.of(1l)));
        other.add(new AsnRange(Asn.of(0l), Asn.of(0l)));
        assertFalse(subject.equals(other));
    }
    
    @Test
    public void shouldIntersectTwoSetsWhenIdenticalSets() {
        subject.add(new AsnRange(Asn.of(0l), Asn.of(0l)));
        
        SortedRangeSet<Asn, AsnRange> other = new SortedRangeSet<Asn, AsnRange>();
        other.add(new AsnRange(Asn.of(0l), Asn.of(0l)));
        
        SortedRangeSet<Asn, AsnRange> expected = new SortedRangeSet<Asn, AsnRange>();
        expected.add(new AsnRange(Asn.of(0l), Asn.of(0l)));
        
        assertEquals(expected, subject.intersection(other));
        
    }
    
    @Test
    public void shouldIntersectTwoSetsWhenEmptySets() {
        SortedRangeSet<Asn, AsnRange> other = new SortedRangeSet<Asn, AsnRange>();
        SortedRangeSet<Asn, AsnRange> expected = new SortedRangeSet<Asn, AsnRange>();
        
        assertEquals(expected, subject.intersection(other));
    }
    
    @Test
    public void shouldIntersectTwoSetsWhenOneOverlappingRange() {
        subject.add(new AsnRange(Asn.of(0l), Asn.of(0l)));
        SortedRangeSet<Asn, AsnRange> other = new SortedRangeSet<Asn, AsnRange>();
        other.add(new AsnRange(Asn.of(0l), Asn.of(1l)));
        
        SortedRangeSet<Asn, AsnRange> expected = new SortedRangeSet<Asn, AsnRange>();
        expected.add(new AsnRange(Asn.of(0l), Asn.of(0l)));
        
        assertEquals(expected, subject.intersection(other));
    }
    
    @Test
    public void shouldIntersectTwoSetsWhenTwoOverlappingRanges() {
        subject.add(new AsnRange(Asn.of(0l), Asn.of(1l)));
        subject.add(new AsnRange(Asn.of(3l), Asn.of(4l)));
        
        SortedRangeSet<Asn, AsnRange> other = new SortedRangeSet<Asn, AsnRange>();
        other.add(new AsnRange(Asn.of(1l), Asn.of(2l)));
        other.add(new AsnRange(Asn.of(4l), Asn.of(5l)));
        
        
        SortedRangeSet<Asn, AsnRange> expected = new SortedRangeSet<Asn, AsnRange>();
        expected.add(new AsnRange(Asn.of(1l), Asn.of(1l)));
        expected.add(new AsnRange(Asn.of(4l), Asn.of(4l)));
        
        assertEquals(expected, subject.intersection(other));
    }
    
    @Test
    public void shouldGetFloor() {
        subject.add(new AsnRange(Asn.of(2l), Asn.of(2l)));
        subject.add(new AsnRange(Asn.of(0l), Asn.of(0l)));
        assertEquals(new AsnRange(Asn.of(2l), Asn.of(2l)), subject.floor(new AsnRange(Asn.of(3l), Asn.of(3l))));
    }
    
    @Test
    public void shouldGetCeiling() {
        subject.add(new AsnRange(Asn.of(2l), Asn.of(2l)));
        subject.add(new AsnRange(Asn.of(0l), Asn.of(0l)));
        assertEquals(new AsnRange(Asn.of(2l), Asn.of(2l)), subject.ceiling(new AsnRange(Asn.of(1l), Asn.of(1l))));
    }

}
