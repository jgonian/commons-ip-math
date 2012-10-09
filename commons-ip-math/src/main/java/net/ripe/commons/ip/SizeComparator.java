package net.ripe.commons.ip;

import java.io.Serializable;

public final class SizeComparator<C extends Rangeable<C, R>, R extends Range<C, R>>
        implements RangeComparator<C, R>, Serializable {

    private static final long serialVersionUID = 1L;

    private static SizeComparator<?, ?> instance;

    @SuppressWarnings({"unchecked"})
    public static <C extends Rangeable<C, R>, R extends Range<C, R>> SizeComparator<C, R> getInstance() {
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
