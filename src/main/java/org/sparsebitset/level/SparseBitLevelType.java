package org.sparsebitset.level;

/**
 * Level type
 */
public enum SparseBitLevelType {

    /**
     * All bits are 0
     *
     * @see SparseBitLevels#NULL
     * @see SparseBitLevelNull
     */
    NULL,

    /**
     * All bits are 1
     *
     * @see SparseBitLevels#SQUASHED
     * @see SparseBitLevelSquashed
     */
    FULL,

    /**
     * Normal layer with some data
     *
     * @see SparseBitLevels#createLevel(int, int)
     * @see SparseBitLevel0
     * @see SparseBitLevelN
     */
    REAL

}
