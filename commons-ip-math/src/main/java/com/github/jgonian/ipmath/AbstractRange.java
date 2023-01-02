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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public abstract class AbstractRange<C extends Rangeable<C, R>, R extends Range<C, R>> implements Range<C, R> {

    private final C start;
    private final C end;

    protected AbstractRange(C start, C end) {
        this.start = Validate.notNull(start, "start of range must not be null");
        this.end = Validate.notNull(end, "end of range must not be null");
        Validate.isTrue(this.start.compareTo(this.end) <= 0, "Invalid range [" + start + ".." + end + "]");
    }

    protected abstract R newInstance(C start, C end);

    @Override
    public C start() {
        return start;
    }

    @Override
    public C end() {
        return end;
    }

    @Override
    public boolean contains(R other) {
        return other != null && (start.compareTo(other.start()) <= 0 && end.compareTo(other.end()) >= 0);
    }

    @Override
    public boolean contains(C value) {
        Validate.notNull(value, "A value is required");
        return start.compareTo(value) <= 0 && end.compareTo(value) >= 0;
    }

    @Override
    public boolean overlaps(R other) {
        return other != null && (other.contains(start) || other.contains(end) || this.contains(other));
    }

    @Override
    public boolean isConsecutive(R other) {
        if (other == null) {
            return false;
        } else {
            return (end.hasNext() && end.next().equals(other.start())) || (other.end().hasNext() && other.end().next().equals(this.start));
        }
    }

    @Override
    public boolean isEmpty() {
        return end.equals(start);
    }

    @Override
    public R merge(R other) {
        Validate.isTrue(this.overlaps(other) || this.isConsecutive(other), "Merge is only possible for overlapping or consecutive ranges");
        C min = min(this.start, other.start());
        C max = max(this.end, other.end());
        return newInstance(min, max);
    }

    @Override
    public R intersection(R other) {
        C max = max(this.start, other.start());
        C min = min(this.end, other.end());
        return newInstance(max, min);
    }

    private C max(C a, C b) {
        return a.compareTo(b) >= 0 ? a : b;
    }

    private C min(C a, C b) {
        return a.compareTo(b) <= 0 ? a : b;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public List<R> exclude(R other) {
        if (!overlaps(other)) {
            return Collections.singletonList((R) this);

        } else if (other.contains((R) this)) {
            return Collections.emptyList();

        } else if (!this.contains(other.start()) && this.contains(other.end())) {
            return Collections.singletonList(newInstance(other.end().next(), this.end));

        } else if (this.contains(other.start()) && !this.contains(other.end())) {
            return Collections.singletonList(newInstance(this.start, other.start().previous()));

        } else {
            if (this.hasSameStart(other)) {
                return Collections.singletonList(newInstance(other.end().next(), this.end));

            } else if (this.hasSameEnd(other)) {
                return Collections.singletonList(newInstance(this.start, other.start().previous()));

            } else {
                ArrayList<R> rs = new ArrayList<R>(2);
                rs.add(newInstance(this.start, other.start().previous()));
                rs.add(newInstance(other.end().next(), this.end));
                return rs;
            }
        }
    }

    @Override
    public boolean isSameRange(R other) {
        return hasSameStart(other) && hasSameEnd(other);
    }

    private boolean hasSameStart(R other) {
        return this.start.equals(other.start());
    }

    private boolean hasSameEnd(R other) {
        return this.end.equals(other.end());
    }

    @Override
    public String toString() {
        return "[" + start.toString() + ".." + end.toString() + "]";
    }

    @Override
    public Iterator<C> iterator() {
        return new RangeIterator();
    }

    private class RangeIterator implements Iterator<C> {

        private C nextValue = start;
        private boolean reachedTheEnd = false;

        @Override
        public boolean hasNext() {
            return nextValue.compareTo(end) <= 0 && !reachedTheEnd;
        }

        @Override
        public C next() {
            if (reachedTheEnd) throw new NoSuchElementException("range iterator out of bounds");
            C valueToReturn = nextValue;
            try {
                nextValue = valueToReturn.next();
            } catch (IllegalArgumentException ignored) {
                reachedTheEnd = true;
            }
            return valueToReturn;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractRange)) {
            return false;
        }
        AbstractRange that = (AbstractRange) o;
        if (!start.equals(that.start)) {
            return false;
        }
        if (!end.equals(that.end)) {
            return false;
        }
        return true;
    }

    @Override
    public final int hashCode() {
        int result = start.hashCode();
        result = 31 * result + end.hashCode();
        return result;
    }

    protected abstract static class AbstractRangeBuilder<C extends Rangeable<C, R>, R extends AbstractRange<C, R>> {

        public abstract R to(C to);
    }

}
