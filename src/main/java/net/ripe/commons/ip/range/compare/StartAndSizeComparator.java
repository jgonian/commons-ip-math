package net.ripe.commons.ip.range.compare;

import net.ripe.commons.ip.range.AbstractRange;
import net.ripe.commons.ip.resource.Rangeable;

public class StartAndSizeComparator<C extends Rangeable<C>, R extends AbstractRange<C, R>>
        implements RangeComparator<C, R> {

    private static StartAndSizeComparator<?,?> INSTANCE;

    @SuppressWarnings({"unchecked"})
    public static <C extends Rangeable<C>, R extends AbstractRange<C, R>> RangeComparator<C, R> getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StartAndSizeComparator<C, R>();
        }
        return (StartAndSizeComparator<C, R>) INSTANCE;
    }

    @Override
    public int compare(AbstractRange<C, R> left, AbstractRange<C, R> right) {
        int result = left.start().compareTo(right.start());
        if (result == 0) result = left.end().compareTo(right.end());
        return result;
    }
}
