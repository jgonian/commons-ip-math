package net.ripe.commons.ip;

import static junit.framework.Assert.assertEquals;
import static net.ripe.commons.ip.Ipv4.FIRST_IPV4_ADDRESS;
import static net.ripe.commons.ip.Ipv4.LAST_IPV4_ADDRESS;
import static net.ripe.commons.ip.Ipv4.MAXIMUM_VALUE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    protected Ipv4Range getFullRange() {
        return new Ipv4Range(FIRST_IPV4_ADDRESS, LAST_IPV4_ADDRESS);
    }

    @Override
    public void shouldCalculateRangeLength() {
        assertEquals(new Length<Long>(1L), FIRST_IPV4_ADDRESS.asRange().length());
        assertEquals(new Length<Long>(4294967296L), Ipv4Range.from(FIRST_IPV4_ADDRESS).to(LAST_IPV4_ADDRESS).length());
    }

    @Test
    public void shouldParseDashNotation() {
        assertEquals(Ipv4Range.from(FIRST_IPV4_ADDRESS).to(LAST_IPV4_ADDRESS), Ipv4Range.parse("0.0.0.0-255.255.255.255"));
    }

    @Test
    public void shouldParseDashNotationWhenEmptyRange() {
        assertEquals(Ipv4.parse("192.168.0.1").asRange(), Ipv4Range.parse("192.168.0.1-192.168.0.1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToParseDashNotationWhenIllegalRange() {
        Ipv4Range.parse("0.0.0.10-0.0.0.1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToParseDashNotationWhenSingleResource() {
        Ipv4Range.parse("0.0.0.1");
    }

    @Test
    public void shouldParseCidrNotation() {
        assertEquals(Ipv4Range.from(FIRST_IPV4_ADDRESS).to(LAST_IPV4_ADDRESS), Ipv4Range.parseCidr("0.0.0.0/0"));
    }

    @Test
    public void shouldParseCidrWhenEmptyRange() {
        assertEquals(Ipv4.parse("192.168.0.1").asRange(), Ipv4Range.parseCidr("192.168.0.1/32"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToParseCidrWhenIllegalPrefix() {
        Ipv4Range.parseCidr("0.0.0.10/33");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToParseCidrWhenSingleResource() {
        Ipv4Range.parseCidr("0.0.0.1");
    }

    @Test
    public void shouldParseWithPrefix() {
        assertEquals(Ipv4Range.from(FIRST_IPV4_ADDRESS).to(LAST_IPV4_ADDRESS), Ipv4Range.parseWithPrefix("0.0.0.0", "0"));
    }

    @Test
    public void shouldParseWithPrefixWhenEmptyRange() {
        assertEquals(Ipv4.parse("192.168.0.1").asRange(), Ipv4Range.parseWithPrefix("192.168.0.1", "32"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToParseWithPrefixWhenIllegalPrefix() {
        Ipv4Range.parseWithPrefix("0.0.0.10", "33");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToParseWithPrefixWhenPrefixIsNull() {
        Ipv4Range.parseWithPrefix("0.0.0.1", null);
    }

    @Test
    public void shouldParseDecimalNotation() {
        assertEquals(Ipv4Range.from(FIRST_IPV4_ADDRESS).to(LAST_IPV4_ADDRESS), Ipv4Range.parseDecimalNotation(FIRST_IPV4_ADDRESS.value() + "-" + LAST_IPV4_ADDRESS.value()));
    }

    @Test
    public void shouldParseDecimalWhenEmptyRange() {
        assertEquals(Ipv4.parse("0.0.0.1").asRange(), Ipv4Range.parseDecimalNotation("1-1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToParseDecimalNotationWhenSingleResource() {
        Ipv4Range.parseDecimalNotation("1");
    }

    @Override
    public void testToString() {
        assertEquals("192.168.0.0-192.168.255.254", Ipv4Range.parse("192.168.0.0-192.168.255.254").toString());
        assertEquals("192.168.0.0/16", Ipv4Range.parse("192.168.0.0-192.168.255.255").toString());
    }

    @Test
    public void testToStringInRangeNotation() {
        assertEquals("0.0.0.0-255.255.255.255", new Ipv4Range(FIRST_IPV4_ADDRESS, LAST_IPV4_ADDRESS).toStringInRangeNotation());
    }

    @Test
    public void testToStringInCidrNotation() {
        assertEquals("0.0.0.0/0", new Ipv4Range(FIRST_IPV4_ADDRESS, LAST_IPV4_ADDRESS).toStringInCidrNotation());
    }

    @Test
    public void testToStringInDecimalNotation() {
        assertEquals("0-4294967295", new Ipv4Range(FIRST_IPV4_ADDRESS, LAST_IPV4_ADDRESS).toStringInDecimalNotation());
    }

    @Test
    public void testToStringInSlashNotation() {
        assertEquals("77.34.132.0/22,/22", Ipv4Range.parse("77.34.132.0 - 77.34.139.255").toStringInSlashNotation());
    }



    @Test
    public void testSize() {
        assertEquals(new Long(1), ip1.asRange().size());
        assertEquals(new Long(MAXIMUM_VALUE + 1), Ipv4Range.from(FIRST_IPV4_ADDRESS).to(LAST_IPV4_ADDRESS).size());
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
        Ipv4Range.from((Ipv4) null).to(ip3);
    }

    @Test(expected = NullPointerException.class)
    public void testBuilderWithNullEnd() {
        Ipv4Range.from(ip1).to((Ipv4) null);
    }

    @Test
    public void testBuilderWithValidAddressAndPrefixLength() {
        Ipv4Range range = Ipv4Range.from("0.0.0.2").andPrefixLength(31);
        assertEquals(Ipv4.of("0.0.0.2"), range.start());
        assertEquals(Ipv4.of("0.0.0.3"), range.end());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderWithInvalidAddressAndLength() {
        Ipv4Range.from("0.0.0.3").andPrefixLength(31);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderWithTooSmallPrefixLength() {
        Ipv4Range.from("0.0.0.2").andPrefixLength(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderWithTooBigPrefixLength() {
        Ipv4Range.from("0.0.0.2").andPrefixLength(33);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullStart() {
        new Ipv4Range(null, ip3);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullEnd() {
        new Ipv4Range(ip1, null);
    }

//    public void shouldContain
}
