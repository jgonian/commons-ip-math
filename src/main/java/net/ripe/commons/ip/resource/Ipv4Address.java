package net.ripe.commons.ip.resource;

import org.apache.commons.lang.Validate;

public class Ipv4Address extends SingleValue<Long> implements IpResource<Ipv4Address> {

    private static final long serialVersionUID = -1L;

    public static final int IPv4_BYTE_MASK = 0xff;
    public static final int IPv4_NUMBER_OF_BITS = 32;
    public static final long IPv4_MINIMUM_VALUE = 0;
    public static final long IPv4_MAXIMUM_VALUE = (1L << IPv4_NUMBER_OF_BITS) - 1;

    protected Ipv4Address(Long value) {
        super(value);
        Validate.isTrue(value.compareTo(IPv4_MINIMUM_VALUE) >= 0, "Value of Ipv4Address has to be greater than or equal to " + IPv4_MINIMUM_VALUE);
        Validate.isTrue(value.compareTo(IPv4_MAXIMUM_VALUE) <= 0, "Value of Ipv4Address has to be less than or equal to " + IPv4_MAXIMUM_VALUE);
    }

    public static Ipv4Address of(Long value) {
        return new Ipv4Address(value);
    }

    public static Ipv4Address of(String value) {
        return parse(value);
    }

    public static Ipv4Address parse(String ipv4String) {
        // 127.0.0.1/8

        return parse(ipv4String, false);
    }

    public static Ipv4Address parse(String ipv4String, boolean defaultMissingOctets) {
        Validate.notNull(ipv4String, "ipv4String must not be empty");
        ipv4String = ipv4String.trim();
        Validate.isTrue(!ipv4String.isEmpty()
                && Character.isDigit(ipv4String.charAt(0))
                && Character.isDigit(ipv4String.charAt(ipv4String.length() - 1)), "Invalid IPv4 address: " + ipv4String);

        long value = 0;
        int octet = 0;
        int octetCount = 1;
        int length = ipv4String.length();

        for (int i = 0; i < length; ++i) {
            char ch = ipv4String.charAt(i);
            if (Character.isDigit(ch)) {
                octet = octet * 10 + (ch - '0');
            } else if (ch == '.') {
                Validate.isTrue(octetCount < 4, "invalid IPv4 address: " + ipv4String);
                octetCount++;
                value = addOctet(value, octet);
                octet = 0;
            } else {
                throw new IllegalArgumentException("invalid IPv4 address: " + ipv4String);
            }
        }

        value = addOctet(value, octet);

        if (defaultMissingOctets) {
            value <<= 8 * (4 - octetCount);
        } else if (octetCount != 4) {
            throw new IllegalArgumentException("invalid IPv4 address: " + ipv4String);
        }

        return new Ipv4Address(value);
    }

    private static long addOctet(long value, int octet) {
        if (octet < 0 || octet > 255) {
            throw new IllegalArgumentException("value of octet not in range 0..255: " + octet);
        }
        value = ((value) << 8) | octet;
        return value;
    }

    /*@Override
    public int getCommonPrefixLength(Ipv4Address other) {
        long temp = value ^ other.value;
        return Integer.numberOfLeadingZeros((int) temp);
    }

    @Override
    public Ipv4Address lowerBoundForPrefix(int prefixLength) {
        long mask = ~((1L << (IPv4_NUMBER_OF_BITS - prefixLength)) -  1);
        return new Ipv4Address(value & mask);
    }

    @Override
    public Ipv4Address upperBoundForPrefix(int prefixLength) {
        long mask = (1L << (IPv4_NUMBER_OF_BITS - prefixLength)) -  1;
        return new Ipv4Address(value | mask);
    }*/

    @Override
    public int compareTo(Ipv4Address other) {
        return value().compareTo(other.value());
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean defaultMissingOctets) {
        long value = value();
        int a = (int) (value >> 24);
        int b = (int) (value >> 16) & IPv4_BYTE_MASK;
        int c = (int) (value >> 8) & IPv4_BYTE_MASK;
        int d = (int) value & IPv4_BYTE_MASK;

        if (!defaultMissingOctets) {
            return a + "." + b + "." + c + "." + d;
        } else if (b == 0 && c == 0 && d == 0) {
            return "" + a;
        } else if (c == 0 && d == 0) {
            return a + "." + b;
        } else if (d == 0) {
            return a + "." + b + "." + c;
        } else {
            return a + "." + b + "." + c + "." + d;
        }
    }

    /*@Override
    public boolean isValidNetmask() {
        int leadingOnesCount = Integer.numberOfLeadingZeros(~(int) value);
        int trailingZeroesCount = Integer.numberOfTrailingZeros((int) value);
        return leadingOnesCount > 0 && (leadingOnesCount + trailingZeroesCount) == IPv4_NUMBER_OF_BITS;
    }*/

}
