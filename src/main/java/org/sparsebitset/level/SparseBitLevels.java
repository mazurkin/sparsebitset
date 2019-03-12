package org.sparsebitset.level;

import org.sparsebitset.SparseBitSet;
import org.sparsebitset.index.SparseBitIndex;

/**
 * Factory for layers
 */
public final class SparseBitLevels {

    private SparseBitLevels() {
    }

    /**
     * Create underlying level
     *
     * @return Level object
     */
    public static SparseBitSet<SparseBitIndex> createUnderlying(int maximumOccupancy, int levels) {
        int underlyingLevel = levels - 1;

        if (underlyingLevel > 0) {
            return new SparseBitLevelN(maximumOccupancy, underlyingLevel);
        } else {
            return new SparseBitLevel0(maximumOccupancy);
        }
    }

}
