package net.ripe.commons.ip.resource;

import static net.ripe.commons.ip.utils.RangeUtils.rangeCheck;

public final class Ipv4Utils {

    public static Ipv4 lowerBoundForPrefix(Ipv4 address, int prefix) {
        rangeCheck(prefix, 0, Ipv4.IPv4_NUMBER_OF_BITS);
        long mask = ~((1L << (Ipv4.IPv4_NUMBER_OF_BITS - prefix)) - 1);
        return new Ipv4(address.value() & mask);
    }

    public static Ipv4 upperBoundForPrefix(Ipv4 address, int prefix) {
        rangeCheck(prefix, 0, Ipv4.IPv4_NUMBER_OF_BITS);
        long mask = (1L << (Ipv4.IPv4_NUMBER_OF_BITS - prefix)) -  1;
        return new Ipv4(address.value() | mask);
    }
}
