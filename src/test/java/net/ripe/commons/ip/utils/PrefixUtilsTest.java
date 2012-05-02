package net.ripe.commons.ip.utils;

import static org.junit.Assert.*;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import net.ripe.commons.ip.range.Ipv6Range;
import net.ripe.commons.ip.resource.Ipv6;
import org.junit.Test;

public class PrefixUtilsTest {

    @Test
    public void shouldDoValidPrefixWhenValid() {
        Ipv6Range range = Ipv6Range.from(BigInteger.valueOf(0)).to(BigInteger.valueOf(0));
        assertTrue(PrefixUtils.isValidPrefix(range));
    }

    @Test
    public void shouldDoValidPrefixWhenInvalid() {
        Ipv6Range range = Ipv6Range.from(BigInteger.valueOf(0)).to(BigInteger.valueOf(2));
        assertFalse(PrefixUtils.isValidPrefix(range));
    }

    @Test
    public void shouldGetPrefixLengthWhenCorrectPrefix0_0() {
        Ipv6Range range = Ipv6Range.from(BigInteger.valueOf(0)).to(BigInteger.valueOf(0));
        assertEquals(128, PrefixUtils.getPrefixLength(range));
    }

    @Test
    public void shouldGetPrefixLengthWhenCorrectPrefix0_3() {
        Ipv6Range range = Ipv6Range.from(BigInteger.valueOf(0)).to(BigInteger.valueOf(3));
        assertEquals(126, PrefixUtils.getPrefixLength(range));
    }

    @Test
    public void shouldGetPrefixLengthWhenCorrectPrefix1_1() {
        Ipv6Range range = Ipv6Range.from(BigInteger.valueOf(1)).to(BigInteger.valueOf(1));
        assertEquals(128, PrefixUtils.getPrefixLength(range));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenInvalidPrefix0_2() {
        Ipv6Range range = Ipv6Range.from(BigInteger.valueOf(0)).to(BigInteger.valueOf(2));
        PrefixUtils.getPrefixLength(range);
    }

    // test zero-starting valid prefix
    // 0..0
    @Test
    public void shouldSplitIntoPrefixesWhenSelfGoodPrefix0_0() {
        Ipv6Range range = Ipv6Range.from(BigInteger.valueOf(0)).to(BigInteger.valueOf(0));
        assertEquals(Collections.singletonList(range), PrefixUtils.splitIntoPrefixes(range));
    }

    // 0..1
    @Test
    public void shouldSplitIntoPrefixesWhenSelfGoodPrefix0_1() {
        Ipv6Range range = Ipv6Range.from(BigInteger.valueOf(0)).to(BigInteger.valueOf(1));
        assertEquals(Collections.singletonList(range), PrefixUtils.splitIntoPrefixes(range));
    }

    // 0..3
    @Test
    public void shouldSplitIntoPrefixesWhenSelfGoodPrefix0_3() {
        Ipv6Range range = Ipv6Range.from(BigInteger.valueOf(0)).to(BigInteger.valueOf(3));
        assertEquals(Collections.singletonList(range), PrefixUtils.splitIntoPrefixes(range));
    }

    // 0..7
    @Test
    public void shouldSplitIntoPrefixesWhenSelfGoodPrefix0_7() {
        Ipv6Range range = Ipv6Range.from(BigInteger.valueOf(0)).to(BigInteger.valueOf(7));
        assertEquals(Collections.singletonList(range), PrefixUtils.splitIntoPrefixes(range));
    }

    // test zero-staring not-valid prefixes
    // 0..2
    @Test
    public void shouldSplitIntoPrefixesWhenInvalidPrefix0_2() {
        Ipv6Range range = Ipv6Range.from(BigInteger.valueOf(0)).to(BigInteger.valueOf(2));
        Ipv6Range split1 = Ipv6Range.from(BigInteger.valueOf(0)).to(BigInteger.valueOf(1));
        Ipv6Range split2 = Ipv6Range.from(BigInteger.valueOf(2)).to(BigInteger.valueOf(2));
        assertEquals(Arrays.asList(split1, split2), PrefixUtils.splitIntoPrefixes(range));
    }

    // 0..4
    @Test
    public void shouldSplitIntoPrefixesWhenInvalidPrefix0_4() {
        Ipv6Range range = Ipv6Range.from(BigInteger.valueOf(0)).to(BigInteger.valueOf(4));
        Ipv6Range split1 = Ipv6Range.from(BigInteger.valueOf(0)).to(BigInteger.valueOf(3));
        Ipv6Range split2 = Ipv6Range.from(BigInteger.valueOf(4)).to(BigInteger.valueOf(4));
        assertEquals(Arrays.asList(split1, split2), PrefixUtils.splitIntoPrefixes(range));
    }

    // 0..5
    @Test
    public void shouldSplitIntoPrefixesWhenInvalidPrefix0_5() {
        Ipv6Range range = Ipv6Range.from(BigInteger.valueOf(0)).to(BigInteger.valueOf(5));
        Ipv6Range split1 = Ipv6Range.from(BigInteger.valueOf(0)).to(BigInteger.valueOf(3));
        Ipv6Range split2 = Ipv6Range.from(BigInteger.valueOf(4)).to(BigInteger.valueOf(5));

        assertEquals(Arrays.asList(split1, split2), PrefixUtils.splitIntoPrefixes(range));
    }

    // 0..6
    @Test
    public void shouldSplitIntoPrefixesWhenInvalidPrefix0_6() {
        Ipv6Range range = Ipv6Range.from(BigInteger.valueOf(0)).to(BigInteger.valueOf(6));
        Ipv6Range split1 = Ipv6Range.from(BigInteger.valueOf(0)).to(BigInteger.valueOf(3));
        Ipv6Range split2 = Ipv6Range.from(BigInteger.valueOf(4)).to(BigInteger.valueOf(5));
        Ipv6Range split3 = Ipv6Range.from(BigInteger.valueOf(6)).to(BigInteger.valueOf(6));

        assertEquals(Arrays.asList(split1, split2, split3), PrefixUtils.splitIntoPrefixes(range));
    }

    // test 1-starting invalid prefix
    // 1..1
    @Test
    public void shouldSplitIntoPrefixesWhenInvalidPrefix1_1() {
        Ipv6Range range = Ipv6Range.from(BigInteger.valueOf(1)).to(BigInteger.valueOf(1));
        assertEquals(Collections.singletonList(range), PrefixUtils.splitIntoPrefixes(range));
    }

    // 1..2
    @Test
    public void shouldSplitIntoPrefixesWhenInvalidPrefix1_2() {
        Ipv6Range range = Ipv6Range.from(BigInteger.valueOf(1)).to(BigInteger.valueOf(2));
        Ipv6Range split1 = Ipv6Range.from(BigInteger.valueOf(1)).to(BigInteger.valueOf(1));
        Ipv6Range split2 = Ipv6Range.from(BigInteger.valueOf(2)).to(BigInteger.valueOf(2));
        assertEquals(Arrays.asList(split1, split2), PrefixUtils.splitIntoPrefixes(range));
    }

    // 1..3
    @Test
    public void shouldSplitIntoPrefixesWhenInvalidPrefix1_3() {
        Ipv6Range range = Ipv6Range.from(BigInteger.valueOf(1)).to(BigInteger.valueOf(3));
        Ipv6Range split1 = Ipv6Range.from(BigInteger.valueOf(1)).to(BigInteger.valueOf(1));
        Ipv6Range split2 = Ipv6Range.from(BigInteger.valueOf(2)).to(BigInteger.valueOf(3));
        assertEquals(Arrays.asList(split1, split2), PrefixUtils.splitIntoPrefixes(range));
    }

    // test 2-starting valid prefix
    // 2..3
    @Test
    public void shouldSplitIntoPrefixesWhenInvalidPrefix2_3() {
        Ipv6Range range = Ipv6Range.from(BigInteger.valueOf(2)).to(BigInteger.valueOf(3));
        assertEquals(Collections.singletonList(range), PrefixUtils.splitIntoPrefixes(range));
    }

    // test 2-starting invalid prefix
    // 2..4
    @Test
    public void shouldSplitIntoPrefixesWhenInvalidPrefix2_4() {
        Ipv6Range range = Ipv6Range.from(BigInteger.valueOf(2)).to(BigInteger.valueOf(4));
        Ipv6Range split1 = Ipv6Range.from(BigInteger.valueOf(2)).to(BigInteger.valueOf(3));
        Ipv6Range split2 = Ipv6Range.from(BigInteger.valueOf(4)).to(BigInteger.valueOf(4));
        assertEquals(Arrays.asList(split1, split2), PrefixUtils.splitIntoPrefixes(range));
    }

    // test whole IPv6 space
    @Test
    public void shouldSplitIntoPrefixesAllIpv6SpaceExceptFirstAddress() {
        Ipv6Range range = Ipv6Range.from(BigInteger.ONE).to(Ipv6.LAST_IPV6_ADDRESS);
        assertEquals(128, PrefixUtils.splitIntoPrefixes(range).size());
    }
}
