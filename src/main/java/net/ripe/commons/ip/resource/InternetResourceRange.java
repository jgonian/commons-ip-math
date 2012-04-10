package net.ripe.commons.ip.resource;

public interface InternetResourceRange<T extends Rangeable<T>, R extends InternetResourceRange<T, R, S>, S extends Number> extends InternetResource<R> {

    S size();
}
