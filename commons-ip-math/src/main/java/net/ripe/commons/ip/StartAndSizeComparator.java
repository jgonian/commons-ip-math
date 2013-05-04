package net.ripe.commons.ip;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;

public final class StartAndSizeComparator<C extends Rangeable<C, R>, R extends Range<C, R>>
        implements Comparator<R>, Serializable {

    private static final long serialVersionUID = 1L;
    
    private static Comparator<?> instance;
    private static Comparator<?> reverse;

    @SuppressWarnings("unchecked")
    public static <C extends Rangeable<C, R>, R extends Range<C, R>> Comparator<R> get() {
        return (Comparator<R>) (instance == null ? instance = new StartAndSizeComparator<C, R>() : instance);
    }

    @SuppressWarnings("unchecked")
    public static <C extends Rangeable<C, R>, R extends Range<C, R>> Comparator<R> reverse() {
        return (Comparator<R>) (reverse == null ? reverse = Collections.reverseOrder(StartAndSizeComparator.<C, R>get()) : reverse);
    }

    private StartAndSizeComparator() {
    }

    @Override
    public int compare(R left, R right) {
        int result = left.start().compareTo(right.start());
        if (result == 0) {
            result = left.end().compareTo(right.end());
        }
        return result;
    }
}
