package net.ripe.commons.ip;

import java.math.BigInteger;
import java.util.Collection;

public class ConservativePrefixFinder extends PrefixFinder {

    public static ConservativePrefixFinder newInstance() {
        return new ConservativePrefixFinder();
    }

    private ConservativePrefixFinder() {
        super(new ConservativeStrategy());
    }

    private static class ConservativeStrategy implements Strategy {

        @Override
        public Ipv6Range findPrefixOrNull(int prefixLength, Collection<Ipv6Range> ranges) {
            final BigInteger desiredPrefixSize = BigInteger.valueOf(2).pow(128 - prefixLength);
            Ipv6Range foundPrefix = null;
            BigInteger currentSize = null;

            for (Ipv6Range freeBlock : ranges) {
                if (freeBlock.size().compareTo(desiredPrefixSize) >= 0) {
                    final Optional<Ipv6Range> smallestPrefix = PrefixUtils.findMinimumPrefixForPrefixLength(freeBlock, prefixLength);
                    if (smallestPrefix.isPresent()) {
                        final Ipv6Range candidatePrefix = smallestPrefix.get();
                        final BigInteger candidateSize = candidatePrefix.size();
                        if ((foundPrefix == null) || candidateSize.compareTo(currentSize) < 0) {
                            foundPrefix = candidatePrefix;
                            currentSize = candidateSize;
                        }
                    }
                }
            }
            return foundPrefix == null ? null : Ipv6Range.from(foundPrefix.start()).andPrefixLength(prefixLength);
        }
    }
}
