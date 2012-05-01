package net.ripe.commons.ip.resource;

import static org.junit.Assert.*;
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
}
