package net.ripe.commons.ip;

import org.junit.Test;

public class RangeUtilsTest {

    @Test
    public void shouldPassRangeCheck() {
        RangeUtils.checkRange(10, 10, 20);
        RangeUtils.checkRange(20, 10, 20);
        RangeUtils.checkRange(15, 10, 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfValueIsOutsideTheLowerBound() {
        RangeUtils.checkRange(9, 10, 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfValueIsOutsideTheUpperBound() {
        RangeUtils.checkRange(21, 10, 20);
    }
}
