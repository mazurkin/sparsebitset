package org.sparsebitset;

import org.junit.Before;
import org.junit.Test;
import org.sparsebitset.index.SparseBitInetAddressIndex;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IPv6Test {

    private SparseBitSet<SparseBitInetAddressIndex> set;

    @Before
    public void setUp() {
        set = DeepSparseBitSet.createWithBits(128);
    }

    @Test
    public void test() {
        // set the individual two bits
        assertTrue(set.set(IPv6(0xFE80CD0000000000L, 0x00000000211E729CL)));
        assertTrue(set.set(IPv6(0xFE80CD0000000000L, 0x76330000211E729CL)));

        // set the huge range
        set.set(IPv6(0x0386330000000000L, 0x0000000000665320L), IPv6(0x0629872300000000L, 0x0089727327777777L));

        // check individual bits
        assertTrue(set.get(IPv6(0xFE80CD0000000000L, 0x00000000211E729CL)));
        assertTrue(set.get(IPv6(0xFE80CD0000000000L, 0x76330000211E729CL)));

        // check bits in the range
        assertTrue(set.get(IPv6(0x0386330000010000L, 0x0000000000000000L)));
        assertTrue(set.get(IPv6(0x0386330000020000L, 0x0000000000000000L)));
        assertTrue(set.get(IPv6(0x0629872300000000L, 0x0000000000000001L)));
        assertTrue(set.get(IPv6(0x0629872300000000L, 0x0000100000000000L)));
        assertTrue(set.get(IPv6(0x0629872300000000L, 0x0080000000000000L)));

        // check other random bits
        assertFalse(set.get(IPv6(0x0000100000000000L, 0x00000000211E729CL)));
        assertFalse(set.get(IPv6(0x0000000001000000L, 0x00000000211E729CL)));

        // invert the set
        set.flipAll();

        // check individual bits
        assertFalse(set.get(IPv6(0xFE80CD0000000000L, 0x00000000211E729CL)));
        assertFalse(set.get(IPv6(0xFE80CD0000000000L, 0x76330000211E729CL)));

        // check bits in the range
        assertFalse(set.get(IPv6(0x0386330000010000L, 0x0000000000000000L)));
        assertFalse(set.get(IPv6(0x0386330000020000L, 0x0000000000000000L)));
        assertFalse(set.get(IPv6(0x0629872300000000L, 0x0000000000000001L)));
        assertFalse(set.get(IPv6(0x0629872300000000L, 0x0000100000000000L)));
        assertFalse(set.get(IPv6(0x0629872300000000L, 0x0080000000000000L)));

        // check other random bits
        assertTrue(set.get(IPv6(0x0000100000000000L, 0x00000000211E729CL)));
        assertTrue(set.get(IPv6(0x0000000001000000L, 0x00000000211E729CL)));
    }

    private static SparseBitInetAddressIndex IPv6(long l1, long l2) {
        byte[] bytes = {
                (byte) (l1 >> 56),
                (byte) (l1 >> 48),
                (byte) (l1 >> 40),
                (byte) (l1 >> 32),
                (byte) (l1 >> 24),
                (byte) (l1 >> 16),
                (byte) (l1 >> 8),
                (byte) (l1),
                (byte) (l2 >> 56),
                (byte) (l2 >> 48),
                (byte) (l2 >> 40),
                (byte) (l2 >> 32),
                (byte) (l2 >> 24),
                (byte) (l2 >> 16),
                (byte) (l2 >> 8),
                (byte) (l2),
        };

        InetAddress address;
        try {
            address = Inet6Address.getByAddress(bytes);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Completely unexpected exception", e);
        }

        return SparseBitInetAddressIndex.of(address);
    }
}
