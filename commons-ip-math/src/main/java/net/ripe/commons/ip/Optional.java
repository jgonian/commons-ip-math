package net.ripe.commons.ip;

import java.io.Serializable;
import java.util.NoSuchElementException;

public abstract class Optional<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Optional<?> ABSENT = new Absent<Object>();

    public static <T> Optional<T> of(T value) {
        return value != null ? new Present<T>(value) : Optional.<T>absent();
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> absent() {
        return (Optional<T>) ABSENT;
    }

    public static <T> T getOrNull(Optional<T> optional) {
        return optional.isPresent() ? optional.get() : null;
    }

    public abstract T get();

    public abstract boolean isPresent();

    public boolean isAbsent() {
        return !isPresent();
    }

    public static final class Present<T> extends Optional<T> {

        private final T value;

        private Present(T value) {
            this.value = value;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public boolean isPresent() {
            return true;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Present present = (Present) o;
            return value.equals(present.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public String toString() {
            return String.format("Present(%s)", value);
        }
    }

    public static final class Absent<T> extends Optional<T> {

        private Absent() {
        }

        @Override
        public T get() {
            throw new NoSuchElementException("Absent.get");
        }

        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public String toString() {
            return "Absent";
        }
    }
}
