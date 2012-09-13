package net.ripe.commons.ip;

import java.math.BigInteger;

public class Ipv4 extends AbstractIp<Long, Ipv4, Ipv4Range> {

    private static final long serialVersionUID = -1L;

    public static final int BYTE_MASK = 0xff;
    public static final int NUMBER_OF_BITS = 32;
    public static final long MINIMUM_VALUE = 0;
    public static final long MAXIMUM_VALUE = (1L << NUMBER_OF_BITS) - 1;

    public static final Ipv4 FIRST_IPV4_ADDRESS = Ipv4.of(MINIMUM_VALUE);
    public static final Ipv4 LAST_IPV4_ADDRESS = Ipv4.of(MAXIMUM_VALUE);

    private static final int TOTAL_OCTETS = 4;
    private static final int MAX_OCTET_VALUE = 255;
    private static final int MIN_OCTET_VALUE = 0;

    static final int THREE_OCTETS = 24;
    static final int TWO_OCTETS = 16;
    static final int ONE_OCTET = 8;

    protected Ipv4(Long value) {
        super(value);
        Validate.isTrue(value.compareTo(MINIMUM_VALUE) >= 0, "Value of IPv4 has to be greater than or equal to " + MINIMUM_VALUE);
        Validate.isTrue(value.compareTo(MAXIMUM_VALUE) <= 0, "Value of IPv4 has to be less than or equal to " + MAXIMUM_VALUE);
    }

    public static Ipv4 of(BigInteger from) {
        if(from == null) {
            throw new IllegalArgumentException("from cannot be null");
        }
        return new Ipv4(from.longValue());
    }

    public static Ipv4 of(Long value) {
        return new Ipv4(value);
    }

    public static Ipv4 of(String value) {
        return parse(value);
    }

    public static Ipv4 parse(String ipv4Address) {
        String ipv4String = Validate.notNull(ipv4Address, "Input IPv4 string must not be empty").trim();
        Validate.isTrue(!ipv4String.isEmpty()
                && Character.isDigit(ipv4String.charAt(0))
                && Character.isDigit(ipv4String.charAt(ipv4String.length() - 1)), "Invalid IPv4 address: " + ipv4Address);

        long value = 0;
        int octet = 0;
        int octetCount = 1;
        int length = ipv4String.length();

        for (int i = 0; i < length; ++i) {
            char ch = ipv4String.charAt(i);
            if (Character.isDigit(ch)) {
                octet = octet * 10 + (ch - '0');
            } else if (ch == '.') {
                Validate.isTrue(octetCount < TOTAL_OCTETS, "Invalid IPv4 address: " + ipv4String);
                octetCount++;
                value = addOctet(value, octet);
                octet = 0;
            } else {
                throw new IllegalArgumentException("Invalid IPv4 address: " + ipv4String);
            }
        }

        value = addOctet(value, octet);

        if (octetCount != TOTAL_OCTETS) {
            throw new IllegalArgumentException("Invalid IPv4 address: " + ipv4String);
        }

        return new Ipv4(value);
    }

    private static long addOctet(long value, int octet) {
        RangeUtils.checkRange(octet, MIN_OCTET_VALUE, MAX_OCTET_VALUE);
        return ((value) << 8) | octet;
    }

    /*
    @Override
    public int getCommonPrefixLength(Ipv4Address other) {
        long temp = value ^ other.value;
        return Integer.numberOfLeadingZeros((int) temp);
    }
    */

    @Override
    public int compareTo(Ipv4 other) {
        return value().compareTo(other.value());
    }

    @Override
    public String toString() {
        long value = value();
        int a = (int) (value >> THREE_OCTETS);
        int b = (int) (value >> TWO_OCTETS) & BYTE_MASK;
        int c = (int) (value >> ONE_OCTET) & BYTE_MASK;
        int d = (int) value & BYTE_MASK;

        return a + "." + b + "." + c + "." + d;
    }

    /*@Override
    public boolean isValidNetmask() {
        int leadingOnesCount = Integer.numberOfLeadingZeros(~(int) value);
        int trailingZeroesCount = Integer.numberOfTrailingZeros((int) value);
        return leadingOnesCount > 0 && (leadingOnesCount + trailingZeroesCount) == IPv4_NUMBER_OF_BITS;
    }*/

    @Override
    public Ipv4 next() {
        return new Ipv4(value() + 1);
    }

    @Override
    public Ipv4 previous() {
        return new Ipv4(value() - 1);
    }

    @Override
    public boolean hasNext() {
        return this.compareTo(LAST_IPV4_ADDRESS) < 0;
    }

    @Override
    public boolean hasPrevious() {
        return this.compareTo(FIRST_IPV4_ADDRESS) > 0;
    }

    @Override
    public Ipv4Range asRange() {
        return new Ipv4Range(this, this);
    }

}
