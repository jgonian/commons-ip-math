package net.ripe.commons.ip.resource;

import static java.math.BigInteger.*;
import java.math.BigInteger;
import net.ripe.commons.ip.range.Ipv6Range;
import org.apache.commons.lang3.Validate;

public class Ipv6 extends SingleValue<BigInteger> implements SingleInternetResource<Ipv6, Ipv6Range> {

    private static final long serialVersionUID = -1L;

    public static final BigInteger NETWORK_MASK = BigInteger.valueOf(0xffff);
    public static final int IPv6_NUMBER_OF_BITS = 128;
    public static final BigInteger IPv6_MINIMUM_VALUE = ZERO;
    public static final BigInteger IPv6_MAXIMUM_VALUE = new BigInteger(String.valueOf((ONE.shiftLeft(IPv6_NUMBER_OF_BITS)).subtract(ONE)));

    public static final Ipv6 FIRST_IPV6_ADDRESS = Ipv6.of(IPv6_MINIMUM_VALUE);
    public static final Ipv6 LAST_IPV6_ADDRESS = Ipv6.of(IPv6_MAXIMUM_VALUE);

    protected Ipv6(BigInteger value) {
        super(value);
        Validate.isTrue(value.compareTo(IPv6_MINIMUM_VALUE) >= 0, "Value of Ipv6 has to be greater than or equal to " + IPv6_MINIMUM_VALUE);
        Validate.isTrue(value.compareTo(IPv6_MAXIMUM_VALUE) <= 0, "Value of Ipv6 has to be less than or equal to " + IPv6_MAXIMUM_VALUE);
    }

    public static Ipv6 of(BigInteger value) {
        return new Ipv6(value);
    }

    public static Ipv6 of(String value) {
        throw new UnsupportedOperationException("TODO(ygoniana) implement");
    }

    @Override
    public int compareTo(Ipv6 other) {
        return value().compareTo(other.value());
    }

    @Override
    public Ipv6 next() {
        return new Ipv6(value().add(ONE));
    }

    @Override
    public Ipv6 previous() {
        return new Ipv6(value().subtract(ONE));
    }

    @Override
    public Ipv6Range asRange() {
        return Ipv6Range.from(this).to(this);
    }

    @Override
    public String toString() {
        long[] list = new long[8];
        int currentZeroLength = 0;
        int maxZeroLength = 0;
        int maxZeroIndex = 0;
        for (int i = 7; i >= 0; i--) {
            list[i] = value().shiftRight(i * 16).and(NETWORK_MASK).longValue();

            if (list[i] == 0) {
                currentZeroLength++;
            } else {
                if (currentZeroLength > maxZeroLength) {
                    maxZeroIndex = i + currentZeroLength;
                    maxZeroLength = currentZeroLength;
                }
                currentZeroLength = 0;
            }
        }
        if (currentZeroLength > maxZeroLength) {
            maxZeroIndex = -1 + currentZeroLength;
            maxZeroLength = currentZeroLength;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            if (i == maxZeroIndex && maxZeroLength > 1) {
                if (i == 7) {
                    sb.append(':');
                }
                i -= (maxZeroLength - 1);
            } else {
                sb.append(String.format("%x", list[i]));
            }
            sb.append(':');
        }
        if ((maxZeroIndex - maxZeroLength + 1) != 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }
}
