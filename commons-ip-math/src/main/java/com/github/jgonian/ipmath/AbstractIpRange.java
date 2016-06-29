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

package com.github.jgonian.ipmath;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

public abstract class AbstractIpRange<C extends AbstractIp<C, R>, R extends AbstractIpRange<C, R>>
        extends AbstractRange<C, R>
        implements InternetResourceRange<C, R> {

    protected static final String SLASH = "/";
    protected static final String DASH = "-";
    protected static final String DASH_WITH_SPACES = " - ";
    private static final BigInteger TWO = BigInteger.valueOf(2);

    protected AbstractIpRange(C start, C end) {
        super(start, end);
    }

    protected abstract R newInstance(BigInteger start, BigInteger end);

    @Override
    public String toString() {
        if (PrefixUtils.isLegalPrefix(this)) {
            return toStringInCidrNotation();
        } else {
            return toStringInRangeNotation();
        }
    }

    public String toStringInRangeNotation() {
        return start() + DASH + end();
    }

    public String toStringInRangeNotationWithSpaces() {
        return start() + DASH_WITH_SPACES + end();
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
