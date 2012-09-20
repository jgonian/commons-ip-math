package net.ripe.commons.ip;

import static java.math.BigInteger.*;
import java.math.BigInteger;

public class Ipv6Range extends AbstractIpRange<Ipv6, Ipv6Range> {

    private static final String DASH = "-";
    private static final String SLASH = "/";

    protected Ipv6Range(Ipv6 start, Ipv6 end) {
        super(start, end);
    }

    @Override
    protected Ipv6Range newInstance(Ipv6 start, Ipv6 end) {
        return new Ipv6Range(start, end);
    }

    public static Ipv6RangeBuilder from(Ipv6 from) {
        return new Ipv6RangeBuilder(from);
    }

    public static Ipv6RangeBuilder from(BigInteger from) {
        return new Ipv6RangeBuilder(Ipv6.of(from));
    }

    public static Ipv6RangeBuilder from(String from) {
        return new Ipv6RangeBuilder(Ipv6.parse(from));
    }

    /**
     * Parses a <tt>String</tt> into an {@link Ipv6Range}.
     *
     * @param range a dash separated string of two IPv6 addresses e.g. "2001:db8::1-2001:db8::2"
     *              or a CIDR-notation string, e.g. "2001:0db8:0:cd30::/60"
     * @return a new {@link Ipv6Range}
     * @throws IllegalArgumentException if the string cannot be parsed
     * @see #parseCidr(String)
     */
    public static Ipv6Range parse(String range) {
        int idx = range.indexOf(DASH);
        if (idx != -1) {
            Ipv6 start = Ipv6.parse(range.substring(0, idx));
            Ipv6 end = Ipv6.parse(range.substring(idx + 1, range.length()));
            return new Ipv6Range(start, end);
        } else {
            return parseCidr(range);
        }
    }

    /**
     * Parses a <tt>String</tt> of an IPv6 address and its subnet mask formatted as in a
     * Classless Inter-Domain Routing (CIDR) notation.
     *
     * @param cidrString a CIDR-notation string, e.g. "2001:0db8:0:cd30::/60"
     * @return a new {@link Ipv6Range}
     * @throws IllegalArgumentException if the string cannot be parsed
     * @see <a href="http://tools.ietf.org/html/rfc4632">rfc4632</a>
     * @see <a href="http://tools.ietf.org/html/rfc4291#section-2.3">rfc4291 §2.3</a>
     */
    public static Ipv6Range parseCidr(String cidrString) {
        int idx = cidrString.indexOf(SLASH);
        Validate.isTrue(idx != -1, "Argument [" + cidrString + "] is not a range or does not comply with the CIDR notation");
        String address = cidrString.substring(0, idx);
        String prefix = cidrString.substring(idx + 1, cidrString.length());
        return Ipv6Range.from(address).andPrefixLength(prefix);
    }

    public static Ipv6Range parseDecimalNotation(String range) {
        int idx = range.indexOf(DASH);
        Validate.isTrue(idx != -1, "Argument [" + range + "] does not comply with the decimal range notation");
        BigInteger start = new BigInteger(range.substring(0, idx));
        BigInteger end = new BigInteger(range.substring(idx + 1, range.length()));
        return Ipv6Range.from(start).to(end);
    }

    @Override
    public BigInteger size() {
        return (end().value().subtract(start().value())).add(ONE);
    }

    @Override
    public String toString() {
        if (PrefixUtils.isValidPrefix(this)) {
            return toStringInCidrNotation();
        } else {
            return toStringInRangeNotation();
        }
    }

    public String toStringInRangeNotation() {
        return start() + DASH + end();
    }

    public String toStringInCidrNotation() {
        return start() + SLASH + Ipv6PrefixUtils.getPrefixLength(this);
    }

    public String toStringInDecimalNotation() {
        return start().value() + DASH + end().value();
    }

    public static class Ipv6RangeBuilder extends AbstractRangeBuilder<Ipv6, Ipv6Range> {

        private final Ipv6 from;

        protected Ipv6RangeBuilder(Ipv6 from) {
            this.from = from;
        }
        
        public Ipv6Range to(BigInteger end) {
            return to(Ipv6.of(end));
        }

        public Ipv6Range to(String end) {
            return to(Ipv6.parse(end));
        }

        public Ipv6Range andPrefixLength(String prefixLength) {
            return andPrefixLength(Integer.parseInt(prefixLength));
        }

        public Ipv6Range andPrefixLength(int prefixLength) {
            Validate.isTrue(from.lowerBoundForPrefix(prefixLength).equals(from),
                    from + "/" + prefixLength + " is not a legal IPv6 address prefix.");
            return to(from.upperBoundForPrefix(prefixLength));
        }

        @Override
        public Ipv6Range to(Ipv6 to) {
            return new Ipv6Range(from, to);
        }
    }
}
