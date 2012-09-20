package net.ripe.commons.ip;

public abstract class AbstractIpRange<C extends AbstractIp<C, R>, R extends AbstractIpRange<C, R>>
        extends AbstractRange<C, R>
        implements InternetResourceRange<C, R> {

    protected static final String SLASH = "/";
    protected static final String DASH = "-";

    protected AbstractIpRange(C start, C end) {
        super(start, end);
    }

    @Override
    public String toString() {
        if (PrefixUtils.isValidPrefix(this)) {
            return toStringInCidrNotation();
        } else {
            return toStringInRangeNotation();
        }
    }

    public String toStringInRangeNotation() {
        return start() + DASH + end();
    }

    public String toStringInCidrNotation() {
        return start() + SLASH + PrefixUtils.getPrefixLength(this);
    }

    public String toStringInDecimalNotation() {
        return start().asBigInteger() + DASH + end().asBigInteger();
    }
}
