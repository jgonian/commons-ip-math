package net.ripe.commons.ip;

import static java.math.BigInteger.*;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.google.common.base.Optional;

public final class PrefixUtils {

    private PrefixUtils() {
    }

    public static <C extends AbstractIp<C, R>, R extends AbstractIpRange<C, R>>
    boolean isValidPrefix(AbstractIpRange<C, R> range) {
        int prefixLength = range.start().getCommonPrefixLength(range.end());
        C lowerBoundForPrefix = range.start().lowerBoundForPrefix(prefixLength);
        C upperBoundForPrefix = range.end().upperBoundForPrefix(prefixLength);
        return range.start().equals(lowerBoundForPrefix) && range.end().equals(upperBoundForPrefix);
    }

    public static <C extends AbstractIp<C, R>, R extends AbstractIpRange<C, R>>
    int getPrefixLength(AbstractIpRange<C, R> range) {
        Validate.isTrue(isValidPrefix(range), range.toStringInRangeNotation() + " is not a legal prefix, cannot get prefix length!");
        return range.start().getCommonPrefixLength(range.end());
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
        List<Ipv4Range> prefixes = range.splitToPrefixes();
        Collections.sort(prefixes, comparator);
        for (Ipv4Range prefix : prefixes) {
            if (prefixLength >= PrefixUtils.getPrefixLength(prefix)) {
                return Optional.of(prefix);
            }
        }
        return Optional.absent();
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
        List<Ipv6Range> prefixes = range.splitToPrefixes();
        Collections.sort(prefixes, comparator);
        for (Ipv6Range prefix : prefixes) {
            if (prefixLength >= PrefixUtils.getPrefixLength(prefix)) {
                return Optional.of(prefix);
            }
        }
        return Optional.absent();
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
}
