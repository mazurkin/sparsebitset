package org.sparsebitset.util;

/**
 * Utility methods and constants
 */
public final class SparseBitUtil {

    /**
     * Each level in sparse bit set contains 256 items (bits or underlying levels). This is index space addressed
     * by one single unsigned byte value.
     */
    public static final int LEVEL_SIZE = 256;

    /**
     * Mask for converting an unsigned byte to an integer
     */
    public static final int BYTE_MASK = 0x000000FF;

    private SparseBitUtil() {
    }

    public static void checkSegment(int segment) {
        if (segment < 0 || segment >= SparseBitUtil.LEVEL_SIZE) {
            throw new IllegalArgumentException("Index returned invalid segment value: " + segment);
        }
    }

    public static void checkSegments(int segmentFrom, int segmentTo) {
        if (segmentFrom < 0) {
            throw new IllegalArgumentException("Index returned invalid segment value: " + segmentFrom);
        }

        if (segmentTo >= SparseBitUtil.LEVEL_SIZE) {
            throw new IllegalArgumentException("Index returned invalid segment value: "
                    + ": " + segmentTo);
        }

        if (segmentFrom > segmentTo) {
            throw new IllegalArgumentException("Indexes returned invalid segment values: " + segmentFrom + " " + segmentTo);
        }
    }
}
