package net.ripe.commons.ip.range;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.ripe.commons.ip.range.compare.RangeComparator;
import net.ripe.commons.ip.range.compare.StartAndSizeComparator;
import net.ripe.commons.ip.resource.Rangeable;

public class SortedRangeSet<C extends Rangeable<C>, R extends AbstractRange<C, R>> implements Iterable<R> {

    private static StartAndSizeComparator<?, ?> DEFAULT_COMPARATOR;

    private Set<R> set;

    /**
     * Creates an instance of {@link SortedRangeSet} with a default
     * {@link StartAndSizeComparator} which compares only the start and end of the range.
     * <em>Note, this comparator imposes orderings that might be inconsistent with the equals
     * method of the compared ranges.</em>
     */
    public SortedRangeSet() {
        this(SortedRangeSet.<C, R>getDefaultComparator());
    }

    public SortedRangeSet(RangeComparator<C, R> rangeComparator) {
        set = new TreeSet<R> (rangeComparator);
    }

    public void addAll(SortedRangeSet<C, R> rangesToAdd) {
        for (R range : rangesToAdd) {
            add(range);
        }
    }

    public void addAll(Collection<R> rangesToAdd) {
        for (R range : rangesToAdd) {
            add(range);
        }
    }

    public void add(R rangeToAdd) {
        // TODO: change the implementation to take advantage of the sorted set
        Iterator<R> it = set.iterator();
        while (it.hasNext()) {
            R rangeInSet = it.next();
            if (rangeInSet.overlaps(rangeToAdd) || rangeInSet.isConsecutive(rangeToAdd)) {
                rangeToAdd = rangeInSet.mergeConsecutive(rangeToAdd);
                it.remove();
            }
        }
        set.add(rangeToAdd);
    }

    public boolean remove(R rangeToRemove) {
        boolean removed = false;
        List<R> remainders = new ArrayList<R>();
        Iterator<R> it = iterator();
        while (it.hasNext()) {
            R rangeInIpSpace = it.next();
            if (rangeInIpSpace.overlaps(rangeToRemove)) {
                remainders.addAll(rangeInIpSpace.exclude(rangeToRemove));
                it.remove();
                removed = true;
            }
        }
        set.addAll(remainders);
        return removed;
    }

    public boolean contains(R range) {
        for (R rangeInSet : set) {
            if (rangeInSet.contains(range)) {
                return true;
            }
        }
        return false;
    }

    public void clear() {
        set.clear();
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }

    public Set<R> unmodifiableSet() {
        return Collections.unmodifiableSet(set);
    }

    @Override
    public Iterator<R> iterator() {
        return set.iterator();
    }

    @Override
    public String toString() {
        return set.toString();
    }

    @SuppressWarnings({"unchecked"})
    private static <C extends Rangeable<C>, R extends AbstractRange<C, R>> RangeComparator<C, R> getDefaultComparator() {
        if (DEFAULT_COMPARATOR == null) {
            DEFAULT_COMPARATOR = new StartAndSizeComparator<C, R>();
        }
        return (StartAndSizeComparator<C, R>) DEFAULT_COMPARATOR;
    }
}
