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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.List;

@RunWith(Parameterized.class)
public class Ipv6DoubleColonTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Parameter(0)
    public String input;

    @Test
    public void test() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid IPv6 address: '" + input + "'");
        Ipv6.parse(input);
    }

    // Invalid IPv6 examples are taken from http://download.dartware.com/thirdparty/test-ipv6-regex.pl
    @Parameters(name = "{index}: parse({0})")
    public static List<Object> data() {
        return Arrays.asList(new Object[] {
                // Double "::" full
                "::1111:2222:3333:4444:5555:6666::",
                "::2222::4444:5555:6666:7777:8888",
                "::2222:4444::5555:6666:7777:8888",
                "::2222:4444:5555::6666:7777:8888",
                "::2222:4444:5555:6666::7777:8888",
                "::2222:4444:5555:6666:7777::8888",
                "::2222:4444:5555:6666:7777:8888::",
                "1111::3333::5555:6666:7777:8888",
                "1111::3333:4444::6666:7777:8888",
                "1111::3333:4444:5555::7777:8888",
                "1111::3333:4444:5555:6666::8888",
                "1111::3333:4444:5555:6666:7777::",
                "1111:2222::4444::6666:7777:8888",
                "1111:2222::4444:5555::7777:8888",
                "1111:2222::4444:5555:6666::8888",
                "1111:2222::4444:5555:6666:7777::",
                "1111:2222:3333::5555::7777:8888",
                "1111:2222:3333::5555:6666::8888",
                "1111:2222:3333::5555:6666::8888",
                "1111:2222:3333::5555:6666:7777::",
                "1111:2222:3333:4444::6666::8888",
                "1111:2222:3333:4444::6666:7777::",
                "1111:2222:3333:4444:5555::7777::",

                // Double "::" shortened
                "3ffe:b00::1::a",

                // Double "::" with embedded IPv4
                "::2222::4444:5555:6666:1.2.3.4",
                "::2222:3333::5555:6666:1.2.3.4",
                "::2222:3333:4444::6666:1.2.3.4",
                "::2222:3333:4444:5555::1.2.3.4",

                "1111::3333::5555:6666:1.2.3.4",
                "1111::3333:4444::6666:1.2.3.4",
                "1111::3333:4444:5555::1.2.3.4",

                "1111:2222::4444::6666:1.2.3.4",
                "1111:2222::4444:5555::1.2.3.4",

                "1111:2222:3333::5555::1.2.3.4",

                // Fully expanded and has single "::"
                "::1111:2222:3333:4444:5555:6666:7777:8888",
                "1111::2222:3333:4444:5555:6666:7777:8888",
                "1111:2222::3333:4444:5555:6666:7777:8888",
                "1111:2222:3333::4444:5555:6666:7777:8888",
                "1111:2222:3333:4444::5555:6666:7777:8888",
                "1111:2222:3333:4444:5555::6666:7777:8888",
                "1111:2222:3333:4444:5555:6666::7777:8888",
                "1111:2222:3333:4444:5555:6666:7777::8888",
                "1111:2222:3333:4444:5555:6666:7777:8888::",

                // Fully expanded and has single "::" with embedded IPv4
                "::1111:2222:3333:4444:5555:6666:1.2.3.4",
                "1111::2222:3333:4444:5555:6666:1.2.3.4",
                "1111:2222::3333:4444:5555:6666:1.2.3.4",
                "1111:2222:3333::4444:5555:6666:1.2.3.4",
                "1111:2222:3333:4444::5555:6666:1.2.3.4",
                "1111:2222:3333:4444:5555::6666:1.2.3.4",
                "1111:2222:3333:4444:5555:6666::1.2.3.4",
                "1111:2222:3333:4444:5555:6666:1.2.3.4::",
        });
    }
}
