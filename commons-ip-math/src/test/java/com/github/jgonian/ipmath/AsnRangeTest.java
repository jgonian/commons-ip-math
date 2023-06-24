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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static com.github.jgonian.ipmath.Asn.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class AsnRangeTest extends AbstractRangeTest<Asn, AsnRange> {

    private final Asn as1 = Asn.of(1L);
    private final Asn as2 = Asn.of(2L);
    private final Asn as3 = Asn.of(3L);

    @Override
    protected Asn from(String s) {
        return Asn.of(s);
    }

    @Override
    protected Asn to(String s) {
        return Asn.of(s);
    }

    @Override
    protected Asn item(String s) {
        return Asn.of(s);
    }

    @Override
    protected AsnRange getTestRange(Asn start, Asn end) {
        return new AsnRange(start, end);
    }

    @Override
    protected AsnRange getFullRange() {
        return new AsnRange(Asn.FIRST_ASN, Asn.LAST_32_BIT_ASN);
    }

    @Override
    public void shouldCalculateRangeSize() {
        assertEquals((Long) 1L, FIRST_ASN.asRange().size());
        assertEquals((Long) 4294967296L, AsnRange.from(FIRST_ASN).to(LAST_32_BIT_ASN).size());
    }

    @Test
    public void shouldParseRange() {
        assertEquals(AsnRange.from(3333l).to(4444l), AsnRange.parse(("AS3333-AS4444")));
    }

    @Test
    public void shouldParseEmptyRange() {
        assertEquals(AsnRange.from(3333l).to(3333l), AsnRange.parse(("AS3333-AS3333")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToParseIllegalRange() {
        AsnRange.parse("AS3333-AS2222");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToParseSingleResource() {
        AsnRange.parse("AS3333");
    }

    @Test
    public void shouldContainOnly16BitAsns() {
        AsnRange range = AsnRange.from(Asn.FIRST_ASN).to(Asn.LAST_16_BIT_ASN);
        assertTrue(range.containsOnly16BitAsns());
        assertFalse(range.containsOnly32BitAsns());
    }

    @Test
    public void shouldContainOnly32BitAsns() {
        AsnRange range = AsnRange.from(Asn.LAST_16_BIT_ASN.next()).to(Asn.LAST_32_BIT_ASN);
        assertFalse(range.containsOnly16BitAsns());
        assertTrue(range.containsOnly32BitAsns());
    }

    @Test
    public void shouldNotContainOnly16Or32BitAsns() {
        AsnRange range = AsnRange.from(Asn.LAST_16_BIT_ASN).to(Asn.LAST_16_BIT_ASN.next());
        assertFalse(range.containsOnly16BitAsns());
        assertFalse(range.containsOnly32BitAsns());
    }

    @Override
    public void testToString() {
        assertEquals("AS1-AS1", AsnRange.from(as1).to(as1).toString());
        assertEquals("AS1-AS3", AsnRange.from(as1).to(as3).toString());
    }

    @Test
    public void testSize() {
        assertEquals(Long.valueOf(1), as1.asRange().size());
        assertEquals(Long.valueOf(ASN_16_BIT_MAX_VALUE + 1), AsnRange.from(FIRST_ASN).to(Asn.LAST_16_BIT_ASN).size());
        assertEquals(Long.valueOf(ASN_32_BIT_MAX_VALUE + 1), AsnRange.from(FIRST_ASN).to(Asn.LAST_32_BIT_ASN).size());
    }

    @Override
    public void testIterator() {
        List<Asn> result = new ArrayList<Asn>();
        for (Asn asn : new AsnRange(as1, as3)) {
            result.add(asn);
        }
        assertEquals(Arrays.asList(as1, as2, as3), result);
    }

    @Test
    public void testBuilderWithLongs() {
        AsnRange range = AsnRange.from(1l).to(3l);
        assertEquals(as1, range.start());
        assertEquals(as3, range.end());
    }

    @Test
    public void testBuilderWithStrings() {
        AsnRange range = AsnRange.from("AS1").to("AS3");
        assertEquals(as1, range.start());
        assertEquals(as3, range.end());
    }

    @Test
    public void testBuilderWithAsn() {
        AsnRange range = AsnRange.from(as1).to(as3);
        assertEquals(as1, range.start());
        assertEquals(as3, range.end());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderWithInvalidRange() {
        AsnRange.from(as3).to(as1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderWithNullStart() {
        AsnRange.from((Asn) null).to(as3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderWithNullEnd() {
        AsnRange.from(as1).to((Asn) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullStart() {
        new AsnRange(null, as3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullEnd() {
        new AsnRange(as1, null);
    }
}
