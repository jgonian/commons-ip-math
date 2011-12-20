package net.ripe.commons.ip.math;

import java.util.Comparator;

public class ShorterFirstComparator<T extends IpRange<T>> implements Comparator<T> {
    @Override
    public int compare(T o1, T o2) {
        int result = o1.start.compareTo(o2.start);
        if (result == 0) result = o1.end.compareTo(o2.end);
        return result;
    }
}
