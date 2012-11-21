package net.ripe.commons.ip;

import static org.junit.Assert.*;
import java.math.BigInteger;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

public class AsnTest {

    @Test
    public void testEqualsContract() {
        EqualsVerifier.forClass(Asn.class).suppress(Warning.NULL_FIELDS).withRedefinedSuperclass().verify();
    }

    @Test
    public void shouldHaveConformingTextualRepresentation() {
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

    @Test
    public void shouldParseTheSameAsnInAllAvailableFormats() {
        Asn expected = new Asn(65546l);
        assertEquals(expected, Asn.parse("AS65546"));
        assertEquals(expected, Asn.parse("65546"));
        assertEquals(expected, Asn.parse("1.10"));
        assertEquals(expected, Asn.parse("AS1.10"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnNullArgument() {
        Asn.parse(null);
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
    public void shouldParseMaximumAndMinimumPossibleAsn() {
        assertEquals("AS0", Asn.parse("AS0").toString());
        assertEquals("AS4294967295", Asn.parse("AS4294967295").toString());
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

    @Test
    public void testIs16Bit() {
        assertTrue(Asn.FIRST_ASN.is16Bit());
        assertTrue(Asn.LAST_16_BIT_ASN.is16Bit());
        assertFalse(Asn.LAST_16_BIT_ASN.next().is16Bit());
        assertFalse(Asn.LAST_32_BIT_ASN.is16Bit());
    }

    @Test
    public void testIs32Bit() {
        assertFalse(Asn.FIRST_ASN.is32Bit());
        assertFalse(Asn.LAST_16_BIT_ASN.is32Bit());
        assertTrue(Asn.LAST_16_BIT_ASN.next().is32Bit());
        assertTrue(Asn.LAST_32_BIT_ASN.is32Bit());
    }

    @Test
    public void shouldHave32BitsSize() {
        assertEquals(Asn.NUMBER_OF_BITS, Asn.FIRST_ASN.bitSize());
    }

    @Test
    public void testAsBigInteger() {
        assertEquals(BigInteger.valueOf(Asn.ASN_32_BIT_MAX_VALUE), Asn.LAST_32_BIT_ASN.asBigInteger());
    }
}
