package net.ripe.commons.ip;

import static net.ripe.commons.ip.RangeUtils.*;

public final class Ipv4Utils {

    private Ipv4Utils() {
    }

    public static Ipv4 lowerBoundForPrefix(Ipv4 address, int prefixLength) {
        rangeCheck(prefixLength, 0, Ipv4.NUMBER_OF_BITS);
        long mask = ~((1L << (Ipv4.NUMBER_OF_BITS - prefixLength)) - 1);
        return new Ipv4(address.value() & mask);
    }

    public static Ipv4 upperBoundForPrefix(Ipv4 address, int prefixLength) {
        rangeCheck(prefixLength, 0, Ipv4.NUMBER_OF_BITS);
        long mask = (1L << (Ipv4.NUMBER_OF_BITS - prefixLength)) - 1;
        return new Ipv4(address.value() | mask);
    }
}
