package net.ripe.commons.ip.math;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.Validate;

@Deprecated
public abstract class Range<T extends Range<T>> implements Serializable {

    private static final long serialVersionUID = 1L;

    protected final BigInteger start;
    protected final BigInteger end;

    public Range(BigInteger start, BigInteger end) {
        Validate.notNull(start, "Start of range is required");
        Validate.isTrue(start.compareTo(BigInteger.ZERO) >= 0, "should be >= 0");
        Validate.notNull(end, "End of range is required");
        Validate.isTrue(end.compareTo(start) >= 0, "end should be >= start");
        this.start = start;
        this.end = end;
    }

    protected abstract T createNew(BigInteger start, BigInteger end);

    protected abstract T getThis();

    public BigInteger getEnd() {
        return end;
    }

    public BigInteger getStart() {
        return start;
    }

    public boolean intersects(T other) {
        return (this.intersect(other) != null);
    }

    public T intersect(T other) {
        if (this.contains(other)) {
            return other;
        }
        if (other.contains(getThis())) {
            return getThis();
        }
        // Other overlaps start
        if (this.start.compareTo(other.start) > 0 && this.start.compareTo(other.end) <= 0) {
            return createNew(this.start, other.end);
        }
        // Other overlaps end
        if (this.end.compareTo(other.start) >= 0 && this.end.compareTo(other.end) < 0) {
            return createNew(other.start, this.end);
        }
        // They don't overlap at all
        return null;
    }

    public T merge(T other) {
        Validate.isTrue(getThis().intersects(other) || getThis().isConsecutiveTo(other) || other.isConsecutiveTo(getThis()), "Merge is only possible for intersecting or consecutive ranges");
        return createNew(other.start.min(this.start), other.end.max(this.end));
    }

    public boolean isConsecutiveTo(T other) {
        return other.end.add(BigInteger.ONE).equals(this.start);
    }

    public boolean contains(T other) {
        return start.compareTo(other.start) <= 0 && end.compareTo(other.end) >= 0;
    }

    public BigInteger size() {
        return end.subtract(start).add(BigInteger.ONE);
    }

    public List<T> relativeComplement(T other) {
        if (!other.intersects(getThis())) {
            return Collections.singletonList(getThis());
        }

        if (other.contains(getThis())) {
            return Collections.emptyList();
        }

        if (other.start.compareTo(this.start) <= 0) {
            // Other overlaps start
            return Collections.singletonList(createNew(other.end.add(BigInteger.ONE), this.end));
        }

        if (other.end.compareTo(this.end) >= 0) {
            // Other overlaps finish
            return Collections.singletonList(createNew(this.start, other.start.subtract(BigInteger.ONE)));
        }

        // Other is contained in this
        List<T> asList = new ArrayList<T>();
        asList.add(createNew(this.start, other.start.subtract(BigInteger.ONE)));
        asList.add(createNew(other.end.add(BigInteger.ONE), this.end));
        return asList;
    }
}
