package net.ripe.commons.ip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.base.Optional;

public final class Ipv4PrefixUtils {   // TODO(yg): Investigate how to abstract for Ipv4 and Ipv6 in an elegant way.

    private Ipv4PrefixUtils() {
    }

    public static int getPrefixLength(Ipv4Range range) {
        Validate.isTrue(PrefixUtils.isValidPrefix(range), range.toStringInRangeNotation() + " is not a valid prefix, cannot get prefix length!");
        return range.start().getCommonPrefixLength(range.end());
    }

    public static List<Ipv4Range> splitIntoPrefixes(Ipv4Range range) {
        Long dynamicStart = range.start().value();
        List<Ipv4Range> result = new ArrayList<Ipv4Range>();

        while (dynamicStart.compareTo(range.end().value()) <= 0) {
            Long availableSize = (range.end().value() - dynamicStart) + 1L;
            int prefixToCut = getBiggestPossiblePrefix(dynamicStart, availableSize);

            Long dynamicEnd = (dynamicStart + getPrefixSize(prefixToCut)) - 1L;
            Ipv4Range cutPrefix = Ipv4Range.from(dynamicStart).to(dynamicEnd);
            result.add(cutPrefix);

            dynamicStart = dynamicEnd + 1L;
        }

        return result;
    }

    public static Optional<Ipv4Range> findMinimumPrefixForPrefixLength(Ipv4Range range, int prefixLength) {
        RangeUtils.checkRange(prefixLength, 0, Ipv4.NUMBER_OF_BITS);
        return findPrefixForPrefixLength(range, prefixLength, SizeComparator.<Ipv4, Ipv4Range>getInstance());
    }

    public static Optional<Ipv4Range> findMaximumPrefixForPrefixLength(Ipv4Range range, int prefixLength) {
        RangeUtils.checkRange(prefixLength, 0, Ipv4.NUMBER_OF_BITS);
        return findPrefixForPrefixLength(range, prefixLength, Collections.reverseOrder(SizeComparator.<Ipv4, Ipv4Range>getInstance()));
    }

    private static Optional<Ipv4Range> findPrefixForPrefixLength(Ipv4Range range, int prefixLength, Comparator<? super Ipv4Range> comparator) {
        List<Ipv4Range> prefixes = splitIntoPrefixes(range);
        Collections.sort(prefixes, comparator);
        for (Ipv4Range prefix : prefixes) {
            if (prefixLength >= getPrefixLength(prefix)) {
                return Optional.of(prefix);
            }
        }
        return Optional.absent();
    }

    private static long getPrefixSize(int prefixLength) {
        return 1L << (Ipv4.NUMBER_OF_BITS - prefixLength);
    }

    private static int getBiggestPossiblePrefix(Long start, Long size) {
        int maxValidPrefix = getMaxValidPrefix(start);
        int maxContainedPrefix = getMaxContainedPrefix(size);
        return (maxValidPrefix < maxContainedPrefix) ? maxContainedPrefix : maxValidPrefix;
    }

    private static int getMaxValidPrefix(Long number) {
        int powerOfTwo = 0;
        int maxPowerOfTwo = powerOfTwo;

        while (powerOfTwo <= Ipv4.NUMBER_OF_BITS && number % (1L << powerOfTwo) == 0) {
            maxPowerOfTwo = powerOfTwo;
            powerOfTwo++;
        }
        return Ipv4.NUMBER_OF_BITS - maxPowerOfTwo;
    }

    private static int getMaxContainedPrefix(Long number) {
        int powerOfTwo = 0;
        int maxPowerOfTwo = powerOfTwo;

        while (powerOfTwo <= Ipv4.NUMBER_OF_BITS && number.compareTo(1L << powerOfTwo) >= 0) {
            maxPowerOfTwo = powerOfTwo;
            powerOfTwo++;
        }
        return Ipv4.NUMBER_OF_BITS - maxPowerOfTwo;
    }

}
