package net.ripe.commons.ip.range;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import net.ripe.commons.ip.range.compare.RangeComparator;
import net.ripe.commons.ip.range.compare.StartAndSizeComparator;
import net.ripe.commons.ip.resource.Rangeable;

public class SortedRangeSet<C extends Rangeable<C>, R extends AbstractRange<C, R>> implements Iterable<R> {

    private final NavigableSet<R> set;

    /**
     * Creates an instance of {@link SortedRangeSet} with a default
     * {@link StartAndSizeComparator} which compares only the start and end of the range.
     * <em>Note, this comparator imposes orderings that might be inconsistent with the equals
     * method of the compared ranges.</em>
     */
    public SortedRangeSet() {
        this(StartAndSizeComparator.<C, R>getInstance());
    }

    public SortedRangeSet(RangeComparator<C, R> rangeComparator) {
        set = new TreeSet<R>(rangeComparator);
    }

    public void addAll(SortedRangeSet<C, R> ranges) {
        for (R range : ranges) {
            add(range);
        }
    }

    public void addAll(Collection<R> ranges) {
        for (R range : ranges) {
            add(range);
        }
    }

    public void add(R range) {
        if (contains(range)) {
            return;
        }
        freeAndMergeConsecutive(range);
    }

    private void freeAndMergeConsecutive(R range) {
        R rightSide = set.higher(range);
        R leftSide = set.lower(range);
        if (!range.overlaps(rightSide) && !range.overlaps(leftSide)) {
            set.add(range);
        }
        if (range.overlaps(rightSide) || range.isConsecutive(rightSide)) {
            R merged = range.mergeConsecutive(rightSide);
            remove(rightSide);
            freeAndMergeConsecutive(merged);
        }
        if (range.overlaps(leftSide) || range.isConsecutive(leftSide)) {
            R merged = range.mergeConsecutive(leftSide);
            remove(leftSide);
            freeAndMergeConsecutive(merged);
        }
    }

    public void removeAll(SortedRangeSet<C, R> ranges) {
        for (R range : ranges) {
            remove(range);
        }
    }

    public void removeAll(Collection<R> ranges) {
        for (R range : ranges) {
            remove(range);
        }
    }

    public boolean remove(R range) {
        boolean removed = false;
        List<R> remainders = new ArrayList<R>();
        Iterator<R> it = iterator();
        while (it.hasNext()) {
            R rangeInSet = it.next();
            if (rangeInSet.overlaps(range)) {
                remainders.addAll(rangeInSet.exclude(range));
                it.remove();
                removed = true;
            }
        }
        set.addAll(remainders);
        return removed;
    }

    public boolean contains(R range) {
        R leftmost = set.floor(range);
        R rightmost = set.ceiling(range);
        return (leftmost != null && leftmost.contains(range)) || (rightmost != null && rightmost.contains(range));
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

}
