package net.ripe.commons.ip.resource;

public interface Rangeable<T> extends Comparable<T> {

    T next();

    T previous();
}
