/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2011-2017, Yannis Gonianakis
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
package com.github.jgonian.ipmath;

import static org.junit.Assert.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class Ipv4ParseInvalidTest {

    @Parameter(0)
    public String input;

    @Test
    public void test() {
        assertThrows("Invalid IPv4 address: '" + input + "'", IllegalArgumentException.class, () -> {
            Ipv4.parse(input);
        });
    }

    @Parameters(name = "{index}: parse({0})")
    public static List<Object> data() {
        return Arrays.asList(new Object[]{
                "",                                             // empty string

                // Leading zero's in IPv4 addresses are not allowed (Issue #15)
                "00.0.0.0",
                "0.00.0.0",
                "0.0.00.0",
                "0.0.0.00",
                "00.00.00.00",

                "000.0.0.0",
                "0.000.0.0",
                "0.0.000.0",
                "0.0.0.000",
                "000.000.000.000",
        });
    }
}
