package net.ripe.commons.ip;

import java.io.Serializable;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public abstract class SingleValue<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final T value;

    protected SingleValue(T value) {
        this.value = Validate.notNull(value, "value is required");
    }

    public T value() {
        return value;
    }

    public static <T> T valueOrElse(SingleValue<T> singleValue, T orElse) {
        return singleValue == null ? orElse : singleValue.value();
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SingleValue other = (SingleValue) o;
        return new EqualsBuilder().append(value, other.value).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(value).toHashCode();
    }
}
