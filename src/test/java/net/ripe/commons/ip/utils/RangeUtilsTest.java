package net.ripe.commons.ip.utils;

import org.junit.Test;

public class RangeUtilsTest {

    @Test
    public void shouldPassRangeCheck() {
        RangeUtils.rangeCheck(10, 10, 20);
        RangeUtils.rangeCheck(20, 10, 20);
        RangeUtils.rangeCheck(15, 10, 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfValueIsOutsideTheLowerBound() {
        RangeUtils.rangeCheck(9, 10, 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfValueIsOutsideTheUpperBound() {
        RangeUtils.rangeCheck(21, 10, 20);
    }
}
