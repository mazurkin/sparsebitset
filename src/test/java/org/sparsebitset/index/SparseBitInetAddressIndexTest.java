package org.sparsebitset.index;

import org.junit.Test;

import java.net.InetAddress;

import static org.junit.Assert.assertEquals;

public class SparseBitInetAddressIndexTest {

    @Test
    public void testIndex() throws Exception {
        SparseBitIndex index = SparseBitInetAddressIndex.of(InetAddress.getByName("127.1.2.3"));

        assertEquals(4, index.levels());

        assertEquals(127, index.segment(3));
        assertEquals(1, index.segment(2));
        assertEquals(2, index.segment(1));
        assertEquals(3, index.segment(0));

        assertEquals("7F010203", index.toString());
    }


}