package net.ripe.commons.ip.range;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.Validate;

public class DefaultSequenceImpl {

    private static final Map<Class<?>, Sequence> sequences;

    static {
        sequences = new HashMap<Class<?>, Sequence>();
        sequences.put(Integer.class, new IntegerSequence());
        sequences.put(Date.class, new DateSequence());
    }

    public synchronized static <C extends Comparable<C>> void registerSequenceOf(Class<C> typeOfSequence, Sequence sequence) {
        sequences.put(typeOfSequence, sequence);
    }

    public synchronized static boolean hasSpecificHandlerFor(Class<?> type) {
        return sequences.containsKey(type);
    }

    public synchronized static Sequence getSequence(Class<? extends Comparable> typeOfSequence) {
        Validate.notNull(hasSpecificHandlerFor(typeOfSequence), "Could not find a sequence for: " + typeOfSequence.getName());
        return sequences.get(typeOfSequence);
    }

    private static class IntegerSequence implements Sequence<Integer> {

        @Override
        public Integer nextOf(Integer value) {
            return value + 1;
        }

        public Integer previous(Integer value) {
            return value - 1;
        }
    }

    private static class DateSequence implements Sequence<Date> {

        @Override
        public Date nextOf(Date value) {
            return new Date(value.getTime() + 1);
        }

        @Override
        public Date previous(Date value) {
            return new Date(value.getTime() - 1);
        }
    }

}
