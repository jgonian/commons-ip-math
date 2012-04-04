package net.ripe.commons.ip.range.compare;

import java.util.Comparator;
import net.ripe.commons.ip.range.AbstractRange;
import net.ripe.commons.ip.resource.Rangeable;

public interface RangeComparator<C extends Rangeable<C>, R extends AbstractRange<C, R>>
        extends Comparator<AbstractRange<C, R>> {
}
