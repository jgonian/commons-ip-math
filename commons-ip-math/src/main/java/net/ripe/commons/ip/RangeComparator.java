package net.ripe.commons.ip;

import java.util.Comparator;

public interface RangeComparator<C extends Rangeable<C, R>, R extends Range<C, R>>
        extends Comparator<Range<C, R>> {
}
