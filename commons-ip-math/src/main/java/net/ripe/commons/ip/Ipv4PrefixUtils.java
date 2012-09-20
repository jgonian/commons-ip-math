package net.ripe.commons.ip;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.google.common.base.Optional;

public final class Ipv4PrefixUtils {   // TODO(yg): Investigate how to abstract for Ipv4 and Ipv6 in an elegant way.

    private Ipv4PrefixUtils() {
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

}
