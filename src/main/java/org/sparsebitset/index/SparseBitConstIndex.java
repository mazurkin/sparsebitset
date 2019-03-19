package org.sparsebitset.index;

/**
 * Implementation returns constant segment value for any level
 */
public final class SparseBitConstIndex implements SparseBitIndex, Comparable<SparseBitConstIndex> {

    /**
     * Instance returns maximum segment value {@code 255} for any level
     */
    public static final SparseBitIndex MAX = new SparseBitConstIndex(0xFF);

    /**
     * Instance returns minimum segment value {@code 0} for any level
     */
    public static final SparseBitIndex MIN = new SparseBitConstIndex(0x00);

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
    public int compareTo(SparseBitConstIndex that) {
        return Integer.compare(this.segment, that.segment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null) return false;

        if (this.getClass() != o.getClass()) return false;

        SparseBitConstIndex that = (SparseBitConstIndex) o;

        return this.segment == that.segment;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(segment);
    }

    @Override
    public String toString() {
        return String.format("CONST=%08X", segment);
    }

}
