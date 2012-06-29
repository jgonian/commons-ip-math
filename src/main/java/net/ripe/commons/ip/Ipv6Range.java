package net.ripe.commons.ip;

import static java.math.BigInteger.*;
import java.math.BigInteger;

public class Ipv6Range extends AbstractIpRange<BigInteger, Ipv6, Ipv6Range> {

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
     * @param range a dash separated string of two Ipv6 addresses e.g. "2001:db8::1-2001:db8::2"
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
     * Parses a <tt>String</tt> of an Ipv6 address and its subnet mask formatted as in a
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
        return parseWithPrefix(address, prefix);
    }

    public static Ipv6Range parseWithPrefix(String address, String prefixLength) {
        return parseWithPrefix(address, Integer.parseInt(prefixLength));
    }

    public static Ipv6Range parseWithPrefix(String address, int prefixLength) {
        Ipv6 ipv6 = Ipv6.parse(address);
        Ipv6 start = Ipv6Utils.lowerBoundForPrefix(ipv6, prefixLength);
        Ipv6 end = Ipv6Utils.upperBoundForPrefix(ipv6, prefixLength);
        return new Ipv6Range(start, end);
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
        return length().value();
    }

    @Override
    public String toString() {
        if (Ipv6PrefixUtils.isValidPrefix(this)) {
            return toStringInCidrNotation();
        } else {
            return toStringInRangeNotation();
        }
    }

    public String toStringInRangeNotation() {
        return new StringBuilder().append(start()).append(DASH).append(end()).toString();
    }

    public String toStringInCidrNotation() {
        return new StringBuilder().append(start()).append(SLASH).append(Ipv6PrefixUtils.getPrefixLength(this)).toString();
    }

    public String toStringInDecimalNotation() {
        return new StringBuilder().append(start().value()).append(DASH).append(end().value()).toString();
    }

    @Override
    public Length<BigInteger> length() {
        return new Length<BigInteger>((end().value().subtract(start().value())).add(ONE));
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

        public Ipv6Range andPrefixLength(int prefixLength) {
            Validate.isTrue(Ipv6Utils.lowerBoundForPrefix(from, prefixLength).equals(from),
                    from + "/" + prefixLength + " is not a valid Ipv6 address prefix.");
            return to(Ipv6Utils.upperBoundForPrefix(from, prefixLength));
        }

        @Override
        public Ipv6Range to(Ipv6 to) {
            return new Ipv6Range(from, to);
        }
    }
}
