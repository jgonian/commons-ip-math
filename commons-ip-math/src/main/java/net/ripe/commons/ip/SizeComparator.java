package net.ripe.commons.ip;

public class SizeComparator<C extends Rangeable<C, R>, R extends Range<C, R>>
        implements RangeComparator<C, R> {

    private static SizeComparator<?, ?> instance;

    @SuppressWarnings({"unchecked"})
    public static <C extends Rangeable<C, R>, R extends Range<C, R>> RangeComparator<C, R> getInstance() {
        if (instance == null) {
            instance = new SizeComparator<C, R>();
        }
        return (SizeComparator<C, R>) instance;
    }

    private SizeComparator() {
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public int compare(Range<C, R> left, Range<C, R> right) {
        return ((Comparable)left.size()).compareTo(right.size());
    }
}
