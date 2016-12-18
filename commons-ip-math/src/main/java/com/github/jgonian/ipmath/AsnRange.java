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

public final class AsnRange extends AbstractRange<Asn, AsnRange> implements InternetResourceRange<Asn, AsnRange> {

    protected AsnRange(Asn start, Asn end) {
        super(start, end);
    }

    @Override
    protected AsnRange newInstance(Asn start, Asn end) {
        return new AsnRange(start, end);
    }

    public static AsnRangeBuilder from(Long from) {
        return from(Asn.of(from));
    }

    public static AsnRangeBuilder from(Asn from) {
        return new AsnRangeBuilder(from);
    }

    public static AsnRangeBuilder from(String from) {
        return new AsnRangeBuilder(Asn.parse(from));
    }

    public static AsnRange parse(String text) {
        int idx = text.indexOf('-');
        Validate.isTrue(idx != -1, "Invalid range of ASNs: '" + text + "'");
        Asn start = Asn.parse(text.substring(0, idx));
        Asn end = Asn.parse(text.substring(idx + 1));
        return new AsnRange(start, end);
    }

    public boolean containsOnly16BitAsns() {
        return end().is16Bit();
    }

    public boolean containsOnly32BitAsns() {
        return start().is32Bit();
    }

    @Override
    public String toString() {
        return start() + "-" + end();
    }

    @Override
    public Long size() {
        return (end().value() - start().value()) + 1;
    }

    public static class AsnRangeBuilder extends AbstractRangeBuilder<Asn, AsnRange> {

        private final Asn from;

        protected AsnRangeBuilder(Asn from) {
            this.from = from;
        }

        public AsnRange to(Long end) {
            return to(Asn.of(end));
        }

        public AsnRange to(String end) {
            return to(Asn.parse(end));
        }

        @Override
        public AsnRange to(Asn to) {
            return new AsnRange(from, to);
        }
    }
}
