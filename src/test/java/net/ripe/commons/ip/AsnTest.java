package net.ripe.commons.ip;

import static org.junit.Assert.*;
import net.ripe.commons.ip.Asn;
import org.junit.Test;

public class AsnTest {

    /**
     * Tests that the textual representation of an ASN conforms to
     * http://tools.ietf.org/html/draft-michaelson-4byte-as-representation-05.
     */
    @Test
    public void shouldHaveConformingTextualRepresentation() {
        assertEquals(null, Asn.parse(null));

        assertEquals(new Asn(3333l), Asn.parse("AS3333"));
        assertEquals(new Asn((12 << 16) | 3333l), Asn.parse("AS12.3333"));

        assertEquals("AS3333", String.valueOf(new Asn(3333l)));
        assertEquals("AS789765", String.valueOf(new Asn((12 << 16) | 3333l)));
    }

    @Test
    public void shouldParseShortVersion() {
        assertEquals(new Asn(3333l), Asn.parse("3333"));
        assertEquals(new Asn((12 << 16) | 3333l), Asn.parse("12.3333"));
        assertEquals(new Asn(65536l), Asn.parse("  65536  "));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnBadFormat() {
        Asn.parse("AS23.321.12");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnIllegalRange() {
        Asn.parse("AS23.321412");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnIllegalValue() {
        Asn.parse("AS232442321412");
    }

    @Test
    public void shouldParseDotNotatedAsNumber() {
        assertEquals(new Asn(65536l), Asn.parse("AS1.0"));
    }

    @Test
    public void shouldParseContinuousNumberNotation() {
        assertEquals(new Asn(65536l), Asn.parse("AS65536"));
    }

    @Test
    public void parseShouldBeCaseInsensitive() {
        assertEquals(Asn.parse("AS3333"), Asn.parse("as3333"));
    }

    @Test
    public void shouldParseNumberWithLeadingAndTrailingSpaces() {
        assertEquals(new Asn(65536l), Asn.parse("  AS65536  "));
    }

    @Test
    public void shouldParseHighestPossibleAsn() {
        Asn.parse("AS4294967295");
    }

    @Test
    public void testCompareTo() {
        assertTrue(Asn.parse("AS3333").compareTo(Asn.parse("AS3333")) == 0);
        assertTrue(Asn.parse("AS3333").compareTo(Asn.parse("AS3334")) < 0);
        assertTrue(Asn.parse("AS3333").compareTo(Asn.parse("AS3332")) > 0);
    }

    @Test
    public void testHasNext() {
        assertTrue(Asn.FIRST_ASN.hasNext());
        assertTrue(Asn.LAST_16_BIT_ASN.hasNext());
        assertFalse(Asn.LAST_32_BIT_ASN.hasNext());
    }

    @Test
    public void testHasPrevious() {
        assertFalse(Asn.FIRST_ASN.hasPrevious());
        assertTrue(Asn.LAST_16_BIT_ASN.hasPrevious());
        assertTrue(Asn.LAST_32_BIT_ASN.hasPrevious());
    }
}
