package net.ripe.commons.ip;

import java.util.Comparator;

public interface RangeComparator<C extends Rangeable<C>, R extends AbstractRange<C, R>>
        extends Comparator<AbstractRange<C, R>> {
}
