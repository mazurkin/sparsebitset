package org.sparsebitset;

import org.junit.Test;
import org.sparsebitset.index.SparseBitInetAddressIndex;
import org.sparsebitset.index.SparseBitIntIndex;
import org.sparsebitset.index.SparseBitLongIndex;

import java.net.Inet6Address;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DeepSparseBitSetTest {

    @Test
    public void testSimple() {
        SparseBitSet<SparseBitIntIndex> set = new DeepSparseBitSet<>(SparseBitIntIndex.LEVELS);

        // check the bit
        assertFalse(set.get(SparseBitIntIndex.of(0x11223344)));
        set.validate();

        // set a bit
        assertTrue(set.set(SparseBitIntIndex.of(0x11223344)));
        set.validate();

        // set a bit once again
        assertFalse(set.set(SparseBitIntIndex.of(0x11223344)));
        set.validate();

        // check the bit
        assertTrue(set.get(SparseBitIntIndex.of(0x11223344)));
        set.validate();

        // check an unknown bit
        assertFalse(set.get(SparseBitIntIndex.of(0x11223355)));
        set.validate();

        // clear a bit
        assertTrue(set.clear(SparseBitIntIndex.of(0x11223344)));
        set.validate();

        // clear a bit once again
        assertFalse(set.clear(SparseBitIntIndex.of(0x11223344)));
        set.validate();

        // check the bit
        assertFalse(set.get(SparseBitIntIndex.of(0x11223344)));
        set.validate();
    }

    @Test
    public void testFolding() {
        SparseBitSet<SparseBitIntIndex> set = new DeepSparseBitSet<>(SparseBitIntIndex.LEVELS);

        // add 255 bits
        for (int i = 0; i < 255; i++) {
            assertTrue(set.set(SparseBitIntIndex.of(0x11223300 + i)));
        }
        set.validate();

        // add 256th bit - this will lead to collapse of 1 final level
        assertTrue(set.set(SparseBitIntIndex.of(0x112233FF)));
        set.validate();

        // check 1 bits
        assertTrue(set.get(SparseBitIntIndex.of(0x11223300)));
        assertTrue(set.get(SparseBitIntIndex.of(0x11223344)));
        assertTrue(set.get(SparseBitIntIndex.of(0x112233FF)));
        set.validate();

        // check neighbour 0 bits
        assertFalse(set.get(SparseBitIntIndex.of(0x112232FF)));
        assertFalse(set.get(SparseBitIntIndex.of(0x11223400)));
        set.validate();

        // clean 1 bit - this will lead to unfolding
        assertTrue(set.clear(SparseBitIntIndex.of(0x11223344)));
        assertFalse(set.clear(SparseBitIntIndex.of(0x11223344)));
        set.validate();

        // check cleaned bit
        assertFalse(set.get(SparseBitIntIndex.of(0x11223344)));
        set.validate();

        // check neighbour 1 bits
        assertTrue(set.get(SparseBitIntIndex.of(0x11223300)));
        assertTrue(set.get(SparseBitIntIndex.of(0x112233FF)));
        set.validate();

        // flip bit
        set.flip(SparseBitIntIndex.of(0x11223344));
        set.validate();

        // check flipped bit
        assertTrue(set.get(SparseBitIntIndex.of(0x11223344)));
        set.validate();
    }

    @Test
    public void testIPv6() throws Exception {
        // IPv6 has 128-bit index space
        SparseBitSet<SparseBitInetAddressIndex> set = DeepSparseBitSet.createWithBits(128);

        // set individual IP
        assertTrue(set.set(
                SparseBitInetAddressIndex.of(Inet6Address.getByName("FE80:CD00:0000:0000:0000:0000:211E:729C"))));
        
        // get individual IP
        assertTrue(set.get(
                SparseBitInetAddressIndex.of(Inet6Address.getByName("FE80:CD00:0000:0000:0000:0000:211E:729C"))));

        // get individual IP - all 0
        assertFalse(set.get(
                SparseBitInetAddressIndex.of(Inet6Address.getByName("FE80:CD00:0000:0000:0000:0000:211E:729B"))));
        assertFalse(set.get(
                SparseBitInetAddressIndex.of(Inet6Address.getByName("FE80:CD00:0000:0000:0000:0000:211E:729D"))));
    }

    @Test
    public void testFull() {
        SparseBitSet<SparseBitIntIndex> set = new DeepSparseBitSet<>(SparseBitIntIndex.LEVELS);

        set.setAll();
        set.validate();

        assertTrue(set.isFull());
        assertFalse(set.isEmpty());

        assertTrue(set.get(SparseBitIntIndex.of(0x11223344)));
        set.validate();

        assertTrue(set.clear(SparseBitIntIndex.of(0x11223344)));
        set.validate();

        assertFalse(set.get(SparseBitIntIndex.of(0x11223344)));
        assertFalse(set.isFull());
        assertFalse(set.isEmpty());

        assertTrue(set.get(SparseBitIntIndex.of(0x11223343)));
        assertTrue(set.get(SparseBitIntIndex.of(0x11223345)));

        assertTrue(set.get(SparseBitIntIndex.of(0x00000000)));
        assertTrue(set.get(SparseBitIntIndex.of(0xFFFFFFFF)));

        assertTrue(set.set(SparseBitIntIndex.of(0x11223344)));
        set.validate();

        assertTrue(set.get(SparseBitIntIndex.of(0x11223344)));
        assertTrue(set.isFull());
        assertFalse(set.isEmpty());
    }

    @Test
    public void testClean() {
        SparseBitSet<SparseBitIntIndex> set = new DeepSparseBitSet<>(SparseBitIntIndex.LEVELS);

        assertTrue(set.isEmpty());
        assertFalse(set.isFull());

        assertTrue(set.set(SparseBitIntIndex.of(0x11223344)));
        set.validate();

        assertTrue(set.get(SparseBitIntIndex.of(0x11223344)));

        assertFalse(set.isEmpty());
        assertFalse(set.isFull());

        set.clearAll();
        set.validate();

        assertFalse(set.get(SparseBitIntIndex.of(0x11223344)));

        assertTrue(set.isEmpty());
        assertFalse(set.isFull());

        assertTrue(set.set(SparseBitIntIndex.of(0x11223344)));
        set.validate();

        assertTrue(set.clear(SparseBitIntIndex.of(0x11223344)));
        set.validate();

        assertTrue(set.isEmpty());
        assertFalse(set.isFull());
    }

    @Test
    public void testRanges() {
        SparseBitSet<SparseBitIntIndex> set = new DeepSparseBitSet<>(SparseBitIntIndex.LEVELS);

        // set the large range
        // 0x11223101 - 0x112235FE
        set.set(SparseBitIntIndex.of(0x11223101), SparseBitIntIndex.of(0x112235FE));
        set.validate();

        assertFalse(set.isEmpty());
        assertFalse(set.isFull());

        checkRange(set, 0x11223101, 0x112235FE, true);

        checkRange(set, 0x112230FF, 0x11223100, false);
        checkRange(set, 0x112235FF, 0x11223600, false);

        // extend the te original range
        // 0x11223100 - 0x112235FF
        assertTrue(set.set(SparseBitIntIndex.of(0x11223100)));
        assertTrue(set.set(SparseBitIntIndex.of(0x112235FF)));
        set.validate();

        assertFalse(set.isEmpty());
        assertFalse(set.isFull());

        checkRange(set, 0x11223100, 0x112235FF, true);

        checkRange(set, 0x112230F0, 0x112230FF, false);
        checkRange(set, 0x11223600, 0x112236FF, false);

        // cut a hole in the center
        // 0x11223100 - 0x11223200, 0x112234FF - 0x112235FF
        set.clear(SparseBitIntIndex.of(0x11223201), SparseBitIntIndex.of(0x112234FE));
        set.validate();

        checkRange(set, 0x11223100, 0x11223200, true);
        checkRange(set, 0x112234FF, 0x112235FF, true);

        checkRange(set, 0x112230F0, 0x112230FF, false);
        checkRange(set, 0x11223201, 0x112234FE, false);
        checkRange(set, 0x11223600, 0x112236FF, false);

        // cut two small holes
        // 0x11223100 - 0x11223160, 0x1122316F - 0x11223200, 0x112234FF - 0x11223560, 0x1122356F - 0x112235FF
        set.clear(SparseBitIntIndex.of(0x11223161), SparseBitIntIndex.of(0x1122316E));
        set.clear(SparseBitIntIndex.of(0x11223561), SparseBitIntIndex.of(0x1122356E));
        set.validate();

        checkRange(set, 0x11223100, 0x11223160, true);
        checkRange(set, 0x1122316F, 0x11223200, true);
        checkRange(set, 0x112234FF, 0x11223560, true);
        checkRange(set, 0x1122356F, 0x112235FF, true);

        checkRange(set, 0x112230F0, 0x112230FF, false);
        checkRange(set, 0x11223161, 0x1122316E, false);
        checkRange(set, 0x11223201, 0x112234FE, false);
        checkRange(set, 0x11223561, 0x1122356E, false);
        checkRange(set, 0x11223600, 0x112236FF, false);

        // flip some
        // 0x11223100 - 0x11223120, 0x11223161 - 0x1122316E, 0x11223180 - 0x11223200, 0x112234FF - 0x11223560, 0x1122356F - 0x112235FF
        set.flip(SparseBitIntIndex.of(0x11223121), SparseBitIntIndex.of(0x1122317F));
        set.validate();

        checkRange(set, 0x11223100, 0x11223120, true);
        checkRange(set, 0x11223161, 0x1122316E, true);
        checkRange(set, 0x11223180, 0x11223200, true);
        checkRange(set, 0x112234FF, 0x11223560, true);
        checkRange(set, 0x1122356F, 0x112235FF, true);

        checkRange(set, 0x112230F0, 0x112230FF, false);
        checkRange(set, 0x11223121, 0x11223160, false);
        checkRange(set, 0x1122316F, 0x1122317E, false);
        checkRange(set, 0x11223201, 0x112234FE, false);
        checkRange(set, 0x11223561, 0x1122356E, false);
        checkRange(set, 0x11223600, 0x112236FF, false);

        // invert everything
        set.flipAll();
        set.validate();

        checkRange(set, 0x11223100, 0x11223120, false);
        checkRange(set, 0x11223161, 0x1122316E, false);
        checkRange(set, 0x11223180, 0x11223200, false);
        checkRange(set, 0x112234FF, 0x11223560, false);
        checkRange(set, 0x1122356F, 0x112235FF, false);

        checkRange(set, 0x112230F0, 0x112230FF, true);
        checkRange(set, 0x11223121, 0x11223160, true);
        checkRange(set, 0x1122316F, 0x1122317E, true);
        checkRange(set, 0x11223201, 0x112234FE, true);
        checkRange(set, 0x11223561, 0x1122356E, true);
        checkRange(set, 0x11223600, 0x112236FF, true);
    }

    @Test
    public void testSingleRanges() {
        SparseBitSet<SparseBitIntIndex> set = new DeepSparseBitSet<>(SparseBitIntIndex.LEVELS);

        // set a single bit
        set.set(SparseBitIntIndex.of(0x11223344), SparseBitIntIndex.of(0x11223344));
        set.validate();

        assertFalse(set.isEmpty());
        assertFalse(set.isFull());

        assertTrue(set.get(SparseBitIntIndex.of(0x11223344)));

        assertFalse(set.get(SparseBitIntIndex.of(0x11223343)));
        assertFalse(set.get(SparseBitIntIndex.of(0x11223345)));

        // clear the neighbour bit - nothing shall be changes
        set.clear(SparseBitIntIndex.of(0x11223345), SparseBitIntIndex.of(0x11223345));
        set.validate();

        assertFalse(set.isEmpty());
        assertFalse(set.isFull());

        assertTrue(set.get(SparseBitIntIndex.of(0x11223344)));

        // clear the original bit
        set.clear(SparseBitIntIndex.of(0x11223344), SparseBitIntIndex.of(0x11223344));
        set.validate();

        assertTrue(set.isEmpty());
        assertFalse(set.isFull());

        assertFalse(set.get(SparseBitIntIndex.of(0x11223344)));

        assertFalse(set.get(SparseBitIntIndex.of(0x11223343)));
        assertFalse(set.get(SparseBitIntIndex.of(0x11223345)));

        // flip the original bit
        set.flip(SparseBitIntIndex.of(0x11223344), SparseBitIntIndex.of(0x11223344));
        set.validate();

        assertFalse(set.isEmpty());
        assertFalse(set.isFull());

        assertTrue(set.get(SparseBitIntIndex.of(0x11223344)));

        assertFalse(set.get(SparseBitIntIndex.of(0x11223343)));
        assertFalse(set.get(SparseBitIntIndex.of(0x11223345)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRanges() {
        SparseBitSet<SparseBitIntIndex> set = new DeepSparseBitSet<>(SparseBitIntIndex.LEVELS);

        set.set(SparseBitIntIndex.of(0x11223345), SparseBitIntIndex.of(0x11223344));
    }

    @Test
    public void testLevel0Bits() {
        SparseBitSet<SparseBitIntIndex> set = DeepSparseBitSet.createWithLevels(1);

        assertTrue(set.isEmpty());
        assertFalse(set.isFull());

        assertTrue(set.set(SparseBitIntIndex.of(0x44)));
        assertFalse(set.set(SparseBitIntIndex.of(0x44)));
        set.validate();

        assertFalse(set.isEmpty());
        assertFalse(set.isFull());

        assertTrue(set.get(SparseBitIntIndex.of(0x44)));
        assertFalse(set.get(SparseBitIntIndex.of(0x01)));
        assertFalse(set.get(SparseBitIntIndex.of(0xFE)));
        
        assertTrue(set.clear(SparseBitIntIndex.of(0x44)));
        assertFalse(set.clear(SparseBitIntIndex.of(0x44)));
        set.validate();

        assertTrue(set.isEmpty());
        assertFalse(set.isFull());

        assertFalse(set.get(SparseBitIntIndex.of(0x44)));

        set.flip(SparseBitIntIndex.of(0x44));
        set.validate();

        assertFalse(set.isEmpty());
        assertFalse(set.isFull());

        assertTrue(set.get(SparseBitIntIndex.of(0x44)));

        set.flipAll();
        set.validate();

        assertFalse(set.get(SparseBitIntIndex.of(0x44)));
        assertTrue(set.get(SparseBitIntIndex.of(0x01)));
        assertTrue(set.get(SparseBitIntIndex.of(0xFE)));

        set.clearAll();
        set.validate();

        assertFalse(set.get(SparseBitIntIndex.of(0x44)));
        assertFalse(set.get(SparseBitIntIndex.of(0x01)));
        assertFalse(set.get(SparseBitIntIndex.of(0xFE)));

        set.setAll();
        set.validate();

        assertTrue(set.get(SparseBitIntIndex.of(0x44)));
        assertTrue(set.get(SparseBitIntIndex.of(0x01)));
        assertTrue(set.get(SparseBitIntIndex.of(0xFE)));
        
        set.clear(SparseBitIntIndex.of(0x10), SparseBitIntIndex.of(0xF0));
        set.validate();

        assertFalse(set.get(SparseBitIntIndex.of(0x44)));
        assertTrue(set.get(SparseBitIntIndex.of(0x01)));
        assertTrue(set.get(SparseBitIntIndex.of(0xFE)));

        set.set(SparseBitIntIndex.of(0x40), SparseBitIntIndex.of(0x60));
        set.validate();

        assertTrue(set.get(SparseBitIntIndex.of(0x44)));
        assertTrue(set.get(SparseBitIntIndex.of(0x01)));
        assertTrue(set.get(SparseBitIntIndex.of(0xFE)));

        set.flip(SparseBitIntIndex.of(0x42), SparseBitIntIndex.of(0x46));
        set.validate();

        assertFalse(set.get(SparseBitIntIndex.of(0x44)));
        assertTrue(set.get(SparseBitIntIndex.of(0x01)));
        assertTrue(set.get(SparseBitIntIndex.of(0xFE)));
    }

    @Test
    public void testPrematureSquashing() {
        SparseBitSet<SparseBitIntIndex> set = new DeepSparseBitSet<>(SparseBitIntIndex.LEVELS, 240);
        set.validate();

        // set 239 bits
        for (int i = 0; i < 239; i++) {
            assertTrue(set.set(SparseBitIntIndex.of(0x11223300 + i)));
        }
        set.validate();

        // there should be no squashing yet
        assertFalse(set.get(SparseBitIntIndex.of(0x112233FF)));

        // set 240th bit
        assertTrue(set.set(SparseBitIntIndex.of(0x11223300 + 240)));
        set.validate();

        // there should be squashing on this level
        assertTrue(set.get(SparseBitIntIndex.of(0x112233FF)));

        // but no squashing on neighbour levels
        assertFalse(set.get(SparseBitIntIndex.of(0x112232FF)));
        assertFalse(set.get(SparseBitIntIndex.of(0x11223400)));
    }

    @Test
    public void testHugeRange() {
        SparseBitSet<SparseBitLongIndex> set = new DeepSparseBitSet<>(SparseBitLongIndex.LEVELS);

        // set all the bits
        set.set(SparseBitLongIndex.of(0x0000_0000_0000_0000L), SparseBitLongIndex.of(0xFFFF_FFFF_FFFF_FFFFL));

        assertTrue(set.get(SparseBitLongIndex.of(0x0000_0000_0000_0000L)));
        assertTrue(set.get(SparseBitLongIndex.of(0x8000_0000_0000_0000L)));
        assertTrue(set.get(SparseBitLongIndex.of(0xFFFF_FFFF_FFFF_FFFFL)));

        // clear all the bits
        set.clear(SparseBitLongIndex.of(0x0000_0000_0000_0000L), SparseBitLongIndex.of(0xFFFF_FFFF_FFFF_FFFFL));

        assertFalse(set.get(SparseBitLongIndex.of(0x0000_0000_0000_0000L)));
        assertFalse(set.get(SparseBitLongIndex.of(0x8000_0000_0000_0000L)));
        assertFalse(set.get(SparseBitLongIndex.of(0xFFFF_FFFF_FFFF_FFFFL)));

        // flip all the bits
        set.flip(SparseBitLongIndex.of(0x0000_0000_0000_0000L), SparseBitLongIndex.of(0xFFFF_FFFF_FFFF_FFFFL));

        assertTrue(set.get(SparseBitLongIndex.of(0x0000_0000_0000_0000L)));
        assertTrue(set.get(SparseBitLongIndex.of(0x8000_0000_0000_0000L)));
        assertTrue(set.get(SparseBitLongIndex.of(0xFFFF_FFFF_FFFF_FFFFL)));
    }

    @Test
    public void testClone() {
        SparseBitSet<SparseBitIntIndex> set = DeepSparseBitSet.createWithLevels(SparseBitIntIndex.LEVELS);

        set.set(SparseBitIntIndex.of(0x7001_0000), SparseBitIntIndex.of(0x9001_FFFF));
        set.validate();

        assertFalse(set.get(SparseBitIntIndex.of(0x7000_0000)));
        assertTrue(set.get(SparseBitIntIndex.of(0x8000_0000)));
        assertFalse(set.get(SparseBitIntIndex.of(0x9002_0000)));

        SparseBitSet<SparseBitIntIndex> clone = set.copy();
        clone.validate();

        assertFalse(clone.get(SparseBitIntIndex.of(0x7000_0000)));
        assertTrue(clone.get(SparseBitIntIndex.of(0x8000_0000)));
        assertFalse(clone.get(SparseBitIntIndex.of(0x9002_0000)));

        set.clearAll();
        set.validate();

        clone.validate();

        assertFalse(clone.get(SparseBitIntIndex.of(0x7000_0000)));
        assertTrue(clone.get(SparseBitIntIndex.of(0x8000_0000)));
        assertFalse(clone.get(SparseBitIntIndex.of(0x9002_0000)));
    }

    private static void checkRange(SparseBitSet<SparseBitIntIndex> set,
                                   int fromIndexInclusive, int toIndexInclusive, boolean expected)
    {
        for (int i = fromIndexInclusive; i <= toIndexInclusive; i++) {
            boolean result = set.get(SparseBitIntIndex.of(i));

            assertEquals(String.format("Mismatch on %08X", i), expected, result);
        }
    }
}