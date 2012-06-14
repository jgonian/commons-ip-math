package net.ripe.commons.ip.range;

public class RangeCreationException extends RuntimeException {

    public RangeCreationException(String format, Exception e) {
        super(format, e);
    }
}
