package net.ripe.commons.ip;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.Validate;

public class Asn extends SingleValue<Long> implements SingleInternetResource<Asn, AsnRange> {

    private static final long serialVersionUID = -1L;

    public static final long ASN_MIN_VALUE = 0L;
    public static final long ASN_16_BIT_MAX_VALUE = (1L << 16) - 1L;
    public static final long ASN_32_BIT_MAX_VALUE = (1L << 32) - 1L;

    public static final Asn FIRST_ASN = Asn.of(ASN_MIN_VALUE);
    public static final Asn LAST_16_BIT_ASN = Asn.of(ASN_16_BIT_MAX_VALUE);
    public static final Asn LAST_32_BIT_ASN = Asn.of(ASN_32_BIT_MAX_VALUE);

    private static final Pattern ASN_PATTERN = Pattern.compile("(?:AS)?(\\d+)(\\.(\\d+))?", Pattern.CASE_INSENSITIVE);

    public Asn(Long value) {
        super(value);
        validateRange(value, ASN_32_BIT_MAX_VALUE);
    }

    public static Asn of(Long value) {
        return new Asn(value);
    }

    public static Asn of(String value) {
        return parse(value);
    }

    public static Asn parse(String text) {
        if (text == null) {
            return null;
        }

        text = text.trim();
        Matcher matcher = ASN_PATTERN.matcher(text);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format("Invalid ASN: '%s'", text));
        }

        long low;
        long high = 0L;
        if (matcher.group(3) != null) {
            low = validateRange(Long.valueOf(matcher.group(3)), ASN_16_BIT_MAX_VALUE);
            high = validateRange(Long.valueOf(matcher.group(1)), ASN_16_BIT_MAX_VALUE);
        } else {
            low = Long.valueOf(matcher.group(1));
        }

        return new Asn((high << 16) | low);
    }

    private static Long validateRange(Long value, Long max) {
        Validate.isTrue(value.compareTo(ASN_MIN_VALUE) >= 0, "Value of ASN has to be greater than or equal to " + ASN_MIN_VALUE);
        Validate.isTrue(value.compareTo(max) <= 0, "Value of ASN has to be less than or equal to " + max);
        return value;
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
        return String.format("AS%d", value());
    }

    @Override
    public AsnRange asRange() {
        return AsnRange.from(this).to(this);
    }
}
