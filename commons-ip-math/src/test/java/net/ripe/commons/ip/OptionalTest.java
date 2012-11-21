package net.ripe.commons.ip;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.NoSuchElementException;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

public class OptionalTest {

    @Test
    public void shouldWrapValueInPresent() {
        Object value = new Object();
        Optional<Object> optional = Optional.of(value);
        assertThat(optional.get(), is(value));
        assertTrue(optional.isPresent());
        assertFalse(optional.isAbsent());
    }

    @Test
    public void shouldWrapNullInAbsent() {
        Optional<Object> optional = Optional.of(null);
        assertFalse(optional.isPresent());
        assertTrue(optional.isAbsent());
    }

    @Test
    public void shouldUnwrapValueFromPresent() {
        Object value = new Object();
        assertThat(Optional.getOrNull(Optional.of(value)), is(value));
    }

    @Test
    public void shouldUnwrapNullFromAbsent() {
        assertNull(Optional.getOrNull(Optional.absent()));
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldNotGetValueFromAbsent() {
        Optional.absent().get();
    }

    @Test
    public void testEqualsContract() {
        EqualsVerifier.forClass(Optional.Present.class).suppress(Warning.NULL_FIELDS).withRedefinedSuperclass().verify();
        EqualsVerifier.forClass(Optional.Absent.class).withRedefinedSuperclass().verify();
    }
}
