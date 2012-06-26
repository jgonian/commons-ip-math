package net.ripe.commons.ip;

public class LengthComparator<C extends Rangeable<C, R>, R extends Range<C, R>>
        implements RangeComparator<C, R> {

    private static LengthComparator<?, ?> instance;

    @SuppressWarnings({"unchecked"})
    public static <C extends Rangeable<C, R>, R extends Range<C, R>> RangeComparator<C, R> getInstance() {
        if (instance == null) {
            instance = new LengthComparator<C, R>();
        }
        return (LengthComparator<C, R>) instance;
    }

    private LengthComparator() {
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public int compare(Range<C, R> left, Range<C, R> right) {
        return ((Comparable)left.length()).compareTo(right.length());
    }
}
