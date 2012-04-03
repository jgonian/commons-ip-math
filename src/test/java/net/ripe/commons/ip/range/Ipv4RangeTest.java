package net.ripe.commons.ip.range;

import static junit.framework.Assert.*;
import net.ripe.commons.ip.resource.Ipv4Address;
import org.junit.Test;

public class Ipv4RangeTest extends AbstractRangeTest<Ipv4Address, Ipv4Range>{

    Ipv4Address ip1 = Ipv4Address.valueOf("1.0.0.1");
    Ipv4Address ip3 = Ipv4Address.valueOf("1.0.0.3");

    @Override
    protected Ipv4Address from(String s) {
        return Ipv4Address.valueOf(Long.parseLong(s));
    }

    @Override
    protected Ipv4Address to(String s) {
        return Ipv4Address.valueOf(Long.parseLong(s));
    }

    @Override
    protected Ipv4Range getTestRange(Ipv4Address start, Ipv4Address end) {
        return new Ipv4Range(start, end);
    }

    @Test
    public void testBuilder() {
        Ipv4Range range = Ipv4Range.from(ip1).to(ip3);
        assertEquals(ip1, range.start());
        assertEquals(ip3, range.end());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderWithInvalidRange() {
        Range.from(ip3).to(ip1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderWithNullStart() {
        Range.from((Ipv4Address)null).to(ip3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderWithNullEnd() {
        Range.from(ip1).to((Ipv4Address)null);
    }
}
