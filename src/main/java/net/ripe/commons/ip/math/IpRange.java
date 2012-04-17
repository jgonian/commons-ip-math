package net.ripe.commons.ip.math;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Deprecated
public abstract class IpRange<T extends IpRange<T>> extends Range<T> {

    private static final long serialVersionUID = 1L;
    private static final BigInteger TWO = BigInteger.valueOf(2L);

    protected IpRange(BigInteger start, BigInteger end) {
        super(start, end);
        
        Validate.isTrue(end.compareTo(limit()) <= 0, "End of range should be <= " + limit());
    }

    public List<T> splitIntoPrefixes() {
        T leftOver = createNew(start, end);
        if (leftOver.isValidPrefix()) {
            return Collections.singletonList(leftOver);
        }
        SizeCondition sizeCondition = new SizeCondition() {
            @Override
            public boolean test(int currentBitLength, int previousBitLength) {
                return currentBitLength > previousBitLength;
            }
        };

        int bitLength = leftOver.findMaxPossibleBitLength(leftOver.start);
        T containedPrefix = null;
        while (containedPrefix == null) {
            containedPrefix = leftOver.findContainedPrefix(leftOver.bits(), bitLength, sizeCondition);
            bitLength++;
        }
        List<T> result = new ArrayList<T>();
        List<T> relativeComplements = leftOver.relativeComplement(containedPrefix);
        result.add(containedPrefix);
        for (T relativeComplement : relativeComplements) {
            result.addAll(relativeComplement.splitIntoPrefixes());
        }
        return result;
    }

    public boolean isValidPrefix() {
        int possibleBitLength = bits() - end.subtract(start).bitLength();
        T prefix = findBiggestContainedPrefix(possibleBitLength);
        return this.equals(prefix);
    }


    public T findBiggestContainedPrefix(int requiredBitLength) {
        SizeCondition sizeCondition = new SizeCondition() {
            @Override
            public boolean test(int currentBitLength, int previousBitLength) {
                return currentBitLength > previousBitLength;
            }
        };

        return findContainedPrefix(bits(), requiredBitLength, sizeCondition);
    }

    public T findSmallestContainedPrefix(int requiredBitLength) {
        SizeCondition sizeCondition = new SizeCondition() {
            @Override
            public boolean test(int currentBitLength, int previousBitLength) {
                return currentBitLength < previousBitLength;
            }
        };

        return findContainedPrefix(0, requiredBitLength, sizeCondition);
    }

    private T findContainedPrefix(int startBitLength, int requiredBitLength, SizeCondition sizeCondition) {
        BigInteger candidateStart = start;
        T foundPrefix = null;
        while (candidateStart.compareTo(end) <= 0) {
            int currentPower = findMaxPossibleBitLength(candidateStart);
            BigInteger currentLength = TWO.pow(currentPower);
            BigInteger maxLength = end.subtract(candidateStart).add(BigInteger.ONE);
            while ((currentLength.compareTo(maxLength) > 0) && (currentPower > 0)) {
                currentPower = currentPower - 1;
                currentLength = TWO.pow(currentPower);
            }
            BigInteger candidateEnd = candidateStart.add(currentLength).subtract(BigInteger.ONE);

            int currentBitLength = bits() - currentPower;
            if (currentBitLength <= requiredBitLength && (foundPrefix == null || sizeCondition.test(currentBitLength, startBitLength))) {
                foundPrefix = createNew(candidateStart, candidateEnd);
                startBitLength = currentBitLength;
            }
            candidateStart = candidateEnd.add(BigInteger.ONE);
        }
        return foundPrefix;
    }

    public int findMaxPossibleBitLength(BigInteger start) {
        int bitLength = this.bits();

        while ((bitLength >= 0) && !start.remainder(TWO.pow(bitLength)).equals(BigInteger.ZERO)) {
            bitLength--;
        }

        return bitLength;
    }

    public T cropFromStart(int requiredBitLength) {
        BigInteger newEnd = start.add(TWO.pow(bits() - requiredBitLength)).subtract(BigInteger.ONE);
        return createNew(start, newEnd);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(start).append(end).append(bits()).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof IpRange)) {
            return false;
        }
        IpRange<?> other = (IpRange<?>) obj;
        if ((start.compareTo(other.start) == 0) && (end.compareTo(other.end) == 0)) {
            return true;
        }
        return false;
    }

    private interface SizeCondition {
        boolean test(int currentBitLength, int previousBitLength);
    }

    @Override
    protected abstract T createNew(BigInteger start, BigInteger end);

    @Override
    protected abstract T getThis();
    
    protected abstract int bits();

    protected BigInteger limit() {
        return BigInteger.ONE.shiftLeft(bits()).subtract(BigInteger.ONE);
    }
}
