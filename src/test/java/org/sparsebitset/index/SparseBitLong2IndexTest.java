package org.sparsebitset.index;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SparseBitLong2IndexTest {

    @Test
    public void testIndex() {
        SparseBitIndex index = new SparseBitLong2Index(0xFF112233_44556677L, 0xA1B1C1D1_E1F10111L);

        assertEquals(16, index.levels());

        assertEquals(0xFF, index.segment(15));
        assertEquals(0x11, index.segment(14));
        assertEquals(0x22, index.segment(13));
        assertEquals(0x33, index.segment(12));
        assertEquals(0x44, index.segment(11));
        assertEquals(0x55, index.segment(10));
        assertEquals(0x66, index.segment(9));
        assertEquals(0x77, index.segment(8));

        assertEquals(0xA1, index.segment(7));
        assertEquals(0xB1, index.segment(6));
        assertEquals(0xC1, index.segment(5));
        assertEquals(0xD1, index.segment(4));
        assertEquals(0xE1, index.segment(3));
        assertEquals(0xF1, index.segment(2));
        assertEquals(0x01, index.segment(1));
        assertEquals(0x11, index.segment(0));

        assertEquals("FF11223344556677A1B1C1D1E1F10111", index.toString());
    }

    @Test
    public void testToString() {
        SparseBitIndex index =new SparseBitLong2Index(0x0000000000000000L, 0x0000000000000000L);

        assertEquals("00000000000000000000000000000000", index.toString());
    }

    @Test
    public void testNegative() {
        SparseBitIndex index = new SparseBitLong2Index(0xFF00_0000_0000_0000L, 0x8000_0000_0000_0000L);

        assertEquals(0xFF, index.segment(15));
        assertEquals(0x80, index.segment(7));
    }

    @Test
    public void testCompare() {
        SparseBitLong2Index i1 = new SparseBitLong2Index(0x00000000_00000000L, 0x00000000_00000000L);
        SparseBitLong2Index i2 = new SparseBitLong2Index(0x80000000_00000000L, 0x00000000_00000000L);
        SparseBitLong2Index i3 = new SparseBitLong2Index(0x80000000_00000000L, 0x00000000_00000001L);
        SparseBitLong2Index i4 = new SparseBitLong2Index(0xFFFFFFFF_FFFFFFFFL, 0xFFFFFFFF_FFFFFFFFL);

        assertTrue(i1.compareTo(i2) < 0);
        assertTrue(i2.compareTo(i1) > 0);

        assertTrue(i2.compareTo(i3) < 0);
        assertTrue(i3.compareTo(i2) > 0);

        assertTrue(i3.compareTo(i4) < 0);
        assertTrue(i4.compareTo(i3) > 0);
    }


}