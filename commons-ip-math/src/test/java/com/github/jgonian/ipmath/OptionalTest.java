/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2011-2014, Yannis Gonianakis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.jgonian.ipmath;

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

    @Test
    public void shouldConvertToString() {
        assertThat(Optional.of("value").toString(), is("Present(value)"));
        assertThat(Optional.absent().toString(), is("Absent"));
    }
}
