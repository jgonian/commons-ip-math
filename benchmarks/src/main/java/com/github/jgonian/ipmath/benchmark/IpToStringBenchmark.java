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
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import org.openjdk.jmh.annotations.*;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"ResultOfMethodCallIgnored", "UnstableApiUsage"})
@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class IpToStringBenchmark {

    String ipv4String = "192.0.2.0";
    String ipv6String = "2001:DB8::";

    Ipv4 ipMath_IPv4 = Ipv4.of(ipv4String);
    Ipv6 ipMath_IPv6 = Ipv6.of(ipv6String);

    InetAddress guava_IPv4 = InetAddresses.forString(ipv4String);
    InetAddress guava_IPv6 = InetAddresses.forString(ipv6String);

    IPAddress ipaddress_IPv4;
    {
        try {
            ipaddress_IPv4 = new IPAddressString(ipv4String).toAddress();
        } catch (AddressStringException e) {
            e.printStackTrace();
        }
    }

    IPAddress ipaddress_IPv6;
    {
        try {
            ipaddress_IPv6 = new IPAddressString(ipv6String).toAddress();
        } catch (AddressStringException e) {
            e.printStackTrace();
        }
    }

    @Benchmark
    public void to_string_ipv4_ipmath() {
        ipMath_IPv4.toString();
    }

    @Benchmark
    public void to_string_ipv4_guava() {
        guava_IPv4.toString();
    }

    @Benchmark
    public void to_string_ipv4_ipaddress() {
        ipaddress_IPv4.toString();
    }

    @Benchmark
    public void to_string_ipv6_ipmath() {
        ipMath_IPv6.toString();
    }

    @Benchmark
    public void to_string_ipv6_guava() {
        guava_IPv6.toString();
    }

    @Benchmark
    public void to_string_ipv6_ipaddress() {
        ipaddress_IPv6.toString();
    }
}
