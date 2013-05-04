package net.ripe.commons.ip;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

public class SortedRangeSet<C extends Rangeable<C, R>, R extends Range<C, R>> implements Iterable<R> {

    private final NavigableSet<R> set;

    /**
     * Creates an instance of {@link SortedRangeSet} with a default
     * {@link StartAndSizeComparator} which compares only the start and end of the range.
     * <em>Note, this comparator imposes orderings that might be inconsistent with the equals
     * method of the compared ranges.</em>
     */
    public SortedRangeSet() {
        set = new TreeSet<R>(StartAndSizeComparator.<C, R>get());
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
        R leftSide = set.floor(range);
        R rightSide = set.ceiling(range);
        boolean removed = false;
        while (range.contains(rightSide)) {
            set.remove(rightSide);
            removed = true;
            rightSide = set.higher(rightSide);
        }
        List<R> remainders = new LinkedList<R>(); 
        if (leftSide != null && leftSide.overlaps(range)) {
            set.remove(leftSide);
            remainders.addAll(leftSide.exclude(range));
            removed = true;
        }
        if (rightSide != null && rightSide.overlaps(range)) {
            set.remove(rightSide);
            remainders.addAll(rightSide.exclude(range));
            removed = true;
        }
        set.addAll(remainders);
        return removed;
    }
    
    public SortedRangeSet<C, R> intersection(SortedRangeSet<C, R> other) {
        SortedRangeSet<C, R> result = new SortedRangeSet<C, R>();
        for (R thisRange : set) {
            R leftSide = other.set.floor(thisRange);
            R rightSide = other.set.ceiling(thisRange);
            if (thisRange.overlaps(leftSide)) {
                result.add(thisRange.intersection(leftSide));
            }
            if (thisRange.overlaps(rightSide)) {
                result.add(thisRange.intersection(rightSide));
            }
        }
        return result;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SortedRangeSet)) {
            return false;
        }
        SortedRangeSet that = (SortedRangeSet) o;
        return set.equals(that.set);
    }

    @Override
    public final int hashCode() {
        return set.hashCode();
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

    public int size() {
        return set.size();
    }

    /**
     * @return an unmodifiable instance of this {@link SortedRangeSet} as a {@link Set}
     */
    public Set<R> unmodifiableSet() {
        return Collections.unmodifiableSet(set);
    }

    /**
     * @return a modifiable copy of this {@link SortedRangeSet} as a {@link Set}
     */
    public Set<R> modifiableSet() {
        TreeSet<R> copy = new TreeSet<R>(set.comparator());
        copy.addAll(set);
        return copy;
    }

    public R getSingleRange() {
        Validate.isTrue(set.size() == 1, "Expected exactly one range");
        return set.first();
    }
    
    public R floor(R range) {
        return set.floor(range);
    }
    
    public R ceiling(R range) {
        return set.ceiling(range);
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
