package net.ripe.commons.ip;

import static net.ripe.commons.ip.Ipv6PrefixUtils.*;
import static org.junit.Assert.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class Ipv6PrefixUtilsTest {

    @Test
    public void shouldFindBiggestAndSmallestPrefixWhenRangeIsSingleValidPrefix() {
        Ipv6Range range = Ipv6Range.parse("::/0");
        assertEquals(Ipv6Range.parse("::/0"), findMinimumPrefixForPrefixLength(range, 0).get());
        assertEquals(Ipv6Range.parse("::/0"), findMaximumPrefixForPrefixLength(range, 128).get());
    }

    @Test
    public void shouldFindBiggestAndSmallestPrefixWhenRangeIsNotValidPrefix() {
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
    public void shouldNotFindBiggestAndSmallestPrefixWhenDoesNotExist() {
        Ipv6Range range = Ipv6Range.from("::").to(BigInteger.valueOf(2).pow(128).subtract(BigInteger.valueOf(2)));
        assertFalse(findMinimumPrefixForPrefixLength(range, 0).isPresent());
        assertFalse(findMaximumPrefixForPrefixLength(range, 0).isPresent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void findSmallestPrefixShouldThrowAnExceptionWhenRequestedPrefixLengthIsTooSmall() {
        findMinimumPrefixForPrefixLength(Ipv6Range.parse("::1-::10"), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findSmallestPrefixShouldThrowAnExceptionWhenRequestedPrefixLengthIsTooBig() {
        findMinimumPrefixForPrefixLength(Ipv6Range.parse("::1-::10"), 129);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findBiggestPrefixShouldThrowAnExceptionWhenRequestedPrefixLengthIsTooSmall() {
        findMaximumPrefixForPrefixLength(Ipv6Range.parse("::1-::10"), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findBiggestPrefixShouldThrowAnExceptionWhenRequestedPrefixLengthIsTooBig() {
        findMaximumPrefixForPrefixLength(Ipv6Range.parse("::1-::10"), 129);
    }

    @Test
    public void shouldReturnTrueForValidPrefix() {
        assertTrue(Ipv6PrefixUtils.isValidPrefix(Ipv6Range.parse("::/0")));
    }

    @Test
    public void shouldReturnFalseForInvalidPrefix() {
       assertFalse(Ipv6PrefixUtils.isValidPrefix(Ipv6Range.parse("::0-::2")));
       assertFalse(Ipv6PrefixUtils.isValidPrefix(Ipv6Range.parse("::1-::3")));
       assertFalse(Ipv6PrefixUtils.isValidPrefix(Ipv6Range.parse("::1-ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff")));
       assertFalse(Ipv6PrefixUtils.isValidPrefix(Ipv6Range.parse("::-ffff:ffff:ffff:ffff:ffff:ffff:ffff:fffe")));
       assertFalse(Ipv6PrefixUtils.isValidPrefix(Ipv6Range.parse("::2-ffff:ffff:ffff:ffff:ffff:ffff:ffff:fffe")));
    }
    
    @Test
    public void shouldGetPrefixLengthWhenCorrectPrefix() {
        assertEquals(128, Ipv6PrefixUtils.getPrefixLength(Ipv6Range.parse("::0-::0")));
        assertEquals(128, Ipv6PrefixUtils.getPrefixLength(Ipv6Range.parse("::1-::1")));
        assertEquals(126, Ipv6PrefixUtils.getPrefixLength(Ipv6Range.parse("::0-::3")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToGetPrefixLengthWhenInvalidPrefix() {
        Ipv6PrefixUtils.getPrefixLength(Ipv6Range.parse("::0-::2"));
    }

    @Test
    public void shouldSplitIntoPrefixes() {
        validateSplitIntoPrefixes(new String[]{"::/128"}, "::0-::0");
        validateSplitIntoPrefixes(new String[]{"::/127"}, "::0-::1");
        validateSplitIntoPrefixes(new String[]{"::/126"}, "::0-::3");
        validateSplitIntoPrefixes(new String[]{"::/125"}, "::0-::7");
        validateSplitIntoPrefixes(new String[]{"::/127", "::2/128"}, "::0-::2");
        validateSplitIntoPrefixes(new String[]{"::/126", "::4/128"}, "::0-::4");
        validateSplitIntoPrefixes(new String[]{"::/126", "::4/127"}, "::0-::5");
        validateSplitIntoPrefixes(new String[]{"::/126", "::4/127", "::6/128"}, "::0-::6");
        validateSplitIntoPrefixes(new String[]{"::1/128"}, "::1-::1");
        validateSplitIntoPrefixes(new String[]{"::1/128", "::2/128"}, "::1-::2");
        validateSplitIntoPrefixes(new String[]{"::1/128", "::2/127"}, "::1-::3");
        validateSplitIntoPrefixes(new String[]{"::2/127"}, "::2-::3");
        validateSplitIntoPrefixes(new String[]{"::2/127", "::4/128"}, "::2-::4");
    }

    private void validateSplitIntoPrefixes(String[] expectedPrefixes, String rangeToSplit) {
        List<Ipv6Range> expected = new ArrayList<Ipv6Range>();
        for (String prefix : expectedPrefixes) {
            expected.add(Ipv6Range.parse(prefix));
        }
        assertEquals(expected, splitIntoPrefixes(Ipv6Range.parse(rangeToSplit)));
    }

    @Test
    public void shouldSplitIntoPrefixesAllIpv6SpaceExceptFirstAddress() {
        Ipv6Range range = Ipv6Range.from("::1").to(Ipv6.LAST_IPV6_ADDRESS);
        assertEquals(128, splitIntoPrefixes(range).size());
    }
}
