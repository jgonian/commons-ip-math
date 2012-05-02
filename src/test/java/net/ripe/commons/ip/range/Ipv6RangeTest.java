package net.ripe.commons.ip.range;

import static java.math.BigInteger.*;
import static junit.framework.Assert.*;
import static net.ripe.commons.ip.resource.Ipv6.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.ripe.commons.ip.resource.Ipv6;
import org.junit.Ignore;
import org.junit.Test;

public class Ipv6RangeTest extends AbstractRangeTest<Ipv6, Ipv6Range> {

    Ipv6 ip1 = Ipv6.of(new BigInteger("1"));   // TODO(ygoniana): use parse when is implemented
    Ipv6 ip2 = Ipv6.of(new BigInteger("2"));
    Ipv6 ip3 = Ipv6.of(new BigInteger("3"));

    @Override
    protected Ipv6 from(String s) {
        return Ipv6.of(BigInteger.valueOf(Long.parseLong(s)));
    }

    @Override
    protected Ipv6 to(String s) {
        return Ipv6.of(BigInteger.valueOf(Long.parseLong(s)));
    }

    @Override
    protected Ipv6 item(String s) {
        return Ipv6.of(BigInteger.valueOf(Long.parseLong(s)));
    }

    @Override
    protected Ipv6Range getTestRange(Ipv6 start, Ipv6 end) {
        return new Ipv6Range(start, end);
    }

    @Test
    @Ignore("TODO(ygoniana): implement toString in IPv6")
    @Override
    public void testToString() {
        Ipv6Range range = new Ipv6Range(ip1, ip3);
        assertEquals("[::1/128..::3/128]", range.toString());
    }

    @Test
    public void testSize() {
        assertEquals(ONE, ip1.asRange().size());
        assertEquals(IPv6_MAXIMUM_VALUE.add(ONE), Ipv6Range.from(FIRST_IPV6_ADDRESS).to(LAST_IPV6_ADDRESS).size());
    }

    @Override
    public void testIterator() {
        List<Ipv6> result = new ArrayList<Ipv6>();
        for (Ipv6 ipv6 : new Ipv6Range(ip1, ip3)) {
            result.add(ipv6);
        }
        assertEquals(Arrays.asList(ip1, ip2, ip3), result);
    }

    @Test
    public void testBuilder() {
        Ipv6Range range = Ipv6Range.from(ip1).to(ip3);
        assertEquals(ip1, range.start());
        assertEquals(ip3, range.end());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderWithInvalidRange() {
        Ipv6Range.from(ip3).to(ip1);
    }

    @Test(expected = NullPointerException.class)
    public void testBuilderWithNullStart() {
        Ipv6Range.from((Ipv6) null).to(ip3);
    }

    @Test(expected = NullPointerException.class)
    public void testBuilderWithNullEnd() {
        Ipv6Range.from(ip1).to((Ipv6) null);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullStart() {
        new Ipv6Range(null, ip3);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullEnd() {
        new Ipv6Range(ip1, null);
    }
}
