package net.ripe.commons.ip.range;

import net.ripe.commons.ip.resource.Asn;

public class AsnRange extends Range<Asn> {

    public AsnRange(Asn start, Asn end) {
        super(start, end);
    }
}
