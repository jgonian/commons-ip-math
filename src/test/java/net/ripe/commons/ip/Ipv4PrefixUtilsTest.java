package net.ripe.commons.ip;

import static net.ripe.commons.ip.Ipv4PrefixUtils.*;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class Ipv4PrefixUtilsTest {

    @Test
    public void shouldFindBiggestAndSmallestPrefixWhenRangeIsSingleValidPrefix() {
        Ipv4Range range = Ipv4Range.parse("0.0.0.0/0");
        assertEquals(Ipv4Range.parse("0.0.0.0/0"), findMinimumPrefixForPrefixLength(range, 0).get());
        assertEquals(Ipv4Range.parse("0.0.0.0/0"), findMaximumPrefixForPrefixLength(range, 32).get());
    }

    @Test
    public void shouldFindBiggestAndSmallestPrefixWhenRangeIsNotValidPrefix() {
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
    public void shouldNotFindBiggestAndSmallestPrefixWhenDoesNotExist() {
        Ipv4Range range = Ipv4Range.from("0.0.0.0").to((long) Math.pow(2, 32) - 2L);
        assertFalse(findMinimumPrefixForPrefixLength(range, 0).isPresent());
        assertFalse(findMaximumPrefixForPrefixLength(range, 0).isPresent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void findSmallestPrefixShouldThrowAnExceptionWhenRequestedPrefixLengthIsTooSmall() {
        findMinimumPrefixForPrefixLength(Ipv4Range.parse("0.0.0.1-0.0.0.10"), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findSmallestPrefixShouldThrowAnExceptionWhenRequestedPrefixLengthIsTooBig() {
        findMinimumPrefixForPrefixLength(Ipv4Range.parse("0.0.0.1-0.0.0.10"), 129);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findBiggestPrefixShouldThrowAnExceptionWhenRequestedPrefixLengthIsTooSmall() {
        findMaximumPrefixForPrefixLength(Ipv4Range.parse("0.0.0.1-0.0.0.10"), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findBiggestPrefixShouldThrowAnExceptionWhenRequestedPrefixLengthIsTooBig() {
        findMaximumPrefixForPrefixLength(Ipv4Range.parse("0.0.0.1-0.0.0.10"), 129);
    }

    @Test
    public void shouldReturnTrueForValidPrefix() {
        assertTrue(Ipv4PrefixUtils.isValidPrefix(Ipv4Range.parse("0.0.0.0/0")));
    }

    @Test
    public void shouldReturnFalseForInvalidPrefix() {
        assertFalse(Ipv4PrefixUtils.isValidPrefix(Ipv4Range.parse("0.0.0.0-0.0.0.2")));
    }

    @Test
    public void shouldGetPrefixLengthWhenCorrectPrefix() {
        assertEquals(32, Ipv4PrefixUtils.getPrefixLength(Ipv4Range.parse("0.0.0.0-0.0.0.0")));
        assertEquals(32, Ipv4PrefixUtils.getPrefixLength(Ipv4Range.parse("0.0.0.1-0.0.0.1")));
        assertEquals(30, Ipv4PrefixUtils.getPrefixLength(Ipv4Range.parse("0.0.0.0-0.0.0.3")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToGetPrefixLengthWhenInvalidPrefix() {
        Ipv4PrefixUtils.getPrefixLength(Ipv4Range.parse("0.0.0.0-0.0.0.2"));
    }

    @Test
    public void shouldSplitIntoPrefixes() {
        validateSplitIntoPrefixes(new String[]{"0.0.0.0/32"}, "0.0.0.0-0.0.0.0");
        validateSplitIntoPrefixes(new String[]{"0.0.0.0/31"}, "0.0.0.0-0.0.0.1");
        validateSplitIntoPrefixes(new String[]{"0.0.0.0/30"}, "0.0.0.0-0.0.0.3");
        validateSplitIntoPrefixes(new String[]{"0.0.0.0/29"}, "0.0.0.0-0.0.0.7");
        validateSplitIntoPrefixes(new String[]{"0.0.0.0/31", "0.0.0.2/32"}, "0.0.0.0-0.0.0.2");
        validateSplitIntoPrefixes(new String[]{"0.0.0.0/30", "0.0.0.4/32"}, "0.0.0.0-0.0.0.4");
        validateSplitIntoPrefixes(new String[]{"0.0.0.0/30", "0.0.0.4/31"}, "0.0.0.0-0.0.0.5");
        validateSplitIntoPrefixes(new String[]{"0.0.0.0/30", "0.0.0.4/31", "0.0.0.6/32"}, "0.0.0.0-0.0.0.6");
        validateSplitIntoPrefixes(new String[]{"0.0.0.1/32"}, "0.0.0.1-0.0.0.1");
        validateSplitIntoPrefixes(new String[]{"0.0.0.1/32", "0.0.0.2/32"}, "0.0.0.1-0.0.0.2");
        validateSplitIntoPrefixes(new String[]{"0.0.0.1/32", "0.0.0.2/31"}, "0.0.0.1-0.0.0.3");
        validateSplitIntoPrefixes(new String[]{"0.0.0.2/31"}, "0.0.0.2-0.0.0.3");
        validateSplitIntoPrefixes(new String[]{"0.0.0.2/31", "0.0.0.4/32"}, "0.0.0.2-0.0.0.4");
    }

    private void validateSplitIntoPrefixes(String[] expectedPrefixes, String rangeToSplit) {
        List<Ipv4Range> expected = new ArrayList<Ipv4Range>();
        for (String prefix : expectedPrefixes) {
            expected.add(Ipv4Range.parse(prefix));
        }
        assertEquals(expected, splitIntoPrefixes(Ipv4Range.parse(rangeToSplit)));
    }

    @Test
    public void shouldSplitIntoPrefixesAllIpv4SpaceExceptFirstAddress() {
        Ipv4Range range = Ipv4Range.from("0.0.0.1").to(Ipv4.LAST_IPV4_ADDRESS);
        assertEquals(32, splitIntoPrefixes(range).size());
    }
}
