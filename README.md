[![Build Status](https://travis-ci.org/jgonian/commons-ip-math.svg?branch=master)](https://travis-ci.org/jgonian/commons-ip-math)
[![Coverage Status](https://img.shields.io/coveralls/jgonian/commons-ip-math.svg)](https://coveralls.io/r/jgonian/commons-ip-math)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.jgonian/commons-ip-math/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.jgonian/commons-ip-math)

Commons IP Math
================

Description
-----------

This library contains a representation of internet resources:

* `IPv4` and `IPv6` addresses and ranges
* Autonomous System Numbers (`ASN`s)

It provides a rich, type-safe API for dealing with the most common operations performed with IP resources, such as:
parsing, printing in several notations, checking if ranges are overlapping or can be merged, etc.

It also provides Comparators, Set collections and other utilities for working with IP ranges and Prefixes.

Dependency Information
-----------------------

Maven:
```xml
<dependency>
    <groupId>com.github.jgonian</groupId>
    <artifactId>commons-ip-math</artifactId>
    <version>${version}</version>
</dependency>
```

SBT:
```scala
libraryDependencies += "com.github.jgonian" % "commons-ip-math" % s"${version}"
```

For the latest released `version`, check [the Central Repository](https://maven-badges.herokuapp.com/maven-central/com.github.jgonian/commons-ip-math).

Examples
---------

### IPv4 addresses

```java
Ipv4 start = Ipv4.of("192.168.0.0");
Ipv4 end = Ipv4.of("192.168.0.255");

// Fluent API for creating new instances
Ipv4Range a = Ipv4Range.from(start).to(end);
Ipv4Range b = Ipv4Range.from("192.168.0.0").andPrefixLength(24);
Ipv4Range c = Ipv4Range.parse("192.168.0.0/24");
Ipv4Range d = Ipv4Range.parse("192.168.1.0/24");

a.equals(b);        // true
a.isEmpty();        // false
a.size();           // 256
a.contains(b);      // true
a.overlaps(d);      // false
a.isConsecutive(d); // true
a.merge(d);         // 192.168.0.0/23
```

### IPv6 addresses

```java
Ipv6 start = Ipv6.of("2001:db8:0:0:0:0:0:0");       // 2001:db8::
Ipv6 end   = IPv6.of("2001:db8:0:0:0:0:0:ffff");    // 2001:db8::ffff

// Fluent API for creating new instances
Ipv6Range a = Ipv6Range.from(start).to(end);
Ipv6Range b = Ipv6Range.from(start).andPrefixLength(112);
Ipv6Range c = Ipv6Range.parse("2001:db8::/112");
Ipv6Range d = Ipv6Range.parse("2001:db8::1:0/112");

a.equals(b);        // true
a.isEmpty();        // false
a.size();           // 65536
a.contains(b);      // true
a.overlaps(d);      // false
a.isConsecutive(d); // true
a.merge(d);         // 2001:db8::/111
```
Printing of IPv6 addresses complies with [RFC 5952](http://tools.ietf.org/html/rfc5952).

### Autonomous System Numbers (ASNs)

```java
Asn.of("AS65546");  // AS65546
Asn.of(65546l);     // AS65546
Asn.of("1.10");     // AS65546

Asn.of("AS65546").is32Bit() // true
```
Parsing of AS numbers complies with [RFC 5396](http://tools.ietf.org/html/rfc5396).

#### Sets
```java
SortedResourceSet<Ipv4, Ipv4Range> set = new SortedResourceSet<Ipv4, Ipv4Range>();
set.add(Ipv4Range.parse("192.168.0.0/16"));
set.add(Ipv4Range.parse("10.0.0.0-10.0.0.253"));
set.add(Ipv4.of("10.0.0.254"));
set.add(Ipv4.of("10.0.0.255"));
System.out.println(set);           // [10.0.0.0/24, 192.168.0.0/16]

```
#### Comparators
```java
Ipv6Range a = Ipv6Range.parse("2001::/64");
Ipv6Range b = Ipv6Range.parse("2001::/65");
SizeComparator.<Ipv6Range>get().compare(a, b);  // 1
```
#### Iterable ranges
```java
for (Ipv4 ip : Ipv4Range.parse("10.0.0.0/30")) {
    System.out.println(ip);
}
// will print:
// 10.0.0.0
// 10.0.0.1
// 10.0.0.2
// 10.0.0.3
```

and more

License
--------
The commons-ip-math is released under the MIT license.

Acknowledgements
------------
The project started as a fork of [IP Resource](https://github.com/RIPE-NCC/ipresource) and it has been evolved 
to the extent that most of the logic was rewritten. Special thanks for the inspiration to many contributors of the 
IP resource library and in particular [Erik Rozendaal](https://github.com/erikrozendaal), [Szabolcs Andrasi](https://github.com/sandrasi), 
[Oleg Muravskiy](https://github.com/omuravskiy), Johan Ählen and Katie Petrusha.

