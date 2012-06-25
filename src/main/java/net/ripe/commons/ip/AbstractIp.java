package net.ripe.commons.ip;

public abstract class AbstractIp<V extends Comparable<V>, T extends SingleInternetResource<T, R>, R extends InternetResourceRange<T, R, V>>
        extends SingleValue<V>
        implements SingleInternetResource<T, R> {

    protected AbstractIp(V value) {
        super(value);
    }
}
