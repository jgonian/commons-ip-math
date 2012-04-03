package net.ripe.commons.ip.resource;

import org.apache.commons.lang.Validate;

public class Asn extends SingleValue<Long> implements InternetResource<Asn> {

    private static final long serialVersionUID = -1L;

    private static long ASN_MIN_VALUE = 0L;
    private static long ASN_16_BIT_MAX_VALUE = (1L << 16) - 1L;
    private static long ASN_32_BIT_MAX_VALUE = (1L << 32) - 1L;

    public Asn(Long value) {
        super(value);
        Validate.isTrue(value.compareTo(ASN_MIN_VALUE) >= 0, "Value of ASN has to be greater than or equal to " + ASN_MIN_VALUE);
        Validate.isTrue(value.compareTo(ASN_32_BIT_MAX_VALUE) <= 0, "Value of ASN has to be less than or equal to " + ASN_32_BIT_MAX_VALUE);
    }

    public static Asn of(Long value) {
        return new Asn(value);
    }

    public static Asn of(String value) {
        return new Asn(Long.parseLong(value));
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
}
