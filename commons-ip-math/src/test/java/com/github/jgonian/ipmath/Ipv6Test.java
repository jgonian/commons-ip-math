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

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;

public class Ipv6Test {

    @Test
    public void testEqualsContract() {
        EqualsVerifier.forClass(Ipv6.class).suppress(Warning.NULL_FIELDS).withRedefinedSuperclass().verify();
    }

    @Test
    public void testFactoryMethodWithBigInteger() {
        assertEquals(new Ipv6(BigInteger.ZERO), Ipv6.of(BigInteger.ZERO));
    }

    @Test
    public void testFactoryMethodWithString() {
        assertEquals(new Ipv6(BigInteger.ZERO), Ipv6.of("::"));
    }

    // Representing IPv6 Addresses

    @Test
    public void shouldPrintFirstIpv6Address() {
        assertEquals("::", Ipv6.FIRST_IPV6_ADDRESS.toString());
    }

    @Test
    public void shouldPrintLastIpv6Address() {
        assertEquals("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff", Ipv6.LAST_IPV6_ADDRESS.toString());
    }

    @Test
    public void shouldPrintIpv6AddressWithTrailingZeros() {
        assertEquals("91ed:ffff:8948:c0a3:1:dc9e:bed9:0", Ipv6.parse("91ed:ffff:8948:c0a3:1:dc9e:bed9:0").toString());
    }

    @Test
    public void shouldPrintIpv6AddressWithLeadingZeros() {
        assertEquals("0:1:4d09:ffff:1:ffff:0:ffff", Ipv6.parse("0:1:4d09:ffff:1:ffff:0:ffff").toString());
        assertEquals("0:16b1:ffff:ffff:1c18:4e9b:31b6:0", Ipv6.parse("0:16b1:ffff:ffff:1c18:4e9b:31b6:0").toString());
        assertEquals("::ffff:ffff:1c18:4e9b:31b6:0", Ipv6.parse("0:0:ffff:ffff:1c18:4e9b:31b6:0").toString());
    }

    @Test
    public void shouldSuppressLeadingZeros() {
        // rfc5952 - §4.1 Handling Leading Zeros in a 16-Bit Field
        assertEquals("2001:db8::1", Ipv6.parse("2001:0db8::0001").toString());
    }

    @Test
    public void shouldRepresentOneSingle16Bit0000FieldAs0() {
        // rfc5952 - §4.1 Handling Leading Zeros in a 16-Bit Field
        assertEquals("2001:db8:0:a::1", Ipv6.parse("2001:0db8:0000:a::0001").toString());
    }

    @Test
    public void shouldShortenAsMuchAsPossible() {
        // rfc5952 - §4.2.1 Shorten as Much as Possible
        assertEquals("2001:db8::2:1", Ipv6.parse("2001:db8:0:0:0:0:2:1").toString());
        assertEquals("2001:db8::1", Ipv6.parse("2001:db8::0:1").toString());
    }

    @Test
    public void shouldShortenFromLeft() {
        assertEquals("::a:0:dead:0:b:0", Ipv6.parse("0:0:a:0:dead:0:b:0").toString());
    }

    @Test
    public void shouldShortenFromRight() {
        assertEquals("a:0:a:0:dead::", Ipv6.parse("a:0:a:0:dead:0:0:0").toString());
    }

    @Test
    public void shouldNotShortenJustOne16BitZeroField() {
        // rfc5952 - §4.2.2 Handling One 16-Bit 0 Field
        assertEquals("2001:db8:0:1:1:1:1:1", Ipv6.parse("2001:db8::1:1:1:1:1").toString());
    }

    @Test
    public void shouldShortenTheLongestRunOfConsecutiveZeroFields() {
        // rfc5952 - §4.2.3 Choice in Placement of "::"
        assertEquals("2001:0:0:1::1", Ipv6.parse("2001:0:0:1:0:0:0:1").toString());
        assertEquals("2001::1:0:0:1", Ipv6.parse("2001:0:0:0:1:0:0:1").toString());
    }

    @Test
    public void shouldShortenTheFirstSequenceOfZeroBitsWhenTheLengthOfConsecutiveZeroFieldsAreEqual() {
        // rfc5952 - §4.2.3 Choice in Placement of "::"
        assertEquals("2001::1:1:0:0:1", Ipv6.parse("2001:0:0:1:1:0:0:1").toString());
    }

    @Test
    public void shouldPrintCharactersInLowercase() {
        // rfc5952 - §4.3 Lowercase
        assertEquals("2001:0:1234::c1c0:abcd:876", Ipv6.parse("2001:0:1234::C1C0:ABCD:876").toString());
        assertEquals("3ffe:b00::1:0:0:a", Ipv6.parse("3fFe:B00::1:0:0:a").toString());
    }

    @Test
    public void shouldParseCompressedAddresses() {
        // rfc4291 - §2.2) 2.
        assertEquals(Ipv6.parse("2001:DB8:0:0:8:800:200C:417A"), Ipv6.parse("2001:DB8::8:800:200C:417A"));
        assertEquals(Ipv6.parse("FF01:0:0:0:0:0:0:101"), Ipv6.parse("FF01::101"));
        assertEquals(Ipv6.parse("0:0:0:0:0:0:0:1"), Ipv6.parse("::1"));
        assertEquals(Ipv6.parse("0:0:0:0:0:0:0:0"), Ipv6.parse("::"));
    }

    @Test
    public void leadingZerosIn16BitFieldCanBeOmitted() {
        assertEquals(Ipv6.parse("2001:db8:aaaa:bbbb:cccc:dddd:eeee:0001"), Ipv6.parse("2001:db8:aaaa:bbbb:cccc:dddd:eeee:001"));
        assertEquals(Ipv6.parse("2001:db8:aaaa:bbbb:cccc:dddd:eeee:0001"), Ipv6.parse("2001:db8:aaaa:bbbb:cccc:dddd:eeee:01"));
        assertEquals(Ipv6.parse("2001:db8:aaaa:bbbb:cccc:dddd:eeee:0001"), Ipv6.parse("2001:db8:aaaa:bbbb:cccc:dddd:eeee:1"));
    }

    @Test
    public void shouldBeAbleToCompressZeros() {
        assertEquals(Ipv6.parse("2001:db8:aaaa:bbbb:cccc:dddd:0:1"), Ipv6.parse("2001:db8:aaaa:bbbb:cccc:dddd::1"));
        assertEquals(Ipv6.parse("2001:db8:0:0:0:0:0:1"), Ipv6.parse("2001:db8:0:0:0::1"));
        assertEquals(Ipv6.parse("2001:db8:0:0:0:0:0:1"), Ipv6.parse("2001:db8:0:0::1"));
        assertEquals(Ipv6.parse("2001:db8:0:0:0:0:0:1"), Ipv6.parse("2001:db8:0::1"));
        assertEquals(Ipv6.parse("2001:db8:0:0:0:0:0:1"), Ipv6.parse("2001:db8::1"));
    }

    @Test
    public void shouldParseAddressesWithLeadingOrTrailingSpaces() {
        assertEquals("1:2:3:4:5:6:0:8", Ipv6.parse("   1:2:3:4:5:6::8").toString());
        assertEquals("1:2:3:4:5:6:0:8", Ipv6.parse("1:2:3:4:5:6::8    ").toString());
    }

    @Test
    public void shouldParseDifferentRepresentationsOfTheSameAddress() {
        Ipv6 ipv6 = Ipv6.parse("2001:db8:0:0:1:0:0:1");
        ArrayList<Ipv6> list = new ArrayList<Ipv6>();
        list.add(Ipv6.parse("2001:0db8:0:0:1:0:0:1"));
        list.add(Ipv6.parse("2001:db8::1:0:0:1"));
        list.add(Ipv6.parse("2001:db8::0:1:0:0:1"));
        list.add(Ipv6.parse("2001:0db8::1:0:0:1"));
        list.add(Ipv6.parse("2001:db8:0:0:1::1"));
        list.add(Ipv6.parse("2001:db8:0000:0:1::1"));
        list.add(Ipv6.parse("2001:DB8:0:0:1::1"));

        for (Ipv6 ipv6Variation : list) {
            assertEquals(ipv6, ipv6Variation);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotParseNull() {
        Ipv6.parse(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnEmptyString() {
        Ipv6.parse("");
    }

    @Test
    public void shouldParseFullAddressesCaseInsensitively() {
        assertEquals(Ipv6.parse("abcd:ef01:2345:6789:abcd:ef01:2345:6789"), Ipv6.parse("ABCD:EF01:2345:6789:ABCD:EF01:2345:6789"));
        assertEquals(Ipv6.parse("2001:db8:0:0:8:800:200c:417a"), Ipv6.parse("2001:DB8:0:0:8:800:200C:417A"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnMissingSegment() {
        Ipv6.parse("1:2:3:4:5:6:7");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnExtraSegment() {
        Ipv6.parse("1:2:3:4:5:6:7:8:9");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotParseIpv6AddressesWithLessThan7ColonsWithoutDoubleColon() {
        Ipv6.parse("a:b:c");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotParseIpv6AddressesWith7ColonsOnly() {
        Ipv6.parse(":::::::");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotContainDoubleColonsMoreThanOnce() {
        Ipv6.parse("2001:db8::aaaa::0:1");
    }

    @Test
    public void shouldFailOnInternalSpace() {
        try {
            Ipv6.parse("2001:0000:1234: 0000:0000:C1C0:ABCD:0876");
            fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            Ipv6.parse("2001:0000:1234:0000 :0000:C1C0:ABCD:0876");
            fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void shouldFailOnExtraZero() {
        try {
            Ipv6.parse("02001:0000:1234:0000:0000:C1C0:ABCD:0876");
            fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            Ipv6.parse("2001:0000:01234:0000:0000:C1C0:ABCD:0876");
            fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void shouldFailOnMultipleDoubleColons() {
        try {
            Ipv6.parse("3ffe:b00::1::a");
            fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            Ipv6.parse("::1111:2222:3333:4444:5555:6666::");
            fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void shouldFailIfSegmentOutOfBound() {
        try {
            Ipv6.parse("::2ffff:10");
            fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            Ipv6.parse("::-2:10");
            fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    // IPv6 Addresses with Embedded IPv4 Addresses

    @Test
    public void shouldParseIpv4CompatibleIpv6Addresses() {
        // |                80 bits               | 16 |      32 bits        |
        // +--------------------------------------+--------------------------+
        // |0000..............................0000|0000|    IPv4 address     |
        // +--------------------------------------+----+---------------------+
        assertEquals(Ipv6.parse("0:0:0:0:0:0:1.2.3.4"), Ipv6.parse("::1.2.3.4"));
        assertEquals(Ipv6.parse("0:0:0:0:0:0:102:304"), Ipv6.parse("::1.2.3.4"));
    }

    @Test
    public void shouldParseIpv4MappedIpv6Addresses() {
        // |                80 bits               | 16 |      32 bits        |
        // +--------------------------------------+--------------------------+
        // |0000..............................0000|FFFF|    IPv4 address     |
        // +--------------------------------------+----+---------------------+
        assertEquals(Ipv6.parse("0:0:0:0:0:FFFF:1.2.3.4"), Ipv6.parse("::FFFF:1.2.3.4"));
        assertEquals(Ipv6.parse("0:0:0:0:0:FFFF:102:304"), Ipv6.parse("::FFFF:1.2.3.4"));
    }

    @Test
    public void shouldParseIpv4EmbeddedFormat() {
        // Examples are copied from http://download.dartware.com/thirdparty/test-ipv6-regex.pl
        Ipv6.parse("1:2:3:4:5:6:1.2.3.4");
        Ipv6.parse("1:2:3:4:5::1.2.3.4");
        Ipv6.parse("1:2:3:4::1.2.3.4");
        Ipv6.parse("1:2:3::1.2.3.4");
        Ipv6.parse("1:2::1.2.3.4");
        Ipv6.parse("1::1.2.3.4");
        Ipv6.parse("1:2:3:4::5:1.2.3.4");
        Ipv6.parse("1:2:3::5:1.2.3.4");
        Ipv6.parse("1:2::5:1.2.3.4");
        Ipv6.parse("1::5:1.2.3.4");
        Ipv6.parse("1::5:11.22.33.44");
        Ipv6.parse("fe80::217:f2ff:254.7.237.98");
        Ipv6.parse("::ffff:192.168.1.26");
        Ipv6.parse("::ffff:192.168.1.1");

        Ipv6.parse("0:0:0:0:0:0:13.1.68.3");        // IPv4-compatible IPv6 address, full, deprecated
        Ipv6.parse("::13.1.68.3");                  // IPv4-compatible IPv6 address, compressed, deprecated
        Ipv6.parse("0:0:0:0:0:FFFF:129.144.52.38"); // IPv4-mapped IPv6 address, full
        Ipv6.parse("::FFFF:129.144.52.38");         // IPv4-mapped IPv6 address, compressed
        Ipv6.parse("fe80:0:0:0:204:61ff:254.157.241.86");
        Ipv6.parse("fe80::204:61ff:254.157.241.86");
        Ipv6.parse("::ffff:12.34.56.78");
    }

    @Test
    public void shouldFailIfIpv4PartExceedsBounds() {
        try {
            Ipv6.parse("::400.2.3.4");
            fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            Ipv6.parse("::260.2.3.4");
            fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            Ipv6.parse("::256.2.3.4");
            fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            Ipv6.parse("::1.256.3.4");
            fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            Ipv6.parse("::1.2.256.4");
            fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            Ipv6.parse("::1.2.256.256");
            fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            Ipv6.parse("::300.2.3.4");
            fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            Ipv6.parse("::1.300.3.4");
            fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            Ipv6.parse("::1.2.300.4");
            fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            Ipv6.parse("::1.2.3.300");
            fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            Ipv6.parse("::256.256.256.256");
            fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void shouldFailIfIpv4PartContainsInvalidCharacters() {
        try {
            Ipv6.parse("::255Z255X255Y255");
            fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            Ipv6.parse("::192x168.1.26");
            fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void shouldFailIfIpv4PartIsMisplaced() {
        try {
            Ipv6.parse("1.2.3.4:FFFF:0000:0000:0000::0000");
            fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            Ipv6.parse("1.2.3.4::");
            fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void shouldFailIfIpv4PartContainsLeadingZeros() {
        try {
            Ipv6.parse("::FFFF:254.157.241.086");
            fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void shouldParseIfIpv4PartContainsSingleZeros() {
        assertEquals(Ipv6.parse("::0.0.0.0"), Ipv6.parse("::"));
        assertEquals(Ipv6.parse("::0.0.0.1"), Ipv6.parse("::1"));
        assertEquals(Ipv6.parse("::0.0.1.0"), Ipv6.parse("::100"));
        assertEquals(Ipv6.parse("::0.1.0.0"), Ipv6.parse("::1:0"));
        assertEquals(Ipv6.parse("::1.0.0.0"), Ipv6.parse("::100:0"));
        assertEquals(Ipv6.parse("::0.0.255.255"), Ipv6.parse("::ffff"));
        assertEquals(Ipv6.parse("::0.1.255.255"), Ipv6.parse("::1:ffff"));
        assertEquals(Ipv6.parse("::255.255.255.255"), Ipv6.parse("::ffff:ffff"));

        assertEquals(Ipv6.parse("::ffff:0.0.0.0"), Ipv6.parse("::ffff:0:0"));
        assertEquals(Ipv6.parse("::ffff:0.0.0.1"), Ipv6.parse("::ffff:0:1"));
        assertEquals(Ipv6.parse("::ffff:0.0.1.0"), Ipv6.parse("::ffff:0:100"));
        assertEquals(Ipv6.parse("::ffff:0.1.0.0"), Ipv6.parse("::ffff:1:0"));
        assertEquals(Ipv6.parse("::ffff:1.0.0.0"), Ipv6.parse("::ffff:100:0"));
        assertEquals(Ipv6.parse("::ffff:0.0.255.255"), Ipv6.parse("::ffff:0:ffff"));
        assertEquals(Ipv6.parse("::ffff:0.1.255.255"), Ipv6.parse("::ffff:1:ffff"));
        assertEquals(Ipv6.parse("::ffff:255.255.255.255"), Ipv6.parse("::ffff:ffff:ffff"));
    }

    // IPv6 Ranges

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToParseRangeInDashNotation() {
        Ipv6.parse("1:2:3:4-1:2:3:5");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToParseRangeInCidrNotation() {
        Ipv6.parse("1:2:3:4/64");
    }

    @Test
    public void testHasNext() {
        assertTrue(Ipv6.FIRST_IPV6_ADDRESS.hasNext());
        assertTrue(Ipv6.of("1:2:3:4:5:6:7:8").hasNext());
        assertFalse(Ipv6.LAST_IPV6_ADDRESS.hasNext());
    }

    @Test
    public void testHasPrevious() {
        assertFalse(Ipv6.FIRST_IPV6_ADDRESS.hasPrevious());
        assertTrue(Ipv6.of("1:2:3:4:5:6:7:8").hasPrevious());
        assertTrue(Ipv6.LAST_IPV6_ADDRESS.hasPrevious());
    }

    @Test
    public void shouldHave128BitsSize() {
        assertEquals(Ipv6.NUMBER_OF_BITS, Ipv6.FIRST_IPV6_ADDRESS.bitSize());
    }

    @Test
    public void shouldCalculateBoundsGivenAnAddressAndAPrefix() {
        Ipv6 address = Ipv6.parse("ffce:abcd::");
        assertEquals(Ipv6.parse("::"), address.lowerBoundForPrefix(0));
        assertEquals(Ipv6.parse("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"), address.upperBoundForPrefix(0));

        assertEquals(Ipv6.parse("ffce:abc0::"), address.lowerBoundForPrefix(28));
        assertEquals(Ipv6.parse("ffce:abcf:ffff:ffff:ffff:ffff:ffff:ffff"), address.upperBoundForPrefix(28));

        assertEquals(Ipv6.parse("ffce:abcd::"), address.lowerBoundForPrefix(128));
        assertEquals(Ipv6.parse("ffce:abcd::"), address.upperBoundForPrefix(128));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToCalculateLowerBoundWhenPrefixIsOutOfRange() {
        Ipv6.parse("ffce:abcd::").lowerBoundForPrefix(129);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToCalculateUpperBoundWhenPrefixIsOutOfRange() {
        Ipv6.parse("ffce:abcd::").upperBoundForPrefix(129);
    }

    @Test
    public void shouldCalculateCommonPrefixLength() {
        Ipv6 ipv6 = Ipv6.of("::ffff");
        assertEquals(0, ipv6.getCommonPrefixLength(Ipv6.of("ffff::")));
        assertEquals(64, ipv6.getCommonPrefixLength(Ipv6.of("::ffff:0:0:0")));
        assertEquals(128, ipv6.getCommonPrefixLength(Ipv6.of("::ffff")));
    }
}
