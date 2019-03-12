package org.sparsebitset.index;

import org.sparsebitset.util.SparseBitUtil;

/**
 * Index for {@code long} value - will treat a value as unsigned
 */
public class SparseBitLongIndex implements SparseBitIndex {

    public static final int LEVELS = Long.BYTES;

    private final long index;

    protected SparseBitLongIndex(long index) {
        this.index = index;
    }

    public static SparseBitLongIndex of(long index) {
        return new SparseBitLongIndex(index);
    }

    @Override
    public int segment(int level) {
        switch (level) {
            case 7:
                return SparseBitUtil.BYTE_MASK & (int) (index >> 56);
            case 6:
                return SparseBitUtil.BYTE_MASK & (int) (index >> 48);
            case 5:
                return SparseBitUtil.BYTE_MASK & (int) (index >> 40);
            case 4:
                return SparseBitUtil.BYTE_MASK & (int) (index >> 32);
            case 3:
                return SparseBitUtil.BYTE_MASK & (int) (index >> 24);
            case 2:
                return SparseBitUtil.BYTE_MASK & (int) (index >> 16);
            case 1:
                return SparseBitUtil.BYTE_MASK & (int) (index >> 8);
            case 0:
                return SparseBitUtil.BYTE_MASK & (int) (index);
            default:
                throw new IndexOutOfBoundsException("Level is out of bound for long [0..7]: " + level);
        }
    }

    @Override
    public int levels() {
        return LEVELS;
    }

    @Override
    public String toString() {
        return String.format("%016X", index);
    }
}
