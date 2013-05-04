package net.ripe.commons.ip;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;

public final class SizeComparator<R extends Range<?, R>> implements Comparator<R>, Serializable {

    private static final long serialVersionUID = 1L;

    private static Comparator<?> instance;
    private static Comparator<?> reverse;

    @SuppressWarnings("unchecked")
    public static <R extends Range<?, R>> Comparator<R> get() {
        return (Comparator<R>) (instance == null ? instance = new SizeComparator<R>() : instance);
    }

    @SuppressWarnings("unchecked")
    public static <R extends Range<?, R>> Comparator<R> reverse() {
        return (Comparator<R>) (reverse == null ? reverse = Collections.reverseOrder(SizeComparator.<R>get()) : reverse);
    }

    private SizeComparator() {
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public int compare(R left, R right) {
        return ((Comparable)left.size()).compareTo(right.size());
    }
}
