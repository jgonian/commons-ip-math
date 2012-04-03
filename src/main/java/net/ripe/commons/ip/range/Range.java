package net.ripe.commons.ip.range;

public class Range<T extends Comparable<T>> extends AbstractRange<T, Range<T>> {

    private final Sequence sequence;

    protected Range(T start, T end) {
        super(start, end);
        sequence = DefaultSequenceImpl.getSequence(start.getClass());
    }

    @Override
    protected Range<T> newInstance(T start, T end) {
        return new Range<T>(start, end);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    protected T nextOf(T rangeItem) {
        return (T) sequence.nextOf(rangeItem);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    protected T previousOf(T rangeItem) {
        return (T) sequence.previous(rangeItem);
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
