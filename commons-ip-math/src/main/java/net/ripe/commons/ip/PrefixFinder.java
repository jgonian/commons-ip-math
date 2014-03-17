package net.ripe.commons.ip;

import java.util.Collection;

public abstract class PrefixFinder {

    public interface Strategy {
        public Ipv6Range findPrefixOrNull(int prefixLength, Collection<Ipv6Range> ranges);
    }

    private final Strategy strategy;

    public PrefixFinder(Strategy strategy) {
        this.strategy = strategy;
    }

    public Ipv6Range findPrefixOrNull(int prefixLength, Collection<Ipv6Range> ranges) {
        return strategy.findPrefixOrNull(prefixLength, ranges);
    }
}
