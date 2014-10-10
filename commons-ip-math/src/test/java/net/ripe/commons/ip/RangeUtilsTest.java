/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2011-2014, Yannis Gonianakis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
