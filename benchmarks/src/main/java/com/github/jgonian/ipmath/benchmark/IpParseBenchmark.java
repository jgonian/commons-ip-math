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
package com.github.jgonian.ipmath.benchmark;

import com.github.jgonian.ipmath.Ipv4;
import com.github.jgonian.ipmath.Ipv6;
import com.google.common.net.InetAddresses;
import inet.ipaddr.AddressStringException;
import inet.ipaddr.IPAddressString;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@SuppressWarnings({"ResultOfMethodCallIgnored", "UnstableApiUsage"})
@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class IpParseBenchmark {

    String ipv4String = "192.0.2.0";
    String ipv6String = "2001:DB8::";

    @Benchmark
    public void parse_ipv4_ipmath() {
        Ipv4.parse(ipv4String);
    }

    @Benchmark
    public void parse_ipv4_guava() {
        InetAddresses.forString(ipv4String);
    }

    @Benchmark
    public void parse_ipv4_ipaddress() throws AddressStringException {
        new IPAddressString(ipv4String).toAddress().toIPv4();
    }

    @Benchmark
    public void parse_ipv6_ipmath() {
        Ipv6.parse(ipv6String);
    }

    @Benchmark
    public void parse_ipv6_guava() {
        InetAddresses.forString(ipv6String);
    }

    @Benchmark
    public void parse_ipv6_ipaddress() throws AddressStringException {
        new IPAddressString(ipv6String).toAddress().toIPv6();
    }

}
