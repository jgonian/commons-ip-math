/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2011-2017, Yannis Gonianakis
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

import static java.math.BigInteger.ONE;

public final class Ipv6 extends AbstractIp<Ipv6, Ipv6Range> {

    private static final long serialVersionUID = -1L;

    public static final BigInteger FOUR_OCTECT_MASK = BigInteger.valueOf(0xFFFF);
    public static final int NUMBER_OF_BITS = 128;
    public static final BigInteger MINIMUM_VALUE = BigInteger.ZERO;
    public static final BigInteger MAXIMUM_VALUE = new BigInteger(String.valueOf((ONE.shiftLeft(NUMBER_OF_BITS)).subtract(ONE)));

    public static final Ipv6 FIRST_IPV6_ADDRESS = Ipv6.of(MINIMUM_VALUE);
    public static final Ipv6 LAST_IPV6_ADDRESS = Ipv6.of(MAXIMUM_VALUE);

    private static final int MIN_PART_VALUE = 0x0;
    private static final int MAX_PART_VALUE = 0xFFFF;
    private static final int MAX_PART_LENGTH = 4;
    private static final String DEFAULT_PARSING_ERROR_MESSAGE = "Invalid IPv6 address: '%s'";
    private static final String COLON = ":";
    private static final String ZERO = "0";
    private static final int BITS_PER_PART = 16;
    private static final int TOTAL_OCTETS = 8;
    private static final int COLON_COUNT_IPV6 = 7;
    private static final BigInteger MINUS_ONE = BigInteger.valueOf(-1);

    private final BigInteger value;

    protected Ipv6(BigInteger value) {
        this.value = Validate.notNull(value, "value is required");
        Validate.isTrue(value.compareTo(MINIMUM_VALUE) >= 0, "Value of IPv6 has to be greater than or equal to " + MINIMUM_VALUE);
        Validate.isTrue(value.compareTo(MAXIMUM_VALUE) <= 0, "Value of IPv6 has to be less than or equal to " + MAXIMUM_VALUE);
    }

    BigInteger value() {
        return value;
    }

    public static Ipv6 of(BigInteger value) {
        return new Ipv6(value);
    }

    public static Ipv6 of(String value) {
        return parse(value);
    }

    @Override
    public int compareTo(Ipv6 other) {
        return value.compareTo(other.value);
    }

    @Override
    public Ipv6 next() {
        return new Ipv6(value.add(ONE));
    }

    @Override
    public Ipv6 previous() {
        return new Ipv6(value.subtract(ONE));
    }

    @Override
    public boolean hasNext() {
        return this.compareTo(LAST_IPV6_ADDRESS) < 0;
    }

    @Override
    public boolean hasPrevious() {
        return this.compareTo(FIRST_IPV6_ADDRESS) > 0;
    }

    @Override
    public Ipv6Range asRange() {
        return new Ipv6Range(this, this);
    }

    @Override
    public String toString() {
        long[] parts = new long[8];

        // Find longest sequence of zeroes. Use the first one if there are
        // multiple sequences of zeroes with the same length.
        int currentZeroPartsLength = 0;
        int currentZeroPartsStart = 0;
        int maxZeroPartsLength = 0;
        int maxZeroPartsStart = 0;
        for (int i = 0; i < parts.length; ++i) {
            parts[i] = value().shiftRight((7 - i) * BITS_PER_PART).and(FOUR_OCTECT_MASK).longValue();
            if (parts[i] == 0) {
                if (currentZeroPartsLength == 0) {
                    currentZeroPartsStart = i;
                }
                ++currentZeroPartsLength;
                if (currentZeroPartsLength > maxZeroPartsLength) {
                    maxZeroPartsLength = currentZeroPartsLength;
                    maxZeroPartsStart = currentZeroPartsStart;
                }
            } else {
                currentZeroPartsLength = 0;
            }
        }

        StringBuilder sb = new StringBuilder(39);
        if (maxZeroPartsStart == 0 && maxZeroPartsLength > 1) {
            sb.append(COLON);
        }
        String delimiter = "";
        for (int i = 0; i < parts.length; ++i) {
            if (i == maxZeroPartsStart && maxZeroPartsLength > 1) {
                i += maxZeroPartsLength;
                sb.append(COLON);
            }
            sb.append(delimiter);
            if (i <= 7) {
                sb.append(Long.toHexString(parts[i]));
            } else {
                break;
            }
            delimiter = COLON;
        }
        return sb.toString();
    }

    /**
     * Parses a <tt>String</tt> into an {@link Ipv6} address.
     *
     * @param ipv6Address a text representation of an IPv6 address as defined in rfc4291
     * @return a new {@link Ipv6}
     * @throws NullPointerException if the string argument is <tt>null</tt>
     * @throws IllegalArgumentException if the string cannot be parsed
     * @see <a href="http://tools.ietf.org/html/rfc4291">rfc4291 - IP Version 6 Addressing Architecture</a>
     */
    public static Ipv6 parse(final String ipv6Address) {
        try {
            String ipv6String = Validate.notNull(ipv6Address).trim();
            Validate.isTrue(!ipv6String.isEmpty());

            final boolean isIpv6AddressWithEmbeddedIpv4 = ipv6String.contains(".");
            if (isIpv6AddressWithEmbeddedIpv4) {
                ipv6String = getIpv6AddressWithIpv4SectionInIpv6Notation(ipv6String);
            }

            final int indexOfDoubleColons = ipv6String.indexOf("::");
            final boolean isShortened = indexOfDoubleColons != -1;
            if (isShortened) {
                Validate.isTrue(indexOfDoubleColons == ipv6String.lastIndexOf("::"));
                ipv6String = expandMissingColons(ipv6String, indexOfDoubleColons);
            }

            final String[] split = ipv6String.split(COLON, TOTAL_OCTETS);
            Validate.isTrue(split.length == TOTAL_OCTETS);
            BigInteger ipv6value = BigInteger.ZERO;
            for (String part : split) {
                Validate.isTrue(part.length() <= MAX_PART_LENGTH);
                Validate.checkRange(Integer.parseInt(part, BITS_PER_PART), MIN_PART_VALUE, MAX_PART_VALUE);
                ipv6value = ipv6value.shiftLeft(BITS_PER_PART).add(new BigInteger(part, BITS_PER_PART));
            }
            return new Ipv6(ipv6value);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format(DEFAULT_PARSING_ERROR_MESSAGE, ipv6Address), e);
        }
    }

    private static String expandMissingColons(final String ipv6String, final int indexOfDoubleColons) {
        final int colonCount = countColons(ipv6String);
        Validate.isTrue(colonCount >= 2 && colonCount <= COLON_COUNT_IPV6 + 1);
        final int missingZeros = COLON_COUNT_IPV6 - colonCount + 1;
        String leftPart = ipv6String.substring(0, indexOfDoubleColons);
        String rightPart = ipv6String.substring(indexOfDoubleColons + 2);

        if (missingZeros == 0) {
            Validate.isTrue(leftPart.isEmpty() || rightPart.isEmpty());
        }

        if (leftPart.isEmpty()) {
            leftPart = ZERO;
        }
        if (rightPart.isEmpty()) {
            rightPart = ZERO;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(leftPart);
        for (int i = 0; i < missingZeros; i++) {
            sb.append(COLON).append(ZERO);
        }
        sb.append(COLON).append(rightPart);

        return sb.toString();
    }

    private static int countColons(String ipv6String) {
        int count = 0;
        for (char c : ipv6String.toCharArray()) {
            if (c == ':') {
                count++;
            }
        }
        return count;
    }

    private static String getIpv6AddressWithIpv4SectionInIpv6Notation(String ipv6String) {
        final int indexOfLastColon = ipv6String.lastIndexOf(COLON);
        final String ipv6Section = ipv6String.substring(0, indexOfLastColon);
        final String ipv4Section = ipv6String.substring(indexOfLastColon + 1);
        final Ipv4 ipv4 = Ipv4.parse(ipv4Section);
        final String ipv4FirstPart = Long.toHexString(ipv4.value() >>> BITS_PER_PART);
        final String ipv4SecondPart = Long.toHexString(ipv4.value() & MAX_PART_VALUE);
        return ipv6Section + COLON + ipv4FirstPart + COLON + ipv4SecondPart;
    }

    @Override
    public int bitSize() {
        return NUMBER_OF_BITS;
    }

    @Override
    public BigInteger asBigInteger() {
        return value;
    }

    @Override
    public Ipv6 lowerBoundForPrefix(int prefixLength) {
        Validate.checkRange(prefixLength, 0, NUMBER_OF_BITS);
        BigInteger mask = bitMask(0).xor(bitMask(prefixLength));
        return new Ipv6(value.and(mask));
    }

    @Override
    public Ipv6 upperBoundForPrefix(int prefixLength) {
        Validate.checkRange(prefixLength, 0, NUMBER_OF_BITS);
        return new Ipv6(value.or(bitMask(prefixLength)));
    }

    private BigInteger bitMask(int prefixLength) {
        return ONE.shiftLeft(NUMBER_OF_BITS - prefixLength).add(MINUS_ONE);
    }

    @Override
    public int getCommonPrefixLength(Ipv6 other) {
        BigInteger temp = value.xor(other.value);
        return NUMBER_OF_BITS - temp.bitLength();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Ipv6 that = (Ipv6) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
