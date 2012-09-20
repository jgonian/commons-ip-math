package net.ripe.commons.ip;

import java.math.BigInteger;

public interface SingleInternetResource<T extends SingleInternetResource<T, R>, R extends InternetResourceRange<T, R>>
        extends InternetResource<T>, Rangeable<T, R> {

    int bitSize();

    BigInteger asBigInteger();
}
