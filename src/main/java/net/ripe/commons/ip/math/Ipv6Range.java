package net.ripe.commons.ip.math;


import java.math.BigInteger;

@Deprecated
public class Ipv6Range extends IpRange<Ipv6Range> {

    private static final long serialVersionUID = 1L;

    public Ipv6Range(BigInteger start, BigInteger end) {
        super(start, end);
    }

    @Override
    protected Ipv6Range createNew(BigInteger start, BigInteger end) {
        return new Ipv6Range(start, end);
    }

    @Override
    protected Ipv6Range getThis() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Ipv6Range) ? super.equals(obj) : false;
    }

    protected int bits() {
        return 128;
    }

}
