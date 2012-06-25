package net.ripe.commons.ip;

import java.io.Serializable;
import java.util.List;

public interface Range<C extends Rangeable<C, R>, R extends Range<C, R>>
        extends Iterable<C>, Serializable {

    C start();

    C end();

    boolean contains(R other);

    boolean contains(C value);

    boolean overlaps(R other);

    boolean isAdjacent(R other);

    boolean isConsecutive(R other);

    boolean isEmpty();

    R mergeOverlapping(R other);

    R mergeConsecutive(R other);

    R intersection(R other);

    List<R> exclude(R other);

    boolean isSameRange(R other);
}
