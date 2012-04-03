package net.ripe.commons.ip.range;

public class Range<T extends Comparable<T>> extends AbstractRange<T, Range<T>> {

    protected Range(T start, T end) {
        super(start, end);
    }

    @Override
    protected Range<T> newInstance(T start, T end) {
        return new Range<T>(start, end);
    }

    public static <K extends Comparable<K>> RangeBuilder<K> from(K start) {
        return new RangeBuilder<K>(start);
    }

    public static class RangeBuilder<K extends Comparable<K>> {
        private K start;

        private RangeBuilder(K start) {
            this.start = start;
        }

        public Range<K> to(K end) {
            return new Range<K>(start, end);
        }
    }
}
