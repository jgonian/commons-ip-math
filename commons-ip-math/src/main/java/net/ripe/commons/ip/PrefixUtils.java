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

    public static <C extends AbstractIp<C, R>, R extends AbstractIpRange<C, R>>
    int getPrefixLength(AbstractIpRange<C, R> range) {
        Validate.isTrue(isValidPrefix(range), range.toStringInRangeNotation() + " is not a legal prefix, cannot get prefix length!");
        return range.start().getCommonPrefixLength(range.end());
    }
}
