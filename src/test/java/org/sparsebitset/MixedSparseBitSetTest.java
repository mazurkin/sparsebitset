package org.sparsebitset;

import org.junit.Test;
import org.sparsebitset.index.SparseBitIntIndex;

import static org.junit.Assert.*;

public class MixedSparseBitSetTest {

    @Test
    public void testOps() {
        SparseBitSet<SparseBitIntIndex> set = DefaultSparseBitSet.createWithLevels(SparseBitIntIndex.LEVELS);

        // set range
        set.set(SparseBitIntIndex.of(0x10000000), SparseBitIntIndex.of(0x1FFFFFFF));

        // set individual index
        assertTrue(set.set(SparseBitIntIndex.of(0x60000000)));

        // check inside the range
        assertTrue(set.get(SparseBitIntIndex.of(0x10000000)));
        assertTrue(set.get(SparseBitIntIndex.of(0x18000000)));
        assertTrue(set.get(SparseBitIntIndex.of(0x1FFFFFFF)));

        // check the individual index
        assertTrue(set.get(SparseBitIntIndex.of(0x60000000)));

        // check outside the range
        assertFalse(set.get(SparseBitIntIndex.of(0x08000000)));
        assertFalse(set.get(SparseBitIntIndex.of(0x28000000)));
    }
}