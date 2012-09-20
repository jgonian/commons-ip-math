package net.ripe.commons.ip;

import static net.ripe.commons.ip.PrefixUtils.*;
import static org.junit.Assert.*;
import java.math.BigInteger;
import org.junit.Test;

public class PrefixUtilsTest {

    @Test
    public void shouldReturnTrueForValidPrefix() {
        assertTrue(PrefixUtils.isValidPrefix(Ipv4Range.parse("0.0.0.0/0")));
        assertTrue(PrefixUtils.isValidPrefix(Ipv6Range.parse("::/0")));
    }

    @Test
    public void shouldReturnFalseForInvalidPrefix() {
        assertFalse(PrefixUtils.isValidPrefix(Ipv4Range.parse("0.0.0.0-0.0.0.2")));
        assertFalse(PrefixUtils.isValidPrefix(Ipv4Range.from(1585324288l).to(1585324799l)));
        assertFalse(PrefixUtils.isValidPrefix(Ipv4Range.parse("0.0.0.1-0.0.0.3")));
        assertFalse(PrefixUtils.isValidPrefix(Ipv4Range.parse("0.0.0.1-255.255.255.255")));
        assertFalse(PrefixUtils.isValidPrefix(Ipv4Range.parse("0.0.0.0-255.255.255.254")));
        assertFalse(PrefixUtils.isValidPrefix(Ipv4Range.parse("0.0.0.1-255.255.255.254")));
        assertFalse(PrefixUtils.isValidPrefix(Ipv4Range.parse("0.0.0.2-255.255.255.254")));

        assertFalse(PrefixUtils.isValidPrefix(Ipv6Range.parse("::0-::2")));
        assertFalse(PrefixUtils.isValidPrefix(Ipv6Range.parse("::1-::3")));
        assertFalse(PrefixUtils.isValidPrefix(Ipv6Range.parse("::1-ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff")));
        assertFalse(PrefixUtils.isValidPrefix(Ipv6Range.parse("::-ffff:ffff:ffff:ffff:ffff:ffff:ffff:fffe")));
        assertFalse(PrefixUtils.isValidPrefix(Ipv6Range.parse("::2-ffff:ffff:ffff:ffff:ffff:ffff:ffff:fffe")));
    }

    @Test
    public void shouldGetPrefixLengthWhenCorrectPrefix() {
        assertEquals(32, PrefixUtils.getPrefixLength(Ipv4Range.parse("0.0.0.0-0.0.0.0")));
        assertEquals(32, PrefixUtils.getPrefixLength(Ipv4Range.parse("0.0.0.1-0.0.0.1")));
        assertEquals(30, PrefixUtils.getPrefixLength(Ipv4Range.parse("0.0.0.0-0.0.0.3")));

        assertEquals(128, PrefixUtils.getPrefixLength(Ipv6Range.parse("::0-::0")));
        assertEquals(128, PrefixUtils.getPrefixLength(Ipv6Range.parse("::1-::1")));
        assertEquals(126, PrefixUtils.getPrefixLength(Ipv6Range.parse("::0-::3")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToGetPrefixLengthWhenInvalidPrefix() {
        PrefixUtils.getPrefixLength(Ipv4Range.parse("0.0.0.0-0.0.0.2"));
        PrefixUtils.getPrefixLength(Ipv6Range.parse("::0-::2"));
    }

    @Test
    public void shouldFindBiggestAndSmallestPrefixWhenRangeIsSingleValidPrefixV4() {
        Ipv6Range range = Ipv6Range.parse("::/0");
        assertEquals(Ipv6Range.parse("::/0"), findMinimumPrefixForPrefixLength(range, 0).get());
        assertEquals(Ipv6Range.parse("::/0"), findMaximumPrefixForPrefixLength(range, 128).get());
    }

    @Test
    public void shouldFindBiggestAndSmallestPrefixWhenRangeIsNotValidPrefixV4() {
        Ipv6Range range = Ipv6Range.from("::1").to("::4");
        assertEquals(Ipv6Range.parse("::2/127"), findMaximumPrefixForPrefixLength(range, 127).get());
        assertEquals(Ipv6Range.parse("::2/127"), findMinimumPrefixForPrefixLength(range, 127).get());
        assertEquals(Ipv6Range.parse("::2/127"), findMaximumPrefixForPrefixLength(range, 128).get());
        assertEquals(Ipv6Range.parse("::1/128"), findMinimumPrefixForPrefixLength(range, 128).get());

        Ipv6Range otherRange = Ipv6Range.from("::").to(BigInteger.valueOf(2).pow(128).subtract(BigInteger.valueOf(2)));
        assertEquals(Ipv6Range.parse("::/1"), findMaximumPrefixForPrefixLength(otherRange, 1).get());
        assertEquals(Ipv6Range.parse("::/1"), findMinimumPrefixForPrefixLength(otherRange, 1).get());
        assertEquals(Ipv6Range.parse("::/1"), findMaximumPrefixForPrefixLength(otherRange, 2).get());
        assertEquals(Ipv6Range.parse("8000::/2"), findMinimumPrefixForPrefixLength(otherRange, 2).get());
        assertEquals(Ipv6Range.parse("::/1"), findMaximumPrefixForPrefixLength(otherRange, 128).get());
        assertEquals(Ipv6Range.parse("ffff:ffff:ffff:ffff:ffff:ffff:ffff:fffe/128"), findMinimumPrefixForPrefixLength(otherRange, 128).get());
    }

    @Test
    public void shouldNotFindBiggestAndSmallestPrefixWhenDoesNotExistV4() {
        Ipv6Range range = Ipv6Range.from("::").to(BigInteger.valueOf(2).pow(128).subtract(BigInteger.valueOf(2)));
        assertFalse(findMinimumPrefixForPrefixLength(range, 0).isPresent());
        assertFalse(findMaximumPrefixForPrefixLength(range, 0).isPresent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void findSmallestPrefixShouldThrowAnExceptionWhenRequestedPrefixLengthIsTooSmallV4() {
        findMinimumPrefixForPrefixLength(Ipv6Range.parse("::1-::10"), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findSmallestPrefixShouldThrowAnExceptionWhenRequestedPrefixLengthIsTooBigV4() {
        findMinimumPrefixForPrefixLength(Ipv6Range.parse("::1-::10"), 129);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findBiggestPrefixShouldThrowAnExceptionWhenRequestedPrefixLengthIsTooSmallV4() {
        findMaximumPrefixForPrefixLength(Ipv6Range.parse("::1-::10"), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findBiggestPrefixShouldThrowAnExceptionWhenRequestedPrefixLengthIsTooBigV4() {
        findMaximumPrefixForPrefixLength(Ipv6Range.parse("::1-::10"), 129);
    }

    // IPv6

    @Test
    public void shouldFindBiggestAndSmallestPrefixWhenRangeIsSingleValidPrefixV6() {
        Ipv4Range range = Ipv4Range.parse("0.0.0.0/0");
        assertEquals(Ipv4Range.parse("0.0.0.0/0"), findMinimumPrefixForPrefixLength(range, 0).get());
        assertEquals(Ipv4Range.parse("0.0.0.0/0"), findMaximumPrefixForPrefixLength(range, 32).get());
    }

    @Test
    public void shouldFindBiggestAndSmallestPrefixWhenRangeIsNotValidPrefixV6() {
        Ipv4Range range = Ipv4Range.from("0.0.0.1").to("0.0.0.4");
        assertEquals(Ipv4Range.parse("0.0.0.2/31"), findMaximumPrefixForPrefixLength(range, 31).get());
        assertEquals(Ipv4Range.parse("0.0.0.2/31"), findMinimumPrefixForPrefixLength(range, 31).get());
        assertEquals(Ipv4Range.parse("0.0.0.2/31"), findMaximumPrefixForPrefixLength(range, 32).get());
        assertEquals(Ipv4Range.parse("0.0.0.1/32"), findMinimumPrefixForPrefixLength(range, 32).get());

        Ipv4Range otherRange = Ipv4Range.from("0.0.0.0").to((long) Math.pow(2, 32) - 2L);
        assertEquals(Ipv4Range.parse("0.0.0.0/1"), findMaximumPrefixForPrefixLength(otherRange, 1).get());
        assertEquals(Ipv4Range.parse("0.0.0.0/1"), findMinimumPrefixForPrefixLength(otherRange, 1).get());
        assertEquals(Ipv4Range.parse("0.0.0.0/1"), findMaximumPrefixForPrefixLength(otherRange, 2).get());
        assertEquals(Ipv4Range.parse("128.0.0.0/2"), findMinimumPrefixForPrefixLength(otherRange, 2).get());
        assertEquals(Ipv4Range.parse("0.0.0.0/1"), findMaximumPrefixForPrefixLength(otherRange, 32).get());
        assertEquals(Ipv4Range.parse("255.255.255.254/32"), findMinimumPrefixForPrefixLength(otherRange, 32).get());
    }

    @Test
    public void shouldNotFindBiggestAndSmallestPrefixWhenDoesNotExistV6() {
        Ipv4Range range = Ipv4Range.from("0.0.0.0").to((long) Math.pow(2, 32) - 2L);
        assertFalse(findMinimumPrefixForPrefixLength(range, 0).isPresent());
        assertFalse(findMaximumPrefixForPrefixLength(range, 0).isPresent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void findSmallestPrefixShouldThrowAnExceptionWhenRequestedPrefixLengthIsTooSmallV6() {
        findMinimumPrefixForPrefixLength(Ipv4Range.parse("0.0.0.1-0.0.0.10"), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findSmallestPrefixShouldThrowAnExceptionWhenRequestedPrefixLengthIsTooBigV6() {
        findMinimumPrefixForPrefixLength(Ipv4Range.parse("0.0.0.1-0.0.0.10"), 129);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findBiggestPrefixShouldThrowAnExceptionWhenRequestedPrefixLengthIsTooSmallV6() {
        findMaximumPrefixForPrefixLength(Ipv4Range.parse("0.0.0.1-0.0.0.10"), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findBiggestPrefixShouldThrowAnExceptionWhenRequestedPrefixLengthIsTooBigV6() {
        findMaximumPrefixForPrefixLength(Ipv4Range.parse("0.0.0.1-0.0.0.10"), 129);
    }
}
