package net.ripe.commons.ip.range;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class NormalizedAbstractRangeSet<C extends Comparable<C>, R extends AbstractRange<C, R>> implements Iterable<AbstractRange<C, R>> {

    private Set<AbstractRange<C, R>> set;

    public NormalizedAbstractRangeSet() {
        set = new HashSet<AbstractRange<C, R>>();
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
}
