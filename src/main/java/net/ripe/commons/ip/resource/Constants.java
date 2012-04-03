package net.ripe.commons.ip.resource;

import net.ripe.commons.ip.range.Ipv4Range;

public final class Constants {

    public static final int IPv4_BYTE_MASK = 0xff;
    public static final int IPv4_NUMBER_OF_BITS = 32;

    public static final long IPv4_MINIMUM_VALUE = 0;
    public static final long IPv4_MAXIMUM_VALUE = (1L << IPv4_NUMBER_OF_BITS) - 1;

    public static final Ipv4Address FIRST_IPv4_ADDRESS = Ipv4Address.valueOf(IPv4_MINIMUM_VALUE);
    public static final Ipv4Address LAST_IPv4_ADDRESS = Ipv4Address.valueOf(IPv4_MAXIMUM_VALUE);

}
