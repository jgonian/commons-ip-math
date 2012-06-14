package net.ripe.commons.ip;

public interface Rangeable<T> extends Comparable<T> {

    T next();

    T previous();

    boolean hasNext();

    boolean hasPrevious();
}
