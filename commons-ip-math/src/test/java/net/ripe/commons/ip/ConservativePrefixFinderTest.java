package net.ripe.commons.ip;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static net.ripe.commons.ip.Ipv6Range.parse;
import static org.junit.Assert.*;

public class ConservativePrefixFinderTest {

    private final ConservativePrefixFinder subject = ConservativePrefixFinder.newInstance();

    @Test
    public void shouldReturnNullIfRequestedPrefixIsTooBig() {
        assertNull(subject.findPrefixOrNull(9, in("::/10, 2::/20, 3::/30")));
    }

    @Test
    public void shouldFindExactMatch() {
        assertEquals(parse("::/10"), subject.findPrefixOrNull(10, in("::/10, 2::/20, 3::/30")));
        assertEquals(parse("2::/20"), subject.findPrefixOrNull(20, in("::/10, 2::/20, 3::/30")));
        assertEquals(parse("3::/30"), subject.findPrefixOrNull(30, in("::/10, 2::/20, 3::/30")));
    }

    @Test
    public void shouldFindPrefixFromClosestMatch() {
        assertEquals(parse("3::/31"), subject.findPrefixOrNull(31, in("::/10, 2::/20, 3::/30")));
        assertEquals(parse("2::/29"), subject.findPrefixOrNull(29, in("::/10, 2::/20, 3::/30")));
        assertEquals(parse("2::/21"), subject.findPrefixOrNull(21, in("::/10, 2::/20, 3::/30")));
        assertEquals(parse("::/19"), subject.findPrefixOrNull(19, in("::/10, 2::/20, 3::/30")));
    }

    @Test
    public void shouldFindPrefixFromTheFirstClosestMatch() {
        assertEquals(parse("2::/20"), subject.findPrefixOrNull(20, in("::/10, 2::/20, 3::/20")));
        assertEquals(parse("3::/20"), subject.findPrefixOrNull(20, in("::/10, 3::/20, 2::/20")));
    }

    private List<Ipv6Range> in(String commaSeparatedRanges) {
        String[] split = commaSeparatedRanges.split(",");
        List<Ipv6Range> result = new ArrayList<Ipv6Range>();
        for (String s : split) {
            result.add(parse(s.trim()));
        }
        return result;
    }
}
