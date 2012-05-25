package net.ripe.commons.ip.resource;

import static java.math.BigInteger.*;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.ripe.commons.ip.range.Ipv6Range;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class Ipv6 extends SingleValue<BigInteger> implements SingleInternetResource<Ipv6, Ipv6Range> {

    private static final long serialVersionUID = -1L;

    public static final BigInteger NETWORK_MASK = BigInteger.valueOf(0xffff);
    public static final int IPv6_NUMBER_OF_BITS = 128;
    public static final BigInteger IPv6_MINIMUM_VALUE = ZERO;
    public static final BigInteger IPv6_MAXIMUM_VALUE = new BigInteger(String.valueOf((ONE.shiftLeft(IPv6_NUMBER_OF_BITS)).subtract(ONE)));

    public static final Ipv6 FIRST_IPV6_ADDRESS = Ipv6.of(IPv6_MINIMUM_VALUE);
    public static final Ipv6 LAST_IPV6_ADDRESS = Ipv6.of(IPv6_MAXIMUM_VALUE);

    private static final Pattern IPV6_PATTERN_RFC4291 = Pattern.compile("(([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))");
    private static final Pattern IPV6_PATTERN_EXPANDED = Pattern.compile("([0-9a-fA-F]{0,4}):([0-9a-fA-F]{0,4}):([0-9a-fA-F]{0,4}):([0-9a-fA-F]{0,4}):([0-9a-fA-F]{0,4}):([0-9a-fA-F]{0,4}):([0-9a-fA-F]{0,4}):([0-9a-fA-F]{0,4})");
    private static final int COLON_COUNT_FOR_EMBEDDED_IPV4 = 6;
    private static final int COLON_COUNT_IPV6 = 7;

    protected Ipv6(BigInteger value) {
        super(value);
        Validate.isTrue(value.compareTo(IPv6_MINIMUM_VALUE) >= 0, "Value of Ipv6 has to be greater than or equal to " + IPv6_MINIMUM_VALUE);
        Validate.isTrue(value.compareTo(IPv6_MAXIMUM_VALUE) <= 0, "Value of Ipv6 has to be less than or equal to " + IPv6_MAXIMUM_VALUE);
    }

    public static Ipv6 of(BigInteger value) {
        return new Ipv6(value);
    }

    public static Ipv6 of(String value) {
        return parse(value);
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
    public boolean hasNext() {
        return this.compareTo(LAST_IPV6_ADDRESS) < 0;
    }

    @Override
    public boolean hasPrevious() {
        return this.compareTo(FIRST_IPV6_ADDRESS) > 0;
    }

    @Override
    public Ipv6Range asRange() {
        return Ipv6Range.from(this).to(this);
    }

    /**
     * Returns a text representation of this Ipv6 address. Note this representation adheres to the
     * recommendations of RFC 5952.
     *
     * @return a text representation of this Ipv6 address
     *
     * @see <a href="http://tools.ietf.org/html/rfc5952">rfc5952 - A Recommendation for IPv6 Address Text Representation</a>
     */
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
        Validate.notNull(ipv6Address);
        ipv6Address = ipv6Address.trim();
        Validate.isTrue(IPV6_PATTERN_RFC4291.matcher(ipv6Address).matches(), "Invalid IPv6 address: " + ipv6Address);

        ipv6Address = expandMissingColons(ipv6Address);
        if (isInIpv4EmbeddedIpv6Format(ipv6Address)) {
            ipv6Address = getIpv6AddressWithIpv4SectionInIpv6Notation(ipv6Address);
        }
        return new Ipv6(ipv6StringtoBigInteger(ipv6Address));
    }

    private static String expandMissingColons(String ipAddressString) {
        int colonCount = isInIpv4EmbeddedIpv6Format(ipAddressString) ? COLON_COUNT_FOR_EMBEDDED_IPV4 : COLON_COUNT_IPV6;
        return ipAddressString.replace("::", StringUtils.repeat(":", colonCount - StringUtils.countMatches(ipAddressString, ":") + 2));
    }

    private static boolean isInIpv4EmbeddedIpv6Format(String ipAddressString) {
        return ipAddressString.contains(".");
    }

    private static String getIpv6AddressWithIpv4SectionInIpv6Notation(String ipAddressString) {
        String ipv6Section = StringUtils.substringBeforeLast(ipAddressString, ":");
        String ipv4Section = StringUtils.substringAfterLast(ipAddressString, ":");
        try {
            Ipv4 ipv4 = Ipv4.parse(ipv4Section);
            Ipv6 ipv6FromIpv4 = new Ipv6(BigInteger.valueOf(ipv4.value()));
            String ipv4SectionInIpv6Notation = StringUtils.join(ipv6FromIpv4.toString().split(":"), ":", 2, 4);
            return ipv6Section + ":" + ipv4SectionInIpv6Notation;
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("Embedded Ipv4 in IPv6 address is invalid: " + ipAddressString, e);
        }
    }

    private static BigInteger ipv6StringtoBigInteger(String ipAddressString) {
        Matcher m = IPV6_PATTERN_EXPANDED.matcher(ipAddressString);
        m.find();

        String ipv6Number = "";
        for (int i = 1; i <= m.groupCount(); i++) {
            String part = m.group(i);
            String padding = "0000".substring(0, 4 - part.length());
            ipv6Number = ipv6Number + padding + part;
        }

        return new BigInteger(ipv6Number, 16);
    }
}
