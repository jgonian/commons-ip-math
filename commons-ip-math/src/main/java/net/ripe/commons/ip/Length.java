package net.ripe.commons.ip;

public class Length<L extends Comparable<L>> extends SingleValue<L> implements Comparable<Length<L>> {

    protected Length(L value) {
        super(value);
    }

    @Override
    public int compareTo(Length<L> other) {
        return value().compareTo(other.value());
    }
}
