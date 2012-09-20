package net.ripe.commons.ip;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;
import org.junit.Test;

public class Ipv4Test {

    @Test
    public void testHasNext() {
        assertTrue(Ipv4.FIRST_IPV4_ADDRESS.hasNext());
        assertTrue(Ipv4.of("192.168.0.1").hasNext());
        assertFalse(Ipv4.LAST_IPV4_ADDRESS.hasNext());
    }

    @Test
    public void testHasPrevious() {
        assertFalse(Ipv4.FIRST_IPV4_ADDRESS.hasPrevious());
        assertTrue(Ipv4.of("192.168.0.1").hasPrevious());
        assertTrue(Ipv4.LAST_IPV4_ADDRESS.hasPrevious());
    }

    @Test
    public void shouldParseDottedDecimalNotation() {
        assertEquals("127.0.8.23", Ipv4.parse("127.0.8.23").toString());
        assertEquals("193.168.15.255", Ipv4.parse("193.168.15.255").toString());
    }

    @Test
    public void shouldParseIPv4AddressWithLeadingAndTrailingSpaces() {
        assertEquals("127.0.8.12", Ipv4.parse("  127.0.8.12  ").toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnOutOfBoundsByte() {
        Ipv4.parse("256.0.0.0");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnOutOfBoundsByte_NegativeByte() {
        Ipv4.parse("13.-40.0.0");
    }

    @Test
    public void shouldHave32BitsSize() {
        assertEquals(Ipv4.NUMBER_OF_BITS, Ipv4.FIRST_IPV4_ADDRESS.bitsSize());
    }

    @Test
    public void shouldCalculateBoundsGivenAnAddressAndAPrefix() {
        Ipv4 address = Ipv4.parse("192.168.0.100");
        assertEquals(Ipv4.parse("0.0.0.0"), address.lowerBoundForPrefix(0));
        assertEquals(Ipv4.parse("255.255.255.255"), address.upperBoundForPrefix(0));

        assertEquals(Ipv4.parse("192.168.0.0"), address.lowerBoundForPrefix(16));
        assertEquals(Ipv4.parse("192.168.255.255"), address.upperBoundForPrefix(16));

        assertEquals(Ipv4.parse("192.168.0.100"), address.lowerBoundForPrefix(32));
        assertEquals(Ipv4.parse("192.168.0.100"), address.upperBoundForPrefix(32));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToCalculateLowerBoundWhenPrefixIsOutOfRange() {
        Ipv4.parse("192.168.0.100").lowerBoundForPrefix(33);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToCalculateUpperBoundWhenPrefixIsOutOfRange() {
        Ipv4.parse("192.168.0.100").upperBoundForPrefix(33);
    }

    @Test
    public void shouldCalculateCommonPrefixLength() {
        Ipv4 ipv4 = Ipv4.of("192.168.0.0");
        assertEquals(0, ipv4.getCommonPrefixLength(Ipv4.of("0.0.0.0")));
        assertEquals(16, ipv4.getCommonPrefixLength(Ipv4.of("192.168.255.255")));
        assertEquals(32, ipv4.getCommonPrefixLength(Ipv4.of("192.168.0.0")));
    }
}
