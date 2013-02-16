package net.ripe.commons.ip;

public interface InternetResourceRange<T extends Rangeable<T, R>, R extends InternetResourceRange<T, R>>
        extends Range<T, R> {

    Comparable<?> size();
}
