package net.ripe.commons.ip;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

public class Ipv4Range extends AbstractIpRange<Ipv4, Ipv4Range> {

    private static final long serialVersionUID = 1L;

    protected Ipv4Range(Ipv4 start, Ipv4 end) {
        super(start, end);
    }

    @Override
    protected Ipv4Range newInstance(BigInteger start, BigInteger end) {
        return Ipv4Range.from(start).to(end);
    }

    @Override
    protected Ipv4Range newInstance(Ipv4 start, Ipv4 end) {
        return new Ipv4Range(start, end);
    }

    public static Ipv4RangeBuilder from(Ipv4 from) {
        return new Ipv4RangeBuilder(from);
    }

    public static Ipv4RangeBuilder from(BigInteger from) {
        return new Ipv4RangeBuilder(Ipv4.of(from));
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
        Validate.isTrue(idx != -1, "Argument [" + cidrString + "] is not a range or does not comply with the CIDR notation");
        String address = cidrString.substring(0, idx);
        String prefix = cidrString.substring(idx + 1, cidrString.length());
        return Ipv4Range.from(address).andPrefixLength(prefix);
    }

    public static Ipv4Range parseDecimalNotation(String range) {
        int idx = range.indexOf(DASH);
        Validate.isTrue(idx != -1, "Argument [" + range + "] does not comply with the decimal range notation");
        long start = Long.valueOf(range.substring(0, idx));
        long end = Long.valueOf(range.substring(idx + 1, range.length()));
        return Ipv4Range.from(start).to(end);
    }

    @Override
    public Long size() {
        return (end().value() - start().value()) + 1;
    }

    // TODO(yg): refactor and move to parent
    public String toStringInSlashNotation() {
        List<Ipv4Range> prefixes = splitToPrefixes();
        Collections.sort(prefixes, StartAndSizeComparator.<Ipv4, Ipv4Range>getInstance());
        StringBuilder notation = new StringBuilder().append(start());

        for (Ipv4Range range : prefixes) {
            notation.append(SLASH).append(PrefixUtils.getPrefixLength(range));
            notation.append(",");
        }
        String noted = notation.toString();
        return noted.substring(0, noted.length() - 1);
    }

    public static class Ipv4RangeBuilder extends AbstractRangeBuilder<Ipv4, Ipv4Range> {

        private final Ipv4 from;

        protected Ipv4RangeBuilder(Ipv4 from) {
            this.from = from;
        }

        public Ipv4Range to(BigInteger end) {
            return to(Ipv4.of(end));
        }

        public Ipv4Range to(Long end) {
            return to(Ipv4.of(end));
        }

        public Ipv4Range to(String end) {
            return to(Ipv4.parse(end));
        }

        public Ipv4Range andPrefixLength(String prefix) {
            return andPrefixLength(Integer.parseInt(prefix));
        }

        public Ipv4Range andPrefixLength(int prefixLength) {
            Validate.isTrue(from.lowerBoundForPrefix(prefixLength).equals(from),
                    from + "/" + prefixLength + " is not a legal IPv4 address prefix.");
            return to(from.upperBoundForPrefix(prefixLength));
        }

        @Override
        public Ipv4Range to(Ipv4 to) {
            return new Ipv4Range(from, to);
        }
    }
}
