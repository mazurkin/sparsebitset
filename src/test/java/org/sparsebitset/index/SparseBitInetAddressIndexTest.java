package org.sparsebitset.index;

import org.junit.Test;

import java.net.InetAddress;

import static junit.framework.TestCase.assertTrue;
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

    @Test
    public void testCompare() throws Exception {
        SparseBitInetAddressIndex i1 = SparseBitInetAddressIndex.of(InetAddress.getByName("127.1.2.3"));
        SparseBitInetAddressIndex i2 = SparseBitInetAddressIndex.of(InetAddress.getByName("127.1.2.4"));
        SparseBitInetAddressIndex i3 = SparseBitInetAddressIndex.of(InetAddress.getByName("255.1.2.3"));

        assertTrue(i1.compareTo(i2) < 0);
        assertTrue(i2.compareTo(i1) > 0);

        assertTrue(i2.compareTo(i3) < 0);
        assertTrue(i3.compareTo(i2) > 0);
    }
}