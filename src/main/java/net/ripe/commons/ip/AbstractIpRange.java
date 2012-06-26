package net.ripe.commons.ip;

public abstract class AbstractIpRange<V extends Comparable<V>, C extends AbstractIp<V, C, R>, R extends AbstractIpRange<V, C, R>>
        extends AbstractRange<C, R>
        implements InternetResourceRange<C, R, V> {

    protected AbstractIpRange(C start, C end) {
        super(start, end);
    }
}
