package net.ripe.commons.ip.resource;

import net.ripe.commons.ip.range.AbstractRange;

public interface SingleInternetResource<T extends SingleInternetResource<T, R>, R extends AbstractRange<T, R>>
        extends InternetResource<T>, Rangeable<T> {

    public R asRange();
}
