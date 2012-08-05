package net.ripe.commons.ip;

import static java.math.BigInteger.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.google.common.base.Optional;

public final class Ipv6PrefixUtils { // TODO(yg): Investigate how to abstract for Ipv4 and Ipv6 in an elegant way.

    private Ipv6PrefixUtils() {
    }

    public static boolean isValidPrefix(Ipv6Range range) {
        int maxContainedPrefix = getMaxContainedPrefix(range.size());
        return getPrefixSize(maxContainedPrefix).compareTo(range.size()) == 0;
    }

    public static int getPrefixLength(Ipv6Range range) {
        int maxContainedPrefix = getMaxContainedPrefix(range.size());
        Validate.isTrue(getPrefixSize(maxContainedPrefix).compareTo(range.size()) == 0, range.toStringInRangeNotation() + " is not a valid prefix, cannot get prefix length!");
        return maxContainedPrefix;
    }

    public static List<Ipv6Range> splitIntoPrefixes(Ipv6Range range) {
        BigInteger dynamicStart = range.start().value();
        List<Ipv6Range> result = new ArrayList<Ipv6Range>();

        while (dynamicStart.compareTo(range.end().value()) <= 0) {
            BigInteger availableSize = range.end().value().subtract(dynamicStart).add(ONE);
            int prefixToCut = getBiggestPossiblePrefix(dynamicStart, availableSize);

            BigInteger dynamicEnd = dynamicStart.add(getPrefixSize(prefixToCut)).subtract(ONE);
            Ipv6Range cutPrefix = Ipv6Range.from(dynamicStart).to(dynamicEnd);
            result.add(cutPrefix);

            dynamicStart = dynamicEnd.add(ONE);
        }

        return result;
    }

    public static Optional<Ipv6Range> findMinimumPrefixForPrefixLength(Ipv6Range range, int prefixLength) {
        RangeUtils.checkRange(prefixLength, 0, Ipv6.NUMBER_OF_BITS);
        return findPrefixForPrefixLength(range, prefixLength, SizeComparator.<Ipv6, Ipv6Range>getInstance());
    }

    public static Optional<Ipv6Range> findMaximumPrefixForPrefixLength(Ipv6Range range, int prefixLength) {
        RangeUtils.checkRange(prefixLength, 0, Ipv6.NUMBER_OF_BITS);
        return findPrefixForPrefixLength(range, prefixLength, Collections.reverseOrder(SizeComparator.<Ipv6, Ipv6Range>getInstance()));
    }
    
    public static int findMaxPrefixLengthForAddress(Ipv6 address) {
        return getMaxValidPrefix(address.value());
    }

    private static Optional<Ipv6Range> findPrefixForPrefixLength(Ipv6Range range, int prefixLength, Comparator<? super Ipv6Range> comparator) {
        List<Ipv6Range> prefixes = splitIntoPrefixes(range);
        Collections.sort(prefixes, comparator);
        for (Ipv6Range prefix : prefixes) {
            if (prefixLength >= getPrefixLength(prefix)) {
                return Optional.of(prefix);
            }
        }
        return Optional.absent();
    }

    private static BigInteger getPrefixSize(int prefixLength) {
        return BigInteger.ONE.shiftLeft(Ipv6.NUMBER_OF_BITS - prefixLength);
    }

    private static int getBiggestPossiblePrefix(BigInteger start, BigInteger size) {
        int maxValidPrefix = getMaxValidPrefix(start);
        int maxContainedPrefix = getMaxContainedPrefix(size);
        return (maxValidPrefix < maxContainedPrefix) ? maxContainedPrefix : maxValidPrefix;
    }

    private static int getMaxValidPrefix(BigInteger number) {
        int powerOfTwo = 0;
        int maxPowerOfTwo = powerOfTwo;

        while (powerOfTwo <= Ipv6.NUMBER_OF_BITS && number.divideAndRemainder(ONE.shiftLeft(powerOfTwo))[1].compareTo(ZERO) == 0) {
            maxPowerOfTwo = powerOfTwo;
            powerOfTwo++;
        }
        return Ipv6.NUMBER_OF_BITS - maxPowerOfTwo;
    }

    private static int getMaxContainedPrefix(BigInteger number) {
        int powerOfTwo = 0;
        int maxPowerOfTwo = powerOfTwo;

        while (powerOfTwo <= Ipv6.NUMBER_OF_BITS && number.compareTo(ONE.shiftLeft(powerOfTwo)) >= 0) {
            maxPowerOfTwo = powerOfTwo;
            powerOfTwo++;
        }
        return Ipv6.NUMBER_OF_BITS - maxPowerOfTwo;
    }

}
