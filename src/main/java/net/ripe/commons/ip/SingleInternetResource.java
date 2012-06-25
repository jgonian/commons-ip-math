package net.ripe.commons.ip;

public interface SingleInternetResource<T extends SingleInternetResource<T, R>, R extends Range<T, R>>
        extends InternetResource<T>, Rangeable<T> {

    R asRange();
}
