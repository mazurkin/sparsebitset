package org.sparsebitset.index;

import org.junit.Test;

import static org.junit.Assert.*;

public class SparseBitLongIndexTest {

    @Test
    public void testIndex() {
        SparseBitIndex index = SparseBitLongIndex.of(0xFF11223344556677L);

        assertEquals(8, index.levels());

        assertEquals(0xFF, index.segment(7));
        assertEquals(0x11, index.segment(6));
        assertEquals(0x22, index.segment(5));
        assertEquals(0x33, index.segment(4));
        assertEquals(0x44, index.segment(3));
        assertEquals(0x55, index.segment(2));
        assertEquals(0x66, index.segment(1));
        assertEquals(0x77, index.segment(0));

        assertEquals("FF11223344556677", index.toString());
    }

    @Test
    public void testToString() {
        SparseBitIndex index = SparseBitLongIndex.of(0x0000000000000000L);

        assertEquals("0000000000000000", index.toString());
    }

    @Test
    public void testNegative() {
        SparseBitLongIndex index = SparseBitLongIndex.of(0x80000000_00000000L);

        assertEquals(0x80, index.segment(7));
    }
}