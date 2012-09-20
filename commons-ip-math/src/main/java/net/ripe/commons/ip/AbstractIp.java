package net.ripe.commons.ip;

public abstract class AbstractIp<T extends AbstractIp<T, R>, R extends AbstractIpRange<T, R>>
        implements SingleInternetResource<T, R> {

}
