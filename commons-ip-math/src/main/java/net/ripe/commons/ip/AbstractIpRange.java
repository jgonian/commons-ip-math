package net.ripe.commons.ip;

import static java.math.BigInteger.*;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractIpRange<C extends AbstractIp<C, R>, R extends AbstractIpRange<C, R>>
        extends AbstractRange<C, R>
        implements InternetResourceRange<C, R> {

    protected static final String SLASH = "/";
    protected static final String DASH = "-";
    private static final BigInteger TWO = BigInteger.valueOf(2);

    protected AbstractIpRange(C start, C end) {
        super(start, end);
    }

    protected abstract R newInstance(BigInteger start, BigInteger end);

    @Override
    public String toString() {
        if (PrefixUtils.isValidPrefix(this)) {
            return toStringInCidrNotation();
        } else {
            return toStringInRangeNotation();
        }
    }

    public String toStringInRangeNotation() {
        return start() + DASH + end();
    }

    public String toStringInCidrNotation() {
        return start() + SLASH + PrefixUtils.getPrefixLength(this);
    }

    public String toStringInDecimalNotation() {
        return start().asBigInteger() + DASH + end().asBigInteger();
    }

    public List<R> splitToPrefixes() {
        BigInteger rangeEnd = end().asBigInteger();
        BigInteger currentRangeStart = start().asBigInteger();
        int startingPrefixLength = start().bitSize();
        List<R> prefixes = new LinkedList<R>();

        while (currentRangeStart.compareTo(rangeEnd) <= 0) {
            int maximumPrefixLength = getMaximumPrefixLengthStartingAtIpAddressValue(currentRangeStart, startingPrefixLength);
            BigInteger maximumSizeOfPrefix = rangeEnd.subtract(currentRangeStart).add(ONE);
            BigInteger currentSizeOfPrefix = TWO.pow(maximumPrefixLength);

            while ((currentSizeOfPrefix.compareTo(maximumSizeOfPrefix) > 0) && (maximumPrefixLength > 0)) {
                maximumPrefixLength--;
                currentSizeOfPrefix = TWO.pow(maximumPrefixLength);
            }
            BigInteger currentRangeEnd = currentRangeStart.add(TWO.pow(maximumPrefixLength).subtract(ONE));
            prefixes.add(newInstance(currentRangeStart, currentRangeEnd));
            currentRangeStart = currentRangeEnd.add(ONE);
        }
        return prefixes;
    }

    private int getMaximumPrefixLengthStartingAtIpAddressValue(BigInteger ipAddressValue, int startingPrefixLength) {
        int prefixLength = startingPrefixLength;
        while ((prefixLength >= 0) && !canBeDividedByThePowerOfTwo(ipAddressValue, prefixLength)) {
            prefixLength--;
        }
        return prefixLength;
    }

    private boolean canBeDividedByThePowerOfTwo(BigInteger number, int power) {
        return number.remainder(TWO.pow(power)).equals(ZERO);
    }
}
