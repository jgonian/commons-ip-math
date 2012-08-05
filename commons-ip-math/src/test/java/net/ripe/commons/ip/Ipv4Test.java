package net.ripe.commons.ip;

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
}
