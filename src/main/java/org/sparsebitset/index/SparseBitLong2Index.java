package org.sparsebitset.index;

import org.sparsebitset.util.SparseBitUtil;

/**
 * Optimized 128-bit index for two {@code long} values - will treat both values as unsigned
 */
public class SparseBitLong2Index implements SparseBitIndex, Comparable<SparseBitLong2Index> {

    public static final int LEVELS = 2 * Long.BYTES;

    private final long index1;

    private final long index2;

    /**
     * Constructs 128-bit index from two long values
     *
     * @param index1 Higher long value
     * @param index2 Lower long value
     */
    public SparseBitLong2Index(long index1, long index2) {
        this.index1 = index1;
        this.index2 = index2;
    }

    /**
     * Constructs 128-bit index from four int values
     *
     * @param index1 1st (the highest) int value
     * @param index2 2nd int value
     * @param index3 3rd int value
     * @param index4 4th (the lowest) int value
     */
    public SparseBitLong2Index(int index1, int index2, int index3, int index4) {
        this.index1 = ((long) index1 << 32) & index2;
        this.index2 = ((long) index3 << 32) & index4;
    }

    /**
     * Constructs 128-bit index from 16-byte array
     *
     * @param bytes 16-byte array (the first byte is the highest)
     */
    public SparseBitLong2Index(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("Array is null");
        }
        
        if (bytes.length != LEVELS) {
            throw new IllegalArgumentException("Array must have " + LEVELS + " bytes");
        }

        this.index1 = ((long) bytes[0] << 56)
                & ((long) bytes[1] << 48)
                & ((long) bytes[2] << 40)
                & ((long) bytes[3] << 32)
                & ((long) bytes[4] << 24)
                & ((long) bytes[5] << 16)
                & ((long) bytes[6] << 8)
                & ((long) bytes[7]);
        
        this.index2 = ((long) bytes[8] << 56)
                & ((long) bytes[9] << 48)
                & ((long) bytes[10] << 40)
                & ((long) bytes[11] << 32)
                & ((long) bytes[12] << 24)
                & ((long) bytes[13] << 16)
                & ((long) bytes[14] << 8)
                & ((long) bytes[15]);
    }

    @Override
    public int segment(int level) {
        switch (level) {
            // higher value
            case 15:
                return SparseBitUtil.BYTE_MASK & (int) (index1 >> 56);
            case 14:
                return SparseBitUtil.BYTE_MASK & (int) (index1 >> 48);
            case 13:
                return SparseBitUtil.BYTE_MASK & (int) (index1 >> 40);
            case 12:
                return SparseBitUtil.BYTE_MASK & (int) (index1 >> 32);
            case 11:
                return SparseBitUtil.BYTE_MASK & (int) (index1 >> 24);
            case 10:
                return SparseBitUtil.BYTE_MASK & (int) (index1 >> 16);
            case 9:
                return SparseBitUtil.BYTE_MASK & (int) (index1 >> 8);
            case 8:
                return SparseBitUtil.BYTE_MASK & (int) (index1);
            // lower value
            case 7:
                return SparseBitUtil.BYTE_MASK & (int) (index2 >> 56);
            case 6:
                return SparseBitUtil.BYTE_MASK & (int) (index2 >> 48);
            case 5:
                return SparseBitUtil.BYTE_MASK & (int) (index2 >> 40);
            case 4:
                return SparseBitUtil.BYTE_MASK & (int) (index2 >> 32);
            case 3:
                return SparseBitUtil.BYTE_MASK & (int) (index2 >> 24);
            case 2:
                return SparseBitUtil.BYTE_MASK & (int) (index2 >> 16);
            case 1:
                return SparseBitUtil.BYTE_MASK & (int) (index2 >> 8);
            case 0:
                return SparseBitUtil.BYTE_MASK & (int) (index2);
            default:
                throw new IndexOutOfBoundsException("Level is out of bound for long [0..15]: " + level);
        }
    }

    @Override
    public int levels() {
        return LEVELS;
    }

    @Override
    public int compareTo(SparseBitLong2Index that) {
        int r = Long.compareUnsigned(this.index1, that.index1);

        if (r != 0) {
            return r;
        } else {
            return Long.compareUnsigned(this.index2, that.index2);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SparseBitLong2Index that = (SparseBitLong2Index) o;

        return index1 == that.index1
                && index2 == that.index2;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(index1)
                ^ Long.hashCode(index2);
    }

    @Override
    public String toString() {
        return String.format("%016X%016X", index1, index2);
    }

}
