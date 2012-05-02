package net.ripe.commons.ip.utils;

import static java.math.BigInteger.*;
import static net.ripe.commons.ip.resource.Ipv6.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import net.ripe.commons.ip.range.Ipv6Range;
import net.ripe.commons.ip.resource.Ipv6;

public class PrefixUtils {
    //TODO change to work for both Ipv6/Ipv4, add tests for Ipv4

    public static int getPrefixLength(Ipv6Range range) {
        int maxContainedPrefix = getMaxContainedPrefix(range.size());
        if (getPrefixSize(maxContainedPrefix).compareTo(range.size()) == 0) {
            return maxContainedPrefix;
        } else {
            throw new IllegalArgumentException(range + " is not a valid prefix, cannot get prefix length!");
        }
    }

    public static List<Ipv6Range> splitIntoPrefixes(Ipv6Range range) {
        BigInteger dynamicStart = range.start().value();
        List<Ipv6Range> result = new ArrayList<Ipv6Range>();

        while (dynamicStart.compareTo(range.end().value()) <= 0) {
            BigInteger availableSize = range.end().value().subtract(dynamicStart).add(ONE);
            int prefixToCut = getBiggestPossiblePrefix(dynamicStart, availableSize);

            BigInteger dynamicEnd = dynamicStart.add(getPrefixSize(prefixToCut)).subtract(ONE);
            Ipv6Range cutPrefix = Ipv6Range.from(Ipv6.of(dynamicStart)).to(Ipv6.of(dynamicEnd));
            result.add(cutPrefix);

            dynamicStart = dynamicEnd.add(ONE);
        }

        return result;
    }

    public static BigInteger getPrefixSize(int prefixLength) {
        return BigInteger.ONE.shiftLeft(IPv6_NUMBER_OF_BITS - prefixLength);
    }

    protected static int getBiggestPossiblePrefix(BigInteger start, BigInteger size) {
        int maxValidPrefix = getMaxValidPrefix(start);
        int maxContainedPrefix = getMaxContainedPrefix(size);
        return (maxValidPrefix < maxContainedPrefix) ? maxContainedPrefix : maxValidPrefix;
    }

    protected static int getMaxValidPrefix(BigInteger number) {
        int powerOfTwo = 0;
        int maxPowerOfTwo = powerOfTwo;

        while (powerOfTwo <= IPv6_NUMBER_OF_BITS && number.divideAndRemainder(ONE.shiftLeft(powerOfTwo))[1].compareTo(ZERO) == 0) {
            maxPowerOfTwo = powerOfTwo;
            powerOfTwo++;
        }
        return IPv6_NUMBER_OF_BITS - maxPowerOfTwo;
    }

    protected static int getMaxContainedPrefix(BigInteger number) {
        int powerOfTwo = 0;
        int maxPowerOfTwo = powerOfTwo;

        while (powerOfTwo <= IPv6_NUMBER_OF_BITS && number.compareTo(ONE.shiftLeft(powerOfTwo)) >= 0) {
            maxPowerOfTwo = powerOfTwo;
            powerOfTwo++;
        }
        return IPv6_NUMBER_OF_BITS - maxPowerOfTwo;
    }

}
