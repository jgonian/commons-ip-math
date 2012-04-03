package net.ripe.commons.ip.range;

public interface Sequence<C extends Comparable<C>> {

    C nextOf(C value);

    C previous(C value);

}
