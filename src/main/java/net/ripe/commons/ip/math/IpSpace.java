package net.ripe.commons.ip.math;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

@Deprecated
public class IpSpace<T extends IpRange<T>> implements Serializable, Iterable<T> {

    private static final long serialVersionUID = 1L;

    private NavigableSet<T> space;

    public IpSpace(Comparator<T> comparator) {
        this.space = new TreeSet<T>(comparator);
    }

    public boolean isFree(T range) {
        T floored = space.floor(range);
        return floored != null && floored.contains(range);
    }

    public void add(T range) {
        if (contains(range)) {
            return;
        }
        freeAndMergeConsecutive(range);
    }

    private boolean contains(T range) {
        T floored = space.floor(range);
        return floored != null && floored.contains(range);
    }

    public void remove(T range) {
        Iterator<T> freeRangesIterator = space.iterator();
        List<T> remainders = new ArrayList<T>();
        while (freeRangesIterator.hasNext()) {
            T freeRange = freeRangesIterator.next();
            if (freeRange.intersects(range)) {
                freeRangesIterator.remove();
                remainders.addAll(freeRange.relativeComplement(range));
            }
        }
        space.addAll(remainders);
    }

    public NavigableSet<T> findFreeBlocksInRanges(Set<T> ranges) {
        NavigableSet<T> freeBlocks = new TreeSet<T>();
        for (T pool : ranges) {
            for (T free : space) {
                T intersection = pool.intersect(free);
                if (intersection != null) {
                    freeBlocks.add(intersection);
                }
            }
        }
        return freeBlocks;
    }

    public List<T> getFreePrefixesInRanges(Collection<T> ranges) {
        List<T> prefixes = new ArrayList<T>();
        for (T range : ranges) {
            for (T freeRange : space) {
                T intersection = freeRange.intersect(range);
                if (intersection != null) {
                    prefixes.addAll(intersection.splitIntoPrefixes());
                }
            }
        }
        return prefixes;
    }

    public NavigableSet<T> getFreeRanges() {
        return new TreeSet<T>(space);
    }

    private void freeAndMergeConsecutive(T toBeFreed) {
        T rightSide = space.higher(toBeFreed);
        if (rightSide != null && areRangesConsecutive(toBeFreed, rightSide)) {
            T merged = merge(toBeFreed, rightSide);
            freeAndMergeConsecutive(merged);
        } else {
            T leftSide = space.lower(toBeFreed);
            if (leftSide != null && areRangesConsecutive(leftSide, toBeFreed)) {
                T mergable = merge(leftSide, toBeFreed);
                freeAndMergeConsecutive(mergable);
            } else {
                space.add(toBeFreed);
            }
        }
    }

    private T merge(T leftSide, T rightSide) {
        space.remove(rightSide);
        space.remove(leftSide);
        T merged = leftSide.merge(rightSide);
        space.add(merged);
        return merged;
    }

    private boolean areRangesConsecutive(T leftSide, T rightSide) {
        return rightSide.getStart().equals(leftSide.getEnd().add(BigInteger.ONE));
    }

    @Override
    public Iterator<T> iterator() {
        return space.iterator();
    }

    public void clear() {
        this.space.clear();
    }
}
