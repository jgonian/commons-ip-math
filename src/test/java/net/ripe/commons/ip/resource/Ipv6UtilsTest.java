package net.ripe.commons.ip.resource;

import static junit.framework.Assert.assertEquals;
import org.junit.Test;

public class Ipv6UtilsTest {

    @Test
    public void shouldCalculateBoundsGivenAnAddressAndAPrefix() {
        Ipv6 address = Ipv6.parse("ffce:abcd::");
        assertEquals(Ipv6.parse("::"), Ipv6Utils.lowerBoundForPrefix(address, 0));
        assertEquals(Ipv6.parse("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"), Ipv6Utils.upperBoundForPrefix(address, 0));

        assertEquals(Ipv6.parse("ffce:abc0::"), Ipv6Utils.lowerBoundForPrefix(address, 28));
        assertEquals(Ipv6.parse("ffce:abcf:ffff:ffff:ffff:ffff:ffff:ffff"), Ipv6Utils.upperBoundForPrefix(address, 28));

        assertEquals(Ipv6.parse("ffce:abcd::"), Ipv6Utils.lowerBoundForPrefix(address, 128));
        assertEquals(Ipv6.parse("ffce:abcd::"), Ipv6Utils.upperBoundForPrefix(address, 128));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToCalculateLowerBoundWhenPrefixIsOutOfRange() {
        Ipv6Utils.lowerBoundForPrefix(Ipv6.parse("ffce:abcd::"), 129);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToCalculateUpperBoundWhenPrefixIsOutOfRange() {
        Ipv6Utils.upperBoundForPrefix(Ipv6.parse("ffce:abcd::"), 129);
    }

}
