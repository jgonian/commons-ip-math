package net.ripe.commons.ip;

public final class RangeUtils {

    private RangeUtils() {
    }

    public static <T extends Comparable<T>> T rangeCheck(T value, T begin, T end) {
        if (value.compareTo(begin) >= 0 && value.compareTo(end) <= 0) {
            return value;
        }
        throw new IllegalArgumentException("Value [" + value + "] out of range: [" + begin + "commons-ip-math/src/test" + end + "]");
    }
}
