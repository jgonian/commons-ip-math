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

import static com.github.jgonian.ipmath.Ipv4Range.parse;
import static com.github.jgonian.ipmath.PrefixUtils.*;
import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;

/**
 * Tests {@link PrefixUtils} with Ipv4
 */
public class PrefixUtilsIpv4Test extends AbstractPrefixUtilsTest {

    @Test
    public void shouldReturnTrueForValidPrefix() {
        assertTrue(PrefixUtils.isLegalPrefix(parse("0.0.0.0/0")));
    }

    @Test
    public void shouldReturnFalseForInvalidPrefix() {
        assertFalse(PrefixUtils.isLegalPrefix(parse("0.0.0.0-0.0.0.2")));
        assertFalse(PrefixUtils.isLegalPrefix(Ipv4Range.from(1585324288l).to(1585324799l)));
        assertFalse(PrefixUtils.isLegalPrefix(parse("0.0.0.1-0.0.0.3")));
        assertFalse(PrefixUtils.isLegalPrefix(parse("0.0.0.1-255.255.255.255")));
        assertFalse(PrefixUtils.isLegalPrefix(parse("0.0.0.0-255.255.255.254")));
        assertFalse(PrefixUtils.isLegalPrefix(parse("0.0.0.1-255.255.255.254")));
        assertFalse(PrefixUtils.isLegalPrefix(parse("0.0.0.2-255.255.255.254")));
    }

    @Test
    public void shouldGetPrefixLengthWhenCorrectPrefix() {
        assertEquals(32, PrefixUtils.getPrefixLength(parse("0.0.0.0-0.0.0.0")));
        assertEquals(32, PrefixUtils.getPrefixLength(parse("0.0.0.1-0.0.0.1")));
        assertEquals(30, PrefixUtils.getPrefixLength(parse("0.0.0.0-0.0.0.3")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToGetPrefixLengthWhenInvalidPrefix() {
        PrefixUtils.getPrefixLength(parse("0.0.0.0-0.0.0.2"));
    }

    @Test
    public void shouldFindBiggestAndSmallestPrefixWhenRangeIsSingleValidPrefix() {
        Ipv4Range range = parse("0.0.0.0/0");
        assertEquals(parse("0.0.0.0/0"), findMinimumPrefixForPrefixLength(range, 0).get());
        assertEquals(parse("0.0.0.0/0"), findMaximumPrefixForPrefixLength(range, 32).get());
    }

    @Test
    public void shouldFindBiggestAndSmallestPrefixWhenRangeIsNotValidPrefix() {
        Ipv4Range range = Ipv4Range.from("0.0.0.1").to("0.0.0.4");
        assertEquals(parse("0.0.0.2/31"), findMaximumPrefixForPrefixLength(range, 31).get());
        assertEquals(parse("0.0.0.2/31"), findMinimumPrefixForPrefixLength(range, 31).get());
        assertEquals(parse("0.0.0.2/31"), findMaximumPrefixForPrefixLength(range, 32).get());
        assertEquals(parse("0.0.0.1/32"), findMinimumPrefixForPrefixLength(range, 32).get());

        Ipv4Range otherRange = Ipv4Range.from("0.0.0.0").to((long) Math.pow(2, 32) - 2L);
        assertEquals(parse("0.0.0.0/1"), findMaximumPrefixForPrefixLength(otherRange, 1).get());
        assertEquals(parse("0.0.0.0/1"), findMinimumPrefixForPrefixLength(otherRange, 1).get());
        assertEquals(parse("0.0.0.0/1"), findMaximumPrefixForPrefixLength(otherRange, 2).get());
        assertEquals(parse("128.0.0.0/2"), findMinimumPrefixForPrefixLength(otherRange, 2).get());
        assertEquals(parse("0.0.0.0/1"), findMaximumPrefixForPrefixLength(otherRange, 32).get());
        assertEquals(parse("255.255.255.254/32"), findMinimumPrefixForPrefixLength(otherRange, 32).get());
    }

    @Test
    public void shouldNotFindBiggestAndSmallestPrefixWhenDoesNotExist() {
        Ipv4Range range = Ipv4Range.from("0.0.0.0").to((long) Math.pow(2, 32) - 2L);
        assertFalse(findMinimumPrefixForPrefixLength(range, 0).isPresent());
        assertFalse(findMaximumPrefixForPrefixLength(range, 0).isPresent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void findSmallestPrefixShouldThrowAnExceptionWhenRequestedPrefixLengthIsTooSmall() {
        findMinimumPrefixForPrefixLength(parse("0.0.0.1-0.0.0.10"), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findSmallestPrefixShouldThrowAnExceptionWhenRequestedPrefixLengthIsTooBig() {
        findMinimumPrefixForPrefixLength(parse("0.0.0.1-0.0.0.10"), 129);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findBiggestPrefixShouldThrowAnExceptionWhenRequestedPrefixLengthIsTooSmall() {
        findMaximumPrefixForPrefixLength(parse("0.0.0.1-0.0.0.10"), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findBiggestPrefixShouldThrowAnExceptionWhenRequestedPrefixLengthIsTooBig() {
        findMaximumPrefixForPrefixLength(parse("0.0.0.1-0.0.0.10"), 129);
    }

    @Test
    public void shouldSumIpv4Prefixes() {
        List<Integer> ipvXPrefixes = ipvXPrefixes(23, 23, 23, 25, 26);
        assertEquals(21, sumIpv4Prefixes(ipvXPrefixes));
    }

    @Test
    public void shouldSumIpv4PrefixesSingle() {
        List<Integer> ipvXPrefixes = ipvXPrefixes(22);
        assertEquals(22, sumIpv4Prefixes(ipvXPrefixes));
    }

    @Test
    public void shouldSumIpv4PrefixesDouble() {
        List<Integer> ipvXPrefixes = ipvXPrefixes(23, 23);
        assertEquals(22, sumIpv4Prefixes(ipvXPrefixes));
    }

    @Test
    public void shouldAttemptToSumIpv4PrefixesEvenIfResultIsTooBig() {
        List<Integer> ipvXPrefixes = ipvXPrefixes(1, 1, 1, 2, 3, 4, 5, 6);
        assertEquals(-1, sumIpv4Prefixes(ipvXPrefixes));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldValidateForNonValidPrefixes() {
        List<Integer> ipvXPrefixes = ipvXPrefixes(Ipv4.NUMBER_OF_BITS + 1);
        sumIpv4Prefixes(ipvXPrefixes);
    }

    @Test
    public void shouldExcludeIpv4Prefixes() {
        HashSet<Ipv4Range> excludeRanges = new HashSet<Ipv4Range>();
        excludeRanges.addAll(Arrays.asList(parse("0.0.0.2-0.0.0.3"), parse("0.0.0.5-0.0.0.6")));

        final SortedSet<Ipv4Range> actual = PrefixUtils.excludeFromRangeAndSplitIntoPrefixes( parse("0.0.0.1-0.0.0.6"), excludeRanges);

        HashSet<Ipv4Range> expectedPrefixes = new HashSet<Ipv4Range>();
        expectedPrefixes.addAll(Arrays.asList(parse("0.0.0.1/32"), parse("0.0.0.4/32")));
        assertEquals(expectedPrefixes, actual);
    }

    @Test
    public void shouldReturnOriginalRangeIfExcludeRangesAreEmpty() {
        HashSet<Ipv4Range> excludeRanges = new HashSet<Ipv4Range>();

        final SortedSet<Ipv4Range> actual = PrefixUtils.excludeFromRangeAndSplitIntoPrefixes( parse("0.0.0.0/30"), excludeRanges);

        HashSet<Ipv4Range> expectedPrefixes = new HashSet<Ipv4Range>();
        expectedPrefixes.addAll(Arrays.asList(parse("0.0.0.0/30")));
        assertEquals(expectedPrefixes, actual);
    }

    @Test
    public void shouldExcludeEverythingIfExcludeRangeIsBiggerThanOriginal() {
        HashSet<Ipv4Range> excludeRanges = new HashSet<Ipv4Range>();
        excludeRanges.addAll(Arrays.asList(parse("0.0.0.0/30")));

        final SortedSet<Ipv4Range> actual = PrefixUtils.excludeFromRangeAndSplitIntoPrefixes( parse("0.0.0.0/31"), excludeRanges);

        HashSet<Ipv4Range> expectedPrefixes = new HashSet<Ipv4Range>();
        assertEquals(expectedPrefixes, actual);
    }
    
}
