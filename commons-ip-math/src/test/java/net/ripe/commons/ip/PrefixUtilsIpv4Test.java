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

import static net.ripe.commons.ip.PrefixUtils.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Tests {@link PrefixUtils} with Ipv4
 */
public class PrefixUtilsIpv4Test {

    @Test
    public void shouldReturnTrueForValidPrefix() {
        assertTrue(PrefixUtils.isLegalPrefix(Ipv4Range.parse("0.0.0.0/0")));
    }

    @Test
    public void shouldReturnFalseForInvalidPrefix() {
        assertFalse(PrefixUtils.isLegalPrefix(Ipv4Range.parse("0.0.0.0-0.0.0.2")));
        assertFalse(PrefixUtils.isLegalPrefix(Ipv4Range.from(1585324288l).to(1585324799l)));
        assertFalse(PrefixUtils.isLegalPrefix(Ipv4Range.parse("0.0.0.1-0.0.0.3")));
        assertFalse(PrefixUtils.isLegalPrefix(Ipv4Range.parse("0.0.0.1-255.255.255.255")));
        assertFalse(PrefixUtils.isLegalPrefix(Ipv4Range.parse("0.0.0.0-255.255.255.254")));
        assertFalse(PrefixUtils.isLegalPrefix(Ipv4Range.parse("0.0.0.1-255.255.255.254")));
        assertFalse(PrefixUtils.isLegalPrefix(Ipv4Range.parse("0.0.0.2-255.255.255.254")));
    }

    @Test
    public void shouldGetPrefixLengthWhenCorrectPrefix() {
        assertEquals(32, PrefixUtils.getPrefixLength(Ipv4Range.parse("0.0.0.0-0.0.0.0")));
        assertEquals(32, PrefixUtils.getPrefixLength(Ipv4Range.parse("0.0.0.1-0.0.0.1")));
        assertEquals(30, PrefixUtils.getPrefixLength(Ipv4Range.parse("0.0.0.0-0.0.0.3")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToGetPrefixLengthWhenInvalidPrefix() {
        PrefixUtils.getPrefixLength(Ipv4Range.parse("0.0.0.0-0.0.0.2"));
    }

    @Test
    public void shouldFindBiggestAndSmallestPrefixWhenRangeIsSingleValidPrefix() {
        Ipv4Range range = Ipv4Range.parse("0.0.0.0/0");
        assertEquals(Ipv4Range.parse("0.0.0.0/0"), findMinimumPrefixForPrefixLength(range, 0).get());
        assertEquals(Ipv4Range.parse("0.0.0.0/0"), findMaximumPrefixForPrefixLength(range, 32).get());
    }

    @Test
    public void shouldFindBiggestAndSmallestPrefixWhenRangeIsNotValidPrefix() {
        Ipv4Range range = Ipv4Range.from("0.0.0.1").to("0.0.0.4");
        assertEquals(Ipv4Range.parse("0.0.0.2/31"), findMaximumPrefixForPrefixLength(range, 31).get());
        assertEquals(Ipv4Range.parse("0.0.0.2/31"), findMinimumPrefixForPrefixLength(range, 31).get());
        assertEquals(Ipv4Range.parse("0.0.0.2/31"), findMaximumPrefixForPrefixLength(range, 32).get());
        assertEquals(Ipv4Range.parse("0.0.0.1/32"), findMinimumPrefixForPrefixLength(range, 32).get());

        Ipv4Range otherRange = Ipv4Range.from("0.0.0.0").to((long) Math.pow(2, 32) - 2L);
        assertEquals(Ipv4Range.parse("0.0.0.0/1"), findMaximumPrefixForPrefixLength(otherRange, 1).get());
        assertEquals(Ipv4Range.parse("0.0.0.0/1"), findMinimumPrefixForPrefixLength(otherRange, 1).get());
        assertEquals(Ipv4Range.parse("0.0.0.0/1"), findMaximumPrefixForPrefixLength(otherRange, 2).get());
        assertEquals(Ipv4Range.parse("128.0.0.0/2"), findMinimumPrefixForPrefixLength(otherRange, 2).get());
        assertEquals(Ipv4Range.parse("0.0.0.0/1"), findMaximumPrefixForPrefixLength(otherRange, 32).get());
        assertEquals(Ipv4Range.parse("255.255.255.254/32"), findMinimumPrefixForPrefixLength(otherRange, 32).get());
    }

    @Test
    public void shouldNotFindBiggestAndSmallestPrefixWhenDoesNotExist() {
        Ipv4Range range = Ipv4Range.from("0.0.0.0").to((long) Math.pow(2, 32) - 2L);
        assertFalse(findMinimumPrefixForPrefixLength(range, 0).isPresent());
        assertFalse(findMaximumPrefixForPrefixLength(range, 0).isPresent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void findSmallestPrefixShouldThrowAnExceptionWhenRequestedPrefixLengthIsTooSmall() {
        findMinimumPrefixForPrefixLength(Ipv4Range.parse("0.0.0.1-0.0.0.10"), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findSmallestPrefixShouldThrowAnExceptionWhenRequestedPrefixLengthIsTooBig() {
        findMinimumPrefixForPrefixLength(Ipv4Range.parse("0.0.0.1-0.0.0.10"), 129);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findBiggestPrefixShouldThrowAnExceptionWhenRequestedPrefixLengthIsTooSmall() {
        findMaximumPrefixForPrefixLength(Ipv4Range.parse("0.0.0.1-0.0.0.10"), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findBiggestPrefixShouldThrowAnExceptionWhenRequestedPrefixLengthIsTooBig() {
        findMaximumPrefixForPrefixLength(Ipv4Range.parse("0.0.0.1-0.0.0.10"), 129);
    }
}
