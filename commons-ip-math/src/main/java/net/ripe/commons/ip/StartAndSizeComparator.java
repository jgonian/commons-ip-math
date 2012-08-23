package net.ripe.commons.ip;

import java.io.Serializable;

public class StartAndSizeComparator<C extends Rangeable<C, R>, R extends Range<C, R>>
        implements RangeComparator<C, R>, Serializable {

    private static final long serialVersionUID = 6525126518038448733L;
    
    private static StartAndSizeComparator<?, ?> instance;

    @SuppressWarnings({"unchecked"})
    public static <C extends Rangeable<C, R>, R extends Range<C, R>> RangeComparator<C, R> getInstance() {
        if (instance == null) {
            instance = new StartAndSizeComparator<C, R>();
        }
        return (StartAndSizeComparator<C, R>) instance;
    }

    private StartAndSizeComparator() {
    }

    @Override
    public int compare(Range<C, R> left, Range<C, R> right) {
        int result = left.start().compareTo(right.start());
        if (result == 0) {
            result = left.end().compareTo(right.end());
        }
        return result;
    }
}
