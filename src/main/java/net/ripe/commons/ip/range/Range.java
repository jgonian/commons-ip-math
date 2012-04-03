package net.ripe.commons.ip.range;

import org.apache.commons.lang.Validate;

public class Range<T extends Comparable<T>> {

    private T start;
    private T end;

    public Range(T start, T end) {
        Validate.notNull(start, "start of range must not be null");
        Validate.notNull(end, "end of range must not be null");
        Validate.isTrue(start.compareTo(end) <= 0, String.format("Invalid range [%s..%s]", start.toString(), end.toString()));
        this.start = start;
        this.end = end;
    }

    public T start() {
        return start;
    }

    public T end() {
        return end;
    }

    public boolean overlaps(Range<T> arg) {
        return arg.contains(start) || arg.contains(end) || this.contains(arg);
    }

    public boolean contains(Range<T> arg) {
        return this.contains(arg.start) && this.contains(arg.end);
    }

    public boolean contains(T arg) {
        Validate.notNull(arg, "A value is required");
        return start.compareTo(arg) <= 0 && end.compareTo(arg) >= 0;
    }

    public Range<T> intersection(Range<T> other) {
        T start = max(this.start(), other.start());
        T end = min(this.end(), other.end());
        return new Range<T>(start, end);
    }

    private T max(T a, T b) {
        return a.compareTo(b) >= 0 ? a : b;
    }

    private T min(T a, T b) {
        return a.compareTo(b) <= 0 ? a : b;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Range range = (Range) o;

        if (end != null ? !end.equals(range.end) : range.end != null) return false;
        if (start != null ? !start.equals(range.start) : range.start != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = start != null ? start.hashCode() : 0;
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("[%s..%s]", start.toString(), end.toString());
    }



/*------------------------------------*/

    /*public static <R extends Range<T>, T extends Comparable<T>> RangeBuilder<R, T> ofType(Class<R> of) {
        return new RangeBuilder<R,T>(of);
    }

    public static class RangeBuilder<R extends Range<T>, T extends Comparable<T>> {
        private Class<R> rangeClass;
        private T start;

        private RangeBuilder(Class<R> rangeClass) {
            this.rangeClass = rangeClass;
        }

        public RangeBuilder<R, T> from(T start) {
            this.start = start;
            return this;
        }

        @SuppressWarnings({"unchecked"})
        public R to(T end) {
            try {
                return rangeClass.getDeclaredConstructor(start.getClass(), end.getClass()).newInstance(start, end);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
            //            return (R) new Range<T>(start, end);
        }
    }*/



/*------------------------------------*/

/*public static <K extends Comparable<K>> RangeBuilder<K> from(K start) {
        return new RangeBuilder<K>(start);
    }

    public static class RangeBuilder<K extends Comparable<K>> {
        private K start;

        protected RangeBuilder(K start) {
            this.start = start;
        }

        public <R extends Range<K>> R to(K end) {
            return (R) new Range<K>(start, end);
        }
    }*/



/*------------------------------------*/
    public static <T extends Comparable<T>> RangeBuilder<T> from(T from) {
        return new RangeBuilder<T>(from);
    }

    public static class RangeBuilder<T extends Comparable<T>> {
        private T start;

        protected RangeBuilder(T from) {
            this.start = from;
        }

        public Range<T> to(T end) {
            return new Range<T>(start, end);
        }
    }

    public <R extends Range<T>> R as(Class<R> rangeType) {
        return as(this, rangeType);
    }

    public <R extends Range<T>> R as(Range<T> range, Class<R> rangeType) {
        try {
            return rangeType.getDeclaredConstructor(range.start().getClass(), range.end().getClass())
                    .newInstance(range.start, range.end);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
