package net.ripe.commons.ip.range;

import net.ripe.commons.ip.resource.Ipv4Address;

public class Ipv4Range extends Range<Ipv4Address> {

    public Ipv4Range(Ipv4Address start, Ipv4Address end) {
        super(start, end);
    }

    public Ipv4Range intersection(Ipv4Range other) {
        return super.intersection(other).as(Ipv4Range.class);
    }
}
