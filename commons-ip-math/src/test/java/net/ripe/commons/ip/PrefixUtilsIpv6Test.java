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
import java.math.BigInteger;
import org.junit.Test;

/**
 * Tests {@link PrefixUtils} with Ipv6
 */
public class PrefixUtilsIpv6Test {

    @Test
    public void shouldReturnTrueForValidPrefix() {
        assertTrue(PrefixUtils.isLegalPrefix(Ipv6Range.parse("::/0")));
    }

    @Test
    public void shouldReturnFalseForInvalidPrefix() {
        assertFalse(PrefixUtils.isLegalPrefix(Ipv6Range.parse("::0-::2")));
        assertFalse(PrefixUtils.isLegalPrefix(Ipv6Range.parse("::1-::3")));
        assertFalse(PrefixUtils.isLegalPrefix(Ipv6Range.parse("::1-ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff")));
        assertFalse(PrefixUtils.isLegalPrefix(Ipv6Range.parse("::-ffff:ffff:ffff:ffff:ffff:ffff:ffff:fffe")));
        assertFalse(PrefixUtils.isLegalPrefix(Ipv6Range.parse("::2-ffff:ffff:ffff:ffff:ffff:ffff:ffff:fffe")));
    }

    @Test
    public void shouldGetPrefixLengthWhenCorrectPrefix() {
        assertEquals(128, PrefixUtils.getPrefixLength(Ipv6Range.parse("::0-::0")));
        assertEquals(128, PrefixUtils.getPrefixLength(Ipv6Range.parse("::1-::1")));
        assertEquals(126, PrefixUtils.getPrefixLength(Ipv6Range.parse("::0-::3")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToGetPrefixLengthWhenInvalidPrefix() {
        PrefixUtils.getPrefixLength(Ipv6Range.parse("::0-::2"));
    }

    @Test
    public void shouldFindBiggestAndSmallestPrefixWhenRangeIsSingleValidPrefix() {
        Ipv6Range range = Ipv6Range.parse("::/0");
        assertEquals(Ipv6Range.parse("::/0"), findMinimumPrefixForPrefixLength(range, 0).get());
        assertEquals(Ipv6Range.parse("::/0"), findMaximumPrefixForPrefixLength(range, 128).get());
    }

    @Test
    public void shouldFindBiggestAndSmallestPrefixWhenRangeIsNotValidPrefix() {
        Ipv6Range range = Ipv6Range.from("::1").to("::4");
        assertEquals(Ipv6Range.parse("::2/127"), findMaximumPrefixForPrefixLength(range, 127).get());
        assertEquals(Ipv6Range.parse("::2/127"), findMinimumPrefixForPrefixLength(range, 127).get());
        assertEquals(Ipv6Range.parse("::2/127"), findMaximumPrefixForPrefixLength(range, 128).get());
        assertEquals(Ipv6Range.parse("::1/128"), findMinimumPrefixForPrefixLength(range, 128).get());

        Ipv6Range otherRange = Ipv6Range.from("::").to(BigInteger.valueOf(2).pow(128).subtract(BigInteger.valueOf(2)));
        assertEquals(Ipv6Range.parse("::/1"), findMaximumPrefixForPrefixLength(otherRange, 1).get());
        assertEquals(Ipv6Range.parse("::/1"), findMinimumPrefixForPrefixLength(otherRange, 1).get());
        assertEquals(Ipv6Range.parse("::/1"), findMaximumPrefixForPrefixLength(otherRange, 2).get());
        assertEquals(Ipv6Range.parse("8000::/2"), findMinimumPrefixForPrefixLength(otherRange, 2).get());
        assertEquals(Ipv6Range.parse("::/1"), findMaximumPrefixForPrefixLength(otherRange, 128).get());
        assertEquals(Ipv6Range.parse("ffff:ffff:ffff:ffff:ffff:ffff:ffff:fffe/128"), findMinimumPrefixForPrefixLength(otherRange, 128).get());
    }

    @Test
    public void shouldNotFindBiggestAndSmallestPrefixWhenDoesNotExist() {
        Ipv6Range range = Ipv6Range.from("::").to(BigInteger.valueOf(2).pow(128).subtract(BigInteger.valueOf(2)));
        assertFalse(findMinimumPrefixForPrefixLength(range, 0).isPresent());
        assertFalse(findMaximumPrefixForPrefixLength(range, 0).isPresent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void findSmallestPrefixShouldThrowAnExceptionWhenRequestedPrefixLengthIsTooSmall() {
        findMinimumPrefixForPrefixLength(Ipv6Range.parse("::1-::10"), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findSmallestPrefixShouldThrowAnExceptionWhenRequestedPrefixLengthIsTooBig() {
        findMinimumPrefixForPrefixLength(Ipv6Range.parse("::1-::10"), 129);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findBiggestPrefixShouldThrowAnExceptionWhenRequestedPrefixLengthIsTooSmall() {
        findMaximumPrefixForPrefixLength(Ipv6Range.parse("::1-::10"), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findBiggestPrefixShouldThrowAnExceptionWhenRequestedPrefixLengthIsTooBig() {
        findMaximumPrefixForPrefixLength(Ipv6Range.parse("::1-::10"), 129);
    }
}
