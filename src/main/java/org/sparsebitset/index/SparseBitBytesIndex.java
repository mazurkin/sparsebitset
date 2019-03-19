package org.sparsebitset.index;

import org.sparsebitset.util.SparseBitUtil;

/**
 * Index for arbitrary {@code byte} array - will treat byte values as unsigned bytes
 */
public class SparseBitBytesIndex implements SparseBitIndex, Comparable<SparseBitBytesIndex> {

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
    public int compareTo(SparseBitBytesIndex that) {
        int levels1 = this.index.length;
        int levels2 = that.index.length;
        if (levels1 != levels2) {
            throw new IllegalArgumentException("Can not compare indices with different levels");
        }

        for (int i = 0; i < levels1; i++) {
            int v1 = this.index[i];
            int v2 = that.index[i];

            if (v1 > v2) {
                return +1;
            } else if (v1 < v2) {
                return -1;
            }
        }

        return 0;
    }

    @Override
    public int hashCode() {
        int hash = 1;

        for (int i = 0, limit = index.length; i < limit; i++) {
            hash = 31 * hash + segment(i);
        }

        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null) return false;

        if (this.getClass() != o.getClass()) return false;

        SparseBitBytesIndex that = (SparseBitBytesIndex) o;

        int levels1 = this.index.length;
        int levels2 = that.index.length;
        if (levels1 != levels2) {
            return false;
        }

        for (int i = 0; i < levels1; i++) {
            int v1 = this.index[i];
            int v2 = that.index[i];

            if (v1 != v2) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        int levels = index.length;

        StringBuilder sb = new StringBuilder(levels * 2);

        for (int i = 0; i < levels; i++) {
            sb.append(String.format("%02X", index[i]));
        }

        return sb.toString();
    }
}
