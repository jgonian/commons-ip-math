package net.ripe.commons.ip;

import static junit.framework.Assert.*;
import org.junit.Test;

public class Ipv4UtilsTest {

    @Test
    public void shouldCalculateBoundsGivenAnAddressAndAPrefix() {
        Ipv4 address = Ipv4.parse("192.168.0.100");
        assertEquals(Ipv4.parse("0.0.0.0"), Ipv4Utils.lowerBoundForPrefix(address, 0));
        assertEquals(Ipv4.parse("255.255.255.255"), Ipv4Utils.upperBoundForPrefix(address, 0));

        assertEquals(Ipv4.parse("192.168.0.0"), Ipv4Utils.lowerBoundForPrefix(address, 16));
        assertEquals(Ipv4.parse("192.168.255.255"), Ipv4Utils.upperBoundForPrefix(address, 16));

        assertEquals(Ipv4.parse("192.168.0.100"), Ipv4Utils.lowerBoundForPrefix(address, 32));
        assertEquals(Ipv4.parse("192.168.0.100"), Ipv4Utils.upperBoundForPrefix(address, 32));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToCalculateLowerBoundWhenPrefixIsOutOfRange() {
        Ipv4Utils.lowerBoundForPrefix(Ipv4.parse("192.168.0.100"), 33);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToCalculateUpperBoundWhenPrefixIsOutOfRange() {
        Ipv4Utils.upperBoundForPrefix(Ipv4.parse("192.168.0.100"), 33);
    }
}
