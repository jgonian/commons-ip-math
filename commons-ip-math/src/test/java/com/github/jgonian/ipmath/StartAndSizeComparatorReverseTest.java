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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertSame;
import java.util.Comparator;

import org.junit.Test;

public class StartAndSizeComparatorReverseTest {

    private static final Asn _10 = Asn.of(10l);
    private static final Asn _9 = _10.previous();
    private static final Asn _11 = _10.next();
    private static final Asn _100 = Asn.of(100l);
    private static final Asn _99 = _100.previous();
    private static final Asn _101 = _100.next();

    private Comparator<AsnRange> reverse = StartAndSizeComparator.<Asn, AsnRange>reverse();

    @Test
    public void testSingletonFactoryMethod() {
        assertSame(reverse, StartAndSizeComparator.<Asn, AsnRange>reverse());
    }

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
        assertGreaterThan(range, AsnRange.from(_11).to(_99));
        assertGreaterThan(range, AsnRange.from(_11).to(_100));
        assertGreaterThan(range, AsnRange.from(_11).to(_101));
        assertLessThan(range, AsnRange.from(_9).to(_99));
        assertLessThan(range, AsnRange.from(_9).to(_100));
        assertLessThan(range, AsnRange.from(_9).to(_101));
        assertLessThan(range, AsnRange.from(_10).to(_99));
        assertEqual(range, AsnRange.from(_10).to(_100));
        assertGreaterThan(range, AsnRange.from(_10).to(_101));
    }

    private void assertLessThan(AsnRange range, AsnRange other) {
        assertTrue(reverse.compare(range, other) < 0);
    }

    private void assertGreaterThan(AsnRange range, AsnRange other) {
        assertTrue(reverse.compare(range, other) > 0);
    }

    private void assertEqual(AsnRange range, AsnRange other) {
        assertTrue(reverse.compare(range, other) == 0);
    }
}
