package net.ripe.commons.ip;

import org.apache.commons.lang3.Validate;

public class Ipv4Range extends AbstractRange<Ipv4, Ipv4Range> implements InternetResourceRange<Ipv4, Ipv4Range, Long> {

    private static final String SLASH = "/";
    private static final String DASH = "-";

    protected Ipv4Range(Ipv4 start, Ipv4 end) {
        super(start, end);
    }

    @Override
    protected Ipv4Range newInstance(Ipv4 start, Ipv4 end) {
        return new Ipv4Range(start, end);
    }

    public static Ipv4RangeBuilder from(Ipv4 from) {
        return new Ipv4RangeBuilder(from);
    }

    public static Ipv4RangeBuilder from(Long from) {
        return new Ipv4RangeBuilder(Ipv4.of(from));
    }

    public static Ipv4RangeBuilder from(String from) {
        return new Ipv4RangeBuilder(Ipv4.parse(from));
    }

    /**
     * Parses a <tt>String</tt> into an {@link Ipv4Range}.
     *
     * @param range a dash separated string of two IPv4 addresses e.g. "192.168.0.0-192.168.255.255"
     *              or a CIDR-notation string, e.g. "192.168.0.0/16"
     * @return a new {@link Ipv4Range}
     * @throws IllegalArgumentException if the string cannot be parsed
     * @see #parseCidr(String)
     */
    public static Ipv4Range parse(String range) {
        int idx = range.indexOf(DASH);
        if (idx != -1) {
            Ipv4 start = Ipv4.parse(range.substring(0, idx));
            Ipv4 end = Ipv4.parse(range.substring(idx + 1, range.length()));
            return new Ipv4Range(start, end);
        } else {
            return parseCidr(range);
        }
    }

    /**
     * Parses a <tt>String</tt> of an IPv4 address and its subnet mask formatted as in a
     * Classless Inter-Domain Routing (CIDR) notation.
     *
     * @param cidrString a CIDR-notation string, e.g. "192.168.0.0/16"
     * @return a new {@link Ipv4Range}
     * @throws IllegalArgumentException if the string cannot be parsed
     * @see <a href="http://tools.ietf.org/html/rfc4632">rfc4632</a>
     */
    public static Ipv4Range parseCidr(String cidrString) {
        int idx = cidrString.indexOf(SLASH);
        Validate.isTrue(idx != -1, String.format("Argument [%s] is not a range or does not comply with the CIDR notation", cidrString));
        String address = cidrString.substring(0, idx);
        String prefix = cidrString.substring(idx + 1, cidrString.length());
        return parseWithPrefix(address, prefix);
    }

    public static Ipv4Range parseWithPrefix(String address, String prefixLength) {
        return parseWithPrefix(address, Integer.parseInt(prefixLength));
    }

    public static Ipv4Range parseWithPrefix(String address, int prefixLength) {
        Ipv4 ipv4 = Ipv4.parse(address);
        Ipv4 start = Ipv4Utils.lowerBoundForPrefix(ipv4, prefixLength);
        Ipv4 end = Ipv4Utils.upperBoundForPrefix(ipv4, prefixLength);
        return new Ipv4Range(start, end);
    }

    public static Ipv4Range parseDecimalNotation(String range) {
        int idx = range.indexOf(DASH);
        Validate.isTrue(idx != -1, String.format("Argument [%s] does not comply with the decimal range notation", range));
        long start = Long.valueOf(range.substring(0, idx));
        long end = Long.valueOf(range.substring(idx + 1, range.length()));
        return Ipv4Range.from(start).to(end);
    }

    @Override
    public Long size() {
        return (end().value() - start().value()) + 1;
    }

    @Override
    public String toString() {
        if (Ipv4PrefixUtils.isValidPrefix(this)) {
            return toStringInCidrNotation();
        } else {
            return toStringInRangeNotation();
        }
    }

    public String toStringInRangeNotation() {
        return new StringBuilder().append(start()).append(DASH).append(end()).toString();
    }

    public String toStringInCidrNotation() {
        return new StringBuilder().append(start()).append(SLASH).append(Ipv4PrefixUtils.getPrefixLength(this)).toString();
    }

    public String toStringInDecimalNotation() {
        return new StringBuilder().append(start().value()).append(DASH).append(end().value()).toString();
    }

    public static class Ipv4RangeBuilder extends AbstractRangeBuilder<Ipv4, Ipv4Range> {

        private final Ipv4 from;

        protected Ipv4RangeBuilder(Ipv4 from) {
            super(from, Ipv4Range.class);
            this.from = from;
        }

        public Ipv4Range to(Long end) {
            return super.to(Ipv4.of(end));
        }

        public Ipv4Range to(String end) {
            return super.to(Ipv4.parse(end));
        }

        public Ipv4Range andPrefixLength(int prefixLength) {
            Validate.isTrue(Ipv4Utils.lowerBoundForPrefix(from, prefixLength).equals(from),
                    String.format("%s/%d is not a valid Ipv4 address prefix.", from, prefixLength));
            return super.to(Ipv4Utils.upperBoundForPrefix(from, prefixLength));
        }
    }
}
