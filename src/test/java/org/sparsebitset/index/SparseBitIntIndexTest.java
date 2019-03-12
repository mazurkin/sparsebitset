package org.sparsebitset.index;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
}