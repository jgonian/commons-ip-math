package net.ripe.commons.ip.math;

import java.util.Comparator;

/**
 * Note: this comparator
 * imposes orderings that are inconsistent with equals.
 */
public class StartOnlyComparator<T extends IpRange<T>> implements Comparator<T> {
    @Override
    public int compare(T o1, T o2) {
        return o1.start.compareTo(o2.start);
    }
}
