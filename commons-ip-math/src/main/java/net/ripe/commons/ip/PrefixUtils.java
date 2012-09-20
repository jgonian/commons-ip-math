package net.ripe.commons.ip;

public final class PrefixUtils {

    private PrefixUtils() {
    }

    public static <C extends AbstractIp<C, R>, R extends AbstractIpRange<C, R>>
    boolean isValidPrefix(AbstractIpRange<C, R> range) {
        int prefixLength = range.start().getCommonPrefixLength(range.end());
        C lowerBoundForPrefix = range.start().lowerBoundForPrefix(prefixLength);
        C upperBoundForPrefix = range.end().upperBoundForPrefix(prefixLength);
        return range.start().equals(lowerBoundForPrefix) && range.end().equals(upperBoundForPrefix);
    }
}
