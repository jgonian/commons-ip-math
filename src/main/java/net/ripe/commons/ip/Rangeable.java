package net.ripe.commons.ip;

public interface Rangeable<T extends Rangeable<T, R>, R extends Range<T,R>> extends Comparable<T> {

    T next();

    T previous();

    boolean hasNext();

    boolean hasPrevious();

    R asRange();
}
