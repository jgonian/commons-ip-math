package net.ripe.commons.ip;

public abstract class AbstractIp<T extends AbstractIp<T, R>, R extends AbstractIpRange<T, R>>
        implements SingleInternetResource<T, R> {

    public abstract T lowerBoundForPrefix(int prefixLength);

    public abstract T upperBoundForPrefix(int prefixLength);

    public abstract int getCommonPrefixLength(T other);
}
