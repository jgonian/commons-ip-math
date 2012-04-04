package net.ripe.commons.ip.range;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import net.ripe.commons.ip.range.compare.RangeComparator;
import net.ripe.commons.ip.range.compare.StartAndSizeComparator;
import net.ripe.commons.ip.resource.Rangeable;

public class NormalizedAbstractRangeSet<C extends Rangeable<C>, R extends AbstractRange<C, R>> implements Iterable<AbstractRange<C, R>> {

    private static StartAndSizeComparator<?, ?> DEFAULT_COMPARATOR;

    private Set<AbstractRange<C, R>> set;

    /**
     * Creates an instance of {@link NormalizedAbstractRangeSet} with a default
     * {@link StartAndSizeComparator} which compares only the start and end of the range.
     * <em>Note, this comparator imposes orderings that might be inconsistent with the equals
     * method of the compared ranges.</em>
     */
    public NormalizedAbstractRangeSet() {
        this(NormalizedAbstractRangeSet.<C, R>getDefaultComparator());
    }

    public NormalizedAbstractRangeSet(RangeComparator<C, R> rangeComparator) {
        set = new TreeSet<AbstractRange<C, R>> (rangeComparator);
    }

    public void add(R rangeToAdd) {
        Iterator<AbstractRange<C, R>> it = set.iterator();
        while (it.hasNext()) {
            AbstractRange<C, R> rangeInSet = it.next();
            if (rangeInSet.overlaps(rangeToAdd)) {
                rangeToAdd = rangeInSet.merge(rangeToAdd);
                it.remove();
            }
        }
        set.add(rangeToAdd);
    }

    public boolean contains(R range) {
        for (AbstractRange<C, R> rangeInSet : set) {
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

    public Set<AbstractRange<C, R>> unmodifiableSet() {
        return Collections.unmodifiableSet(set);
    }

    @Override
    public Iterator<AbstractRange<C, R>> iterator() {
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
