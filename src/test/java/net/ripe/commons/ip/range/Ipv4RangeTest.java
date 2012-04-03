package net.ripe.commons.ip.range;

import static junit.framework.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.ripe.commons.ip.resource.Ipv4;
import org.junit.Test;

public class Ipv4RangeTest extends AbstractRangeTest<Ipv4, Ipv4Range>{

    Ipv4 ip1 = Ipv4.of("1.0.0.1");
    Ipv4 ip2 = Ipv4.of("1.0.0.2");
    Ipv4 ip3 = Ipv4.of("1.0.0.3");

    @Override
    protected Ipv4 from(String s) {
        return Ipv4.of(Long.parseLong(s));
    }

    @Override
    protected Ipv4 to(String s) {
        return Ipv4.of(Long.parseLong(s));
    }

    @Override
    protected Ipv4 item(String s) {
        return Ipv4.of(Long.parseLong(s));
    }

    @Override
    protected Ipv4Range getTestRange(Ipv4 start, Ipv4 end) {
        return new Ipv4Range(start, end);
    }

    @Override
    public void testToString() {
        Ipv4Range range = new Ipv4Range(ip1, ip3);
        assertEquals("[1.0.0.1..1.0.0.3]", range.toString());
    }

    @Override
    public void testIterator() {
        List<Ipv4> result = new ArrayList<Ipv4>();
        for (Ipv4 ipv4 : new Ipv4Range(ip1, ip3)) {
            result.add(ipv4);
        }
        assertEquals(Arrays.asList(ip1, ip2, ip3), result);
    }

    @Test
    public void testBuilder() {
        Ipv4Range range = Ipv4Range.from(ip1).to(ip3);
        assertEquals(ip1, range.start());
        assertEquals(ip3, range.end());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderWithInvalidRange() {
        Ipv4Range.from(ip3).to(ip1);
    }

    @Test(expected = NullPointerException.class)
    public void testBuilderWithNullStart() {
        Ipv4Range.from((Ipv4)null).to(ip3);
    }

    @Test(expected = NullPointerException.class)
    public void testBuilderWithNullEnd() {
        Ipv4Range.from(ip1).to((Ipv4)null);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullStart() {
        new Ipv4Range(null, ip3);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullEnd() {
        new Ipv4Range(ip1, null);
    }
}
