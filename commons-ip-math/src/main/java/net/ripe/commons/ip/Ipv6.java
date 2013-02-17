package net.ripe.commons.ip;

import static java.math.BigInteger.*;
import static net.ripe.commons.ip.RangeUtils.*;
import java.math.BigInteger;

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
    private static final String DEFAULT_PARSING_ERROR_MESSAGE = "Invalid IPv6 address: ";
    private static final String COLON = ":";
    private static final String ZERO = "0";
    private static final int BITS_PER_PART = 16;
    private static final int TOTAL_OCTETS = 8;
    private static final int COLON_COUNT_FOR_EMBEDDED_IPV4 = 6;
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
    public static Ipv6 parse(String ipv6Address) {
        String ipv6String = Validate.notNull(ipv6Address, "IPv6 address must not be null").trim();
        Validate.isTrue(!ipv6String.isEmpty(), "IPv6 address must not be empty");

        int indexOfDoubleColons = ipv6String.indexOf("::");
        boolean isShortened = indexOfDoubleColons != -1;
        if (isShortened) {
            Validate.isTrue(indexOfDoubleColons == ipv6String.lastIndexOf("::"), DEFAULT_PARSING_ERROR_MESSAGE + ipv6Address);
            ipv6String = expandMissingColons(ipv6String, indexOfDoubleColons);
        }

        boolean isIpv6AddressWithEmbeddedIpv4 = isIpv6AddressWithEmbeddedIpv4(ipv6String);
        if (isIpv6AddressWithEmbeddedIpv4) {
            ipv6String = getIpv6AddressWithIpv4SectionInIpv6Notation(ipv6String);
        }

        String[] split = ipv6String.split(COLON, TOTAL_OCTETS);
        Validate.isTrue(split.length == TOTAL_OCTETS, DEFAULT_PARSING_ERROR_MESSAGE + ipv6Address);
        BigInteger ipv6value = BigInteger.ZERO;
        for (String part : split) {
            Validate.isTrue(part.length() <= MAX_PART_LENGTH, DEFAULT_PARSING_ERROR_MESSAGE + ipv6Address);
            checkRange(Integer.parseInt(part, BITS_PER_PART), MIN_PART_VALUE, MAX_PART_VALUE);
            ipv6value = ipv6value.shiftLeft(BITS_PER_PART).add(new BigInteger(part, BITS_PER_PART));
        }
        return new Ipv6(ipv6value);
    }

    private static String expandMissingColons(String ipv6String, int indexOfDoubleColons) {
        int colonCount = ipv6String.contains(".") ? COLON_COUNT_FOR_EMBEDDED_IPV4 : COLON_COUNT_IPV6;
        int count = colonCount - countColons(ipv6String) + 2;
        String leftPart = ipv6String.substring(0, indexOfDoubleColons);
        String rightPart = ipv6String.substring(indexOfDoubleColons + 2);
        if (leftPart.isEmpty()) {
            leftPart = ZERO;
        }
        if (rightPart.isEmpty()) {
            rightPart = ZERO;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(leftPart);
        for (int i = 0; i < count - 1; i++) {
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
        try {
            int indexOfLastColon = ipv6String.lastIndexOf(COLON);
            String ipv6Section = ipv6String.substring(0, indexOfLastColon);
            String ipv4Section = ipv6String.substring(indexOfLastColon + 1);
            Ipv4 ipv4 = Ipv4.parse(ipv4Section);
            Ipv6 ipv6FromIpv4 = new Ipv6(BigInteger.valueOf(ipv4.value()));
            String ipv4SectionInIpv6Notation = ipv6FromIpv4.toString().substring(2);
            return ipv6Section + COLON + ipv4SectionInIpv6Notation;
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("Embedded IPv4 in IPv6 address is invalid: " + ipv6String, e);
        }
    }

    // TODO(yg) make it compliant with RFC6052 (http://tools.ietf.org/html/rfc6052)
    private static boolean isIpv6AddressWithEmbeddedIpv4(String ipv6String) {
        if (ipv6String.contains(".")) {
            int indexOfLastColon = ipv6String.lastIndexOf(COLON);
            Validate.isTrue(isIpv4CompatibleIpv6Address(ipv6String, indexOfLastColon) ^
                            isIpv4MappedToIpv6Address(ipv6String, indexOfLastColon),
                    "Invalid IPv6 address with embedded IPv4");
            return true;
        }
        return false;
    }

    /*
     * RFC4291 - http://tools.ietf.org/html/rfc4291#section-2.5.5.1
     * +-----------------------------------------------------------------+
     * |                80 bits               | 16 |      32 bits        |
     * +--------------------------------------+--------------------------+
     * |0000..............................0000|0000|    IPv4 address     |
     * +--------------------------------------+----+---------------------+
     */
    private static boolean isIpv4CompatibleIpv6Address(String ipv6String, int indexOfLastColon) {
        int i = indexOfLastColon - 1;
        char charAt = ipv6String.charAt(i);
        boolean result = (charAt == ':');
        while (charAt != ':') {
            if (charAt == '0') {
                result = true;
            } else {
                return false;
            }
            charAt = ipv6String.charAt(--i);
        }
        char[] chars = ipv6String.toCharArray();
        for (int k = 0; k < i; k++) {
            if (chars[k] != '0' && chars[k] != ':') {
                return false;
            }
        }
        return result;
    }

    /*
     * RFC4291 - http://tools.ietf.org/html/rfc4291#section-2.5.5.2
     * +-----------------------------------------------------------------+
     * |                80 bits               | 16 |      32 bits        |
     * +--------------------------------------+--------------------------+
     * |0000..............................0000|FFFF|    IPv4 address     |
     * +--------------------------------------+----+---------------------+
     */
    private static boolean isIpv4MappedToIpv6Address(String ipv6String, int indexOfLastColon) {
        int i = indexOfLastColon - 1;
        int result = 0x0;
        char charAt = ipv6String.charAt(i);
        while (charAt != ':') {
            if (charAt == 'f' || charAt == 'F') {
                result = (result << 4) + 0xf;
            }
            charAt = ipv6String.charAt(--i);
        }
        char[] chars = ipv6String.toCharArray();
        for (int k = 0; k < i; k++) {
            if (chars[k] != '0' && chars[k] != ':') {
                return false;
            }
        }
        return result == MAX_PART_VALUE;
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
        checkRange(prefixLength, 0, NUMBER_OF_BITS);
        BigInteger mask = bitMask(0).xor(bitMask(prefixLength));
        return new Ipv6(value.and(mask));
    }

    @Override
    public Ipv6 upperBoundForPrefix(int prefixLength) {
        checkRange(prefixLength, 0, NUMBER_OF_BITS);
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
