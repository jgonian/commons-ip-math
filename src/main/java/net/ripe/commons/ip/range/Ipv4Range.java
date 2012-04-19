package net.ripe.commons.ip.range;

import net.ripe.commons.ip.resource.InternetResourceRange;
import net.ripe.commons.ip.resource.Ipv4;
import net.ripe.commons.ip.resource.Ipv4Utils;
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
     * or a CIDR-notation string, e.g. "192.168.0.0/16"
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
        Validate.isTrue(idx != -1, String.format("Argument does not comply with the CIDR notation"));
        String address = cidrString.substring(0, idx);
        String prefix = cidrString.substring(idx + 1, cidrString.length());
        return parseWithPrefix(address, prefix);
    }

    public static Ipv4Range parseWithPrefix(String address, String prefix) {
        return parseWithPrefix(address, Integer.parseInt(prefix));
    }

    public static Ipv4Range parseWithPrefix(String address, int prefix) {
        Ipv4 ipv4 = Ipv4.parse(address);
        Ipv4 start = Ipv4Utils.lowerBoundForPrefix(ipv4, prefix);
        Ipv4 end = Ipv4Utils.upperBoundForPrefix(ipv4, prefix);
        return new Ipv4Range(start, end);
    }

    @Override
    public Long size() {
        return (end().value() - start().value()) + 1;
    }

    public static class Ipv4RangeBuilder extends AbstractRangeBuilder<Ipv4, Ipv4Range> {
        protected Ipv4RangeBuilder(Ipv4 from) {
            super(from, Ipv4Range.class);
        }

        public Ipv4Range to(Long end) {
            return super.to(Ipv4.of(end));
        }

        public Ipv4Range to(String end) {
            return super.to(Ipv4.parse(end));
        }
    }
}
