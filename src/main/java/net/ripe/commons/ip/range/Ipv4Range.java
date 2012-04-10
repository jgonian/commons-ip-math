package net.ripe.commons.ip.range;

import net.ripe.commons.ip.resource.InternetResourceRange;
import net.ripe.commons.ip.resource.Ipv4;

public class Ipv4Range extends AbstractRange<Ipv4, Ipv4Range> implements InternetResourceRange<Ipv4, Ipv4Range, Long> {

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

    @Override
    public Long size() {
        return (end().value() - start().value()) + 1;
    }

    public static class Ipv4RangeBuilder extends AbstractRangeBuilder<Ipv4, Ipv4Range> {
        protected Ipv4RangeBuilder(Ipv4 from) {
            super(from, Ipv4Range.class);
        }
    }
}
