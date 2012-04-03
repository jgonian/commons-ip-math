package net.ripe.commons.ip.range;

import net.ripe.commons.ip.resource.Ipv4Address;

public class Ipv4Range extends AbstractRange<Ipv4Address, Ipv4Range> {

    protected Ipv4Range(Ipv4Address start, Ipv4Address end) {
        super(start, end);
    }

    @Override
    protected Ipv4Range newInstance(Ipv4Address start, Ipv4Address end) {
        return new Ipv4Range(start, end);
    }

    public static Ipv4RangeBuilder from(Ipv4Address from) {
        return new Ipv4RangeBuilder(from);
    }

    public static class Ipv4RangeBuilder extends AbstractRangeBuilder<Ipv4Address, Ipv4Range> {
        protected Ipv4RangeBuilder(Ipv4Address from) {
            super(from, Ipv4Range.class);
        }
    }
}
