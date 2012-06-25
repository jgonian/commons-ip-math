package net.ripe.commons.ip;

public interface InternetResourceRange<T extends Rangeable<T, R>, R extends InternetResourceRange<T, R, S>, S extends Number>
        extends InternetResource<R>, Range<T, R> {

    S size();
}
