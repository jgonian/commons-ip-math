package net.ripe.commons.ip;

public abstract class AbstractIpRange<C extends AbstractIp<C, R>, R extends AbstractIpRange<C, R>>
        extends AbstractRange<C, R>
        implements InternetResourceRange<C, R> {

    protected AbstractIpRange(C start, C end) {
        super(start, end);
    }
}
