package net.ripe.commons.ip.range;

import static junit.framework.Assert.*;
import java.util.Date;
import org.junit.Test;

public class RangeTest extends AbstractRangeTest<Date,Range<Date>> {

    @Override
    protected Date from(String s) {
        return new Date(Long.parseLong(s));
    }

    @Override
    protected Date to(String s) {
        return new Date(Long.parseLong(s));
    }

    @Override
    protected Date item(String s) {
        return new Date(Long.parseLong(s));
    }

    @Override
    protected Range<Date> getTestRange(Date start, Date end) {
        return new Range<Date>(start, end);
    }

    @Override
    public void testToString() {
        Range<Character> range = new Range<Character>('a', 'c');
        assertEquals("[a..c]", range.toString());
    }

    @Override
    public void testNextOf() {
        Range<Integer> range = Range.from(1).to(2);
        assertEquals(new Integer(2), range.nextOf(1));
        assertEquals(new Integer(3), range.nextOf(2));
    }

    @Override
    public void testPreviousOf() {
        Range<Integer> range = Range.from(2).to(3);
        assertEquals(new Integer(2), range.previousOf(3));
        assertEquals(new Integer(1), range.previousOf(2));
    }

    @Test
    public void testBuilder() {
        Range<Integer> range = Range.from(1).to(3);
        assertEquals(new Integer(1), range.start());
        assertEquals(new Integer(3), range.end());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderWithInvalidRange() {
        Range.from(3).to(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderWithNullStart() {
        Range.from((Integer)null).to(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderWithNullEnd() {
        Range.from(1).to((Integer)null);
    }
}
