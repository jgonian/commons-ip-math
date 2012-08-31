package net.ripe.commons.ip;

import static net.ripe.commons.ip.RangeUtils.*;

public final class Ipv4Utils {

    private Ipv4Utils() {
    }

    public static Ipv4 lowerBoundForPrefix(Ipv4 address, int prefixLength) {
        checkRange(prefixLength, 0, Ipv4.NUMBER_OF_BITS);
        long mask = ~((1L << (Ipv4.NUMBER_OF_BITS - prefixLength)) - 1);
        return new Ipv4(address.value() & mask);
    }

    public static Ipv4 upperBoundForPrefix(Ipv4 address, int prefixLength) {
        checkRange(prefixLength, 0, Ipv4.NUMBER_OF_BITS);
        long mask = (1L << (Ipv4.NUMBER_OF_BITS - prefixLength)) - 1;
        return new Ipv4(address.value() | mask);
    }
    
    public static int getCommonPrefixLength(Ipv4 leftAddress, Ipv4 rightAddress) {
        long temp = leftAddress.value() ^ rightAddress.value();
        return Integer.numberOfLeadingZeros((int) temp);
    }
}
