package net.ripe.commons.ip.range;

import static junit.framework.Assert.*;
import static net.ripe.commons.ip.resource.Ipv4.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.ripe.commons.ip.resource.Ipv4;
import org.junit.Test;

public class Ipv4RangeTest extends AbstractRangeTest<Ipv4, Ipv4Range> {

    Ipv4 ip1 = Ipv4.of("0.0.0.1");
    Ipv4 ip2 = Ipv4.of("0.0.0.2");
    Ipv4 ip3 = Ipv4.of("0.0.0.3");

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
        assertEquals("[0.0.0.1..0.0.0.3]", range.toString());
    }

    @Test
    public void testSize() {
        assertEquals(new Long(1), ip1.asRange().size());
        assertEquals(new Long(IPv4_MAXIMUM_VALUE + 1), Ipv4Range.from(FIRST_IPV4_ADDRESS).to(LAST_IPV4_ADDRESS).size());
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
    public void testBuilderWithLongs() {
        Ipv4Range range = Ipv4Range.from(1l).to(3l);
        assertEquals(ip1, range.start());
        assertEquals(ip3, range.end());
    }

    @Test
    public void testBuilderWithStrings() {
        Ipv4Range range = Ipv4Range.from("0.0.0.1").to("0.0.0.3");
        assertEquals(ip1, range.start());
        assertEquals(ip3, range.end());
    }

    @Test
    public void testBuilderWithIpv4s() {
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
