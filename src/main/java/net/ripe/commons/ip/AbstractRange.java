package net.ripe.commons.ip;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
        return start.compareTo(other.start()) <= 0 && end.compareTo(other.end()) >= 0;
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
    public boolean isAdjacent(R other) {
        return other != null && (this.end.equals(other.start()) || other.end().equals(this.start));
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
    public R mergeOverlapping(R other) {
        Validate.isTrue(this.overlaps(other), "Merge is only possible for overlapping ranges");
        return merge(other);
    }

    @Override
    public R mergeConsecutive(R other) {
        Validate.isTrue(this.overlaps(other) || this.isConsecutive(other), "Merge is only possible for overlapping or consecutive ranges");
        return merge(other);
    }

    private R merge(R other) {
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

        @Override
        public boolean hasNext() {
            return nextValue.compareTo(end) <= 0;
        }

        @Override
        public C next() {
            C valueToReturn = nextValue;
            nextValue = valueToReturn.next();
            return valueToReturn;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractRange that = (AbstractRange) o;

        if (end != null ? !end.equals(that.end) : that.end != null) {
            return false;
        }
        if (start != null ? !start.equals(that.start) : that.start != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = start != null ? start.hashCode() : 0;
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }

    protected abstract static class AbstractRangeBuilder<C extends Rangeable<C, R>, R extends AbstractRange<C, R>> {
        private final C start;
        private final Class<R> typeOfRange;

        protected AbstractRangeBuilder(C from, Class<R> typeOfRange) {
            Validate.notNull(from);
            Validate.notNull(typeOfRange);
            this.start = from;
            this.typeOfRange = typeOfRange;
        }

        public R to(C end) {
            Validate.notNull(end);
            try {
                return typeOfRange.getDeclaredConstructor(start.getClass(), end.getClass()).newInstance(start, end);
            } catch (InvocationTargetException e) {
                handleValidationExceptions(e);
                throw new RangeCreationException("Failed to create range [" + start + ".." + end + "]", e);
            } catch (Exception e) {
                throw new RangeCreationException("Failed to create range [" + start + ".." + end + "]", e);
            }
        }

        private void handleValidationExceptions(InvocationTargetException e) {
            if (e.getCause() instanceof IllegalArgumentException) {
                throw (IllegalArgumentException) e.getCause();
            } else if (e.getCause() instanceof NullPointerException) {
                throw (NullPointerException) e.getCause();
            }
        }
    }

}
