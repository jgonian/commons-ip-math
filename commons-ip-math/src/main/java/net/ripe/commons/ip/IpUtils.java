package net.ripe.commons.ip;

import java.math.BigInteger;

public final class IpUtils {

    private IpUtils() {
    }

    public static <T extends AbstractIp<T, R>, R extends AbstractIpRange<T, R>>
    int getCommonPrefixLength(T leftAddress, T rightAddress) {
        BigInteger temp = leftAddress.asBigInteger().xor(rightAddress.asBigInteger());
        return leftAddress.bitsSize() - temp.bitLength();
    }
}
