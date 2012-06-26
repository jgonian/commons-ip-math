package net.ripe.commons.ip;

public abstract class AbstractIp<V extends Comparable<V>, T extends AbstractIp<V, T, R>, R extends AbstractIpRange<V, T, R>>
        extends SingleValue<V>
        implements SingleInternetResource<T, R> {

    protected AbstractIp(V value) {
        super(value);
    }
}
