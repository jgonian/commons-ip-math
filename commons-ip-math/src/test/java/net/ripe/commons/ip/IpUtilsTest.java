package net.ripe.commons.ip;

import static junit.framework.Assert.assertEquals;
import org.junit.Test;

public class IpUtilsTest {

    @Test
    public void shouldCalculateCommonPrefixLength() {
        Ipv4 ipv4 = Ipv4.of("192.168.0.0");
        assertEquals(0, IpUtils.getCommonPrefixLength(ipv4, Ipv4.of("0.0.0.0")));
        assertEquals(16, IpUtils.getCommonPrefixLength(ipv4, Ipv4.of("192.168.255.255")));
        assertEquals(32, IpUtils.getCommonPrefixLength(ipv4, Ipv4.of("192.168.0.0")));

        Ipv6 ipv6 = Ipv6.of("::ffff");
        assertEquals(0, IpUtils.getCommonPrefixLength(ipv6, Ipv6.of("ffff::")));
        assertEquals(64, IpUtils.getCommonPrefixLength(ipv6, Ipv6.of("::ffff:0:0:0")));
        assertEquals(128, IpUtils.getCommonPrefixLength(ipv6, Ipv6.of("::ffff")));
    }
}
