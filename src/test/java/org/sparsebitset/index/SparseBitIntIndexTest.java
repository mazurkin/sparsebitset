package org.sparsebitset.index;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SparseBitIntIndexTest {

    @Test
    public void testIndex() {
        SparseBitIndex index = SparseBitIntIndex.of(0xFF112233);

        assertEquals(4, index.levels());

        assertEquals(0xFF, index.segment(3));
        assertEquals(0x11, index.segment(2));
        assertEquals(0x22, index.segment(1));
        assertEquals(0x33, index.segment(0));

        assertEquals("FF112233", index.toString());
    }

    @Test
    public void testToString() {
        SparseBitIndex index = SparseBitIntIndex.of(0x00000000);

        assertEquals("00000000", index.toString());
    }

    @Test
    public void testCompare() {
        SparseBitIntIndex i1 = SparseBitIntIndex.of(0x00000000);
        SparseBitIntIndex i2 = SparseBitIntIndex.of(0x80000000);
        SparseBitIntIndex i3 = SparseBitIntIndex.of(0x80000001);
        SparseBitIntIndex i4 = SparseBitIntIndex.of(0xFFFFFFFF);

        assertTrue(i1.compareTo(i2) < 0);
        assertTrue(i2.compareTo(i1) > 0);

        assertTrue(i2.compareTo(i3) < 0);
        assertTrue(i3.compareTo(i2) > 0);

        assertTrue(i3.compareTo(i4) < 0);
        assertTrue(i4.compareTo(i3) > 0);
    }
}