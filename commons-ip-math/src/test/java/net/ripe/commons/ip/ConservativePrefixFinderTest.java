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

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static net.ripe.commons.ip.Ipv6Range.parse;
import static org.junit.Assert.*;

public class ConservativePrefixFinderTest {

    private final ConservativePrefixFinder subject = ConservativePrefixFinder.newInstance();

    @Test
    public void shouldReturnNullIfRequestedPrefixIsTooBig() {
        assertNull(subject.findPrefixOrNull(9, in("::/10, 2::/20, 3::/30")));
    }

    @Test
    public void shouldFindExactMatch() {
        assertEquals(parse("::/10"), subject.findPrefixOrNull(10, in("::/10, 2::/20, 3::/30")));
        assertEquals(parse("2::/20"), subject.findPrefixOrNull(20, in("::/10, 2::/20, 3::/30")));
        assertEquals(parse("3::/30"), subject.findPrefixOrNull(30, in("::/10, 2::/20, 3::/30")));
    }

    @Test
    public void shouldFindPrefixFromClosestMatch() {
        assertEquals(parse("3::/31"), subject.findPrefixOrNull(31, in("::/10, 2::/20, 3::/30")));
        assertEquals(parse("2::/29"), subject.findPrefixOrNull(29, in("::/10, 2::/20, 3::/30")));
        assertEquals(parse("2::/21"), subject.findPrefixOrNull(21, in("::/10, 2::/20, 3::/30")));
        assertEquals(parse("::/19"), subject.findPrefixOrNull(19, in("::/10, 2::/20, 3::/30")));
    }

    @Test
    public void shouldFindPrefixFromTheFirstClosestMatch() {
        assertEquals(parse("2::/20"), subject.findPrefixOrNull(20, in("::/10, 2::/20, 3::/20")));
        assertEquals(parse("3::/20"), subject.findPrefixOrNull(20, in("::/10, 3::/20, 2::/20")));
    }

    private List<Ipv6Range> in(String commaSeparatedRanges) {
        String[] split = commaSeparatedRanges.split(",");
        List<Ipv6Range> result = new ArrayList<Ipv6Range>();
        for (String s : split) {
            result.add(parse(s.trim()));
        }
        return result;
    }
}
