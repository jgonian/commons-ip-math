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

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.github.jgonian.ipmath.Ipv4.FIRST_IPV4_ADDRESS;
import static com.github.jgonian.ipmath.Ipv4.LAST_IPV4_ADDRESS;
import static com.github.jgonian.ipmath.Ipv4.MAXIMUM_VALUE;
import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

public class Ipv4RangeTest extends AbstractRangeTest<Ipv4, Ipv4Range> {

    Ipv4 ip1 = Ipv4.of("0.0.0.1");
    Ipv4 ip2 = Ipv4.of("0.0.0.2");
    Ipv4 ip3 = Ipv4.of("0.0.0.3");

    @Override
    protected Ipv4 from(String s) {
        return Ipv4.of(Long.parseLong(s));
    }

    @Override
    protected Ipv4 to(String s) {
        return Ipv4.of(Long.parseLong(s));
    }

    @Override
    protected Ipv4 item(String s) {
        return Ipv4.of(Long.parseLong(s));
    }

    @Override
    protected Ipv4Range getTestRange(Ipv4 start, Ipv4 end) {
        return new Ipv4Range(start, end);
    }

    @Override
    protected Ipv4Range getFullRange() {
        return new Ipv4Range(FIRST_IPV4_ADDRESS, LAST_IPV4_ADDRESS);
    }

    @Override
    public void shouldCalculateRangeSize() {
        assertEquals((Long) 1L, FIRST_IPV4_ADDRESS.asRange().size());
        assertEquals((Long) 4294967296L, Ipv4Range.from(FIRST_IPV4_ADDRESS).to(LAST_IPV4_ADDRESS).size());
    }

    @Test
    public void shouldParseDashNotation() {
        assertEquals(Ipv4Range.from(FIRST_IPV4_ADDRESS).to(LAST_IPV4_ADDRESS), Ipv4Range.parse("0.0.0.0-255.255.255.255"));
    }

    @Test
    public void shouldParseDashNotationWhenEmptyRange() {
        assertEquals(Ipv4.parse("192.168.0.1").asRange(), Ipv4Range.parse("192.168.0.1-192.168.0.1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToParseDashNotationWhenIllegalRange() {
        Ipv4Range.parse("0.0.0.10-0.0.0.1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToParseDashNotationWhenSingleResource() {
        Ipv4Range.parse("0.0.0.1");
    }

    @Test
    public void shouldParseCidrNotation() {
        assertEquals(Ipv4Range.from(FIRST_IPV4_ADDRESS).to(LAST_IPV4_ADDRESS), Ipv4Range.parseCidr("0.0.0.0/0"));
    }

    @Test
    public void shouldParseCidrWhenEmptyRange() {
        assertEquals(Ipv4.parse("192.168.0.1").asRange(), Ipv4Range.parseCidr("192.168.0.1/32"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToParseCidrWhenIllegalPrefix() {
        Ipv4Range.parseCidr("0.0.0.10/33");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToParseCidrWhenIllegalPrefixTwo() {
        Ipv4Range.parse("5.57.249.88/28");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToParseCidrWhenSingleResource() {
        Ipv4Range.parseCidr("0.0.0.1");
    }

    @Test
    public void shouldParseWithPrefix() {
        assertEquals(Ipv4Range.from(FIRST_IPV4_ADDRESS).to(LAST_IPV4_ADDRESS), Ipv4Range.from("0.0.0.0").andPrefixLength("0"));
    }

    @Test
    public void shouldParseWithPrefixWhenEmptyRange() {
        assertEquals(Ipv4.parse("192.168.0.1").asRange(), Ipv4Range.from("192.168.0.1").andPrefixLength("32"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToParseWithPrefixWhenIllegalPrefix() {
        Ipv4Range.from("0.0.0.10").andPrefixLength("33");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToParseWithPrefixWhenPrefixIsNull() {
        Ipv4Range.from("0.0.0.1").andPrefixLength(null);
    }

    @Test
    public void shouldParseDecimalNotation() {
        assertEquals(Ipv4Range.from(FIRST_IPV4_ADDRESS).to(LAST_IPV4_ADDRESS), Ipv4Range.parseDecimalNotation(FIRST_IPV4_ADDRESS.value() + "-" + LAST_IPV4_ADDRESS.value()));
    }

    @Test
    public void shouldParseDecimalWhenEmptyRange() {
        assertEquals(Ipv4.parse("0.0.0.1").asRange(), Ipv4Range.parseDecimalNotation("1-1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToParseDecimalNotationWhenSingleResource() {
        Ipv4Range.parseDecimalNotation("1");
    }

    @Override
    public void testToString() {
        assertEquals("192.168.0.0-192.168.255.254", Ipv4Range.parse("192.168.0.0-192.168.255.254").toString());
        assertEquals("192.168.0.0/16", Ipv4Range.parse("192.168.0.0-192.168.255.255").toString());
    }

    @Test
    public void testToStringInRangeNotation() {
        assertEquals("0.0.0.0-255.255.255.255", new Ipv4Range(FIRST_IPV4_ADDRESS, LAST_IPV4_ADDRESS).toStringInRangeNotation());
    }

    @Test
    public void testToStringInRangeNotationWithSpaces() {
        assertEquals("0.0.0.0 - 255.255.255.255", new Ipv4Range(FIRST_IPV4_ADDRESS, LAST_IPV4_ADDRESS).toStringInRangeNotationWithSpaces());
    }

    @Test
    public void testToStringSpecialFoundInvalidCase() {
        assertFalse("94.126.33.0/23".equals(Ipv4Range.from(1585324288L).to(1585324799L).toString()));
    }

    @Test
    public void testToStringInCidrNotation() {
        assertEquals("0.0.0.0/0", new Ipv4Range(FIRST_IPV4_ADDRESS, LAST_IPV4_ADDRESS).toStringInCidrNotation());
    }

    @Test
    public void testToStringInDecimalNotation() {
        assertEquals("0-4294967295", new Ipv4Range(FIRST_IPV4_ADDRESS, LAST_IPV4_ADDRESS).toStringInDecimalNotation());
    }

    @Test
    public void testSize() {
        assertEquals(new Long(1), ip1.asRange().size());
        assertEquals(new Long(MAXIMUM_VALUE + 1), Ipv4Range.from(FIRST_IPV4_ADDRESS).to(LAST_IPV4_ADDRESS).size());
    }

    @Override
    public void testIterator() {
        List<Ipv4> result = new ArrayList<Ipv4>();
        for (Ipv4 ipv4 : new Ipv4Range(ip1, ip3)) {
            result.add(ipv4);
        }
        assertEquals(Arrays.asList(ip1, ip2, ip3), result);
    }

    @Override
    public void testIteratorEnd() {
        List<Ipv4> result = new ArrayList<Ipv4>();
        for (Ipv4 ipv4 : Ipv4Range.from(LAST_IPV4_ADDRESS).to(LAST_IPV4_ADDRESS)) {
            result.add(ipv4);
        }
        assertEquals(Collections.singletonList(LAST_IPV4_ADDRESS), result);
    }

    @Test
    public void testBuilderWithLongs() {
        Ipv4Range range = Ipv4Range.from(1l).to(3l);
        assertEquals(ip1, range.start());
        assertEquals(ip3, range.end());
    }

    @Test
    public void testBuilderWithStrings() {
        Ipv4Range range = Ipv4Range.from("0.0.0.1").to("0.0.0.3");
        assertEquals(ip1, range.start());
        assertEquals(ip3, range.end());
    }

    @Test
    public void testBuilderWithIpv4s() {
        Ipv4Range range = Ipv4Range.from(ip1).to(ip3);
        assertEquals(ip1, range.start());
        assertEquals(ip3, range.end());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderWithInvalidRange() {
        Ipv4Range.from(ip3).to(ip1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderWithNullStart() {
        Ipv4Range.from((Ipv4) null).to(ip3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderWithNullEnd() {
        Ipv4Range.from(ip1).to((Ipv4) null);
    }

    @Test
    public void testBuilderWithValidAddressAndPrefixLength() {
        Ipv4Range range = Ipv4Range.from("0.0.0.2").andPrefixLength(31);
        assertEquals(Ipv4.of("0.0.0.2"), range.start());
        assertEquals(Ipv4.of("0.0.0.3"), range.end());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderWithInvalidAddressAndLength() {
        Ipv4Range.from("0.0.0.3").andPrefixLength(31);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderWithTooSmallPrefixLength() {
        Ipv4Range.from("0.0.0.2").andPrefixLength(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderWithTooBigPrefixLength() {
        Ipv4Range.from("0.0.0.2").andPrefixLength(33);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullStart() {
        new Ipv4Range(null, ip3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullEnd() {
        new Ipv4Range(ip1, null);
    }

    @Test
    public void shouldSplitIntoPrefixes() {
        validateSplitIntoPrefixes(new String[]{"0.0.0.0/32"}, "0.0.0.0-0.0.0.0");
        validateSplitIntoPrefixes(new String[]{"0.0.0.0/31"}, "0.0.0.0-0.0.0.1");
        validateSplitIntoPrefixes(new String[]{"0.0.0.0/30"}, "0.0.0.0-0.0.0.3");
        validateSplitIntoPrefixes(new String[]{"0.0.0.0/29"}, "0.0.0.0-0.0.0.7");
        validateSplitIntoPrefixes(new String[]{"0.0.0.0/31", "0.0.0.2/32"}, "0.0.0.0-0.0.0.2");
        validateSplitIntoPrefixes(new String[]{"0.0.0.0/30", "0.0.0.4/32"}, "0.0.0.0-0.0.0.4");
        validateSplitIntoPrefixes(new String[]{"0.0.0.0/30", "0.0.0.4/31"}, "0.0.0.0-0.0.0.5");
        validateSplitIntoPrefixes(new String[]{"0.0.0.0/30", "0.0.0.4/31", "0.0.0.6/32"}, "0.0.0.0-0.0.0.6");
        validateSplitIntoPrefixes(new String[]{"0.0.0.1/32"}, "0.0.0.1-0.0.0.1");
        validateSplitIntoPrefixes(new String[]{"0.0.0.1/32", "0.0.0.2/32"}, "0.0.0.1-0.0.0.2");
        validateSplitIntoPrefixes(new String[]{"0.0.0.1/32", "0.0.0.2/31"}, "0.0.0.1-0.0.0.3");
        validateSplitIntoPrefixes(new String[]{"0.0.0.2/31"}, "0.0.0.2-0.0.0.3");
        validateSplitIntoPrefixes(new String[]{"0.0.0.2/31", "0.0.0.4/32"}, "0.0.0.2-0.0.0.4");
    }

    private void validateSplitIntoPrefixes(String[] expectedPrefixes, String rangeToSplit) {
        List<Ipv4Range> expected = new ArrayList<Ipv4Range>();
        for (String prefix : expectedPrefixes) {
            expected.add(Ipv4Range.parse(prefix));
        }
        assertEquals(expected, Ipv4Range.parse(rangeToSplit).splitToPrefixes());
    }

    @Test
    public void shouldSplitIntoPrefixesAllIpv4SpaceExceptFirstAddress() {
        Ipv4Range range = Ipv4Range.from("0.0.0.1").to(Ipv4.LAST_IPV4_ADDRESS);
        assertEquals(32, range.splitToPrefixes().size());
    }
}
