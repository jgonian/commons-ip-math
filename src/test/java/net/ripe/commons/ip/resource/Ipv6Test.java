package net.ripe.commons.ip.resource;

import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.Ignore;
import org.junit.Test;

public class Ipv6Test {

    @Test
    public void shouldToStringFirst() {
        Ipv6 addr = Ipv6.FIRST_IPV6_ADDRESS;
        assertEquals("::", addr.toString());
    }

    @Test
    public void shouldToStringLast() {
        Ipv6 addr = Ipv6.LAST_IPV6_ADDRESS;
        assertEquals("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff", addr.toString());
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

    @Test(expected = NullPointerException.class)
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
        } catch(IllegalArgumentException e) {
        }
        try {
            Ipv6.parse("2001:0000:1234:0000 :0000:C1C0:ABCD:0876");
            fail();
        } catch(IllegalArgumentException e) {
        }
    }

    @Test
    public void shouldFailOnExtraZero() {
        try {
            Ipv6.parse("02001:0000:1234:0000:0000:C1C0:ABCD:0876");
            fail();
        } catch(IllegalArgumentException e) {
        }
        try {
            Ipv6.parse("2001:0000:01234:0000:0000:C1C0:ABCD:0876");
            fail();
        } catch(IllegalArgumentException e) {
        }
    }

    @Test
    public void shouldFailOnMultipleDoubleColons() {
        try {
            Ipv6.parse("3ffe:b00::1::a");
            fail();
        } catch(IllegalArgumentException e) {
        }
        try {
            Ipv6.parse("::1111:2222:3333:4444:5555:6666::");
            fail();
        } catch(IllegalArgumentException e) {
        }
    }

    @Test
    public void shouldFailIfSegmentOutOfBound() {
        try {
            Ipv6.parse("2ffff::10");
            fail();
        } catch(IllegalArgumentException e) {
        }
        try {
            Ipv6.parse("-2::10");
            fail();
        } catch(IllegalArgumentException e) {
        }
    }

    // Ipv6 Addresses with Embedded Ipv4 Addresses

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

    @Ignore("TODO(ygoniana): Update the parse implementation to comply with §2.5.5. of rfc4291")
    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToParseInvalidIpv4EmbeddedFormat() {
        Ipv6.parse("a:b:c:d:e:f:13.1.68.3");
    }

    @Test
    public void shouldFailIfIpv4PartExceedsBounds() {
        try {
            Ipv6.parse("::400.2.3.4");
            fail();
        } catch(IllegalArgumentException e) {
        }
        try {
            Ipv6.parse("::260.2.3.4");
            fail();
        } catch(IllegalArgumentException e) {
        }
        try {
            Ipv6.parse("::256.2.3.4");
            fail();
        } catch(IllegalArgumentException e) {
        }
        try {
            Ipv6.parse("::1.256.3.4");
            fail();
        } catch(IllegalArgumentException e) {
        }
        try {
            Ipv6.parse("::1.2.256.4");
            fail();
        } catch(IllegalArgumentException e) {
        }
        try {
            Ipv6.parse("::1.2.256.256");
            fail();
        } catch(IllegalArgumentException e) {
        }
        try {
            Ipv6.parse("::300.2.3.4");
            fail();
        } catch(IllegalArgumentException e) {
        }
        try {
            Ipv6.parse("::1.300.3.4");
            fail();
        } catch(IllegalArgumentException e) {
        }
        try {
            Ipv6.parse("::1.2.300.4");
            fail();
        } catch(IllegalArgumentException e) {
        }
        try {
            Ipv6.parse("::1.2.3.300");
            fail();
        } catch(IllegalArgumentException e) {
        }
    }

    @Test
    public void shouldFailIfIpv4PartContainsInvalidCharacters() {
        try {
            Ipv6.parse("::255Z255X255Y255");
            fail();
        } catch(IllegalArgumentException e) {
        }
        try {
            Ipv6.parse("::192x168.1.26");
            fail();
        } catch(IllegalArgumentException e) {
        }
    }

    @Test
    public void shouldFailIfIpv4PartIsMisplaced() {
        try {
            Ipv6.parse("1.2.3.4:FFFF:0000:0000:0000::0000");
            fail();
        } catch(IllegalArgumentException e) {
        }
        try {
            Ipv6.parse("1.2.3.4::");
            fail();
        } catch(IllegalArgumentException e) {
        }
    }

    @Test
    public void shouldFailIfIpv4PartContainsLeadingZeros() {
        try {
            Ipv6.parse("::FFFF:254.157.241.086");
            fail();
        } catch(IllegalArgumentException e) {
        }
    }

    // Ipv6 Ranges

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToParseRangeInDashNotation() {
        Ipv6.parse("1:2:3:4-1:2:3:5");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToParseRangeInCidrNotation() {
        Ipv6.parse("1:2:3:4/64");
    }
}
