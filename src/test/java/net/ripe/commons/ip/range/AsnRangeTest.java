package net.ripe.commons.ip.range;

import static junit.framework.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.ripe.commons.ip.resource.Asn;
import org.junit.Test;

public class AsnRangeTest extends AbstractRangeTest<Asn, AsnRange> {

    private final Asn as1 = Asn.of(1L);
    private final Asn as2 = Asn.of(2L);
    private final Asn as3 = Asn.of(3L);

    @Override
    protected Asn from(String s) {
        return Asn.of(s);
    }

    @Override
    protected Asn to(String s) {
        return Asn.of(s);
    }

    @Override
    protected Asn item(String s) {
        return Asn.of(s);
    }

    @Override
    protected AsnRange getTestRange(Asn start, Asn end) {
        return new AsnRange(start, end);
    }

    @Override
    public void testToString() {
        AsnRange range = new AsnRange(as1, as3);
        assertEquals("[1..3]", range.toString());
    }

    @Override
    public void testNextOf() {
        AsnRange range = new AsnRange(as1, as2);
        assertEquals(as2, range.nextOf(as1));
        assertEquals(as3, range.nextOf(as2));
    }

    @Override
    public void testPreviousOf() {
        AsnRange range = new AsnRange(as2, as3);
        assertEquals(as2, range.previousOf(as3));
        assertEquals(as1, range.previousOf(as2));
    }

    @Override
    public void testIterator() {
        List<Asn> result = new ArrayList<Asn>();
        for (Asn asn : new AsnRange(as1, as3)) {
            result.add(asn);
        }
        assertEquals(Arrays.asList(as1, as2, as3), result);
    }

    @Test
    public void testBuilder() {
        AsnRange range = AsnRange.from(as1).to(as3);
        assertEquals(as1, range.start());
        assertEquals(as3, range.end());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderWithInvalidRange() {
        AsnRange.from(as3).to(as1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderWithNullStart() {
        AsnRange.from((Asn)null).to(as3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderWithNullEnd() {
        AsnRange.from(as1).to((Asn)null);
    }


}
