package net.ripe.commons.ip;

import java.io.Serializable;
import java.util.Comparator;

public final class StartAndSizeComparator<C extends Rangeable<C, R>, R extends Range<C, R>>
        implements Comparator<R>, Serializable {

    private static final long serialVersionUID = 1L;
    
    private static StartAndSizeComparator<?, ?> instance;

    @SuppressWarnings({"unchecked"})
    public static <C extends Rangeable<C, R>, R extends Range<C, R>> StartAndSizeComparator<C, R> get() {
        if (instance == null) {
            instance = new StartAndSizeComparator<C, R>();
        }
        return (StartAndSizeComparator<C, R>) instance;
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
