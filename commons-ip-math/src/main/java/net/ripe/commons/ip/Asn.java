package net.ripe.commons.ip;

import static net.ripe.commons.ip.RangeUtils.checkRange;

public class Asn extends SingleValue<Long> implements SingleInternetResource<Asn, AsnRange> {

    private static final long serialVersionUID = -1L;

    public static final long ASN_MIN_VALUE = 0L;
    public static final long ASN_16_BIT_MAX_VALUE = (1L << 16) - 1L;
    public static final long ASN_32_BIT_MAX_VALUE = (1L << 32) - 1L;

    public static final Asn FIRST_ASN = Asn.of(ASN_MIN_VALUE);
    public static final Asn LAST_16_BIT_ASN = Asn.of(ASN_16_BIT_MAX_VALUE);
    public static final Asn LAST_32_BIT_ASN = Asn.of(ASN_32_BIT_MAX_VALUE);

    private static final int _16 = 16;

    public Asn(Long value) {
        super(value);
        checkRange(value, ASN_MIN_VALUE, ASN_32_BIT_MAX_VALUE);
    }

    public static Asn of(Long value) {
        return new Asn(value);
    }

    public static Asn of(String value) {
        return parse(value);
    }

    /**
     * Parses a <tt>String</tt> into an {@link Asn}. The representation formats that are supported are
     * asplain, asdot+ and asdot as defined in RFC5396.
     *
     * @param text a string of an AS number e.g. "AS123", "AS0.123", "123" e.t.c.
     * @return a new {@link Asn}
     * @throws IllegalArgumentException if the string cannot be parsed
     * @see <a href="http://tools.ietf.org/html/rfc5396">RFC5396 -
     * Textual Representation of Autonomous System (AS) Numbers</a>
     */
    public static Asn parse(String text) {
        try {
            String asnString = Validate.notNull(text, "AS Number must not be null").trim().toUpperCase();
            if (asnString.startsWith("AS")) {
                asnString = asnString.substring(2);
            }
            long low;
            long high = 0L;
            int indexOfDot = asnString.indexOf(".");
            if (indexOfDot != -1) {
                low = checkRange(Long.valueOf(asnString.substring(indexOfDot + 1)), ASN_MIN_VALUE, ASN_16_BIT_MAX_VALUE);
                high = checkRange(Long.valueOf(asnString.substring(0, indexOfDot)), ASN_MIN_VALUE, ASN_16_BIT_MAX_VALUE);
            } else {
                low = Long.valueOf(asnString);
            }
            return new Asn((high << _16) | low);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid AS number: '" + text + "'. Details: " + ex.getMessage());
        }
    }

    public boolean is16Bit() {
        return this.compareTo(LAST_16_BIT_ASN) <= 0;
    }

    public boolean is32Bit() {
        return !is16Bit();
    }

    @Override
    public int compareTo(Asn other) {
        return value().compareTo(other.value());
    }

    @Override
    public Asn next() {
        return new Asn(value() + 1);
    }

    @Override
    public Asn previous() {
        return new Asn(value() - 1);
    }

    @Override
    public boolean hasNext() {
        return this.compareTo(LAST_32_BIT_ASN) < 0;
    }

    @Override
    public boolean hasPrevious() {
        return this.compareTo(FIRST_ASN) > 0;
    }

    @Override
    public String toString() {
        return "AS" + value();
    }

    @Override
    public AsnRange asRange() {
        return new AsnRange(this, this);
    }
}