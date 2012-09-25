package net.ripe.commons.ip;

public class AsnRange extends AbstractRange<Asn, AsnRange> implements InternetResourceRange<Asn, AsnRange>, Comparable<AsnRange> {

    protected AsnRange(Asn start, Asn end) {
        super(start, end);
    }

    @Override
    protected AsnRange newInstance(Asn start, Asn end) {
        return new AsnRange(start, end);
    }

    public static AsnRangeBuilder from(Long from) {
        return from(Asn.of(from));
    }

    public static AsnRangeBuilder from(Asn from) {
        return new AsnRangeBuilder(from);
    }

    public static AsnRangeBuilder from(String from) {
        return new AsnRangeBuilder(Asn.parse(from));
    }

    public static AsnRange parse(String text) {
        int idx = text.indexOf('-');
        Validate.isTrue(idx != -1, "Invalid range of ASNs: '" + text + "'");
        Asn start = Asn.parse(text.substring(0, idx));
        Asn end = Asn.parse(text.substring(idx + 1));
        return new AsnRange(start, end);
    }

    public boolean containsOnly16BitAsns() {
        return end().is16Bit();
    }

    public boolean containsOnly32BitAsns() {
        return start().is32Bit();
    }

    @Override
    public String toString() {
        return isEmpty() ? start().toString() : start() + "-" + end();
    }

    @Override
    public Long size() {
        return (end().value() - start().value()) + 1;
    }

    @Override
    public int compareTo(AsnRange o) {
        if (start().compareTo(o.start()) > 0) {
            return 2;
        } else if (start().compareTo(o.start()) < 0) {
            return -2;
        } else if (end().compareTo(o.end()) < 0) {
            return 1;
        } else if (end().compareTo(o.end()) > 0) {
            return -1;
        }
        return 0;
    }

    public static class AsnRangeBuilder extends AbstractRangeBuilder<Asn, AsnRange> {

        private final Asn from;

        protected AsnRangeBuilder(Asn from) {
            this.from = from;
        }

        public AsnRange to(Long end) {
            return to(Asn.of(end));
        }

        public AsnRange to(String end) {
            return to(Asn.parse(end));
        }

        @Override
        public AsnRange to(Asn to) {
            return new AsnRange(from, to);
        }
    }
}
