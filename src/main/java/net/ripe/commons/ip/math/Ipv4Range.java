package net.ripe.commons.ip.math;

import java.math.BigInteger;

public class Ipv4Range extends IpRange<Ipv4Range> {

    private static final long serialVersionUID = 1L;

    public Ipv4Range(BigInteger start, BigInteger end) {
        super(start, end);
    }

    @Override
    protected Ipv4Range createNew(BigInteger start, BigInteger end) {
        return new Ipv4Range(start, end);
    }

    @Override
    protected Ipv4Range getThis() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
       return (obj instanceof Ipv4Range) ? super.equals(obj) : false;
    }
    
    protected int bits() {
        return 32;
    }


}
