package org.sparsebitset.index;

/**
 * Implementation returns constant segment value for any level
 */
public final class SparseBitConstIndex implements SparseBitIndex {

    /**
     * Instance returns maximum segment value {@code 255} for any level
     */
    public static final SparseBitIndex MAX = new SparseBitConstIndex(255);

    /**
     * Instance returns minimum segment value {@code 0} for any level
     */
    public static final SparseBitIndex MIN = new SparseBitConstIndex(0);

    private final int segment;

    private SparseBitConstIndex(int segment) {
        this.segment = segment;
    }

    @Override
    public int segment(int level) {
        return segment;
    }

    @Override
    public int levels() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String toString() {
        return "CONST=" + segment;
    }

}
