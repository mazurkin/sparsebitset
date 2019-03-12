package org.sparsebitset.index;

import org.sparsebitset.util.SparseBitUtil;

/**
 * Index for {@code int} value - will treat a value as unsigned
 */
public class SparseBitIntIndex implements SparseBitIndex {

    public static final int LEVELS = Integer.BYTES;

    private final int index;

    protected SparseBitIntIndex(int index) {
        this.index = index;
    }

    public static SparseBitIntIndex of(int index) {
        return new SparseBitIntIndex(index);
    }

    @Override
    public int segment(int level) {
        switch (level) {
            case 3:
                return SparseBitUtil.BYTE_MASK & (index >> 24);
            case 2:
                return SparseBitUtil.BYTE_MASK & (index >> 16);
            case 1:
                return SparseBitUtil.BYTE_MASK & (index >> 8);
            case 0:
                return SparseBitUtil.BYTE_MASK & (index);
            default:
                throw new IndexOutOfBoundsException("Level is out of bound for integer [0..3]: " + level);
        }
    }

    @Override
    public int levels() {
        return LEVELS;
    }

    @Override
    public String toString() {
        return String.format("%08X", index);
    }
}
