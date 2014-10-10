/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2011-2014, Yannis Gonianakis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
