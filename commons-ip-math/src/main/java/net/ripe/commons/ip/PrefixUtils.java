package net.ripe.commons.ip;

import static java.math.BigInteger.ZERO;
import static net.ripe.commons.ip.RangeUtils.checkRange;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.google.common.base.Optional;

public final class PrefixUtils {

    private PrefixUtils() {
    }

    public static <C extends AbstractIp<C, R>, R extends AbstractIpRange<C, R>>
    boolean isLegalPrefix(AbstractIpRange<C, R> range) {
        int prefixLength = range.start().getCommonPrefixLength(range.end());
        C lowerBoundForPrefix = range.start().lowerBoundForPrefix(prefixLength);
        C upperBoundForPrefix = range.end().upperBoundForPrefix(prefixLength);
        return range.start().equals(lowerBoundForPrefix) && range.end().equals(upperBoundForPrefix);
    }

    public static <C extends AbstractIp<C, R>, R extends AbstractIpRange<C, R>>
    int getPrefixLength(AbstractIpRange<C, R> range) {
        Validate.isTrue(isLegalPrefix(range), range.toStringInRangeNotation() + " is not a legal prefix, cannot get prefix length!");
        return range.start().getCommonPrefixLength(range.end());
    }

    public static <C extends AbstractIp<C, R>, R extends AbstractIpRange<C, R>>
    Optional<R> findMinimumPrefixForPrefixLength(R range, int prefixLength) {
        checkRange(prefixLength, 0, range.start().bitSize());
        return findPrefixForPrefixLength(range, prefixLength, SizeComparator.<C, R>getInstance());
    }

    public static <C extends AbstractIp<C, R>, R extends AbstractIpRange<C, R>>
    Optional<R> findMaximumPrefixForPrefixLength(R range, int prefixLength) {
        checkRange(prefixLength, 0, range.start().bitSize());
        return findPrefixForPrefixLength(range, prefixLength, Collections.reverseOrder(SizeComparator.<C, R>getInstance()));
    }

    private static <C extends AbstractIp<C, R>, R extends AbstractIpRange<C, R>>
    Optional<R> findPrefixForPrefixLength(R range, int prefixLength, Comparator<? super R> comparator) {
        List<R> prefixes = range.splitToPrefixes();
        Collections.sort(prefixes, comparator);
        for (R prefix : prefixes) {
            if (prefixLength >= PrefixUtils.getPrefixLength(prefix)) {
                return Optional.of(prefix);
            }
        }
        return Optional.absent();
    }

    // TODO(yg): generify and move to AbstractIp
    public static int findMaxPrefixLengthForAddress(Ipv6 address) {
        return getMaxValidPrefix(address.value());
    }

    private static int getMaxValidPrefix(BigInteger number) {
        int powerOfTwo = 0;
        int maxPowerOfTwo = powerOfTwo;

        while (powerOfTwo <= Ipv6.NUMBER_OF_BITS && number.divideAndRemainder(BigInteger.ONE.shiftLeft(powerOfTwo))[1].compareTo(ZERO) == 0) {
            maxPowerOfTwo = powerOfTwo;
            powerOfTwo++;
        }
        return Ipv6.NUMBER_OF_BITS - maxPowerOfTwo;
    }

}
