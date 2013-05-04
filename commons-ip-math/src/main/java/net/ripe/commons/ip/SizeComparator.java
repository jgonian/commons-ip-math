package net.ripe.commons.ip;

import java.io.Serializable;
import java.util.Comparator;

public final class SizeComparator<R extends Range<?, R>> implements Comparator<R>, Serializable {

    private static final long serialVersionUID = 1L;

    private static SizeComparator<?> instance;

    @SuppressWarnings({"unchecked"})
    public static <R extends Range<?, R>> SizeComparator<R> get() {
        if (instance == null) {
            instance = new SizeComparator<R>();
        }
        return (SizeComparator<R>) instance;
    }

    private SizeComparator() {
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public int compare(R left, R right) {
        return ((Comparable)left.size()).compareTo(right.size());
    }
}
