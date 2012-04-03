package net.ripe.commons.ip.resource;

public interface Rangeable<T> {

    T next();

    T previous();
}
