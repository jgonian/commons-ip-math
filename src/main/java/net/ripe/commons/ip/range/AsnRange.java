package net.ripe.commons.ip.range;

import net.ripe.commons.ip.resource.Asn;
import net.ripe.commons.ip.resource.InternetResourceRange;

public class AsnRange extends AbstractRange<Asn, AsnRange> implements InternetResourceRange<Asn, AsnRange, Long> {

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

    @Override
    public String toString() {
        return isEmpty() ? start().toString() : String.format("%s-%s", start(), end());
    }

    @Override
    public Long size() {
        return (end().value() - start().value()) + 1;
    }

    public static class AsnRangeBuilder extends AbstractRangeBuilder<Asn, AsnRange> {
        protected AsnRangeBuilder(Asn from) {
            super(from, AsnRange.class);
        }

        public AsnRange to(Long end) {
            return super.to(Asn.of(end));
        }
    }
}
