package net.ripe.commons.ip.range;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.ripe.commons.ip.resource.Ipv6;
import org.junit.Test;

import static java.math.BigInteger.*;
import static junit.framework.Assert.*;
import static net.ripe.commons.ip.resource.Ipv6.*;

public class Ipv6RangeTest extends AbstractRangeTest<Ipv6, Ipv6Range> {

    Ipv6 ip1 = Ipv6.of("::1");
    Ipv6 ip2 = Ipv6.of("::2");
    Ipv6 ip3 = Ipv6.of("::3");

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

    @Override
    protected Ipv6Range getFullRange() {
        return new Ipv6Range(FIRST_IPV6_ADDRESS, LAST_IPV6_ADDRESS);
    }

    @Test
    public void shouldParseDashNotation() {
        assertEquals(Ipv6Range.from(FIRST_IPV6_ADDRESS).to(LAST_IPV6_ADDRESS), Ipv6Range.parse("::-ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"));
    }

    @Test
    public void shouldParseDashNotationWhenEmptyRange() {
        assertEquals(Ipv6.of("::1").asRange(), Ipv6Range.parse("::1-::1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToParseDashNotationWhenIllegalRange() {
        Ipv6Range.parse("::10-::1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToParseDashNotationWhenSingleResource() {
        Ipv6Range.parse("::1");
    }

    @Test
    public void shouldParseCidrNotation() {
        assertEquals(Ipv6Range.from(FIRST_IPV6_ADDRESS).to(LAST_IPV6_ADDRESS), Ipv6Range.parseCidr("::/0"));
    }

    @Test
    public void shouldParseCidrWhenEmptyRange() {
        assertEquals(Ipv6.parse("ffce:abcd::").asRange(), Ipv6Range.parseCidr("ffce:abcd::/128"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToParseCidrWhenIllegalPrefix() {
        Ipv6Range.parseCidr("ffce:abcd::/129");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToParseCidrWhenSingleResource() {
        Ipv6Range.parseCidr("ffce:abcd::");
    }

    @Test
    public void shouldParseWithPrefix() {
        assertEquals(Ipv6Range.from(FIRST_IPV6_ADDRESS).to(LAST_IPV6_ADDRESS), Ipv6Range.parseWithPrefix("::", "0"));
    }

    @Test
    public void shouldParseWithPrefixWhenEmptyRange() {
        assertEquals(Ipv6.parse("ffce:abcd::").asRange(), Ipv6Range.parseWithPrefix("ffce:abcd::", "128"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToParseWithPrefixWhenIllegalPrefix() {
        Ipv6Range.parseWithPrefix("ffce:abcd::", "129");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToParseWithPrefixWhenPrefixIsNull() {
        Ipv6Range.parseWithPrefix("0.0.0.1", null);
    }

    @Test
    @Override
    public void testToString() {
        assertEquals("::1-::3", new Ipv6Range(ip1, ip3).toString());
        assertEquals("::/0", new Ipv6Range(FIRST_IPV6_ADDRESS, LAST_IPV6_ADDRESS).toString());
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
    public void testBuilderWithBigIntegers() {
        Ipv6Range range = Ipv6Range.from(BigInteger.valueOf(1l)).to(BigInteger.valueOf(3l));
        assertEquals(ip1, range.start());
        assertEquals(ip3, range.end());
    }

    @Test
    public void testBuilderWithStrings() {
        Ipv6Range range = Ipv6Range.from("::1").to("::3");
        assertEquals(ip1, range.start());
        assertEquals(ip3, range.end());
    }

    @Test
    public void testBuilderWithIpv6s() {
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

    @Test
    public void testCidrBuilderWithValidPrefixAndLength() {
        Ipv6Range range = Ipv6Range.withPrefix(Ipv6.of("::2")).andLength(127);
        assertEquals(Ipv6.of("::2"), range.start());
        assertEquals(Ipv6.of("::3"), range.end());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCidrBuilderWithInvalidPrefixAndLength() {
        Ipv6Range.withPrefix(Ipv6.of("::3")).andLength(127);
    }

    @Test(expected = NullPointerException.class)
    public void testCidrBuilderWithNullStart() {
        Ipv6Range.withPrefix(null).andLength(127);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCidrWithTooSmallPrefixLength() {
        Ipv6Range.withPrefix(Ipv6.of("::2")).andLength(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCidrWithTooBigPrefixLength() {
        Ipv6Range.withPrefix(Ipv6.of("::2")).andLength(129);
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
