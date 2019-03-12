package org.sparsebitset.index;

import org.sparsebitset.util.SparseBitUtil;

/**
 * Index for arbitrary {@code byte} array - will treat byte values as unsigned bytes
 */
public class SparseBitBytesIndex implements SparseBitIndex {

    private final byte[] index;

    protected SparseBitBytesIndex(byte[] index) {
        if (index == null) {
            throw new IllegalArgumentException("Index can't be null");
        }

        if (index.length == 0) {
            throw new IllegalArgumentException("Index can't be empty");
        }

        this.index = index;
    }

    public static SparseBitBytesIndex of(byte... index) {
        return new SparseBitBytesIndex(index);
    }

    @Override
    public int segment(int level) {
        if (0 <= level && level < index.length) {
            // return unsigned byte value
            return SparseBitUtil.BYTE_MASK & index[index.length - 1 - level];
        } else {
            throw new IndexOutOfBoundsException("Level is out of bound for byte[" + index.length + "]: " + level);
        }
    }

    @Override
    public int levels() {
        return index.length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(index.length * 2);

        for (byte b : index) {
            sb.append(String.format("%02X", b));
        }

        return sb.toString();
    }

}
