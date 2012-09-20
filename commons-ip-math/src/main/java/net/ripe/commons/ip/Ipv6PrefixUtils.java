
package net.ripe.commons.ip;

import static java.math.BigInteger.*;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.google.common.base.Optional;

public final class Ipv6PrefixUtils { // TODO(yg): Investigate how to abstract for Ipv4 and Ipv6 in an elegant way.

    private Ipv6PrefixUtils() {
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
