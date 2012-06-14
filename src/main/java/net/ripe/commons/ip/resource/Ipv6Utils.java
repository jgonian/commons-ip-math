package net.ripe.commons.ip.resource;

import static net.ripe.commons.ip.utils.RangeUtils.*;
import java.math.BigInteger;

public final class Ipv6Utils {

    private Ipv6Utils() {
    }

    public static Ipv6 lowerBoundForPrefix(Ipv6 address, int prefixLength) {
        rangeCheck(prefixLength, 0, Ipv6.NUMBER_OF_BITS);
        BigInteger mask = bitMask(0).xor(bitMask(prefixLength));
        return new Ipv6(address.value().and(mask));
    }

    public static Ipv6 upperBoundForPrefix(Ipv6 address, int prefixLength) {
        rangeCheck(prefixLength, 0, Ipv6.NUMBER_OF_BITS);
        return new Ipv6(address.value().or(bitMask(prefixLength)));
    }

    private static BigInteger bitMask(int prefixLength) {
        return BigInteger.ONE.shiftLeft(Ipv6.NUMBER_OF_BITS - prefixLength).add(BigInteger.valueOf(-1));
    }
}
