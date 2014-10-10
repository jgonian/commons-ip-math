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
package net.ripe.commons.ip;

import static junit.framework.Assert.*;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

public class SortedResourceSetTest {

    private static SortedResourceSet<Asn, AsnRange> subject;

    @Before
    public void before() {
        subject = new SortedResourceSet<Asn, AsnRange>();
    }

    @Test
    public void testAddResource() {
        // subject     |--|   |--|    [2,4] [8,10]
        // add           |.   .       [3]
        // add            |   .       [4]
        // add            .|  .       [5]
        // add            .   .|      [9]
        // add            .   |       [8]
        // add            .  |.       [7]
        // add            . | .       [6]
        // add       |    .   .       [0]
        // add            .   .   |   [12]
        // result    | |--------| |   [0,0] [2,10] [12,12]
        subject.add(new AsnRange(Asn.of(2l), Asn.of(4l)));
        subject.add(new AsnRange(Asn.of(8l), Asn.of(10l)));

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(2l), Asn.of(4l)));
        result.add(new AsnRange(Asn.of(8l), Asn.of(10l)));

        subject.add(Asn.of(3l));
        assertEquals(result, subject.unmodifiableSet());

        subject.add(Asn.of(4l));
        assertEquals(result, subject.unmodifiableSet());

        subject.add(Asn.of(5l));
        result.clear();
        result.add(new AsnRange(Asn.of(2l), Asn.of(5l)));
        result.add(new AsnRange(Asn.of(8l), Asn.of(10l)));
        assertEquals(result, subject.unmodifiableSet());

        subject.add(Asn.of(9l));
        assertEquals(result, subject.unmodifiableSet());

        subject.add(Asn.of(8l));
        assertEquals(result, subject.unmodifiableSet());

        subject.add(Asn.of(7l));
        result.clear();
        result.add(new AsnRange(Asn.of(2l), Asn.of(5l)));
        result.add(new AsnRange(Asn.of(7l), Asn.of(10l)));
        assertEquals(result, subject.unmodifiableSet());

        subject.add(Asn.of(6l));
        result.clear();
        result.add(new AsnRange(Asn.of(2l), Asn.of(10l)));
        assertEquals(result, subject.unmodifiableSet());

        subject.add(Asn.of(0l));
        result.add(Asn.of(0l).asRange());
        assertEquals(result, subject.unmodifiableSet());

        subject.add(Asn.of(12l));
        result.add(Asn.of(12l).asRange());
        assertEquals(result, subject.unmodifiableSet());
    }

    @Test
    public void testRemoveResource() {
        // subject     |-----|          [2,8]
        // remove    . |-----|    0 -> [2,8]
        // remove      |-----| . 10 -> [2,8]
        // remove      |-|.|-|    5 -> [2,4][6,8]
        // remove      .|| |-|    2 -> [3,4][6,8]
        // remove       |. |-|    4 -> [3,3][6,8]
        // remove       .  |-|    3 -> [6,8]
        subject.add(new AsnRange(Asn.of(2l), Asn.of(8l)));

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(2l), Asn.of(8l)));

        assertFalse(subject.remove(Asn.of(0l)));
        assertEquals(result, subject.unmodifiableSet());

        assertFalse(subject.remove(Asn.of(10l)));
        assertEquals(result, subject.unmodifiableSet());

        assertTrue(subject.remove(Asn.of(5l)));
        result.clear();
        result.add(new AsnRange(Asn.of(2l), Asn.of(4l)));
        result.add(new AsnRange(Asn.of(6l), Asn.of(8l)));
        assertEquals(result, subject.unmodifiableSet());

        assertTrue(subject.remove(Asn.of(2l)));
        result.clear();
        result.add(new AsnRange(Asn.of(3l), Asn.of(4l)));
        result.add(new AsnRange(Asn.of(6l), Asn.of(8l)));
        assertEquals(result, subject.unmodifiableSet());

        assertTrue(subject.remove(Asn.of(4l)));
        result.clear();
        result.add(new AsnRange(Asn.of(3l), Asn.of(3l)));
        result.add(new AsnRange(Asn.of(6l), Asn.of(8l)));
        assertEquals(result, subject.unmodifiableSet());

        assertTrue(subject.remove(Asn.of(3l)));
        result.clear();
        result.add(new AsnRange(Asn.of(6l), Asn.of(8l)));
        assertEquals(result, subject.unmodifiableSet());
    }

    @Test
    public void testContainsResource() {
        // subject     |--| |--|    [2,4] [6,8]
        // contains  |    . .       [0]
        // contains      |. .       [3]
        // contains       | .       [4]
        // contains       .|.       [5]
        // contains       . |       [6]
        // contains       . .|      [7]
        // contains       . .   |   [10]
        subject.add(new AsnRange(Asn.of(2l), Asn.of(4l)));
        subject.add(new AsnRange(Asn.of(6l), Asn.of(8l)));

        assertFalse(subject.contains(Asn.of(0l)));
        assertTrue(subject.contains(Asn.of(3l)));
        assertTrue(subject.contains(Asn.of(4l)));
        assertFalse(subject.contains(Asn.of(5l)));
        assertTrue(subject.contains(Asn.of(6l)));
        assertTrue(subject.contains(Asn.of(7l)));
        assertFalse(subject.contains(Asn.of(10l)));
    }
}
