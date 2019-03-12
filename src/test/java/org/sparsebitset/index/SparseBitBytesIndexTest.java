package org.sparsebitset.index;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SparseBitBytesIndexTest {

    @Test
    public void testIndex() {
        byte[] bytes = new byte[] { (byte) 0xFF, (byte) 0x11, (byte) 0x22, (byte) 0x33 };

        SparseBitIndex index = SparseBitBytesIndex.of(bytes);

        assertEquals(4, index.levels());

        assertEquals(0xFF, index.segment(3));
        assertEquals(0x11, index.segment(2));
        assertEquals(0x22, index.segment(1));
        assertEquals(0x33, index.segment(0));

        assertEquals("FF112233", index.toString());
    }


}