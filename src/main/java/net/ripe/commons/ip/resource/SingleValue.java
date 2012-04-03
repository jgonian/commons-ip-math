package net.ripe.commons.ip.resource;

import java.io.Serializable;
import org.apache.commons.lang.Validate;

public abstract class SingleValue<T> extends EqualsSupport implements Serializable {

    private static final long serialVersionUID = 1L;

    private final T value;

    protected SingleValue(T value) {
        Validate.notNull(value, "value is required");
        this.value = value;
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
}
