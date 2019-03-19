package org.sparsebitset.level;

/**
 * Factory for layers
 */
public final class SparseBitLevels {

    /**
     * Null layer (all bits are set)
     */
    public static final SparseBitLevel NULL = SparseBitLevelNull.INSTANCE;

    /**
     * Squashed layer (no any bits are set)
     */
    public static final SparseBitLevel FULL = SparseBitLevelFull.INSTANCE;

    /**
     * Create underlying level
     *
     * @return Level object
     */
    public static SparseBitLevel createLevel(int maximumOccupancy, int levels) {
        int level = levels - 1;

        if (level > 0) {
            return new SparseBitLevelN(maximumOccupancy, level);
        } else {
            return new SparseBitLevel0(maximumOccupancy);
        }
    }

    private SparseBitLevels() {
    }

}
